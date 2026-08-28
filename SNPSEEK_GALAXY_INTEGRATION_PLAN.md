# SNPseek → Galaxy Integration Plan

**Scope:** Hackathon project to give Galaxy (usegalaxy.org-style workflow platform) data connectivity to SNPseek v3.
**Date scoped:** 2026-08-19
**Author:** Scoped from codebase investigation (see cited files below).

---

## 1. Executive Summary

SNPseek v3 already exposes a public REST endpoint (`GET /ws/genotype/gettable`) that supports **Use Case 1 with no server-side changes**: querying SNPs for multiple accessions restricted to a chromosome + coordinate range returns TSV or CSV output today. A Galaxy tool XML wrapper calling that endpoint is achievable in a single hackathon day for UC1.

**Use Case 2** (genome-wide, single variety) is blocked at the REST API layer: `GenotypeWS.java` requires a `chr` parameter and throws if it is absent with no locus or position list. The underlying service layer (`SnpsStringHDF5nRDBMSHybridService`) does have an empty-chr code path, so exposing genome-wide is a small server-side change — but it requires a build and redeploy, making it out of scope for a hackathon day unless the server is accessible for live patching.

Neither use case produces VCF natively. Current output is a **wide matrix** (varieties as rows, SNP positions as columns) in JSON, TSV, or CSV. VCF requires a transposed, per-variant serialization with `CHROM/POS/ID/REF/ALT/FORMAT/SAMPLE` columns. A Python converter from the existing TSV/CSV inside the Galaxy tool is feasible at hackathon scale; a server-side `format=vcf` is the clean production answer.

The existing `portal/galaxy/` Java package (`GalaxyController`, blend4j) is **outbound** — SNPseek submitting jobs *to* Galaxy — the opposite direction from what is needed here. It provides no reusable infrastructure for the inbound pull.

**Recommended hackathon approach:** Write a Galaxy tool XML + Python script wrapper that calls `/ws/genotype/gettable?format=tsv` directly (public, no auth), transposes the output to VCF (or tabular) in Python, handles UC1 in a single call, and handles UC2 by looping over the 12 rice chromosomes. Total estimated hackathon time: **8–10 hours**.

---

## 2. Current-State Findings

### 2.1 What Already Works for This Use Case

| Finding | Evidence |
|---|---|
| `/ws/genotype/gettable` is public (no auth) | Jersey servlet mounted at `/ws/*` (`web.xml`); no `@RolesAllowed` on `getVariantTable()` (`GenotypeWS.java`) |
| UC1 is supported today — varid + chr + start + end + format=tsv | `GenotypeWS.java:280–341`; `buildTsv()` at same file |
| Coordinate-range queries on `snp_featureloc` are indexed | `snp_featureloc_organism_id_srcfeature_id_position_idx`, `snp_featureloc_srcfeature_id_position_organism_id_idx` in `docker/postgresDB/iric_schemaOnly.sql` |
| Service layer has an empty-chr (genome-wide) code path | `SnpsStringHDF5nRDBMSHybridService.getSNPPoslist()`: falls to `getSNPs(organism_id, "", null, null, ...)` when chr, locus, and poslist are all absent |
| TSV output has reference allele (row 2) and variety names | `GenotypeWS.buildTsv()` and `buildCsv()` in `GenotypeWS.java` |
| Variety-axis pagination: `varOffset` + `varLimit` query params | `GenotypeWS.java:280–281, 327–341` |
| GENO_VAR_CAP = 20,000 varieties — covers all accessions | `GenotypeWS.java:80`; production has 18,752 accessions (`brs.properties`) |
| BrAPI v1 allelematrix endpoint exists with TSV+gzip streaming | `BrAPI.java`; `bStreaming = true`, `bTSVGzipped = true` (static flags) |

### 2.2 Gaps That Block the Use Cases

| Gap | Impact | Evidence |
|---|---|---|
| `chr` parameter is required by `/gettable` | Blocks UC2 (genome-wide) | `GenotypeWS.java`: `if (chr == null && locus == null && poslist == null) throw ...` (lines ~290–295) |
| No VCF output format | Both UCs land in Galaxy as wide-matrix TSV/CSV, not VCF | `GenotypeWS.java`: `format` param accepts `json`, `tsv`, `csv` only; `VariantTableWS.java` structure is positions-as-columns |
| No streaming in `/gettable` | Large datasets (all varieties × Chr1) will fully materialize in Tomcat heap before first byte sent | `GenotypeWS.java:327–341`: `sliceVarieties()` builds full result then serializes |
| BrAPI is v1 only | Standard Galaxy BrAPI tools expect v2.1 | `BrAPI.java:@Path("/brapi/v1")`; `CLAUDE.md` acknowledges v1 |
| Existing Galaxy code is outbound | The blend4j client (`GalaxyController.java`) submits workflows *to* Galaxy — no reusable code for pulling data *from* SNPseek into Galaxy | `GalaxyController.java:@Autowired GalaxyFacade galaxy` |
| `vsnp_refposindex` is a regular VIEW | Coordinate + HDF5-index joins are not pre-materialized; heavy queries recompute the join | `iric_schemaOnly.sql`: `CREATE VIEW vsnp_refposindex AS SELECT ...` |

### 2.3 Production Deployment Context

- Deployed at `https://brs-snpseek.duckdns.org/19kRG` (`brs.properties`)
- Default dataset: `20k` (18,752 accessions); also supports `3k`, `3kfiltered`, `3kcore`, `3kbase`, `hdra`
- Committed `config.properties` has `webserver=brs` — server changes require an actual production build and WAR redeploy

---

## 3. Recommended Integration Approach

### Options Evaluated

| Option | Summary | Verdict |
|---|---|---|
| **A. Galaxy data-source / fetch tool (Python XML calling /gettable)** | Galaxy tool XML + Python script hits the existing public REST endpoint; handles format conversion in Python | **Recommended for hackathon** |
| **B. BrAPI-based Galaxy tool** | Use existing `/ws/brapi/v1/allelematrix` endpoint, which has streaming + TSV+gzip; BrAPI is a known pattern in the Galaxy bioinformatics ecosystem | Lower priority — BrAPI v1 is not standard; token auth adds friction; allelematrix response format still needs VCF conversion |
| **C. New server-side /vcf endpoint in GenotypeWS** | Add `format=vcf` to existing endpoint, remove chr requirement | **Best production path** — but requires build + deploy; not hackathon-day feasible unless server access is available |
| **D. Bioblend scripted pull** | Pure Python using bioblend to upload results to Galaxy history | Adds bioblend dependency complexity for no benefit vs. Option A; bioblend is meant for managing Galaxy, not fetching from external APIs |

### Recommendation: Option A for Hackathon, Option A + C for Production

**Hackathon day (Option A):**

1. Write a Galaxy tool XML with parameters: `accession_ids` (text, comma-separated IDs or "all"), `chromosome` (select: 1–12 or "all"), `start`/`end` (integers, optional), `dataset` (select: 3kfiltered / 3k / 20k), `output_format` (tabular or VCF).
2. Python script calls `GET /ws/genotype/gettable?varid=...&chr=...&start=...&end=...&dataset=...&format=tsv`.
3. For UC2 (genome-wide), loop over chromosomes 1–12, collect 12 TSV responses, concatenate.
4. For VCF output: convert the wide-matrix TSV to VCF in Python — feasible because `buildTsv()` includes position information as column headers and the reference allele is available from `buildCsv()` (or from a reference FASTA lookup). At hackathon scale: tabular output first, VCF conversion as a stretch goal.
5. Produce a Galaxy `.tar.gz` tool tarball using Planemo.

**Production path (Option A + C):**

After the hackathon, add server-side changes:
- Remove the chr-required constraint for genome-wide queries (small change in `GenotypeWS.java`).
- Add `format=vcf` to the serialization switch in `GenotypeWS.java` — write a `buildVcf()` method using the existing `VariantTableWS` data structure.
- Add response streaming (chunked transfer encoding via Jersey `StreamingOutput`) for large genome-wide results.
- Submit the Galaxy tool to the ToolShed.

The BrAPI v1 allelematrix route could replace Option A at the data-pull layer once BrAPI v2 compliance is added, giving the tool a standards-defined interface — but that is a separate, larger project (see Risks).

---

## 4. Work Breakdown

| # | Task | UC(s) | Depends on | Hackathon-MVP estimate | Production-ready estimate | Confidence | Notes / Risk |
|---|---|---|---|---|---|---|---|
| 1 | Galaxy tool XML: parameters, help text, output dataset declaration | UC1, UC2 | — | 1 h | 2 h | High | XML spec is well-documented; Planemo validates |
| 2 | Python fetch script: call `/gettable?format=tsv`, handle HTTP errors, return tabular output | UC1 | Task 1 | 2 h | 2 h | High | `/gettable` is public; no auth; GENO_VAR_CAP=20,000 covers all accessions |
| 3 | UC2 genome-wide loop: iterate chromosomes 1–12, concat results, deduplicate header | UC2 | Task 2 | 1.5 h | 1.5 h | Medium | Chr count is hardcoded (12 for *O. sativa*); verify chromosome feature IDs for non-default organisms |
| 4 | TSV-to-VCF converter in Python: wide-matrix → per-variant rows, ALT allele inference | UC1, UC2 | Task 2 | 3 h | 2 h | Medium | Reference allele is in `buildCsv()` row 2, not in TSV — must call CSV or parse REF from a reference FASTA; ALT allele inference from biallelic IUPAC codes is non-trivial for indels |
| 5 | Planemo lint + functional test with test data | UC1, UC2 | Tasks 1–4 | 1 h | 2 h | High | Planemo test requires a running SNPseek instance or recorded cassette |
| 6 | **Server-side:** remove chr requirement from `/gettable` for genome-wide queries (`GenotypeWS.java`) | UC2 | — | out of scope | 1 h | High | Service layer already handles empty chr; change is ~5 lines in REST layer |
| 7 | **Server-side:** add `format=vcf` to `GenotypeWS.buildVcf()` | UC1, UC2 | Task 6 | out of scope | 4 h | High | VCF header + per-position row serialization from `VariantTableWS`; straightforward but verbose |
| 8 | **Server-side:** add streaming (Jersey `StreamingOutput`) to `/gettable` for large results | UC1, UC2 | Task 7 | out of scope | 8 h | Low | Requires restructuring the response pipeline — currently fully loads into memory before serialize; risk of breaking existing callers |
| 9 | Tool packaging and ToolShed submission | UC1, UC2 | Tasks 1–5, 7 | out of scope | 4 h | Medium | Requires ToolShed account, IUC review process (weeks not hours) |
| 10 | BrAPI v2 compliance upgrade (full protocol) | UC1, UC2 | — | out of scope | 20 h+ | Low | Separate epic; enables standards-based Galaxy BrAPI tools without custom code |

### Rolled-Up Totals

| Phase | Tasks included | Estimate | What you get |
|---|---|---|---|
| **Hackathon MVP** | 1, 2, 3, 4, 5 | **8–9 hours** | Galaxy tool (tabular + best-effort VCF) for both use cases; no server changes required; UC2 does 12 HTTP calls per job |
| **Production-ready** | All above + 6, 7, 8, 9 | **35–40 hours** (excluding BrAPI v2) | Single-call genome-wide support, native VCF format, streaming for large results, ToolShed-published tool |

---

## 5. Risks and Unknowns

| # | Risk / Unknown | Severity | Mitigation |
|---|---|---|---|
| R1 | **TSV output lacks REF allele** — `buildTsv()` puts variety rows and position column headers, but the reference allele is only in `buildCsv()` row 2. VCF requires REF. | High | For hackathon: call `/gettable?format=csv` instead of tsv (CSV has REF row); add parsing logic. For production: `buildVcf()` on server side. |
| R2 | **ALT allele inference for indels** — The genotype strings are IUPAC nucleotide codes. Biallelic SNPs are straightforward; indels (bIndel=true) use a different encoding. `GenotypeWS.java:snp=true, indel=false` by default. | Medium | Hackathon: restrict to SNPs only (default). Production: need indel encoding documentation (not found in scoped files). |
| R3 | **Per-chromosome UC2 latency** — 12 sequential HTTP calls for genome-wide; each call loads a full chromosome into Tomcat heap. On a 20k-accession genome-wide query this is a very large result set. | High | For hackathon (3k dataset, single variety): response per chromosome is manageable. For production (20k × genome-wide): streaming (Task 8) is mandatory. Flag as hard blocker for production scale. |
| R4 | **GENO_VAR_CAP = 20,000** — `sliceVarieties()` silently caps at 20,000 varieties. Production has 18,752, so this is not hit today. If accession count grows above 20,000, results will be silently truncated. | Low | Monitor; raise cap or add explicit error if accession count approaches limit. |
| R5 | **vsnp_refposindex is a regular VIEW** — not materialized. Under concurrent Galaxy job load, coordinate-range queries recompute the join each time. | Medium | No action needed for hackathon. Production: evaluate `MATERIALIZED VIEW` + incremental refresh strategy. |
| R6 | **Server access needed for Tasks 6–8** — The production config is committed (`config.properties: webserver=brs`); server-side changes require a Maven build and WAR redeploy on the BRS server. This is not self-service from a Galaxy tool PR. | High | Coordinate with BRS sysadmin before scheduling production-track work. |
| R7 | **SNPseek v4 migration risk** — `CLAUDE.md` mentions v4 and `dockerv3/` placeholder exists. A major version migration could make this Galaxy tool's REST endpoint URLs stale. | Medium | Verify v4 timeline; build the tool endpoint URL as a Galaxy tool input parameter (not hardcoded) so it can be redirected without a tool update. |
| R8 | **BrAPI v1 vs. v2** — README claims BrAPI v2.1; actual implementation is v1 (`BrAPI.java: @Path("/brapi/v1")`). Galaxy's BrAPI-aware tools (if any are used) will fail to negotiate v2.1 features. | Low (for Option A) | Option A bypasses BrAPI entirely; not a hackathon blocker. Document the v1 reality. |
| R9 | **Chromosome feature IDs** — The `chr` parameter in `/gettable` maps to chromosome names (e.g., "Chr01"); the mapping to `srcfeature_id` in Chado is via a lookup. If the Galaxy tool passes chromosome numbers as integers vs. the expected string format, queries return empty. | Medium | Test UC1 end-to-end before hackathon day to confirm accepted chr format; confirm with `GET /ws/genotype/variety` response for valid chromosome strings. |

---

## 6. Hackathon-Day Plan

**Assumptions:** One developer, 8–9 hours, access to a running SNPseek instance (localhost or BRS), Python 3.9+, Planemo installed.

### Priority Sequence

| Time | Task | Deliverable |
|---|---|---|
| 0:00–0:30 | Confirm endpoint manually with `curl` — verify UC1 request format, chr parameter format, TSV column layout, response size for test accession on Chr09:3M–3.5M | Working `curl` command for UC1 |
| 0:30–1:30 | Write `snpseek_fetch.py` — Python script takes accession IDs, chr, start, end, dataset as args; calls `/gettable?format=csv` (CSV for REF row); prints tabular output | Script UC1 functional |
| 1:30–2:30 | Write `snpseek_genotype.xml` — Galaxy tool XML wrapping the script; define inputs (accession IDs text area, chr select, start/end integer, dataset select); define output (tabular) | Galaxy tool loads in Planemo serve |
| 2:30–3:00 | Run `planemo lint`; fix any XML errors; run a first `planemo test` against the test instance | Tool passes lint |
| 3:00–4:30 | Extend script for UC2: add `--genome-wide` flag; loop chromosomes 1–12; concatenate TSV responses (strip header from rows 2–12); produce single tabular output | UC2 functional |
| 4:30–7:30 | TSV/CSV → VCF converter: parse header for positions, REF row for reference alleles, data rows for genotype calls; write VCF header (##fileformat, ##contig, #CHROM...) and records; handle IUPAC → GT field (e.g., A→0/0, T→1/1, W→0/1 for A/T SNP) | VCF output format functional for SNPs |
| 7:30–8:30 | End-to-end test both UCs; test tabular and VCF output; verify in a live Galaxy instance history | Demo-ready tool |
| 8:30–9:00 | Package with `planemo shed_init`; write tool description; prepare demo | `.tar.gz` ready |

### Fallback Priorities (if VCF proves harder than expected)

1. Ship the tabular (TSV) output tool for UC1 and UC2 — tabular is a first-class Galaxy dataset type and can be downstream-processed.
2. Defer VCF conversion to a post-hackathon Task 4 (2h standalone task once the tool structure is in place).
3. Skip UC2 genome-wide looping if time runs short — deliver UC1 (coordinate-range) fully and document UC2 as a follow-up that needs Task 6 (server-side chr removal).

### What Not to Attempt on Hackathon Day

- BrAPI route (more auth/protocol friction than it saves)
- Server-side Java changes (build + deploy cycle kills hackathon time)
- ToolShed submission (asynchronous review process)
- Indel support (encoding unclear from scoped files)
