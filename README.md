# 🏠 PropTech MVP — Plataforma de Arrendamiento Inteligente

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Coverage](https://img.shields.io/badge/coverage-80%25-brightgreen.svg)](TEST_PLAN.md)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

> **Taller Semana 7:** Ejecución Ágil, MVP y Estrategia de Pruebas  
> Micro-sprints del 24 de marzo al 1 de abril de 2026

---

## 📋 Tabla de Contenidos

- [Acerca del Proyecto](#-acerca-del-proyecto)
- [Problema de Negocio](#-problema-de-negocio)
- [MVP - Producto Mínimo Viable](#-mvp---producto-mínimo-viable)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Documentación de Pruebas](#-documentación-de-pruebas)
- [Gestión del Proyecto](#-gestión-del-proyecto)
- [Equipo](#-equipo)
- [Lecciones Aprendidas](#-lecciones-aprendidas)

---

## 🎯 Acerca del Proyecto

**PropTech** es una plataforma de arrendamiento inteligente dirigida a propietarios en Colombia que buscan aumentar la cantidad de clientes, la velocidad y la seguridad del alquiler de sus propiedades. 

El sistema digitaliza y automatiza el flujo completo de alquiler, desde el registro de usuarios hasta la generación de contratos, incluyendo una evaluación financiera automatizada del arrendatario y cálculo dinámico de depósitos de garantía basados en el nivel de riesgo.

---

## 🔍 Problema de Negocio

| Problema | Solución PropTech |
|----------|-------------------|
| **Fricción y lentitud** en el proceso de alquiler tradicional | Digitalización y automatización del flujo de alquiler (meta: < 1 día) |
| **Riesgo de incumplimiento** por falta de evaluación financiera | Evaluación automática del score crediticio e ingresos mensuales del arrendatario |
| **Cálculo estático de depósitos** que no refleja el riesgo real | Cálculo dinámico del depósito de garantía (1 a 3 meses) basado en el nivel de riesgo |

### Objetivos de Negocio

- ✅ Aumentar alquileres activos en un **20%** reduciendo fricciones en el flujo de contratación
- ✅ Disminuir el riesgo de incumplimiento de contrato en un **10%** mediante validación previa
- ✅ Duración del flujo de alquiler **menor a 1 día** mediante automatización

---

## 🚀 MVP - Producto Mínimo Viable

Este MVP fue desarrollado en **3 micro-sprints de 7 días** como parte del Taller Semana 7. Incluye las funcionalidades esenciales para validar la propuesta de valor del negocio.

### Historias de Usuario Implementadas

**Total Story Points:** 33

#### 📌 EP01 — Gestión de Usuarios (11 SP)
- **HU001:** Registro de Arrendador (3 SP)
- **HU002:** Registro de Arrendatario (3 SP)
- **HU003:** Inicio de Sesión con JWT (5 SP)

#### 📌 EP02 — Gestión de Propiedades (10 SP)
- **HU004:** Publicar Propiedad (5 SP)
- **HU005:** Visualizar Propiedades Disponibles (2 SP)
- **HU006:** Aplicar para Alquilar Propiedad (3 SP)

#### 📌 EP03 — Evaluación Financiera y Contratación (12 SP)
- **HU007:** Evaluar Perfil Financiero del Arrendatario (2 SP)
- **HU008:** Cálculo Dinámico del Depósito de Garantía (2 SP)
- **HU010:** Gestionar Solicitudes de Alquiler (5 SP)

### Funcionalidades Fuera del Alcance del MVP

- Firma digital de contratos
- Gestión de pagos de alquiler
- Integración con bancos reales
- Notificaciones en tiempo real
- Filtros avanzados de búsqueda

---

## 🏗 Arquitectura

El proyecto sigue una **arquitectura hexagonal (Clean Architecture)** con separación de responsabilidades en capas.

```
prop-tech-mvp/
├── backend/
│   ├── domain/                    # Lógica de negocio pura
│   │   ├── model/                 # Entidades del dominio
│   │   └── usecase/               # Casos de uso
│   ├── infrastructure/            # Adaptadores externos
│   │   ├── adapters/
│   │   │   ├── jpa/              # Persistencia (PostgreSQL)
│   │   │   └── rest/             # API REST (Spring Boot)
│   ├── applications/
│   │   └── app-service/          # Configuración y orquestación
│   └── deployment/               # Configuración Docker
├── frontend/                      # Interfaz de usuario (React + Vite)
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   └── services/
└── docs/                          # Documentación del proyecto
    ├── TEST_PLAN.md
    ├── TEST_CASES.md
    ├── REALITY_CHECK.md
    ├── USER_STORIES.md
    └── PRD.md
```

### Flujo de Datos

```
Frontend (React) 
    ↓ HTTP/REST
API Gateway (Spring Boot)
    ↓
Use Cases (Domain Layer)
    ↓
Repositories (Port/Adapter)
    ↓
PostgreSQL Database
```

---

## 🛠 Tecnologías

### Backend
- **Java 17** — Lenguaje de programación
- **Spring Boot 3.2.x** — Framework web
- **Gradle** — Gestión de dependencias
- **PostgreSQL** — Base de datos relacional
- **Lombok** — Reducción de boilerplate
- **JWT** — Autenticación y autorización
- **BCrypt** — Hashing de contraseñas
- **Docker** — Contenedorización

### Frontend
- **React 18** — Biblioteca UI
- **Vite** — Build tool y dev server
- **Axios** — Cliente HTTP
- **React Router** — Navegación SPA
- **TailwindCSS** — Estilos

### Calidad y Pruebas
- **JUnit 5 + Mockito** — Pruebas unitarias (> 80% coverage)
- **SerenityBDD + Cucumber** — Pruebas E2E con BDD
- **Karate DSL** — Pruebas de API REST
- **k6** — Pruebas de rendimiento
- **Jacoco** — Reporte de cobertura

### DevOps
- **Docker Compose** — Orquestación local
- **GitHub Actions** — CI/CD
- **Nginx** — Servidor web para frontend

---

## 🚀 Instalación y Ejecución

### Prerrequisitos

- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **Git**

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU-USUARIO/prop-tech-mvp.git
cd prop-tech-mvp
```

### 2. Ejecutar con Docker Compose

```bash
docker-compose up --build
```

Esto levantará:
- **Backend API** en `http://localhost:8080`
- **Frontend** en `http://localhost:3000`
- **PostgreSQL** en `localhost:5432`

### 3. Verificar que todo funciona

```bash
# Health check del backend
curl http://localhost:8080/actuator/health

# Abrir frontend en el navegador
open http://localhost:3000
```

### 4. Ejecutar solo el backend (modo desarrollo)

```bash
cd backend
./gradlew bootRun
```

### 5. Ejecutar solo el frontend (modo desarrollo)

```bash
cd frontend
npm install
npm run dev
```

### 6. Ejecutar pruebas unitarias

```bash
cd backend
./gradlew test
./gradlew jacocoTestReport  # Reporte de cobertura
```

---

## 📊 Documentación de Pruebas

Este proyecto cuenta con una **estrategia de pruebas completa** documentada en los siguientes archivos:

| Documento | Descripción | Enlace |
|-----------|-------------|--------|
| **TEST_PLAN.md** | Plan de pruebas completo con estrategia, alcance, cronograma y riesgos | [Ver documento](TEST_PLAN.md) |
| **TEST_CASES.md** | Matriz de casos de prueba con escenarios Gherkin para cada HU | [Ver documento](TEST_CASES.md) |
| **REALITY_CHECK.md** | Análisis retrospectivo: estimaciones vs realidad, lecciones aprendidas | [Ver documento](REALITY_CHECK.md) |


### Repositorio de Pruebas API (Karate)

Las pruebas de API automatizadas con Karate se encuentran en un repositorio separado:

🔗 **[proptech-api-tests](https://github.com/TU-USUARIO/proptech-api-tests)** *(Actualizar con tu enlace real)*

Incluye:
- ✅ 4 escenarios de prueba API (GET, POST, PUT, DELETE)
- ✅ Validación de contratos de API
- ✅ Instrucciones de ejecución en el README

---

## 📈 Gestión del Proyecto

### GitHub Projects

Seguimiento ágil del proyecto con tablero Kanban:

🔗 **[PropTech MVP - Tablero Ágil](https://github.com/users/TU-USUARIO/projects/X)** *(Actualizar con tu enlace real)*

### Estructura del Tablero

- **📋 Backlog** — Historias de usuario pendientes
- **📝 To Do** — Sprint actual
- **🚧 In Progress** — En desarrollo
- **✅ Done** — Completadas

Cada Historia de Usuario incluye:
- Descripción en formato "Como / Quiero / Para"
- Story Points estimados
- Criterios de aceptación
- Sub-tareas (casos de prueba)
- Etiquetas por épica

---

## � CI/CD Pipeline

El proyecto cuenta con un pipeline automatizado de GitHub Actions que garantiza la calidad del código en cada commit.

### Pipeline de Integración Continua

El workflow principal (`.github/workflows/ci.yml`) ejecuta automáticamente:

#### ✅ Backend
- **Pruebas Unitarias** — JUnit 5 + Mockito
- **Cobertura de Código** — Jacoco (mínimo 80%)
- **Build** — Compilación con Gradle
- **Artefactos** — Generación de JAR

#### ✅ Frontend
- **Análisis Estático** — ESLint
- **Build de Producción** — Vite optimizado
- **Artefactos** — Bundle minificado

#### ✅ Calidad y Seguridad
- **Análisis de Código** — SonarCloud (opcional)
- **Escaneo de Vulnerabilidades** — Trivy
- **Pruebas de Integración** — Docker Compose smoke tests

#### ✅ Docker
- **Build de Imágenes** — Backend y Frontend
- **Optimización** — Caché de layers
- **Push** — Docker Hub (solo en main/develop)

### Triggers del Pipeline

```yaml
# Se ejecuta en:
- Push a main o develop
- Pull requests a main o develop
```

### Visualización de Resultados

Puedes ver el estado del pipeline en:
- **GitHub Actions Tab** — Ejecuciones detalladas
- **Pull Request Checks** — Validaciones en cada PR
- **Badges en README** — Estado actual del pipeline

📚 **Documentación completa:** [.github/workflows/README.md](.github/workflows/README.md)

---

## �👥 Equipo

| Rol | Nombre | Responsabilidades | GitHub |
|-----|--------|------------------|--------|
| **DEV** | Cristian Davila | Desarrollo backend/frontend, arquitectura, pruebas unitarias | [@cristian-davila](https://github.com/cristian-davila) |
| **QA** | Cristian Renz | Estrategia de pruebas, automatización, documentación de calidad | [@cristian-renz](https://github.com/cristian-renz) |

---

## 💡 Lecciones Aprendidas

### ⏱ Expectativa vs. Realidad

Durante los 3 micro-sprints, enfrentamos la diferencia entre nuestras estimaciones iniciales y el esfuerzo real:

| Aspecto | Estimación | Realidad | Desviación |
|---------|------------|----------|------------|
| **Story Points Totales** | 33 SP | 33 SP completados | ✅ 0% |
| **Tiempo Estimado** | 7 días | 7 días | ✅ 0% |
| **Tareas Más Complejas** | HU003 (JWT), HU007 (Evaluación) | HU009 (PDF), HU010 (Gestión) | ⚠️ Subestimadas |

**Detalles completos en:** [REALITY_CHECK.md](REALITY_CHECK.md)

### 📚 Aprendizajes Clave

1. **Arquitectura hexagonal** facilita la separación de responsabilidades y testing
2. **Automatización de pruebas** desde el inicio ahorra tiempo en regresiones
3. **Documentación clara** acelera la integración y reduce fricción
4. **Micro-sprints cortos** permiten ajustes rápidos y feedback continuo
5. **Pair programming** (DEV-QA) mejora la calidad desde el diseño

---

## 📄 Licencia

Este proyecto fue desarrollado como parte del **Taller Semana 7** del programa de formación.

---

## 🙏 Agradecimientos

- **Instructores** del programa por el diseño del taller y feedback continuo
- **Compañeros de clase** por el apoyo y colaboración
- **Comunidad Open Source** por las herramientas utilizadas (Spring Boot, React, Karate, k6)

---

## 📞 Contacto

¿Preguntas o sugerencias? Contáctanos:

- **Email:** proptech.mvp@example.com *(Actualizar con email real)*
- **Issues:** [GitHub Issues](https://github.com/TU-USUARIO/prop-tech-mvp/issues)

---

**Hecho con ❤️ durante el Taller Semana 7 — Marzo 2026**