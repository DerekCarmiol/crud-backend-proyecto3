# Proyecto 3 — Tarea 1: CRUD Back-end

API REST con **Spring Boot 3.5**, **Spring Data JPA**, **Spring Security con JWT** y **MySQL**, que implementa dos CRUD completos: **Producto** y **Categoría**, con control de acceso por roles.

## Tecnologías

- Java 21
- Spring Boot 3.5.6 (Web, Data JPA, Security, Validation)
- MySQL 8
- JWT (io.jsonwebtoken / jjwt 0.12)
- Maven (con wrapper `mvnw`)

## Modelo de datos

- **Categoría**: `id`, `nombre`, `descripcion`.
- **Producto**: `id`, `nombre`, `descripcion`, `precio`, `cantidadEnStock`, `categoria`.
- Un producto pertenece a **una** categoría; una categoría puede tener **uno o más** productos (relación `ManyToOne`).
- **Rol**: `SUPER-ADMIN-ROLE` y `USER`.
- **Usuario**: `email`, `password` (encriptada con BCrypt), `rol`.

## Roles y reglas de acceso

| Acción | USER | SUPER-ADMIN-ROLE |
|---|:---:|:---:|
| Listar / consultar Productos y Categorías | ✅ (autenticado) | ✅ |
| Registrar / Actualizar / Borrar Productos y Categorías | ❌ (403) | ✅ |

## Usuarios precargados

Se insertan automáticamente al arrancar el proyecto (contraseña encriptada en la base de datos):

| Email | Contraseña | Rol |
|---|---|---|
| `superadmin@proyecto3.com` | `admin123` | `SUPER-ADMIN-ROLE` |
| `user@proyecto3.com` | `user123` | `USER` |

> No existe endpoint de registro de usuarios: los usuarios ya están creados al arrancar.
> No se crea ninguna categoría previamente.

## Base de datos

Por defecto se conecta a `jdbc:mysql://localhost:3306/proyecto3_db` con usuario `root` / `Derek123`.
Ajuste las credenciales en [`src/main/resources/application.properties`](src/main/resources/application.properties) según su instalación.

### Opción A — Docker (recomendado)

```bash
docker compose up -d
```

Levanta MySQL 8 en el puerto `3306` y crea la base `proyecto3_db`.

### Opción B — MySQL/MariaDB local

Cree la base de datos (o deje que se cree sola gracias a `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE proyecto3_db;
```

## Cómo ejecutar

Desde IntelliJ: abra la carpeta del proyecto y ejecute la clase `CrudBackendApplication`.

Desde la terminal:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Endpoints

### Autenticación
- `POST /auth/login` — body `{ "email": "...", "password": "..." }` → devuelve `{ token, email, rol }`.

En las peticiones protegidas se envía el header: `Authorization: Bearer <token>`.

### Categorías (`/api/categorias`)
- `GET /api/categorias` — listar (autenticado)
- `GET /api/categorias/{id}` — obtener (autenticado)
- `POST /api/categorias` — crear (SUPER-ADMIN-ROLE)
- `PUT /api/categorias/{id}` — actualizar (SUPER-ADMIN-ROLE)
- `DELETE /api/categorias/{id}` — borrar (SUPER-ADMIN-ROLE)

### Productos (`/api/productos`)
- `GET /api/productos` — listar (autenticado)
- `GET /api/productos/{id}` — obtener (autenticado)
- `POST /api/productos` — crear (SUPER-ADMIN-ROLE)
- `PUT /api/productos/{id}` — actualizar (SUPER-ADMIN-ROLE)
- `DELETE /api/productos/{id}` — borrar (SUPER-ADMIN-ROLE)

## Pruebas con Insomnia

Importe el archivo [`insomnia_collection.json`](insomnia_collection.json) en Insomnia (Application → Import → From File).

La colección incluye:
- **01 - Autenticación**: Login como SUPER-ADMIN y como USER.
- **02 - Categorías**: listar, obtener, registrar, actualizar y borrar.
- **03 - Productos**: listar, obtener, registrar, actualizar y borrar.
- **04 - Demostración de roles**: el USER puede listar (200) pero recibe 403 al intentar crear.

El token JWT se inyecta automáticamente en cada petición a partir de la respuesta del login correspondiente (encadenamiento de respuestas de Insomnia). Si su versión de Insomnia no resuelve el encadenamiento, ejecute primero el login y copie el `token` en el header `Authorization`.

> Sugerencia de flujo: primero ejecute **Registrar Categoría**, luego **Registrar Producto** (usa `categoriaId: 1`).
