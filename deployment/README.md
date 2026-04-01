# PropTech MVP - Production Deployment

Este directorio contiene la configuración para desplegar la aplicación usando las **imágenes pre-construidas** de GitHub Container Registry.

## 🚀 Quick Start

### 1. Configurar variables de entorno

```bash
cp .env.example .env
# Edita .env con tus valores reales
```

### 2. Pull de las últimas imágenes

```bash
docker-compose pull
```

### 3. Iniciar servicios

```bash
docker-compose up -d
```

### 4. Verificar estado

```bash
docker-compose ps
docker-compose logs -f
```

## 📦 Imágenes utilizadas

- **Backend**: `ghcr.io/cristian686504/proptech-backend:latest`
- **Frontend**: `ghcr.io/cristian686504/proptech-frontend:latest`
- **Database**: `postgres:16-alpine`

## 🔄 Actualizar a la última versión

```bash
# 1. Pull de nuevas imágenes
docker-compose pull

# 2. Recrear contenedores
docker-compose up -d

# 3. Limpiar imágenes antiguas (opcional)
docker image prune -f
```

## 🛠️ Comandos útiles

### Ver logs en tiempo real
```bash
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Reiniciar un servicio
```bash
docker-compose restart backend
```

### Detener todos los servicios
```bash
docker-compose down
```

### Detener y eliminar volúmenes (⚠️ BORRA LA BASE DE DATOS)
```bash
docker-compose down -v
```

## 🔐 Variables de entorno requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DATABASE_PASSWORD` | Contraseña de PostgreSQL | `P@ssw0rd123!` |
| `JWT_SECRET` | Secret key para JWT (mínimo 256 bits) | `your-secret-key-here-min-256-bits` |
| `DATABASE_USER` | Usuario de PostgreSQL (opcional) | `proptech` |
| `SPRING_PROFILES_ACTIVE` | Profile de Spring (opcional) | `default` |

## 📊 Health Checks

- **Backend**: http://localhost:8080/actuator/health
- **Frontend**: http://localhost
- **Database**: `docker-compose exec postgres pg_isready -U proptech`

## 🌐 Acceso a la aplicación

Una vez iniciado:
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432

## ⚙️ Configuración avanzada

### Cambiar puertos

Edita el archivo `docker-compose.yml` en la sección `ports`:

```yaml
frontend:
  ports:
    - "3000:80"  # Cambia 80 a tu puerto deseado
```

### Usar una versión específica

En lugar de `:latest`, puedes usar un SHA específico:

```yaml
backend:
  image: ghcr.io/cristian686504/proptech-backend:eb850b229e372d0272c597b52786331733bb69de
```

## 🐛 Troubleshooting

### Las imágenes no se encuentran
```bash
# Verificar que las imágenes existen
docker pull ghcr.io/cristian686504/proptech-backend:latest
```

### Backend no conecta a la base de datos
```bash
# Verificar que PostgreSQL está listo
docker-compose logs postgres

# Verificar variables de entorno
docker-compose exec backend env | grep DATABASE
```

### Frontend no carga
```bash
# Verificar logs de nginx
docker-compose logs frontend

# Verificar que el backend está corriendo
curl http://localhost:8080/actuator/health
```

## 📝 Notas

- Las imágenes se construyen automáticamente en el pipeline de CI/CD cuando se hace merge a `main`
- Los volúmenes persisten la base de datos y los archivos subidos
- Las imágenes son públicas en GitHub Container Registry
