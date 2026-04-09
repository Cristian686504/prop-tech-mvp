# REALITY_CHECK.md — Expectativa vs. Realidad

**Proyecto:** PropTech MVP — Plataforma de Arrendamiento Inteligente  
**Equipo:** Cristian Renz (QA) y Cristian Davila (DEV)  
**Fecha de corte:** 8 de abril de 2026  
**Periodo de desarrollo:** 24 de marzo – 7 de abril de 2026 (10 días hábiles de trabajo)

---

## 1. Timeline real del proyecto

### 1.1 Datos verificables del repositorio Git

| Métrica | Valor |
|---------|:-----:|
| Commits totales | 159 |
| Pull Requests mergeados | 41 |
| Ramas feature creadas | 9 |
| Ramas hotfix creadas | 8 |
| Commits tipo `feat:` | 57 |
| Commits tipo `fix:` | 33 |
| Commits tipo `test:` | 9 |
| Commits tipo `docs:` | 16 |

### 1.2 Fases del proyecto (reconstruidas desde el historial de Git)

| Fase | Fechas | Actividad principal |
|------|--------|---------------------|
| **Setup e infraestructura** | 24 mar | Scaffold de Clean Architecture (plugin Bancolombia), Docker, Vite + React, estructura de carpetas. 18 commits en un solo día. |
| **Desarrollo de features core** | 25–27 mar | HU001–HU008 y HU010. Branches: `feature/registro-arrendador`, `feature/login`, `feature/publish-property`, `feature/list-properties`, `feature/application`, `feature/evaluate-financial-risk`. |
| **CI/CD y despliegue** | 31 mar | Pipeline CI/CD con GitHub Actions, publicación de imágenes Docker en GHCR. Branch `feature/CICD` con 12 commits de ajustes. |
| **Estabilización dirigida por QA** | 1–5 abr | 12 hotfix PRs. El QA ejecutaba pruebas, reportaba defectos y el DEV pausaba nuevas features para corregirlos. |
| **Ampliación de cobertura de tests** | 5–7 abr | Branch `feat/increase-unit-test-coverage`: +119 tests nuevos. Tests de integración con `@SpringBootTest` + H2. |

---

## 2. Backlog: planificado vs. entregado

### 2.1 Alcance definido

La HU009 (Generación de Contrato PDF) fue **excluida del alcance del MVP desde la planificación**. Se decidió que el valor del MVP reside en la evaluación de riesgo financiero y la toma de decisiones, no en la generación de un documento PDF que requiere librerías adicionales (iText o Apache PDFBox) y no aporta valor sin firma digital.

**Story Points planificados: 30 · Story Points entregados: 30 (100%)**

| Épica | HU | Nombre | SP | Estado | Tiempo Real |
|-------|----|--------|:--:|:------:|:-----------:|
| EP01 | HU001 | Registro de Arrendador | 3 | ✅ Completada | ~2h |
| EP01 | HU002 | Registro de Arrendatario | 3 | ✅ Completada | ~1h (reutilizó lógica de HU001) |
| EP01 | HU003 | Inicio de Sesión | 5 | ✅ Completada | ~3h |
| EP02 | HU004 | Publicar Propiedad | 5 | ✅ Completada | ~5h |
| EP02 | HU005 | Visualizar Propiedades Disponibles | 2 | ✅ Completada | ~2h |
| EP02 | HU006 | Aplicar para Alquilar Propiedad | 3 | ✅ Completada | ~2h |
| EP03 | HU007 | Evaluar Perfil Financiero | 2 | ✅ Completada | ~2h |
| EP03 | HU008 | Cálculo Dinámico Depósito de Garantía | 2 | ✅ Completada | ~1h |
| EP02 | HU010 | Gestionar Solicitudes de Alquiler | 5 | ✅ Completada | ~4h |
| — | — | **Total planificado** | **30** | **30 SP entregados (100%)** | — |

### 2.2 Trabajo no planificado que consumió tiempo

El backlog no contempló tareas técnicas transversales. El historial de Git muestra que estas tareas consumieron una parte significativa del esfuerzo total:

| Tarea no planificada | Tiempo aprox. | Evidencia en Git |
|----------------------|:-------------:|------------------|
| Configuración Docker + Compose | ~4h | Commits del 24 mar; `docker-compose.yml`, Dockerfiles multi-stage |
| Pipeline CI/CD (GitHub Actions + GHCR) | ~6h | Branch `feature/CICD` (31 mar): 12 commits con correcciones de sintaxis, permisos, dependencias y versiones de Node |
| Clean Architecture Plugin Bancolombia | ~2h | Auto-inyección de `tools.jackson.core:jackson-databind` (groupId incorrecto) bloqueó `./gradlew test` |
| Frontend completo (7 páginas + estilos) | ~8h | Branches `feature/registro-arrendador`, `feature/login`, `feature/publish-property`; refactor completo en `refactor/frontv2` |
| Corrección de códigos HTTP (reportados por QA) | ~4h | 4 ramas de hotfix: `hotfix/http-responses` → `v2` → `v3` → `v4` (2–4 abr) |
| Datos precargados para pruebas | ~1h | Branch `hotfix/datos-precargados` (1 abr) |
| IDs de componentes frontend para QA | ~1h | Branch `hotfix/components-id` (2 abr) |
| Tamaño máximo de imágenes | ~2h | Branch `hotfix/images-max-size` (5 abr): 3 PRs iterativos |
| Aumento de cobertura de tests | ~4h | Branch `feat/increase-unit-test-coverage` (5–7 abr): +119 tests |
| **Total no planificado** | **~32h** | — |

### 2.3 Tareas subestimadas

#### a) HU004 — Publicar Propiedad (estimada en 5 SP, requirió ~5h reales)
**Causa:** La HU tiene 9 criterios de aceptación, la mayor cantidad del backlog. La subida de imágenes (`FileUploadController`, `LocalFileStorageAdapter`, permisos de directorio) y las validaciones extensas generaron más trabajo del esperado. Además, el límite de tamaño de imagen requirió 3 iteraciones de hotfix posteriores (PR #32, #34, #37) reportadas por QA.

#### b) CI/CD (no estimada — costó ~6h)
**Causa:** El pipeline de GitHub Actions con publicación a GHCR no fue trivial. Se necesitaron 12 commits en un día (31 mar) para resolver problemas de sintaxis YAML, permisos de `packages:write`, versión de Node, dependencias de Actuator y nombres de imágenes.

#### c) Corrección iterativa de códigos HTTP (no estimada — costó ~4h)
**Causa:** QA detectó que varios endpoints retornaban códigos HTTP incorrectos (ej. 200 en vez de 201 para creación, manejo incorrecto de excepciones). Esto generó 4 ramas de hotfix sucesivas entre el 2 y el 4 de abril, reflejando el ciclo: QA prueba → reporta → DEV corrige → QA vuelve a probar → encuentra otro caso.

### 2.4 Tareas sobreestimadas

| HU | SP Estimados | Tiempo Real | Observación |
|----|:------------:|:-----------:|-------------|
| HU002 | 3 | ~1h | Reutilizó completamente la lógica de HU001 (mismo endpoint `POST /api/auth/register`, solo cambia el rol) |
| HU008 | 2 | ~1h | La lógica de cálculo (1, 2 o 3 meses según riesgo) es un `switch` simple que depende de `EvaluateFinancialRiskUseCase` ya implementado |

---

## 3. Dinámica de trabajo QA ↔ DEV

### 3.1 Cómo funcionó la colaboración

La dinámica de trabajo fue **reactiva y dirigida por las pruebas de QA**:

1. **DEV** desarrollaba una feature y la mergeaba a `release/1.0.0` vía Pull Request.
2. **QA** ejecutaba las pruebas automatizadas y los casos de prueba manuales contra el ambiente desplegado.
3. Cuando QA encontraba un **bug o comportamiento inesperado**, lo reportaba directamente al DEV.
4. **DEV pausaba** el desarrollo de nuevas funcionalidades y creaba una rama `hotfix/*` para corregir el defecto.
5. Se mergeaba el hotfix y QA **re-ejecutaba** las pruebas para verificar la corrección.

### 3.2 Evidencia en el historial de Git

Esta dinámica queda reflejada de forma clara en los **12 Pull Requests de hotfix** mergeados entre el 1 y el 5 de abril:

| Fecha | Hotfix | Problema detectado por QA | PR |
|-------|--------|---------------------------|----|
| 1 abr | `hotfix/docker-composev1` | Variables de entorno incorrectas en despliegue | #12 |
| 1 abr | `hotfix/datos-precargados` | No había datos de prueba para ejecutar escenarios | #14 |
| 2 abr | `hotfix/http-responses` | Códigos HTTP incorrectos en respuestas REST | #16 |
| 2 abr | `hotfix/components-id` | Componentes del frontend sin IDs únicos (dificultaba pruebas E2E) | #18 |
| 2 abr | `hotfix/http-responsesv2` | Más códigos HTTP incorrectos en registro | #20, #22, #24 |
| 4 abr | `hotfix/http-responsesv3` | Manejo incorrecto de excepciones | #28 |
| 4 abr | `hotfix/httpresponsesv4` | Mejora en manejo de errores HTTP | #30 |
| 5 abr | `hotfix/images-max-size` | Límite de tamaño de imagen demasiado bajo | #32, #34, #37 |

> **Patrón claro:** Los hotfixes de `http-responses` escalaron de v1 a v4 en 3 días (2–4 abr). Cada vez que QA verificaba la corrección, encontraba otro endpoint con el mismo problema. Esto muestra que el enfoque de corregir caso por caso, en lugar de auditar todos los endpoints de una vez, fue ineficiente y generó retrabajo.

### 3.3 Impacto en la velocidad de entrega

De los 33 commits tipo `fix:` del proyecto, **21 ocurrieron entre el 1 y el 5 de abril** — es decir, la segunda mitad del desarrollo se dedicó mayoritariamente a estabilización, no a nuevas funcionalidades. La única feature nueva en ese periodo fue la **paginación de propiedades** (`feature/pageable-properties`, 2 abr), que surgió como necesidad operativa al probar con múltiples propiedades.

---

## 4. ¿El MVP construido es realmente valioso para el negocio?

### Sí. El MVP entrega el 100% del flujo de negocio planificado

| Objetivo de Negocio | ¿Se cumple? | Justificación |
|---------------------|:---------------------:|---------------|
| Reducir fricción en el flujo de alquiler | ✅ Sí | El flujo completo (registro → login → publicar → aplicar → evaluar → decidir) es digital y funciona en menos de 5 minutos |
| Evaluar riesgo financiero del inquilino | ✅ Sí | El sistema evalúa automáticamente el score crediticio vs ingresos mensuales y clasifica en 3 niveles de riesgo (bajo, medio, alto) |
| Calcular depósito dinámico según riesgo | ✅ Sí | El depósito se calcula automáticamente (1–3 meses) basado en el nivel de riesgo evaluado |
| Permitir al propietario tomar decisiones informadas | ✅ Sí | El dashboard del arrendador muestra solicitudes con nombre, ingresos, score crediticio, nivel de riesgo y depósito calculado |

### Funcionalidades entregadas

**Para el arrendador:**
- Registro y autenticación con JWT
- Publicación de propiedades con imágenes, título, dirección, descripción y precio
- Listado paginado de propiedades
- Dashboard con estadísticas (propiedades activas, solicitudes pendientes)
- Visualización de solicitudes con evaluación financiera completa
- Capacidad de aprobar/rechazar solicitudes

**Para el arrendatario:**
- Registro con perfil financiero (ingresos mensuales, score crediticio)
- Visualización paginada de propiedades disponibles
- Aplicación para alquilar una propiedad
- Dashboard con seguimiento del estado de sus solicitudes

---

## 5. ¿Cómo garantizó el QA la calidad del MVP entregado?

### 5.1 Estrategia de pruebas ejecutada

La estrategia de QA se ejecutó en **dos fases** a lo largo de los 10 días de desarrollo:

- **Fase 1 (paralela al desarrollo):** Pruebas unitarias y de integración sobre la lógica de negocio conforme se iban completando las HU. Foco en dominio y casos de uso.
- **Fase 2 (estabilización):** Pruebas funcionales de API (Karate), pruebas E2E (SerenityBDD + Cucumber) y pruebas de rendimiento (k6). Se ejecutaron los 59 casos de prueba definidos en `TEST_CASES.md`.

### 5.2 Pruebas unitarias y de integración (260 tests)

Se pasó de 141 tests iniciales (dominio + use cases) a **260 tests** cubriendo todas las capas de la arquitectura:

#### Dominio — Model (3 archivos, 72 tests)
| Archivo de Test | Tests |
|----------------|:-----:|
| `UserTest.java` | 38 |
| `PropertyTest.java` | 22 |
| `ApplicationTest.java` | 12 |

#### Dominio — Use Cases (11 archivos, 127 tests)
| Archivo de Test | Tests |
|----------------|:-----:|
| `EvaluateFinancialRiskUseCaseTest.java` | 19 |
| `LoginUserUseCaseTest.java` | 19 |
| `GetUserByIdUseCaseTest.java` | 18 |
| `ApplyForPropertyUseCaseTest.java` | 17 |
| `GetPropertyApplicationsUseCaseTest.java` | 11 |
| `GetPropertiesUseCaseTest.java` | 10 |
| `PublishPropertyUseCaseTest.java` | 9 |
| `CalculateSecurityDepositUseCaseTest.java` | 8 |
| `GetTenantApplicationsUseCaseTest.java` | 8 |
| `GetPropertyByIdUseCaseTest.java` | 7 |
| `RegisterUserUseCaseTest.java` | 6 |

#### Infraestructura (2 archivos, 17 tests)
| Archivo de Test | Tests |
|----------------|:-----:|
| `GlobalExceptionHandlerTest.java` | 10 |
| `PropertyRepositoryAdapterTest.java` | 7 |

#### Integración — App Service (7 archivos, 44 tests)
| Archivo de Test | Tests | Tipo |
|----------------|:-----:|:----:|
| `PropertyControllerPaginationIntegrationTest.java` | 11 | `@SpringBootTest` + H2 |
| `AuthControllerRegisterIntegrationTest.java` | 10 | `@SpringBootTest` + H2 |
| `PropertyControllerPriceValidationTest.java` | 9 | `@SpringBootTest` + H2 |
| `ArchitectureTest.java` | 5 | ArchUnit |
| Otros tests de configuración e infraestructura | 9 | — |

| | **Total: 260 tests** |

### 5.3 Cobertura JaCoCo

**Módulo Model (dominio):**

| Métrica | Cobertura |
|---------|:---------:|
| Instrucciones | 90.75% |
| Ramas | 100.00% |
| Líneas | 91.11% |
| Complejidad | 95.45% |
| Métodos | 85.71% |
| Clases | 85.71% |

**Módulo UseCase (casos de uso):**

| Métrica | Cobertura |
|---------|:---------:|
| Instrucciones | 100.00% |
| Ramas | 97.96% |
| Líneas | 100.00% |
| Complejidad | 97.92% |
| Métodos | 100.00% |
| Clases | 100.00% |

### 5.4 Mutation Testing (Pitest)

| Módulo | Mutaciones Generadas | Mutaciones Eliminadas | Score |
|--------|:--------------------:|:---------------------:|:-----:|
| Model | 19 | 18 | 79% |
| UseCase | — | — | 82% |

El mutation testing garantiza que los tests no solo ejecutan las líneas sino que realmente **detectan cambios** en el comportamiento del código.

### 5.5 Pruebas funcionales de API — Karate DSL

Se implementaron escenarios de pruebas funcionales sobre los endpoints REST utilizando **Karate DSL 1.5.2**. Los escenarios cubrieron:

- `POST /api/auth/register` — registro exitoso, correo duplicado, datos inválidos
- `POST /api/auth/login` — credenciales válidas e inválidas
- `POST /api/properties` — publicación con y sin autenticación, validaciones de campos
- `GET /api/properties` — listado paginado, filtros
- `POST /api/applications` — aplicación a propiedad, evaluación financiera automática
- `PUT /api/applications/{id}` — aprobar/rechazar solicitudes, validación de permisos

### 5.6 Pruebas E2E — SerenityBDD + Cucumber

Se automatizaron los flujos end-to-end de los criterios de aceptación con **SerenityBDD 4.0.1 + Cucumber**, cubriendo los escenarios Gherkin definidos en `TEST_CASES.md`:

- Flujo completo de registro → login → publicar propiedad → aplicar → evaluar riesgo → aprobar/rechazar
- Validaciones de formularios (boundary testing en campos de nombre, correo, teléfono, precio)
- Verificación de dashboards diferenciados por rol (arrendador vs arrendatario)

### 5.7 Pruebas de rendimiento — k6

Se ejecutaron scripts de carga con **k6** sobre los endpoints críticos:

| Endpoint | VUs | Duración | p95 Objetivo |
|----------|:---:|:--------:|:------------:|
| `POST /api/auth/register` | 100 | 60s | < 2s |
| `POST /api/auth/login` | 100 | 60s | < 2s |
| `GET /api/properties` | 100 | 60s | < 1s |

### 5.8 Casos de prueba ejecutados

Los **59 casos de prueba** definidos en `TEST_CASES.md` fueron ejecutados en su totalidad, cubriendo:

| HU | Casos de Prueba | Descripción |
|----|:---------------:|-------------|
| HU001 | TC-001 a TC-018 | Registro de arrendador: happy path, correo duplicado, validaciones de campos, boundary testing, rendimiento |
| HU002 | TC-019 a TC-020 | Registro de arrendatario: happy path, correo duplicado |
| HU003 | TC-021 a TC-027 | Login: credenciales válidas/inválidas, token JWT, campos vacíos, rendimiento |
| HU004 | TC-028 a TC-042 | Publicar propiedad: validaciones extensas, imágenes, autenticación, autorización |
| HU005 | TC-057 a TC-059 | Visualización de propiedades: listado, autenticación requerida, rendimiento |
| HU006 | TC-043 a TC-046 | Aplicar a propiedad: happy path, duplicados, autenticación |
| HU007 | TC-047 a TC-051 | Evaluación financiera: todas las combinaciones score/ingresos |
| HU008 | TC-052 a TC-053 | Cálculo de depósito: derivado del riesgo evaluado |
| HU010 | TC-054 a TC-056 | Gestión de solicitudes: aprobar, rechazar, validación de permisos |

### 5.9 Técnicas de QA aplicadas

1. **Foco en lógica de negocio crítica:** Cobertura prioritaria del algoritmo de evaluación financiera (`EvaluateFinancialRiskUseCase` — 19 tests con todas las combinaciones de score/ingresos de la tabla de negocio).
2. **Tests parametrizados:** `@ParameterizedTest` con `@NullSource`, `@ValueSource` y `@NullAndEmptySource` para cubrir múltiples escenarios con menos código.
3. **Boundary testing:** Valores frontera exactos del algoritmo de riesgo (score = 500, 699, 700; ratio de ingresos = 2x, 3x).
4. **Verificación de interacciones:** `Mockito.verify()` y `ArgumentCaptor` para confirmar interacciones correctas con repositorios.
5. **Tests de integración con H2:** Validación end-to-end de la capa HTTP con `@SpringBootTest`, base de datos en memoria y Spring Security configurado.
6. **Pruebas multi-nivel:** Unitarias → Integración → API funcional (Karate) → E2E (SerenityBDD) → Rendimiento (k6).

---

## 6. Lecciones Aprendidas

### Lo que funcionó bien

- **Clean Architecture** separó claramente dominio, casos de uso e infraestructura, permitiendo testear la lógica de negocio sin dependencias externas (BD, HTTP, archivos).
- **Docker Compose** permitió levantar todo el stack (backend + frontend + PostgreSQL) con un solo comando, reduciendo fricciones de entorno.
- **Excluir la HU009 desde el inicio** fue acertado — el valor de negocio está en la evaluación de riesgo, no en el PDF.
- **Mutation testing** validó que los tests son efectivos, no solo cosméticos.
- **Dinámica QA→DEV reactiva:** Los hotfixes rápidos permitieron que el QA no se bloqueara. Cuando se encontraba un defecto, la corrección se mergeaba el mismo día y QA podía continuar.
- **Conventional commits + Git Flow:** La trazabilidad del proyecto es total. Cada feature, cada fix y cada mejora se puede rastrear a su branch, su PR y su fecha exacta.

### Lo que mejoraríamos

- **Estimar las tareas técnicas transversales:** Docker, CI/CD, plugins y frontend sumaron ~32h no planificadas (más que las propias HU). En el próximo ciclo se estimarán como tareas técnicas dentro del backlog.
- **Auditar todos los endpoints de una vez, no uno por uno:** Los hotfixes de `http-responses` v1→v4 muestran que el enfoque incremental de corregir caso por caso fue ineficiente. Una revisión global de códigos HTTP tras la primera detección habría ahorrado 3 rondas de hotfix.
- **Investigar herramientas antes de usarlas:** El plugin de Clean Architecture de Bancolombia generó un bloqueo de ~2h por auto-inyectar dependencias incorrectas. Un spike previo lo habría detectado.
- **Agregar IDs a los componentes del frontend desde el inicio:** El hotfix `components-id` (2 abr) fue necesario porque QA no podía automatizar pruebas E2E sin selectores estables. Esto debería ser un estándar de desarrollo desde el día 1.

---

## 7. Resumen Ejecutivo

| Métrica | Valor |
|---------|:-----:|
| Tiempo planificado | 10 días |
| Tiempo real | 10 días (24 mar – 7 abr) |
| Commits totales | 159 |
| Pull Requests mergeados | 41 |
| Story Points planificados | 30 |
| Story Points entregados | 30 (100%) |
| Historias de usuario completadas | 9 de 9 (HU009 excluida del alcance) |
| Tests automatizados (unitarios + integración) | 260 |
| Casos de prueba ejecutados (`TEST_CASES.md`) | 59 de 59 |
| Cobertura de líneas (model) | 91.11% |
| Cobertura de líneas (usecase) | 100.00% |
| Cobertura de ramas (model) | 100.00% |
| Cobertura de ramas (usecase) | 97.96% |
| Mutation score (model) | 79% |
| Mutation score (usecase) | 82% |
| Herramientas de QA utilizadas | JUnit 5, Mockito, JaCoCo, Pitest, Karate, SerenityBDD, k6 |
| Hotfix PRs (defectos detectados por QA) | 12 |
| Defectos bloqueantes abiertos | 0 |
| MVP funcional e integrado | ✅ Sí |