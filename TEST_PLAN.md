# Plan de Pruebas — PropTech MVP

---

## 1. Identificación del Plan

| Campo | Detalle |
|-------|---------|
| **Proyecto** | PropTech MVP |
| **Sistema bajo prueba** | Plataforma de Arrendamiento Inteligente |
| **Versión** | 1.0.0-MVP |
| **Fecha** | 24 de marzo de 2026 |
| **Ciclo de pruebas** | Sprint 1 — HU001 a HU005 |
| **Equipo** | Cristian Renz y Cristian Davila |

---

## 2. Contexto

PropTech es una plataforma de arrendamiento inteligente dirigida a propietarios en Colombia que buscan aumentar la cantidad de clientes, la velocidad y la seguridad del alquiler de sus propiedades. El sistema permite el registro de arrendadores y arrendatarios, la publicación y visualización de propiedades, la evaluación financiera automatizada de inquilinos, el cálculo dinámico de depósitos de garantía y la generación de contratos de arrendamiento en PDF.

### 2.1 Problema de negocio que resuelve

| Problema | Solución PropTech |
|----------|-------------------|
| Fricción y lentitud en el proceso de alquiler tradicional | Digitalización y automatización del flujo de alquiler (meta: < 1 día) |
| Riesgo de incumplimiento por falta de evaluación financiera | Evaluación automática del score crediticio e ingresos mensuales del arrendatario |
| Cálculo estático de depósitos que no refleja el riesgo real | Cálculo dinámico del depósito de garantía (1 a 3 meses) basado en el nivel de riesgo |

### 2.2 Objetivos de negocio vinculados

- Aumentar alquileres activos en un 20% reduciendo fricciones en el flujo de contratación.
- Disminuir el riesgo de incumplimiento de contrato en un 10% mediante validación previa a la firma.
- Duración del flujo de alquiler menor a 1 día mediante automatización.

---

## 3. Alcance de las Pruebas

### 3.1 Historias de Usuario en alcance (este ciclo)

| Épica | HU | Nombre | Story Points | Prioridad |
|-------|----|--------|:------------:|:---------:|
| EP01 — Gestión de Usuarios | HU001 | Registro de Arrendador | 3 | Alta |
| EP01 — Gestión de Usuarios | HU002 | Registro de Arrendatario | 3 | Alta |
| EP01 — Gestión de Usuarios | HU003 | Inicio de Sesión | 5 | Alta |
| EP02 — Gestión de Propiedades | HU004 | Publicar Propiedad | 5 | Alta |
| EP02 — Gestión de Propiedades | HU005 | Visualizar Propiedades Disponibles | 2 | Media |

**Total Story Points en alcance:** 18

### 3.2 Criterios de aceptación a validar

#### HU001 — Registro de Arrendador (3 CA)
| CA | Escenario | Tipo de ejecución |
|----|-----------|:-----------------:|
| CA001 | Registro exitoso del arrendador | Automatizada |
| CA002 | Registro fallido por correo electrónico ya registrado | Automatizada |
| CA003 | Registro fallido por datos inválidos | Automatizada |

#### HU002 — Registro de Arrendatario (3 CA)
| CA | Escenario | Tipo de ejecución |
|----|-----------|:-----------------:|
| CA001 | Registro exitoso del arrendatario | Automatizada |
| CA002 | Registro fallido por correo electrónico ya registrado | Automatizada |
| CA003 | Registro fallido por datos inválidos | Automatizada |

#### HU003 — Inicio de Sesión (3 CA)
| CA | Escenario | Tipo de ejecución |
|----|-----------|:-----------------:|
| CA001 | Inicio de sesión exitoso | Automatizada |
| CA002 | Inicio de sesión con campos vacíos | Automatizada |
| CA003 | Inicio de sesión con credenciales inválidas | Automatizada |

#### HU004 — Publicar Propiedad (9 CA)
| CA | Escenario | Tipo de ejecución |
|----|-----------|:-----------------:|
| CA001 | Publicación exitosa de la propiedad | Automatizada |
| CA002 | Publicación con campos vacíos | Automatizada |
| CA003 | Publicación con imágenes > 250 MB | Manual |
| CA004 | Publicación con título > 255 caracteres | Automatizada |
| CA005 | Publicación con dirección > 255 caracteres | Automatizada |
| CA006 | Publicación con descripción > 2000 caracteres | Automatizada |
| CA007 | Publicación con precio no numérico | Automatizada |
| CA008 | Usuario no autenticado intenta crear publicación | Automatizada |
| CA009 | Usuario con rol distinto a arrendador intenta crear publicación | Automatizada |

#### HU005 — Visualizar Propiedades Disponibles (3 CA)
| CA | Escenario | Tipo de ejecución |
|----|-----------|:-----------------:|
| CA001 | Visualización de propiedades disponibles | Automatizada |
| CA002 | No hay propiedades disponibles | Automatizada |
| CA003 | Usuario no autenticado quiere ver propiedades | Automatizada |

### 3.3 Fuera del alcance (este ciclo)

| HU | Nombre | Razón |
|----|--------|-------|
| HU006 | Aplicar para Alquilar Propiedad | Planificada para Sprint 2 |
| HU007 | Evaluar Perfil Financiero del Arrendatario | Planificada para Sprint 2 |
| HU008 | Cálculo Dinámico del Depósito de Garantía | Planificada para Sprint 2 |
| HU009 | Generación de Contrato de Arrendamiento | Planificada para Sprint 2 |
| — | Firma digital de contrato | Fuera del alcance del MVP |
| — | Gestión de pagos de alquiler | Fuera del alcance del MVP |
| — | Usuario administrador | Fuera del alcance del MVP |
| — | Integración con bancos reales | Fuera del alcance del MVP |
| — | Notificaciones | Fuera del alcance del MVP |
| — | Soporte a clientes | Fuera del alcance del MVP |
| — | Filtro de propiedades | Fuera del alcance del MVP |
| — | Pruebas de seguridad (inyección SQL, XSS, manipulación JWT, hashing de contraseñas) | Fuera del alcance del Sprint 1 — se abordarán en un ciclo dedicado de seguridad |
| — | Condiciones de carrera en alquiler simultáneo de propiedades | Funcionalidad de contratos planificada para Sprint 2 |
| — | Acceso a contratos por URL sin verificar sesión | Funcionalidad de contratos planificada para Sprint 2 |
| — | Integración con permisos bancarios | Integración bancaria fuera del alcance del Sprint 1 |

---

## 4. Estrategia de Pruebas

### 4.1 Tipos de pruebas

| Tipo de prueba | Descripción | Herramienta | Responsable |
|----------------|-------------|-------------|:-----------:|
| **Funcional — API** | Validación exclusiva de contratos de API REST: request/response, códigos HTTP, estructura JSON y schemas | Karate DSL | QA |
| **Funcional — E2E (BDD)** | Validación de flujos de negocio de extremo a extremo con escenarios Gherkin sobre criterios de aceptación | SerenityBDD + Cucumber | QA |
| **Manual** | Validación de escenarios no automatizables | Postman / navegador | QA |
| **Rendimiento** | Pruebas de carga y estrés sobre endpoints críticos | k6 | QA + DEV |
| **Unitaria** | Pruebas de unidad con cobertura > 80% | JUnit 5 / Mockito | DEV |

### 4.2 Estrategia de ejecución

1. **Smoke Test:** Verificar que el entorno de pruebas esté disponible y haya conectividad con la base de datos.
2. **Pruebas funcionales API (Karate):** Ejecutar los escenarios de cada endpoint individualmente validando contratos de API (códigos HTTP, estructura JSON y schemas de respuesta). Karate se usa exclusivamente para la capa de API.
3. **Pruebas funcionales E2E (SerenityBDD + Cucumber):** Ejecutar los escenarios Gherkin que validan flujos de negocio de extremo a extremo sobre los criterios de aceptación de cada HU. SerenityBDD se usa exclusivamente para flujos E2E.
4. **Pruebas manuales:** Ejecutar los escenarios marcados como "Manual" en la matriz de criterios de aceptación. El QA documenta evidencia con capturas de pantalla y resultado en el reporte.
5. **Pruebas de rendimiento (k6):** Ejecutar scripts de carga sobre los endpoints `POST api/auth/register`, `POST api/auth/login`, `POST /api/properties` y `GET /api/properties`.
6. **Regresión:** Al final de cada sprint se ejecuta el suite completo de pruebas automatizadas para garantizar que no se introdujeron regresiones.
7. **Reporte de defectos:** Los defectos encontrados se registran en el Bug Tracker (GitHub Issues) con severidad, pasos de reproducción y evidencia. Se notifica al equipo DEV para corrección y se valida en un ciclo 2 de ejecución.

### 4.3 Estrategia de datos

- Los datos de prueba para registro de usuarios serán generados de forma aleatoria utilizando la librería **Java Faker**.
- Para las pruebas de API con Karate se utilizarán payloads JSON definidos en archivos `.json` dentro del proyecto de pruebas.
- La base de datos de pruebas se reiniciará antes de cada ciclo de ejecución para garantizar un estado limpio y reproducible.
- Se prepararán sets de datos específicos para escenarios negativos (correos duplicados, campos vacíos, datos inválidos).

---

## 5. Criterios de Entrada y Salida

### 5.1 Criterios de entrada (para iniciar pruebas)

| # | Criterio |
|---|----------|
| 1 | Código fuente disponible en el repositorio con PR aprobado |
| 2 | Ambiente de pruebas desplegado y estable |
| 3 | Base de datos de pruebas configurada y accesible |
| 4 | Endpoints de la API documentados y disponibles |
| 5 | Pruebas unitarias ejecutadas por DEV con cobertura > 80% |
| 6 | Historias de usuario y criterios de aceptación revisados y aprobados |
| 7 | Datos de prueba preparados |
| 8 | Herramientas de prueba configuradas (SerenityBDD, Cucumber, Karate, k6) |

### 5.2 Criterios de salida (para dar una HU por probada)

| # | Criterio |
|---|----------|
| 1 | Todos los criterios de aceptación validados (100% ejecutados) |
| 2 | Tasa de aprobación de casos de prueba ≥ 95% |
| 3 | Sin defectos críticos o bloqueantes abiertos |
| 4 | Defectos de severidad media resueltos o con plan de mitigación aprobado |
| 5 | Reporte de pruebas de SerenityBDD generado y revisado |
| 6 | Pruebas de rendimiento k6 ejecutadas y dentro de umbrales aceptables |
| 7 | Ciclo de regresión ejecutado sin hallazgos nuevos |

---

## 6. Entorno de Pruebas

### 6.1 Configuración del entorno

| Componente | Detalle |
|------------|---------|
| **Ambiente** | Staging / QA |
| **Sistema operativo del servidor** | Linux (contenedor Docker) |
| **Base de datos** | PostgreSQL |
| **Backend** | API REST desplegada en contenedor Docker |
| **URL base API** | por definir |
| **Autenticación** | JWT (JSON Web Token) |
| **CI/CD** | GitHub Actions — ejecución automática de suites de prueba en cada PR |

### 6.2 Datos del entorno

| Dato | Valor |
|------|-------|
| **Usuarios de prueba precargados** | 1 arrendador, 1 arrendatario (seed de BD) |
| **Propiedades de prueba precargadas** | 3 propiedades con imágenes de ejemplo |
| **BD limpia antes de cada ciclo** | Sí — script de reset disponible |

---

## 7. Herramientas

| Herramienta | Versión | Propósito |
|-------------|---------|-----------|
| **SerenityBDD + Cucumber** | 4.0.1 | Pruebas funcionales E2E — flujos de negocio de extremo a extremo con escenarios Gherkin y reportes detallados |
| **Karate DSL** | 1.5.2 | Pruebas exclusivas de API REST — validación de contratos de API: códigos HTTP, estructura JSON y schemas |
| **k6** | 1.6.1 | Pruebas de rendimiento — carga, estrés y umbrales de latencia |
| **JUnit 5 / Mockito** | 5.13.4 / 5.23.0 | Pruebas unitarias (responsabilidad DEV) |
| **Java Faker** | 1.0.2 | Generación de datos de prueba aleatorios |
| **GitHub Issues** | — | Bug Tracker — registro y seguimiento de defectos |
| **GitHub Actions** | — | CI/CD — ejecución automática de suites de prueba |
| **Postman** | 12.3.0 | Exploración y pruebas manuales |
| **IntelliJ IDEA / VS Code** | 2025.3.4 / 1.112 | IDE para desarrollo y ejecución de scripts de prueba |
| **Jacoco** | 0.8.14 | Comprobación de coverage |
| **Github Copilot** | 1.0.11 | Apalancamiento con IA para todo el proceso de pruebas |
---

## 8. Roles y Responsabilidades

| Rol | Nombre | Responsabilidades |
|-----|-------------------|-------------------|
| **QA** | Cristian Renz | Diseñar y mantener el plan de pruebas. Definir casos de prueba. Priorizar ejecución según riesgo. Revisar y aprobar reportes. Coordinar ciclos de prueba. Implementar escenarios Gherkin en SerenityBDD + Cucumber. Implementar pruebas de API con Karate. Crear y ejecutar scripts de rendimiento con k6. Mantener el framework de automatización. Generar reportes de ejecución. |
| **DEV** | Cristian Davila |Implementar pruebas unitarias con cobertura > 80%. Corregir defectos reportados por QA. Proveer documentación de API (endpoints, payloads, códigos de respuesta). Asegurar que el ambiente de staging esté desplegado. Implementar pruebas unitarias de componentes UI. Corregir defectos de interfaz reportados por QA. |

### 8.1 Distribución de tareas QA por HU

| HU | Tareas QA |
|----|-----------|
| HU001 | TQA001–TQA007: Verificar creación de usuario arrendador, unicidad de correo, asignación de rol, validación de campos vacíos, códigos HTTP y errores |
| HU002 | TQA008–TQA014: Verificar creación de usuario arrendatario, unicidad de correo, asignación de rol, validación de campos vacíos, códigos HTTP y errores |
| HU003 | TQA015–TQA020: Verificar login con credenciales válidas/inválidas, generación y validación de JWT, redirecciones, campos vacíos |
| HU004 | TQA022–TQA026: Verificar acceso exclusivo por arrendador, creación de propiedad, restricciones de datos, campos vacíos, códigos de error |
| HU005 | TQA027–TQA028: Verificar listado de propiedades disponibles con paginación |

### 8.2 Distribución de tareas DEV por HU

| HU | Tareas DEV-F (Funcional) | Tareas DEV-NF (No Funcional) |
|----|--------------------------|------------------------------|
| HU001 | TD001: Verificar/Crear tabla usuarios en BD (id, nombre, correo, contraseña hash, teléfono, rol, fecha de creación) · TD002: Crear modelo/entidad Usuario y DTOs · TD003: Implementar endpoint `POST api/auth/register` · TD004: Validaciones funcionales (correo ya registrado, asignación de rol arrendador) | TD005: Validaciones de entrada (formato correo, longitud mínima contraseña, formato teléfono, campos requeridos) · TD006: Manejo de errores HTTP y mensajes claros · TD007: Hashing de contraseña (bcrypt/argon2) |
| HU002 | TD008: Verificar/Crear tabla usuarios (reutiliza con HU001) · TD009: Verificar modelo/entidad Usuario y DTOs · TD010: Implementar endpoint `POST api/auth/register` (reutiliza con HU001, asignación de rol arrendatario) · TD011: Validaciones funcionales (correo ya registrado, asignación de rol arrendatario) | TD012: Validaciones de entrada (formato correo, longitud mínima contraseña, formato teléfono, campos requeridos) · TD013: Manejo de errores HTTP y mensajes claros · TD014: Hashing de contraseña |
| HU003 | TD015: Implementar endpoint `POST api/auth/login` con validación de credenciales · TD017: Implementar redirección con datos de usuario y rol | TD016: Generación de token JWT · TD018: Manejo de errores HTTP y mensajes claros · TD019: Comparación de contraseñas con hashing |
| HU004 | TD020: Crear tabla propiedades en BD (id, titulo, direccion, descripcion, precio_alquiler, imagenes, fecha_publicacion, arrendador_id, fecha_actualizacion) · TD021: Crear modelo/entidad Propiedad y DTOs · TD022: Implementar endpoint `POST /api/properties` (solo accesible por arrendadores) | TD023: Validaciones de entrada (formato de datos, campos requeridos, límites de caracteres) · TD024: Manejo de errores HTTP y mensajes claros |
| HU005 | TD025: Implementar endpoint `GET /api/properties` para listar propiedades disponibles · TD026: Componente frontend para mostrar lista de propiedades con detalles | TD027: Manejo de errores HTTP y mensajes claros · TD028: Implementar paginación |

---

## 9. Cronograma y Estimación

### 9.1 Esfuerzo de QA por HU

| HU | Tareas de QA | Esfuerzo QA estimado (días) | Actividades QA |
|----|:--------------------:|:---------------------------:|----------------|
| HU001 | 7 (TQA001–TQA007) | 0.5 | Diseño de casos, automatización Karate + Cucumber, ejecución |
| HU002 | 7 (TQA008–TQA014) | 0.5 | Diseño de casos, automatización Karate + Cucumber, ejecución |
| HU003 | 6 (TQA015–TQA020) | 1.5 | Diseño de casos, automatización Karate + Cucumber, pruebas JWT, ejecución |
| HU004 | 5 (TQA022–TQA026) | 1.5 | Diseño de casos, automatización Karate + Cucumber, E2E, ejecución |
| HU005 | 2 (TQA027–TQA028) | 1.0 | Diseño de casos, automatización Karate + Cucumber, paginación, ejecución |
| **Regresión** | Suite completo | 1.0 | Ejecución del suite completo automatizado |
| **Rendimiento (k6)** | 4 scripts | 1.0 | Creación y ejecución de scripts k6 |
| **TOTAL** | **27 + regresión + rendimiento** | **7.0** | |

### 9.2 Cronograma

| Sprint | Actividad | Duración estimada |
|------|-----------|:-----------------:|
| Micro-Sprint 1 (2 días) | Diseño del plan de pruebas | 2 día |
| Micro-Sprint 2 (2 días) | Diseño de casos de prueba (todas las HU) | 2 días |
| Sprint 3 | Automatización HU001 + HU002 (Karate + Cucumber) | 1 días |
| Sprint 3 | Automatización HU003 (Karate + Cucumber + JWT) | 2 días |
| Sprint 3 | Automatización HU004 + HU005 (Karate + Cucumber + E2E) | 2 días |
| Sprint 3 | Pruebas de rendimiento k6 | 1 día |
| Sprint 3 | Regresión + reporte final | 1 día |
| **TOTAL** | | **11 días** |

---

## 10. Entregables de Prueba

| # | Entregable | Formato | Descripción |
|---|-----------|---------|-------------|
| 1 | Plan de pruebas | `TEST_PLAN.md` | Este documento — estrategia, alcance, cronograma y riesgos |
| 2 | Casos de prueba | `TEST_CASES.md` | Detalle de cada caso de prueba con precondiciones, pasos y resultado esperado |
| 3 | Escenarios Gherkin (BDD) | Archivos `.feature` | Escenarios Cucumber vinculados a criterios de aceptación |
| 4 | Scripts de prueba API | Archivos `.feature` (Karate) | Scripts Karate para validación de endpoints REST |
| 5 | Scripts de rendimiento | Archivos `.js` (k6) | Scripts de carga y estrés para endpoints críticos |
| 6 | Reporte SerenityBDD | HTML generado | Reporte detallado de ejecución de pruebas funcionales con evidencias |
| 7 | Reporte k6 | HTML / JSON | Reporte de métricas de rendimiento (latencia p95, throughput, tasa de error) |
| 8 | Registro de defectos | GitHub Issues | Defectos encontrados con severidad, pasos de reproducción y evidencia |
| 9 | Matriz de trazabilidad | Sección en `TEST_CASES.md` | Mapeo HU → CA → Caso de prueba → Resultado |
| 10 | Informe final de pruebas | Markdown / PDF | Resumen ejecutivo del ciclo: cobertura, métricas, defectos y recomendaciones |

---

## 11. Riesgos y Contingencias

### 11.1 Riesgos de proyecto

| # | Descripción | Impacto | Probabilidad | Riesgo | Mitigación |
|---|-------------|:-------:|:------------:|:------:|------------|
| RP-01 | Estimaciones inexactas por falta de experiencia del equipo en este tipo de proyecto | 3 | 3 | 9 | Estimar en base a story points con revisiones iterativas |
| RP-02 | Retrasos por diferencia horaria entre integrantes del equipo (diferentes países) | 3 | 2 | 6 | Definir horarios de trabajo coincidentes y comunicación asíncrona por chat |
| RP-03 | Ambiente de pruebas no disponible a tiempo | 3 | 2 | 6 | Preparar entorno Docker local como respaldo; automatizar despliegue con GitHub Actions |
| RP-04 | Documentación de API incompleta o desactualizada | 2 | 2 | 4 | Exigir documentación como parte del Definition of Done de DEV; usar Swagger/OpenAPI |

### 11.2 Riesgos de producto

| # | Descripción | Impacto | Probabilidad | Riesgo | Mitigación |
|---|-------------|:-------:|:------------:|:------:|------------|
| RT-01 | El responsive puede romper la UI | 2 | 2 | 4 | Probar la UI en distintas resoluciones y dispositivos |
| RT-02 | Tokens JWT mal configurados permiten acceso no autorizado a endpoints protegidos | 3 | 2 | 6 | Validar expiración, firma y permisos del JWT en pruebas funcionales de cada endpoint protegido |
| RT-03 | Datos sensibles del usuario expuestos en respuestas de la API | 3 | 2 | 6 | Validar schemas de respuesta con Karate para asegurar que no se filtren campos sensibles |
| RT-04 | Registro duplicado de usuarios por condiciones de concurrencia en solicitudes simultáneas | 2 | 2 | 4 | Aplicar restricción UNIQUE en BD sobre el campo correo y manejar el error de duplicado a nivel de servicio |