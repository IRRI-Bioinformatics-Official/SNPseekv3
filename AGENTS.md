# AGENTS.md

## Build

```bash
mvn clean package -DskipTests   # produces target/v3.war
```

There are no tests. `mvn test` will fail because the external `snpseek-DS` library classes (Chado domain objects) must be installed locally first:

```bash
cd ../SNPseek-Datasource_JUL2025 && mvn clean install -DskipTests
```

Compilation errors in `GenomicsFacadeImpl.java` are pre-existing (missing classes from the above library) and unrelated to your changes.

## Verify changes

After editing Java, run `mvn compile` and confirm no **new** errors beyond `GenomicsFacadeImpl.java`.

## ZK @Wire gotcha

ZK's `@Wire` matches by **component type + ID**. The ZUL element must match the Java field type exactly:

| ZUL tag | Java type |
|---------|-----------|
| `<label>` | `Label` |
| `<span>` | `Span` |
| `<html>` | `Html` |
| `<image>` | `Image` |
| `<a>` | `A` |
| `<div>` | `Div` |

A mismatch silently leaves the field `null`, causing NPE at runtime.

## Config system

All deployment settings flow through `AppContext.java` (~3000 lines). It reads:
1. `config.properties` → determines `webserver` environment name
2. Environment-specific file (e.g., `brs.properties`, `science_cloud.properties`)
3. Environment variables for secrets

When adding a new property:
1. Add the key constant to `ApplicationConstants.java`
2. Add a getter in `AppContext.java`
3. Add the value to **all** relevant `.properties` files (there are 13+)

## Properties files to update when adding a deployment-wide property

- `localhost.properties` — local dev
- `brs.properties` — 19K RG BRS mirror
- `science_cloud.properties` — 19K RG Science Cloud
- `science_cloud_3kRG.properties` — 3K RG Science Cloud
- `science_cloud_1k1.properties` — 1K1 Science Cloud
- `localhost-SC1.properties` / `localhost-SC2.properties` — Science Cloud dev variants
- `localhost-SC2-1k1.properties` — 1K1 dev variant
- `beanstalk.properties` — AWS Elastic Beanstalk
- Plus any `*-SC1*` / `*-SC2*` variants

## File change workflow

ZK controllers live in `src/main/java/user/ui/module/`. The landing page chain is:

```
index.zul → template.zul → main.zul → home.zul
```

Controller: `HomeQueryController.java`, wired via `apply="user.ui.module.HomeQueryController"` on the root `<div>` in `home.zul`.

REST endpoints: `src/main/java/org/irri/iric/portal/ws/rest/` (Jersey, mapped in `web.xml`).

## Secrets

Never commit: `keys.properties`, `ga-credentials.json`, `docker/.env`, `setenv.sh`/`setenv.bat`, `config.properties`. All are gitignored.

## Pre-existing build issues

- `GenomicsFacadeImpl.java` has unresolved symbols (`Locus`, `TextSearchOptions`, etc.) from the external `snpseek-DS` library. These are **not** bugs you introduced.
- `pom.xml` has extensive `<exclusion>` blocks for Jersey/Hibernate/AWS SDK/Google library conflicts. Changing dependencies requires careful conflict checking.
