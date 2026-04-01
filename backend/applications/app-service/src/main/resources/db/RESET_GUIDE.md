# 🔄 Guía Rápida: Reset de Base de Datos para Pruebas

Esta guía te ayuda a elegir el método correcto para resetear la base de datos según tu situación.

---

## 🎯 ¿Qué Método Usar?

| Situación | Comando | Tiempo | ¿Recarga Datos? |
|-----------|---------|:------:|:---------------:|
| **Entre ciclos de prueba (uso diario QA)** | `reset_and_seed_test_data.sql` | ~2s | ✅ Sí |
| **Quiero BD vacía (sin datos)** | `reset_test_data.sql` | ~1s | ❌ No |
| **Problemas de esquema o Flyway** | `docker-compose down -v` | ~60s | ✅ Sí |
| **Cambié migraciones SQL** | `./gradlew flywayClean flywayMigrate` | ~15s | ✅ Sí |

---

## 📋 Comandos Completos

### 1️⃣ Reset Rápido + Recarga Datos (⚡ Recomendado)

**Windows PowerShell:**
```powershell
Get-Content backend/applications/app-service/src/main/resources/db/reset_and_seed_test_data.sql | docker exec -i proptech-db psql -U proptech
```

**Linux/Mac:**
```bash
docker exec -i proptech-db psql -U proptech < backend/applications/app-service/src/main/resources/db/reset_and_seed_test_data.sql
```

**✅ Resultado:**
- 2 usuarios (landlord + tenant)
- 3 propiedades con imágenes
- Listo para pruebas inmediatas

---

### 2️⃣ Solo Limpiar (Sin Recargar)

**Windows PowerShell:**
```powershell
Get-Content backend/applications/app-service/src/main/resources/db/reset_test_data.sql | docker exec -i proptech-db psql -U proptech
```

**Linux/Mac:**
```bash
docker exec -i proptech-db psql -U proptech < backend/applications/app-service/src/main/resources/db/reset_test_data.sql
```

**⚠️ Resultado:**
- Base de datos vacía
- Necesitarás crear usuarios y datos manualmente

---

### 3️⃣ Recrear TODO (Más Lento pero Seguro)

```bash
docker-compose down -v
docker-compose up -d
```

**🔄 Resultado:**
- Borra todo (volúmenes, contenedores)
- Recrea esquema desde cero
- Ejecuta todas las migraciones (V1-V5)
- Datos de prueba cargados

---

### 4️⃣ Flyway Clean + Migrate (Para Desarrollo)

```bash
cd backend
./gradlew flywayClean flywayMigrate
```

**🛠️ Resultado:**
- Borra esquema completo
- Re-ejecuta todas las migraciones
- Útil cuando cambias archivos SQL

---

## 🧪 Verificar que Funcionó

Después de cualquier reset, verifica:

```bash
docker exec -it proptech-db psql -U proptech -c "SELECT COUNT(*) FROM users; SELECT COUNT(*) FROM properties;"
```

**Resultado esperado:**
- `users`: 2 (si usaste reset_and_seed)
- `properties`: 3 (si usaste reset_and_seed)
- `users`: 0 (si solo limpiaste)

---

## 🔐 Credenciales de Prueba

Después de reset con datos:

| Usuario | Email | Contraseña | Rol |
|---------|-------|-----------|-----|
| Arrendador | `landlord@test.com` | `Test123!` | LANDLORD |
| Arrendatario | `tenant@test.com` | `Test123!` | TENANT |

---

## 📖 Más Información

- [README completo de migraciones](README.md)
- [TEST_PLAN.md - Sección 6.2](../../../TEST_PLAN.md#62-datos-del-entorno)

---

**💡 Tip:** Agrega un alias en tu shell para el comando más usado:

```bash
# Bash/Zsh (~/.bashrc o ~/.zshrc)
alias reset-db='docker exec -i proptech-db psql -U proptech < backend/applications/app-service/src/main/resources/db/reset_and_seed_test_data.sql'

# PowerShell ($PROFILE)
function Reset-PropTechDB { Get-Content backend/applications/app-service/src/main/resources/db/reset_and_seed_test_data.sql | docker exec -i proptech-db psql -U proptech }
Set-Alias reset-db Reset-PropTechDB
```

Luego simplemente ejecuta: `reset-db`
