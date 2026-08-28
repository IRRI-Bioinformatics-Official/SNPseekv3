# SNPseek v3 — Engineering Onboarding Overview

> Generated: 2026-08-19. All claims are grounded in files found in this repository;
> nothing has been invented or inferred from general bioinformatics knowledge alone.

---

## 1. Repo Orientation

### Directory Structure

```
SNPseekv3_JUL2025/
├── src/main/java/           Java source — all backend logic
│   ├── ncsa/hdf/hdf5lib/    Vendored NCSA HDF5 JNI bindings (H5.java)
│   ├── org/biojava3/        BioJava3 phylogenetic I/O (vendored)
│   ├── org/forester/        Forester phylogenetics library (vendored)
│   ├── org/irri/iric/portal/ Core application packages (see Architecture)
│   ├── com/github/jmchilton/ blend4j Galaxy REST client (vendored)
│   └── user/ui/module/      Workspace utility classes
├── src/main/resources/      Spring XML contexts, 40+ .properties files
├── WebContent/              Web root packaged into the WAR
│   ├── *.zul                ZK ZUML page definitions (UI)
│   ├── WEB-INF/             web.xml, lib/, (previously hdf5_lib/)
│   ├── assets/              Bootstrap CSS, fonts, shared JS
│   ├── api-docs/            Static Swagger UI for custom REST API
│   ├── brapi-docs/          Static Swagger UI for BrAPI v1
│   ├── rice-ideogram-v2-prod/ Chromosome ideogram JavaScript widget
│   ├── haplo/               Haplotype viewer assets
│   ├── explore/             Explore/browse sub-section assets
│   ├── galaxy/              Galaxy integration assets
│   └── legacy/              Old API docs (v2-era)
├── docker/                  Legacy Docker setup (Tomcat 8.5 + JRE 8, old WAR name hardcoded)
├── dockerv2/                Current Docker setup (Tomcat 9 + JDK 17, env-file driven)
│   ├── tomcat/              Dockerfile + pre-built WAR + HDF5 .so/.jar
│   ├── postgresDB/          PostgreSQL Dockerfile + schema SQL
│   ├── R/                   Minimal R-base container for Rscript calls
│   └── IRCStorage/          Flat-files, GWAS emmax .dat files, sample HDF5
├── dockerv3/                Empty — placeholder only
├── docs/                    One call-flow diagram (genotype_diagram.svg/md)
├── .github/java-upgrade/    Exploratory Java upgrade logs — NOT CI pipeline configs
└── pom.xml                  Maven build descriptor (artifact: SNP-seekV3_clean, v3.18.7)
```

`bin/` and `build/` (Eclipse-generated `.class` output) are committed to the
repository and are noise; they should be gitignored.

### Languages, Frameworks, and Major Dependencies

**Language:** Java 15 (Maven compiler plugin `<release>15</release>`). The
`dockerv2` Tomcat image uses `tomcat:9.0-jdk17`, creating a mismatch between
compiler target and runtime JDK that has not caused reported failures but is a
latent risk.

**Framework stack:**

| Layer | Library | Version |
|---|---|---|
| UI framework | ZK Framework | 8.5 (CE, from ZKoss Maven repo) |
| REST / JAX-RS | Jersey (GlassFish) | 2.29.1 |
| IoC / DI | Spring Framework | 5.3.18 |
| Spring Security (main) | spring-security-oauth2-client, spring-security-config | 5.3.13.RELEASE |
| Spring Security (ACL) | spring-security-acl, spring-security-taglibs | 3.0.5.RELEASE ⚠ |
| ORM | Spring Data JPA 2.6.4 → Hibernate 5.6 | implicit |
| JSON | Jackson | 2.13.5 |
| Big-data genotype store | NCSA HDF5 JNI (vendored `ncsa.hdf.hdf5lib`) | pre-1.8 API |
| AWS integration | AWS SDK v1 (EC2, S3, EMR) | 1.12.757 |
| Microsoft OAuth2 | MSAL4J | 1.11.3 |
| Google OAuth2 / Analytics | google-api-services-oauth2, google-analytics-data | v2-rev157 / 0.25.0 |
| gRPC (Google deps) | grpc-netty-shaded et al. | 1.42.0 |
| Galaxy workflow | blend4j (vendored in-source) | — |
| GWAS / data export | Apache POI | 5.0.0 |
| Phylogenetics | BioJava3 + Forester (vendored in-source) | — |
| Password hashing | jBCrypt | 0.4 |
| CLI support | picocli | 4.7.5 |
| Env loading | dotenv-java | 3.0.0 |

**Repo topology:** Single-repository (not a monorepo). The frontend (ZK/ZUL) and
backend (Java/Spring/Jersey) are co-deployed in the same WAR. There are no
microservices or API gateways. The only external code dependency requiring a
separate build step is the `SNPseek-Datasource` library (see §6).

---

## 2. Project Purpose & Domain

SNPseek v3 is IRRI's (International Rice Research Institute) web platform for
browsing, querying, downloading, and visualizing SNP genotype data across rice
(*Oryza sativa*) accessions. It is the successor to older SNP-Seek v2 deployments
and serves both end-user researchers and programmatic API clients (BrAPI
consumers, bioinformatics pipelines).

The application is multi-tenant by configuration: a single WAR binary is deployed
under different `config.properties` files to produce different-branded instances.
As of the current checked-in `src/main/resources/config.properties`, the active
deployment target is `webserver=brs`, which corresponds to the **BRS (CGIAR
Breeding Resources Services)** mirror at `https://brs-snpseek.duckdns.org/19kRG`
serving approximately **18,752 rice accessions** from the 19K Rice Genomes (19K
RG) project.

### Genomic Data Served

**Species:** *Oryza sativa* (rice). Default organism ID = 9 (Japonica Nipponbare).

**Reference genome:** IRGSP-1.0 (Nipponbare).

**HDF5 variant datasets** — documented in `README.md`:

| Dataset key | Description | Size |
|---|---|---|
| `3kall` | Full 3K RG SNP set (biallelic + multiallelic) | 32,064,217 SNPs, 3,024 samples |
| `3kbase` | 3K RG base SNP set (no excess heterozygotes) | ~18M SNPs |
| `3kfiltered` | Default 3K dataset (AAF ≥ 0.01, missing ≤ 0.2) | 4.8M SNPs |
| `3kcore` | CoreSNP set after LD pruning | ~404K SNPs |
| `hdra` | High-Density Rice Array (1,568 diverse lines) | 700K SNPs |
| `rice_rp` | 3K + HDRA combined with imputation | 4,591 samples |
| `baap` | Bengal and Assam Aus Panel (299 cultivars) | 2M SNPs |
| `20k` | 19K RG (~18,752 accessions, used in BRS instance) | TBD |

The `20k` / `defaultDataset=20k` entries in `brs.properties` are the newest
dataset and are not yet documented in `README.md`.

**Phenotype and passport data:** Sourced from IRGCIS (International Rice
Genebank Collection Information System), exposed via variety/passport queries and
BrAPI germplasm endpoints.

No CHANGELOG, dedicated release-notes file, or semver tags exist. Version
tracking is through commit messages (`v 3.18.x`).

---

## 3. Architecture

### Backend

The backend is a Spring 5 + JAX-RS/Jersey monolith packaged as a WAR. All
business logic is in `src/main/java/org/irri/iric/portal/`. The central
singleton `AppContext.java` (~3,107 lines) initializes all configuration,
feature flags, and environment variables at startup; it is the first file to read
when understanding any runtime behavior.

**Core business packages:**

| Package | Purpose |
|---|---|
| `portal/genotype/` | SNP/indel query logic, HDF5 access coordination, phylogenetics |
| `portal/variety/` | Rice accession / germplasm queries and passport data |
| `portal/genomics/` | Gene locus queries, GO-term search, BLAST invocation |
| `portal/gwas/` | GWAS Manhattan plot display (flat-file backed) |
| `portal/galaxy/` | Galaxy workflow submission and job tracking |
| `portal/auth/` | OAuth2 (Google + Microsoft Azure AD) and local login |
| `portal/email/` | SMTP email composition and file attachment support |
| `portal/google/` | Google Analytics Data API integration |
| `portal/admin/` | Health servlet (`/health`), async job facade |
| `portal/hdf5/` | Low-level HDF5 dataset read wrappers |

**REST API** (Jersey, mounted at `/ws/*` per `web.xml`):

| Class | Path | Purpose |
|---|---|---|
| `GenotypeWS` | `/ws/genotype` | SNP/indel table fetch (`/gettable`, `/posttable`), variety and gene list lookups |
| `BrAPI` | `/ws/brapi/v1` | BrAPI v1 endpoints (germplasm, allele matrix, maps) |
| `BlastWS` | `/ws/blast` | BLAST job submission and result polling |
| `GenomicsWS` | `/ws/genomics` | Gene/locus information |
| `VarietyWS` | `/ws/variety` | Variety/accession metadata queries |

**Important discrepancy:** The `README.md` and feature descriptions state "BrAPI
v2.1 support." The actual implementation class `BrAPI.java` is annotated
`@Path("/brapi/v1")` — it implements **BrAPI v1**, not v2.1. There is no `BrAPIv2`
class in the codebase.

### Frontend

The UI is built with **ZK Framework 8.5** (ZUML pages with `*.zul` extension).
The ZK `DHtmlLayoutServlet` renders all `.zul` files server-side with AJAX updates
over `/zkau`. There is no separate SPA or frontend build tool.

**Key ZUL pages** (all in `WebContent/`):

| Page | Purpose |
|---|---|
| `index.zul` / `home.zul` | Landing page with stat cards |
| `genotype.zul` / `genotypeContent.zul` | SNP genotype search and matrix viewer |
| `varieties.zul` / `varietiesContent.zul` | Variety/germplasm search |
| `geneLoci.zul` / `geneLociContent.zul` | Gene/locus search with BLAST |
| `gwas.zul` | GWAS result browser |
| `galaxy.zul` | Galaxy workflow submission |
| `jbrowse.zul` / `jbrowse2.zul` | Embedded JBrowse 1 / JBrowse 2 genome browser iframes |
| `download.zul` | Dataset download interface |
| `sendEmail.zul` / `_sendEmail.zul` | Contact / email form with file attachments |
| `login.zul`, `register.zul` | Authentication pages |
| `traitgenes.zul` | Trait-associated gene browser |

The rice chromosome ideogram widget (`WebContent/rice-ideogram-v2-prod/`) is a
standalone JavaScript component embedded in relevant pages.

### Data Layer

Three parallel data access strategies coexist:

1. **JPA/Hibernate over PostgreSQL (Chado schema):** Domain objects and DAOs come
   from the external `snpseek-DS` library (`org.irri:snpseek-DS:3.10.1`, built
   from the sibling `SNPseek-Datasource_JUL2025` repository). Key Chado tables
   include `stock` (rice accessions), `feature` (genes/SNP features),
   `genotype`/`genotype_call` (variant call records), `phenotype`, `cvterm`
   (ontology terms). Schema DDL is in `docker/postgresDB/iric_schemaOnly.sql`.

2. **HDF5 DAOs** (`org.irri.iric.portal.hdf5.dao`): Read columnar genotype
   matrices from `.h5` files. The access path is:
   `GenotypeWS → GenotypeFacadeChadoImpl → VarietiesGenotypeSNPsIndelServiceImpl →
   SnpsStringHDF5nRDBMSHybridService / SnpsStringMultiHDF5nRDBMSHybridService →
   H5ReadCharmatrix / H5ReadStringmatrix`. A call-flow diagram is in
   `docs/genotype_diagram.svg`. The HDF5 JNI bindings (`ncsa.hdf.hdf5lib.H5`)
   are vendored in-source and use **pre-HDF5-1.8 deprecated APIs**.

3. **Flat-file DAOs** (`org.irri.iric.portal.gwas.dao.ManhattanPlotDAOFlatfileImpl`,
   `variety.service.VarietyPropertiesServiceImplURLsFlatfiles`): Read
   pre-generated tab-delimited files from `flatFileDir` (e.g., EMMAX GWAS output
   `.dat` files, variety property exports). The `dockerv2/IRCStorage/` directory
   contains sample flat files.

### Pipelines / Workflows

There are no Nextflow, Snakemake, or WDL pipelines in this repository. Genomic
analysis is triggered on-demand via user requests:

- **R + PLINK** subprocesses: Invoked by `PhylotreeServiceImpl` (neighbor-joining
  trees), `HaplotypeImageRHeatmapServiceImpl` (R heatmaps), and GWAS analysis.
  Managed via `SystemCommandExecutor` and `ThreadedStreamHandler` (which have
  known hang-risk TODO comments if incorrect passwords are supplied).
- **BLAST**: Invoked as a local subprocess via `LocalAlignmentBLASTServiceImpl`.
  BLAST+ binaries must be installed separately.
- **Galaxy workflows**: Submitted asynchronously via blend4j; job status polled
  through `JobsFacade` / `JobsFacadeGalaxyImpl`.
- **Long-running jobs**: Managed by the internal `AsyncJob` framework; results
  pushed to the UI via ZK server-push.

No cron jobs, batch scripts, or scheduled tasks are present in the repository.

### External Integrations

| Service | Integration point | Notes |
|---|---|---|
| JBrowse 1 | `jbrowseDir` property → iframe URL | External instance at `brs-snpseek.duckdns.org/jbrowse` |
| JBrowse 2 | `jbrowse2Dir` property → iframe URL | Same host |
| Galaxy | blend4j REST client | URL, API key from `keys.properties` |
| Google OAuth2 | `auth/LoginServlet`, `auth/OAuth2CallbackServlet` | Client ID/secret via env vars |
| Microsoft Azure AD | `auth/MicrosoftLoginServlet` | MSAL4J; client ID/tenant/secret via env vars |
| Google reCAPTCHA Enterprise | `google-cloud-recaptchaenterprise` | Secret key via env var |
| Google Analytics | `google-analytics-data` API | GA4 property ID; service account JSON via `ga-credentials.json` |
| SMTP email | `javax.mail`, port 587 | SMTP password in `keys.properties` |
| AWS EC2/S3/EMR | `aws-java-sdk` v1 | Used for instance metadata; S3 for job artifacts |
| External chatbot | HTTP client to configurable URL | Disabled by default (`chatbot.enabled=false`); no chatbot logic in this codebase — purely a proxy/redirect |

---

## 4. Current State of the Code

### Finished / Stable

The core genotype search pipeline (HDF5 query → variant table → JSON response via
`GenotypeWS`) is the most mature and best-documented path in the codebase (see
`docs/genotype_diagram.svg`). Variety search, BLAST, phylogenetic tree generation,
and the ZK UI framework wiring are all stable and have been in production use
across multiple deployment profiles.

User authentication (local + Google OAuth2 + Microsoft Azure AD) and the health
servlet (`/health`) are complete. Email sending with file attachments was added in
v3.18.1 (commit `42ddafd`). The send-email ZUL page and `SendEmailController` +
`MailUtils` appear complete.

### In Progress / Uncertain

**Chatbot integration (v3.17.3):** Configuration constants exist
(`ApplicationConstants.CHATBOT_ENABLED`, `CHATBOT_SERVER_URL`), and the feature
flag appears in `brs.properties` and `localhost.properties`. However, no
controller, ZUL page, or HTTP client code for the chatbot is visible in the Java
source. It is either a pure UI-side redirect to an external URL or the
implementation was not committed.

**BrAPI v2.1:** Advertised in `README.md` but not implemented. `BrAPI.java` is
v1-only. Whether v2 is genuinely planned or the README is aspirational is not
discernible from the repo.

**Second reference genome support:** Multiple TODO comments in
`VariantAlignmentTableArraysImpl.java` (`// TODO: 2nd reference Genome`,
`// TODO: NULL MULTIREFERENCE`) and `Object2StringMultirefsMatrixModel.java`
indicate multi-reference display is incomplete.

**19K / 20K dataset configuration:** `brs.properties` uses `defaultDataset=20k`
but the 20k dataset is absent from the README HDF5 manifest and from the GWAS
flat-file structure. The integration may be partial.

**AWS deprecated package:** `org.irri.iric.portal.aws_deprecated` contains
`AWSInstanceCountdown` and `AWSTimer` — explicitly named as deprecated, no callers
expected.

### Tests

There are **zero test files** in `src/test/`. `mvn test` will produce no test
results. There is no test coverage of any kind.

### Build / Deploy

**Build:** `mvn clean package -DskipTests` (required, since there are no tests but
the flag avoids future failures). The Maven compiler plugin targets Java 15. The
SNPseek-Datasource sibling library must be installed into the local Maven
repository first (`mvn clean install -DskipTests` in the `SNPseek-Datasource_JUL2025`
directory).

**CI/CD:** There are **no GitHub Actions workflow files** (`.github/workflows/`
does not exist). The `.github/java-upgrade/` directory contains run logs from a
previous Java-upgrade exploration tool, not a CI pipeline. There is no automated
build, test, or deployment pipeline in the repository.

**Docker:**
- `docker/` (legacy): Uses `tomcat:8.5-jre8-alpine` and hardcodes the WAR
  filename `SNP-seekV3_clean-8.13.3.war`. This will fail with any other version
  and should be considered broken for current builds.
- `dockerv2/` (current): Uses `tomcat:9.0-jdk17`, copies `snpseekv3.war`
  (which must be placed there manually), and uses an `.env` file for secrets.
  Adds a separate `r_seek` container. HDF5 native libraries (`.so` files) are
  bundled in `dockerv2/tomcat/hdf5/`. A pre-built WAR binary
  (`dockerv2/tomcat/snpseekv3.war`) is committed to the repository — this binary
  should not be version-controlled.

### Tech Debt and Known Issues

- **Spring Security version split:** `spring-security-acl` and
  `spring-security-taglibs` are pinned at `3.0.5.RELEASE` while the rest of
  Spring Security is at `5.3.13.RELEASE` — a two-major-version gap that is
  managed through exclusions but is a fragile arrangement.
- **HDF5 JNI API age:** The vendored `H5.java` uses pre-HDF5-1.8 APIs that are
  deprecated in the current HDF5 Java library. Updating HDF5 binaries will
  require API migration.
- **Committed Eclipse output:** `bin/` and `build/` directories (compiled `.class`
  files from Eclipse) are checked into git, inflating the repo and polluting diffs.
- **Committed binary WAR:** `dockerv2/tomcat/snpseekv3.war` is a binary in git.
- **`SystemCommandExecutor` hang risk:** Multiple `TODO` comments acknowledge that
  the stdin password handling hangs if credentials are wrong. External process
  invocation has no timeout wrapper.
- **`TabixReader.parseReg` FIXME:** `FIXME: NOT working when the sequence name
  contains : or -.` — known broken case for sequence names with those characters.
- **Production config committed:** `src/main/resources/config.properties` has
  `webserver=brs` (production setting). A fresh clone targeting local development
  requires manually editing this file before building.
- **`docker/` Dockerfile frozen at v8.13.3:** The legacy dockerfile is entirely
  broken for current version builds.

---

## 5. Recent Activity

### Commit Themes (last ~50 commits)

The repository has two distinct versioning epochs. The older commits (prefixed
`version 8.x.x`) originate from a Bitbucket mirror (`dagsbarboza/snp-seekv3`)
and represent what was the application's historical numbering scheme. At some
point the version scheme was reset to `v 3.x.x` on GitHub, indicating a fresh
start or major restructuring.

Recent `v 3.x` commits cluster into four themes:

1. **Infrastructure and deployment (v3.8–v3.9):** Adding health check servlet,
   HDF5 environment variable support, Docker deployment automation, README
   documentation.
2. **UI and feature additions (v3.12–v3.16):** Multiple patch releases with no
   detailed commit messages; the merge from Bitbucket remote suggests syncing
   across forks.
3. **New end-user features (v3.17–v3.18):** Chatbot configuration (v3.17.3),
   email sending (v3.18.0), email attachments (v3.18.1).
4. **Documentation and ops (recent):** Google Analytics initialization fix, Docker
   deployment automation, README HDF5 manifest.

### Contributors

All commits in this repository are authored by a single developer:
**Lord Hendrix Barboza** (appearing as `Barboza`, `LHBarboza`, `lhbarboza-cgiar`,
`dagsbarboza`). No other contributors are present in the `git log`.

### Version and Changelog

Current version: `3.18.7` (from `pom.xml`); `3.18.9` (from
`src/main/resources/config.properties`). No formal CHANGELOG file exists. No git
tags are present. Version history is tracked only through commit message prefixes
(`v 3.18.1`, `v 3.17.0`, etc.).

---

## 6. How to Run It

### Prerequisites

- Java 15 JDK (matching `pom.xml` compiler target)
- Maven 3.6+
- A PostgreSQL instance with the Chado schema loaded (DDL in
  `docker/postgresDB/iric_schemaOnly.sql`)
- R and Rscript installed (for phylogenetics / haplotype features)
- BLAST+ binaries installed (for BLAST search)
- HDF5 data files (`.h5`) placed in the configured `flatFileDir`

### Step 1 — Build the sibling datasource library

```bash
cd ../SNPseek-Datasource_JUL2025
mvn clean install -DskipTests
```

This installs `org.irri:snpseek-DS` into the local Maven repository. Without
this step, the SNPseek WAR build will fail to resolve the dependency.

### Step 2 — Configure for local development

Edit `src/main/resources/config.properties`:
```properties
webserver=localhost
compiletype=dev
os=windows   # or linux
dockerize=false
```

Copy `localhost.properties` and adjust:
- `tomcatserver` — path to your Tomcat webapps directory
- `flatFileDir` — directory containing HDF5 and flat-file data
- `pathToR` — path to `Rscript` executable
- `pathToLocalBlast` — path to BLAST+ `bin/` directory

Create `src/main/resources/keys.properties` from the template
(`keys.properties.template`), filling in SMTP credentials.

### Step 3 — Set environment variables

Required at Tomcat startup (via `setenv.sh` / `setenv.bat`):

```
DB_URLS=jdbc:postgresql://localhost:5432/iric
DB_USERS=<db-user>
DB_PASSWORDS=<db-password>
HOSTNAME=http://localhost:5555
RECAPTCHA_SECRET_KEY=<key>
MICROSOFT_CLIENT_ID=<id>
MICROSOFT_TENANT_ID=<tenant>
MICROSOFT_SECRET=<secret>
GOOGLE_OAUTH_CLIENT_ID=<id>
GOOGLE_OAUTH_CLIENT_SECRET=<secret>
GOOGLE_ANALYTICS_PROPERTY_ID=<id>
```

### Step 4 — Build

```bash
mvn clean package -DskipTests
```

Output: `target/v3.war`

### Step 5 — Deploy (Docker, recommended)

```bash
cp target/v3.war dockerv2/tomcat/snpseekv3.war
cd dockerv2
cp .env.example .env   # fill in credentials
docker-compose up --build -d
```

Application available at `http://localhost:8080/v3/`.

### Step 6 — Deploy (manual Tomcat)

Copy `target/v3.war` to `TOMCAT_HOME/webapps/v3.war` and restart Tomcat.

### Known Setup Issues

- `docker/` (the non-`v2` directory) will **fail to build** because its
  Dockerfile hardcodes `SNP-seekV3_clean-8.13.3.war`; use `dockerv2/` instead.
- `src/main/resources/config.properties` must be manually changed from `webserver=brs`
  to `webserver=localhost` before local development builds.
- There is no seed data script for a minimal working database; the only SQL
  present is the schema DDL. A database restore from a dump is required to get
  any data.
- The `ga-credentials.json` Google Analytics service account file is gitignored
  and must be obtained separately; without it, the Analytics service will throw
  at startup (impact depends on whether `google.enabled=true`).

---

## 7. Open Questions / Gaps

- **What is the `20k` / `20kRG` dataset?** `brs.properties` sets
  `defaultDataset=20k` and `defaultVariantset=20k` and describes 18,752
  accessions, but no HDF5 file manifest, schema description, or processing
  pipeline for this dataset is present in the repository. Its provenance and
  status are unknown from the repo alone.

- **Where are the production HDF5 files stored?** `flatFileDir` in deployment
  configs points to local filesystem paths. Whether a shared network mount,
  S3, or another mechanism is used in production cannot be determined from this
  repo.

- **What is the chatbot backend?** `brs.properties` references
  `chatbot.api.url=https://brs-snpseek.duckdns.org/api/chat`. No chatbot server
  code is present here. Is this a separate microservice? A third-party LLM
  integration?

- **Why are `bin/` and `build/` (Eclipse class output) committed?** These inflate
  the repo and should be in `.gitignore`. Was this intentional (e.g., for
  deployment without Maven)?

- **BrAPI v2.1 claim:** The README and feature lists say v2.1, but only v1 is
  implemented. Is v2.1 planned for an active branch or is this aspirational
  documentation?

- **Spring Security 3.0.5 ACL:** `spring-security-acl` and
  `spring-security-taglibs` are at `3.0.5.RELEASE` while the rest of Spring
  Security is at `5.3.13.RELEASE`. Is ACL actually used? A quick grep shows
  these JARs in the dependency list but it is unclear if any ACL annotations or
  configurations are active. This may be vestigial.

- **Database dumps and seed data:** There is no script to load a minimal working
  dataset. Where does a new developer get a database to work against?

- **Data licensing:** Are the 3K RG, HDRA, BAAP, and 19K RG datasets under
  licenses that restrict redistribution? This is a data governance question not
  answerable from code.

- **Production infrastructure:** The production deployment at IRRI and the
  BRS/CGIAR mirror are not described in the repo. Server specs, backup strategy,
  storage layout, and how the Docker images are actually deployed are unknown.

- **SNPseek v4:** Several `CLAUDE.md` references mention a v4 migration. Is v4
  a separate repository in active development? What is the timeline for v3's
  end-of-life?

- **AWS SDK v1 usage:** The AWS SDK v1 is present (EC2, S3, EMR). `aws_deprecated`
  package is explicitly named deprecated. What AWS services are actively used in
  production, and is migration to SDK v2 planned?

---

## Suggested Next Steps

**1. Add a zero-test baseline and enforce it in CI (highest priority).**
There are no tests at all. Before any refactor or dependency upgrade, establish
at least integration tests for the core genotype query path
(`GenotypeWS → GenotypeFacadeChadoImpl → SnpsStringHDF5nRDBMSHybridService`).
Even a smoke test that starts the Spring context and queries a real or in-memory
stub database catches startup-time regressions. Then wire a GitHub Actions workflow
(`.github/workflows/ci.yml`) to run `mvn clean package` on each push to confirm
the build is always green.

**2. Fix the BrAPI v1 / v2.1 documentation gap.**
Either implement the BrAPI v2.1 endpoints that are advertised (germplasm, allele
matrix, variant sets per the BrAPI v2 spec) or correct the README and Swagger
docs to accurately state v1. The current mismatch misleads API consumers expecting
standards-compliant v2 responses.

**3. Resolve the Spring Security version split.**
`spring-security-acl` and `spring-security-taglibs` at `3.0.5.RELEASE` are two
major versions behind the rest of the Spring Security stack at `5.3.13.RELEASE`.
Determine whether ACL features are actually used, remove those dependencies if
they are vestigial, or upgrade them to match 5.3.x. The current exclusion-heavy
workaround is fragile.

**4. Fix `docker/` to be build-reproducible, or remove it.**
The legacy `docker/tomcat/Dockerfile` hardcodes WAR version `8.13.3` and uses a
deprecated base image (`tomcat:8.5-jre8-alpine`). Either update it to use the
WAR name produced by `pom.xml` (i.e., parameterize via `ARG`) or delete it and
standardize on `dockerv2/` only. Also, remove the committed `dockerv2/tomcat/snpseekv3.war`
binary from version control and add it to `.gitignore`.

**5. Gitignore `bin/` and `build/` and remove them from history.**
The Eclipse-generated output directories add noise to every diff. Add them to
`.gitignore` and remove the committed class files (via `git rm -r --cached bin/
build/`).

**6. Document and automate the 19K/20K dataset onboarding.**
The `20k` dataset used in the BRS production instance has no manifest, schema
notes, or ingestion documentation in the repository. A new engineer cannot
reproduce the BRS configuration from this repo alone. Write a short `docs/datasets.md`
documenting the HDF5 file names, their origin, and the Chado import procedure for
each dataset.
