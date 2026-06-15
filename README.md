# 🌾 SNPseek

**SNPseek** is a genomic data platform developed to support the exploration, visualization, and analysis of high-density Single Nucleotide Polymorphisms (SNPs) across rice accessions. It provides researchers with the tools to query genotypic data, download filtered datasets, and integrate results into downstream pipelines via APIs.

---

## 📖 Table of Contents

- [Features](#-features)
- [Project Structure](#-project-structure)
- [Technologies Used](#-technologies-used)
- [Environment Variables](#-environment-variables)
- [Configuration & Property Files](#-configuration--property-files)
- [Database Setup](#-database-setup)
- [HDF5 Manifest](#-hdf5-manifest)
- [Required Project Dependency](#-required-project-dependency)
- [How to Deploy](#-how-to-deploy)
- [API Documentation](#-api-documentation)

---

## 🔍 Features

- 🔎 **SNP Search** by gene, chromosome position, or accession.
- 🧬 **Genotype Visualization** for multiple samples using an integrated viewer.
- 📦 **Downloadable SNP Datasets** in tabular format with filtering options.
- 🧠 **BrAPI v2.1** Support for standardized API access and integration with bioinformatics tools.
- 📊 **R-based SNP Analysis** with PLINK support for advanced genomic studies.
- 🐳 **Docker-based Deployment** for easy setup and scalability.
- 🏥 **Health Monitoring** via dedicated health check endpoints.

---

## 📁 Project Structure

- `src/main/java`: Backend source code (Spring, Jersey, ZK logic).
  - `org.irri.iric.portal.ws.rest`: Jersey-based REST and BrAPI endpoints.
  - `org.irri.iric.portal.*.zkui`: UI logic for the ZK Framework.
  - `org.irri.iric.portal.config`: Programmatic configuration and property management.
- `src/main/resources`: Configuration files, Spring XML contexts, and property templates.
- `WebContent`: Web resources, including ZK ZUML (`.zul`) files, HTML, and assets.
- `docker`: Containerization assets, including specialized Dockerfiles for PostgreSQL and Tomcat.
- `docs`: Technical documentation and diagrams.

---

## 🛠️ Technologies Used

- **Java 15** – Backend services and application logic.
- **Spring Framework 5.3.38** – Dependency injection, security, and data management.
- **ZK Framework** – Rich web interface using ZUML and AJAX.
- **Jersey** – Implementation of RESTful web services and BrAPI.
- **PostgreSQL + Chado schema** – Standardized genomic and metadata storage.
- **R + PLINK** – Backend SNP analysis scripts.
- **Docker** – Deployment and containerization.

---

## ⚙️ Environment Variables

Before running the project, ensure the following environment variables are configured. These variables are required for authentication, third-party integration, and database connectivity.

### Required Variables

#### Database Configuration
```yaml
DB_URLS=jdbc:postgresql://<db-host>:5432/<db-name>
DB_USERS=<database-username>
DB_PASSWORDS=<database-password>
```

#### Application Configuration
```yaml
HOSTNAME=<your-server-hostname>              # e.g., http://localhost:8080 or https://yourdomain.com
RECAPTCHA_SECRET_KEY=<your-recaptcha-secret>
MICROSOFT_CLIENT_ID=<your-azure-client-id>
MICROSOFT_TENANT_ID=<your-azure-tenant-id>
MICROSOFT_SECRET=<your-azure-secret>
GOOGLE_OAUTH_CLIENT_ID=<your-google-client-id>
GOOGLE_OAUTH_CLIENT_SECRET=<your-google-client-secret>
GOOGLE_ANALYTICS_PROPERTY_ID=<your-google-propertyid>
```

---

## 🛠️ Configuration & Property Files

The application uses a hierarchical property loading mechanism managed by `AppContext.java` to handle environment-specific settings.

### **1. Master Configuration: `config.properties`**
Located in `src/main/resources/config.properties`, this is the first file loaded. It defines the environment type and determines which secondary property file to load.

**Key settings:**
- `webserver`: Deployment target (e.g., `localhost`, `beanstalk`, `brs`).
- `compiletype`: Environment type (`prod`, `dev`, `test`).
- `os`: Operating system (`linux`, `windows`).
- `dockerize`: Boolean flag for Docker environments (`true`/`false`).

### **2. Environment-Specific Properties**
`AppContext` dynamically loads a secondary property file based on the `webserver` value. These files contain paths for external tools, server-specific URLs, and UI feature toggles.

- **Example**: If `webserver=brs`, it loads `src/main/resources/brs.properties`.
- **Common Settings in these files**:
    - `hostname`: Base URL of the application.
    - `jbrowseDir` / `jbrowse2Dir`: URLs for JBrowse instances.
    - `pathToLocalBlast`: Path to the BLAST+ binaries.
    - `pathToR`: Path to the `Rscript` executable.
    - `flatFileDir`: Directory for storing temporary and exported flat files.

---

## 🗄️ Database Setup

SNPseek uses a **PostgreSQL** database with a **Chado** schema. 

- **Schema Scripts**: Initial schema definitions can be found in `docker/postgresDB/iric_schemaOnly.sql`.
- **Dockerized DB**: The `docker/postgresDB/Dockerfile` can be used to build a database container pre-configured for SNPseek.
- **Connection**: Ensure `DB_URLS`, `DB_USERS`, and `DB_PASSWORDS` match your database instance.

---

## 🗂️ HDF5 Manifest

SNPseek utilizes the HDF5 format to store and efficiently query large-scale genomic datasets. Below is a manifest of the primary HDF5 datasets (Variant Sets) available in the system:

| Dataset | Description | Details |
| :--- | :--- | :--- |
| **3kall** | 32 million full 3K RG SNPs Dataset | 3kRG full set (32mio) biallelic & multiallelic SNP. Total SNPs: 32,064,217. Samples: 3024. |
| **3kbase** | 18 million base SNP dataset | A Base SNP set of ~18 million SNPs was created from the ~29 million biallelic SNPs subset from the 32M full SNP set by removing SNPs with excess of heterozygous calls. |
| **3kcore** | 404k CoreSNP dataset | Obtained from the filtered SNP set by applying two-step LD pruning: 1) 10kb window, R2 0.8; 2) 50 SNPs window, R2 0.8. |
| **3kfiltered** | 4.8 million filtered SNP dataset | Obtained from the Base SNP set by applying: alternative allele frequency ≥ 0.01 and proportion of missing calls ≤ 0.2. |
| **hdra** | High-density rice array (HDRA) | 1,568 diverse rice lines genotyped using a high-density rice array (HDRA) comprised of 700,000 SNPs. (DOI: 10.1038/ncomms10532) |
| **rice_rp** | Rice RP Imputed Dataset | 4,591 combined samples from HDRA and 3kRG. Complete SNP calls obtained through imputation across unique genotypes. (DOI: 10.1038/s41467-018-05538-1) |
| **baap** | Bengal and Assam Aus Panel (BAAP) | 299 cultivars with 2 million SNPs after imputation relative to the 3KRG 4.8M filtered dataset. (DOI: 10.3389/fpls.2018.01223) |

---

## 📦 Required Project Dependency

This project depends on the **SNPseek-Datasource** library for database access and data models.

### **SNPseek-Datasource**
- **Repository:** [SNPseek-Datasource](https://github.com/IRRI-Bioinformatics-Official/SNPseek-Datasource) (`SNPseek-Datasource`)
- **Artifact:** `org.irri:snpseek-DS:3.10.1`

**Installation:**
Before building SNPseek, you must install the datasource library into your local Maven repository:
```bash
cd ../SNPseek-Datasource_JUL2025
mvn clean install -DskipTests
```

---

## 🚀 How to Deploy

### Prerequisites
- **Java 15** (as specified in `pom.xml`)
- **Maven 3.6+**
- **Docker & Docker Compose** (for containerized deployment)
- **Tomcat 8.5/9.0** (for manual deployment)

### 1. Build the Project
Run the following command in the project root to compile and generate the WAR file:
```bash
mvn clean package -DskipTests
```
The generated WAR file will be located in `target/SNP-seekV3_clean-<version>.war`.

### 2. Deploy with Docker (Recommended)
You can use the provided `Dockerfile` and `docker-compose.yaml` for a quick setup.

**Build the Docker Image Locally:**
```bash
docker build -t snpseek-v3 .
cd docker
docker-compose up -d
```
The application will be available at: [http://localhost/v3/](http://localhost/v3/) (Port 80)

**Deploy Using Local Build (dockerv2):**
For local development and specific architectures:
```bash
# 1. Build the application
mvn clean package -DskipTests

# 2. Prepare the artifacts
cp target/SNP-seekV3_clean-*.war dockerv2/tomcat/snpseekv3.war

# 3. Start the containers
cd dockerv2
docker-compose up --build -d
```
The application will be available at: [http://localhost:8080/v3/](http://localhost:8080/v3/) (Port 8080)

**Deploy Using Pre-built Private Images (dockerv2):**
If you have access to the `IRRI-Bioinformatics-Official` private images, use the `dockerv2` setup.
```bash
cd dockerv2
# Authenticate with ghcr.io (see README.Docker.md for details)
docker-compose up -d
```
The application will be available at: [http://localhost:8080/v3/](http://localhost:8080/v3/)

### 3. Manual Deployment to Tomcat
1.  Copy the generated `.war` file to your Tomcat `webapps` directory.
2.  Rename it to `v3.war` if you want it accessible at `/v3`.
3.  Configure environment variables in `setenv.sh` (Linux) or `setenv.bat` (Windows).
4.  Start/Restart Tomcat.

---

## 📖 API Documentation

SNPseek provides comprehensive API support:
- **BrAPI v2.1**: Accessible at `/ws/brapi/v2`.
- **Custom REST WS**: Located in the `org.irri.iric.portal.ws.rest` package, providing endpoints for varieties, genotypes, and genomics.
- **Swagger**: API documentation files (`api-docs.json`) are available in the `WebContent/api-docs` directory.
