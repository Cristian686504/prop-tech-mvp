# Database Migrations and Test Data

Este directorio contiene las migraciones de base de datos usando Flyway y scripts de utilidad para el ambiente de pruebas.

## Migraciones de Esquema

Las migraciones se ejecutan automáticamente al iniciar la aplicación:

| Archivo | Descripción |
|---------|-------------|
| `V1__create_users.sql` | Crea tabla de usuarios con roles LANDLORD/TENANT |
| `V2__create_properties.sql` | Crea tablas de propiedades e imágenes |
| `V3__create_applications.sql` | Crea tabla de solicitudes de alquiler |
| `V4__add_financial_fields_to_users.sql` | Agrega campos financieros para evaluación de riesgo |
| `V5__seed_test_data.sql` | **Datos de prueba**: inserta usuarios y propiedades de ejemplo |

## Datos de Prueba Precargados

La migración `V5__seed_test_data.sql` inserta los siguientes datos automáticamente:

### 👤 Usuarios de Prueba

#### Arrendador
- **Email:** `landlord@test.com`
- **Contraseña:** `Test123!`
- **Nombre:** Juan Pérez
- **Rol:** LANDLORD
- **ID:** `11111111-1111-1111-1111-111111111111`

#### Arrendatario
- **Email:** `tenant@test.com`
- **Contraseña:** `Test123!`
- **Nombre:** María Rodríguez
- **Rol:** TENANT
- **ID:** `22222222-2222-2222-2222-222222222222`
- **Perfil financiero:** Score crediticio 650, Ingreso mensual $4,000,000 COP (Riesgo medio)

> 💡 **Nota:** Ambos usuarios usan la misma contraseña (`Test123!`) para facilitar las pruebas del equipo QA.

### 🏠 Propiedades de Prueba

1. **Apartamento Moderno en Chapinero**
   - Precio: $2,500,000/mes
   - Ubicación: Bogotá
   - Estado: AVAILABLE
   - 3 imágenes de ejemplo

2. **Casa Familiar en Usaquén**
   - Precio: $3,800,000/mes
   - Ubicación: Bogotá
   - Estado: AVAILABLE
   - 3 imágenes de ejemplo

3. **Studio Ejecutivo en El Poblado**
   - Precio: $1,800,000/mes
   - Ubicación: Medellín
   - Estado: AVAILABLE
   - 3 imágenes de ejemplo

## Resetear la Base de Datos de Pruebas

> 📖 **Guía rápida visual:** [RESET_GUIDE.md](../RESET_GUIDE.md) — Tabla de decisión y ejemplos

Existen **3 formas** de resetear la base de datos según tu necesidad:

### Opción 1: Reset Rápido con Datos (Recomendado para QA) ⚡

**Cuándo usarlo:** Entre ciclos de prueba cuando necesitas un estado limpio con datos precargados.

```bash
# Linux/Mac (desde raíz del proyecto)
docker exec -i proptech-db psql -U proptech < backend/applications/app-service/src/main/resources/db/reset_and_seed_test_data.sql

# Windows PowerShell (desde raíz del proyecto)
Get-Content backend/applications/app-service/src/main/resources/db/reset_and_seed_test_data.sql | docker exec -i proptech-db psql -U proptech
```

**Resultado:**
- ✅ Limpia todas las tablas
- ✅ Recarga 2 usuarios de prueba
- ✅ Recarga 3 propiedades
- ⚡ **Rápido:** ~1-2 segundos

### Opción 2: Reset Solo (Sin Recargar Datos)

**Cuándo usarlo:** Cuando quieres una BD completamente vacía o vas a cargar tus propios datos.

```bash
# Linux/Mac
docker exec -i proptech-db psql -U proptech < backend/applications/app-service/src/main/resources/db/reset_test_data.sql

# Windows PowerShell
Get-Content backend/applications/app-service/src/main/resources/db/reset_test_data.sql | docker exec -i proptech-db psql -U proptech
```

**Resultado:**
- ✅ Limpia todas las tablas
- ⚠️ Base de datos vacía (sin usuarios ni propiedades)

### Opción 3: Recrear Contenedor (Más Limpio pero Lento) 🔄

**Cuándo usarlo:** Cuando hay problemas de esquema, Flyway, o necesitas garantizar un estado 100% limpio.

```bash
# Detener y eliminar el contenedor de base de datos
docker-compose down -v

# Levantar nuevamente (recreará la BD con todas las migraciones)
docker-compose up -d
```

**Resultado:**
- ✅ Esquema recreado desde cero
- ✅ Todas las migraciones (V1-V5) ejecutadas
- ✅ Datos de prueba cargados
- ⏱️ **Lento:** ~30-60 segundos

### Opción 4: Flyway Clean + Migrate (Para Desarrollo)

**Cuándo usarlo:** Durante desarrollo cuando cambias migraciones SQL.

```bash
cd backend
./gradlew flywayClean flywayMigrate
```

**Resultado:**
- ✅ Borra todo el esquema
- ✅ Re-ejecuta todas las migraciones
- ✅ Datos de prueba cargados
- ⏱️ Tiempo: ~10-15 segundos

## Configuración de Entorno

Los datos de prueba (V5) **solo deben usarse en ambientes de prueba y desarrollo**, nunca en producción.

Para desactivar la migración V5 en producción, puedes:

1. Configurar el perfil de Spring para que Flyway ignore ciertos archivos
2. Eliminar físicamente el archivo `V5__seed_test_data.sql` en el build de producción
3. Usar la propiedad `flyway.locations` para excluir los seed scripts

## Testing con Datos Precargados

Los datos precargados permiten ejecutar pruebas E2E sin necesidad de crear usuarios y propiedades manualmente:

```javascript
// Ejemplo de test usando datos precargados
describe('Login con usuario de prueba', () => {
  it('debe autenticar al landlord', async () => {
    const response = await request(API_URL)
      .post('/api/auth/login')
      .send({
        email: 'landlord@test.com',
        password: 'Test123!'
      });
    
    expect(response.status).toBe(200);
    expect(response.body.role).toBe('LANDLORD');
  });
});
```

## Verificar Datos Cargados

Puedes verificar que los datos se insertaron correctamente con:

```sql
-- Ver usuarios de prueba
SELECT name, email, role FROM users;

-- Ver propiedades de prueba
SELECT title, price, status FROM properties;

-- Contar registros
SELECT 
  (SELECT COUNT(*) FROM users) as users_count,
  (SELECT COUNT(*) FROM properties) as properties_count,
  (SELECT COUNT(*) FROM property_images) as images_count;
```

Deberías ver:
- 2 usuarios (1 landlord, 1 tenant)
- 3 propiedades
- 9 imágenes (3 por propiedad)

## Troubleshooting

### "La migración V5 no se ejecuta"

- Verifica que Flyway esté habilitado en `application.yaml` (`spring.flyway.enabled: true`)
- Revisa que la migración no haya sido ejecutada previamente (tabla `flyway_schema_history`)
- Si la migración ya se ejecutó y quieres re-ejecutarla, usa `flywayClean` primero

### "Error de constraint al insertar datos"

- Verifica que las migraciones V1-V4 se hayan ejecutado correctamente
- Revisa que los UUIDs no estén duplicados en tu base de datos
- Confirma que los emails no existan previamente

### "Las contraseñas no funcionan"

- Las contraseñas están hasheadas con BCrypt (costo factor 10)
- Usa exactamente: `Test123!` (case-sensitive) para ambos usuarios
- El hash actual es: `$2a$10$K6etRva0G/5Pzmzf.ZHLc.0NJkPSVaLVw7h6mSq6LTDgB/aj3TLsq`
- Si necesitas regenerar el hash:
  1. Registra un usuario temporal con contraseña `Test123!`
  2. Extrae el hash: `SELECT password_hash FROM users WHERE email = 'temp@test.com';`
  3. Actualiza V5__seed_test_data.sql con el nuevo hash
  4. Recrea la base de datos con `docker-compose down -v && docker-compose up -d`

### "Los usuarios existen pero no puedo hacer login"

Si ya ejecutaste la migración V5 pero el hash es incorrecto:

```bash
# Opción 1: Actualizar directamente en la BD (más rápido)
docker exec -it proptech-db psql -U proptech -c "UPDATE users SET password_hash = '\$2a\$10\$K6etRva0G/5Pzmzf.ZHLc.0NJkPSVaLVw7h6mSq6LTDgB/aj3TLsq' WHERE email IN ('landlord@test.com', 'tenant@test.com');"

# Opción 2: Recrear completamente la BD
docker-compose down -v
docker-compose up -d
```

## Referencias

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [TEST_PLAN.md - Sección 6.2](../../../TEST_PLAN.md#62-datos-del-entorno)
- [BCrypt Password Hash Generator Test](../../test/java/co/com/proptech/util/GeneratePasswordHashTest.java) — Utilidad para regenerar hashes
