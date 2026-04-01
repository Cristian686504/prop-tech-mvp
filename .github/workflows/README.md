# GitHub Actions Workflows

Este directorio contiene los pipelines de CI/CD para el proyecto PropTech MVP.

## 📋 Workflows Disponibles

### 🔄 `ci.yml` - Pipeline Principal de CI/CD

Pipeline completo que se ejecuta en cada push y pull request.

**Trigger:**
- Push a cualquier rama
- Pull requests a cualquier rama

**Jobs:**

#### 1️⃣ **Backend Tests** (`backend-tests`)
- Ejecuta pruebas unitarias con JUnit 5
- Genera reporte de cobertura con Jacoco
- Verifica cobertura mínima del 80%
- Publica resultados de pruebas
- Utiliza base de datos PostgreSQL en servicio

#### 2️⃣ **Backend Build** (`backend-build`)
- Compila la aplicación backend con Gradle
- Genera artefactos JAR
- Sube artefactos para descarga

#### 3️⃣ **Frontend Lint** (`frontend-lint`)
- Ejecuta ESLint para validar estándares de código
- Verifica formato y buenas prácticas

#### 4️⃣ **Frontend Build** (`frontend-build`)
- Compila la aplicación React con Vite
- Genera bundle optimizado para producción
- Sube artefactos de distribución

#### 5️⃣ **Docker Build** (`docker-build`)
- Construye imágenes Docker del backend y frontend
- Solo se ejecuta en push a `main` o `develop`
- Usa cache para optimizar tiempos de build
- Opcional: push a Docker Hub (requiere secrets)

#### 6️⃣ **Integration Test** (`integration-test`)
- Levanta stack completo con Docker Compose
- Ejecuta smoke tests end-to-end
- Verifica health de servicios
- Solo en pull requests

#### 7️⃣ **Code Quality** (`code-quality`)
- Análisis de código con SonarCloud (opcional)
- Detecta code smells y vulnerabilidades
- Requiere `SONAR_TOKEN` secret

#### 8️⃣ **Security Scan** (`security-scan`)
- Escaneo de vulnerabilidades con Trivy
- Detecta dependencias con CVEs
- Sube resultados a GitHub Security

#### 9️⃣ **Pipeline Summary** (`pipeline-summary`)
- Consolida resultados de todos los jobs
- Genera resumen final del pipeline

---

## 🔐 Secrets Requeridos

Para aprovechar todas las funcionalidades del pipeline, configura los siguientes secrets en GitHub:

| Secret | Descripción | Requerido |
|--------|-------------|:---------:|
| `DOCKER_USERNAME` | Usuario de Docker Hub | Opcional |
| `DOCKER_PASSWORD` | Password/Token de Docker Hub | Opcional |
| `SONAR_TOKEN` | Token de SonarCloud | Opcional |

### Cómo configurar secrets:

1. Ve a tu repositorio en GitHub
2. Settings → Secrets and variables → Actions
3. Click en "New repository secret"
4. Agrega cada secret con su valor correspondiente

---

## 🚀 Cómo usar los workflows

### Ejecución automática

Los workflows se ejecutan automáticamente en:

```bash
# Al hacer push a cualquier rama
git push origin main
git push origin develop
git push origin feature/CICD  # ✅ Ahora también activa el pipeline

# Al crear un pull request a cualquier rama
gh pr create --base main
gh pr create --base develop
```

### Ejecución manual

También puedes ejecutar workflows manualmente desde la UI de GitHub:

1. Ve a la pestaña "Actions" en tu repositorio
2. Selecciona el workflow que deseas ejecutar
3. Click en "Run workflow"
4. Selecciona la rama y confirma

---

## 📊 Visualización de Resultados

### Badges de Estado

Agrega estos badges a tu README.md:

```markdown
[![CI/CD Pipeline](https://github.com/TU-USUARIO/prop-tech-mvp/workflows/PropTech%20MVP%20-%20CI/CD%20Pipeline/badge.svg)](https://github.com/TU-USUARIO/prop-tech-mvp/actions)
[![Coverage](https://codecov.io/gh/TU-USUARIO/prop-tech-mvp/branch/main/graph/badge.svg)](https://codecov.io/gh/TU-USUARIO/prop-tech-mvp)
```

### Artifacts

Los siguientes artefactos se generan y están disponibles para descarga:

- **backend-jar**: Archivo JAR compilado del backend (7 días)
- **frontend-dist**: Build optimizado del frontend (7 días)
- **backend-coverage-report**: Reporte HTML de cobertura (permanente)

---

## 🛠 Configuración Local

### Requisitos previos

```bash
# Java 17
java -version

# Node.js 18+
node -version

# Docker
docker --version
```

### Ejecutar las mismas validaciones localmente

```bash
# Backend tests
cd backend
./gradlew test jacocoTestReport

# Frontend lint & build
cd frontend
npm ci
npm run lint
npm run build

# Integration test con Docker Compose
docker-compose up -d
curl http://localhost:8080/actuator/health
docker-compose down
```

---

## 🔧 Personalización

### Cambiar umbrales de cobertura

Edita `backend/build.gradle`:

```gradle
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // Cambia este valor
            }
        }
    }
}
```

### Agregar más jobs

Puedes extender el pipeline agregando nuevos jobs en `ci.yml`:

```yaml
  my-custom-job:
    name: My Custom Job
    runs-on: ubuntu-latest
    needs: [backend-tests]  # Dependencias
    
    steps:
      - name: Custom step
        run: echo "Hello World"
```

---

## 📚 Recursos

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle CI/CD](https://docs.gradle.org/current/userguide/ci_integration.html)
- [Vite Build](https://vitejs.dev/guide/build.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

## 🐛 Troubleshooting

### Error: "Gradle daemon disappeared unexpectedly"

**Solución:** Agrega `--no-daemon` a los comandos de Gradle (ya incluido en el workflow).

### Error: "EACCES: permission denied"

**Solución:** Asegúrate de que `gradlew` tenga permisos de ejecución:
```bash
chmod +x backend/gradlew
git add backend/gradlew
git commit -m "fix: add execute permission to gradlew"
```

### Error: "Docker build failed"

**Solución:** Verifica que los Dockerfile existan y sean válidos:
```bash
docker build -t test-backend ./backend
docker build -t test-frontend ./frontend
```

---

**Última actualización:** Marzo 31, 2026
