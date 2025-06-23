# 🌾 SNPseek

**SNPseek** is a genomic data platform developed by the International Rice Research Institute (IRRI) to support the exploration, visualization, and analysis of high-density Single Nucleotide Polymorphisms (SNPs) across rice accessions. It provides researchers with the tools to query genotypic data, download filtered datasets, and integrate results into downstream pipelines via APIs.

---

## 📖 Table of Contents

- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [API Access](#-api-access)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

---

## 🔍 Features

- 🔎 **SNP Search** by gene, chromosome position, or accession
- 🧬 **Genotype Visualization** for multiple samples
- 📦 **Downloadable SNP Datasets** in tabular format
- 🧠 **API Access** for integration with pipelines or other bioinformatics tools
- 📊 **R-based SNP Analysis** with PLINK support
- 🐳 **Docker-based Deployment**

---

## 🛠️ Technologies Used

- **Java** – Backend services
- **Spring Boot** – REST API and service layer
- **PostgreSQL + Chado schema** – Genotype and metadata storage
- **R + PLINK** – Backend SNP analysis scripts
- **Docker** – Deployment and containerization
- **GitHub Projects & Actions** – Version control and CI/CD

---

## ⚙️ Environment Variables

Before running the project, configure the following variables in a `.env` file in the project root:

```env
POSTGRES_DB=snpseek
POSTGRES_USER=snpuser
POSTGRES_PASSWORD=supersecure
R_SCRIPTS_DIR=/IRCStorage/scripts
APP_ENV=production

---

## 🚀 Getting Started

### 🔧 Prerequisites

- [Docker](https://www.docker.com/)
- [Git](https://git-scm.com/)
- (Optional) Java 11+ and R installed locally for script debugging

### 🐳 Quick Start with Docker

```bash
git clone git@github.com:IRRI-Bioinformatics-Official/SNPseek-Datasource.git
cd SNPseek-Datasource

# Edit config files if necessary (e.g., DB paths, secrets)

# Build and run
docker-compose up --build
