# 🌾 SNPseek

**SNPseek** is a genomic data platform developed by the International Rice Research Institute (IRRI) to support the exploration, visualization, and analysis of high-density Single Nucleotide Polymorphisms (SNPs) across rice accessions. It provides researchers with the tools to query genotypic data, download filtered datasets, and integrate results into downstream pipelines via APIs.

---

## 📖 Table of Contents

- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [Environment Variable](#-environment-variable)

---

## 🔍 Features

- 🔎 **SNP Search** by gene, chromosome position, or accession
- 🧬 **Genotype Visualization** for multiple samples
- 📦 **Downloadable SNP Datasets** in tabular format
- 🧠 **BrAPI v2.1** Support for standardized API access and integration with bioinformatics tools
- 📊 **R-based SNP Analysis** with PLINK support
- 🐳 **Docker-based Deployment**

---

## 🛠️ Technologies Used

- **Java** – Backend services
- **PostgreSQL + Chado schema** – Genotype and metadata storage
- **R + PLINK** – Backend SNP analysis scripts
- **Docker** – Deployment and containerization
- **GitHub Projects & Actions** – Version control and CI/CD

---

## ⚙️ Environment Variables

Before running the project, ensure the following environment variables are set **directly** in your `docker-compose.yml` or your environment configuration.

These variables are required for authentication and third-party integration:

```yaml
RECAPTCHA_SECRET_KEY=<your-recaptcha-secret>

MICROSOFT_CLIENT_ID=<your-azure-client-id>
MICROSOFT_TENANT_ID=<your-azure-tenant-id>
MICROSOFT_SECRET=<your-azure-secret>

GOOGLE_OAUTH_CLIENT_ID=<your-google-client-id>
GOOGLE_OAUTH_CLIENT_SECRET=<your-google-client-secret>
```

You can set these inside the environment: block of a service in docker-compose.yml, for example:

```yaml
services:
  web:
    build: .
    environment:
      - RECAPTCHA_SECRET_KEY=your-value
      - MICROSOFT_CLIENT_ID=your-value
      - MICROSOFT_TENANT_ID=your-value
      - MICROSOFT_SECRET=your-value
      - GOOGLE_OAUTH_CLIENT_ID=your-value
      - GOOGLE_OAUTH_CLIENT_SECRET=your-value
```
**Tip:** Do not commit secrets into version control. Use GitHub secrets, environment-level configuration, or a secure secrets manager in production.

---

# Edit config files if necessary (e.g., DB paths, secrets)

# Build and run
docker-compose up --build
