# 🌾 SNPseek

**SNPseek** is a genomic data platform developed to support the exploration, visualization, and analysis of high-density Single Nucleotide Polymorphisms (SNPs) across rice accessions. It provides researchers with the tools to query genotypic data, download filtered datasets, and integrate results into downstream pipelines via APIs.

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

Before running the project, ensure the following environment variables are configured. These variables are required for authentication and third-party integration.

### Required Variables

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

### Configuration Methods

#### Option 1: Docker Compose (Recommended)

Add the variables directly in your `docker-compose.yml`:

```yaml
services:
  tomcat:
    image: tomcat:9.0
    environment:
      - HOSTNAME=http://localhost:8080
      - RECAPTCHA_SECRET_KEY=${RECAPTCHA_SECRET_KEY}
      - MICROSOFT_CLIENT_ID=${MICROSOFT_CLIENT_ID}
      - MICROSOFT_TENANT_ID=${MICROSOFT_TENANT_ID}
      - MICROSOFT_SECRET=${MICROSOFT_SECRET}
      - GOOGLE_OAUTH_CLIENT_ID=${GOOGLE_OAUTH_CLIENT_ID}
      - GOOGLE_OAUTH_CLIENT_SECRET=${GOOGLE_OAUTH_CLIENT_SECRET}
      - GOOGLE_ANALYTICS_PROPERTY_ID=${GOOGLE_ANALYTICS_PROPERTY_ID}
```

#### Option 2: Tomcat setenv.sh (Linux/Mac)

Create or edit `TOMCAT_HOME/bin/setenv.sh`:

```bash
#!/bin/bash
export HOSTNAME="http://localhost:8080"
export RECAPTCHA_SECRET_KEY="your-recaptcha-secret"
export MICROSOFT_CLIENT_ID="your-azure-client-id"
export MICROSOFT_TENANT_ID="your-azure-tenant-id"
export MICROSOFT_SECRET="your-azure-secret"
export GOOGLE_OAUTH_CLIENT_ID="your-google-client-id"
export GOOGLE_OAUTH_CLIENT_SECRET="your-google-client-secret"
export GOOGLE_ANALYTICS_PROPERTY_ID="your-google-propertyid"
```

Make it executable:
```bash
chmod +x TOMCAT_HOME/bin/setenv.sh
```

#### Option 3: Tomcat setenv.bat (Windows)

Create or edit `TOMCAT_HOME/bin/setenv.bat`:

```batch
set HOSTNAME=http://localhost:8080
set RECAPTCHA_SECRET_KEY=your-recaptcha-secret
set MICROSOFT_CLIENT_ID=your-azure-client-id
set MICROSOFT_TENANT_ID=your-azure-tenant-id
set MICROSOFT_SECRET=your-azure-secret
set GOOGLE_OAUTH_CLIENT_ID=your-google-client-id
set GOOGLE_OAUTH_CLIENT_SECRET=your-google-client-secret
set GOOGLE_ANALYTICS_PROPERTY_ID=your-google-propertyid
```

#### Option 4: System Environment Variables

**Linux/Mac:**
```bash
# Add to ~/.bashrc or ~/.zshrc
export HOSTNAME="http://localhost:8080"
export RECAPTCHA_SECRET_KEY="your-recaptcha-secret"
# ... (add all variables)

# Reload
source ~/.bashrc
```

**Windows:**
1. Open System Properties → Environment Variables
2. Add each variable under "System variables" or "User variables"
3. Restart Tomcat

### Accessing Variables in Java

In your `AppContext.java`:

```java
package org.irri.iric.portal;

public class AppContext {
    public static final String hostname = System.getenv("HOSTNAME");
    
    // Or with fallback
    public static String getHostname() {
        return System.getenv().getOrDefault("HOSTNAME", "http://localhost:8080");
    }
}
```

### Verification

After configuration, verify the variables are loaded:

```java
System.out.println("HOSTNAME: " + System.getenv("HOSTNAME"));
```

Or check Tomcat logs on startup.
