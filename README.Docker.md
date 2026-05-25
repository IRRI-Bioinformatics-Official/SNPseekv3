# Docker Deployment Guide

## 🐳 Running with Docker Compose

You can start the application using the local build configuration:
```bash
cd docker
docker-compose up --build -d
```

## 🚀 Using Private Images (dockerv2)

The `dockerv2` directory contains a configuration that uses pre-built, private images from the `IRRI-Bioinformatics-Official` organization on GitHub Container Registry (GHCR).

### **1. Authentication (Required)**
Since these images are private, you must authenticate with `ghcr.io`:

1.  **Generate a Personal Access Token (PAT):**
    - Go to [GitHub Settings > Developer Settings > Personal Access Tokens > Tokens (classic)](https://github.com/settings/tokens).
    - Generate a new token with the `read:packages` scope.
2.  **Log in to the registry:**
    ```bash
    export CR_PAT=YOUR_TOKEN
    echo $CR_PAT | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
    ```

### **2. Deploying with dockerv2 (Local Build)**
If you prefer to build images locally (e.g., for different architectures or local development):

1.  **Build the application:**
    ```bash
    mvn clean package -DskipTests
    ```
2.  **Prepare the artifacts:**
    ```bash
    # Copy and rename the WAR file to the expected location
    cp target/SNP-seekV3_clean-*.war dockerv2/tomcat/snpseekv3.war
    ```
3.  **Start the containers:**
    ```bash
    cd dockerv2
    # Ensure you have an .env file configured (see .env.example)
    docker-compose up --build -d
    ```

Once the containers are running, the application will be available at:
**URL:** [http://localhost:8080/v3/](http://localhost:8080/v3/)

### **3. Using Private Images (GHCR)**
The `dockerv2` directory can also pull pre-built, private images.

## ☁️ Cloud Deployment

### **Building for Specific Platforms**
If your cloud provider uses a different CPU architecture (e.g., `amd64` while you are on a Mac M1):
```bash
docker build --platform=linux/amd64 -t snpseek-v3 .
```

### **Pushing to a Registry**
```bash
docker tag snpseek-v3 myregistry.com/snpseek-v3
docker push myregistry.com/snpseek-v3
```

For more details, consult the official [Docker documentation](https://docs.docker.com/go/get-started-sharing/).
