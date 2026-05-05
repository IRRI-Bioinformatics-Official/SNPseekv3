# 🌾 SNPseek

**SNPseek** is a genomic data platform developed to support the exploration, visualization, and analysis of high-density Single Nucleotide Polymorphisms (SNPs) across rice accessions. It provides researchers with the tools to query genotypic data, download filtered datasets, and integrate results into downstream pipelines via APIs.

---

## 📖 Table of Contents

- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [Environment Variables](#-environment-variables)
- [Configuration & Property Files](#-configuration--property-files)
- [Required Project Dependency](#-required-project-dependency)
- [How to Deploy](#-how-to-deploy)

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

### **3. Database Properties: `db.properties`**
Used for database credentials, typically mapped to environment variables:
- `user=${DB_USERS}`
- `password=${DB_PASSWORDS}`
- `url=${DB_URLS}`

### **4. How to Set a Customized Property File**
To create and use a custom configuration:
1.  **Create a new file**: Add `myenv.properties` to `src/main/resources/`.
2.  **Define your settings**: Copy and modify entries from an existing file like `localhost.properties`.
3.  **Update Master Config**: Set `webserver=myenv` in `config.properties`.
4.  **Rebuild**: Run `mvn clean package` to include the new configuration in the WAR file.

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

**Build the Docker Image:**
```bash
docker build -t snpseek-v3 .
```

**Run with Docker Compose:**
Ensure your `.env` file or `docker-compose.yaml` has the required environment variables.
```bash
cd docker
docker-compose up -d
```

### 3. Manual Deployment to Tomcat
1.  Copy the generated `.war` file to your Tomcat `webapps` directory.
2.  Rename it to `v3.war` if you want it accessible at `/v3`.
3.  Configure environment variables in `setenv.sh` (Linux) or `setenv.bat` (Windows).
4.  Start/Restart Tomcat.
