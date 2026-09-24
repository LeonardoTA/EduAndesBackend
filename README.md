# EduAndes Backend

API REST de matrícula académica construida con Java 21, Spring Boot 4, Maven y Oracle. Expone recursos versionados bajo `/api/v1` y documenta el contrato en OpenAPI.

## Arquitectura

El proyecto separa responsabilidades por capas:

- `controller`: contrato HTTP, Bean Validation y códigos de respuesta.
- `service`: reglas de negocio y transacciones. La matrícula valida estado, carrera, vacantes, unicidad por periodo y el límite de 20 créditos antes de guardar cabecera y detalles.
- `repository`: persistencia JPA, filtros y consultas agregadas de reportes.
- `entity`: modelo relacional; `dto`: objetos de entrada/salida sin exponer entidades.
- `config` y `exception`: CORS/OpenAPI y manejo uniforme de errores.

`Matricula` y sus detalles se guardan dentro de una única transacción. Cada detalle conserva créditos y costo históricos; el costo por crédito se configura en `matricula.costo-credito`.

## Perfiles

- `dev` (predeterminado): puerto `8081`, Oracle local, `ddl-auto: update`, SQL y Swagger habilitados.
- `prod`: puerto configurable, `ddl-auto: validate`, credenciales obligatorias por entorno y Swagger deshabilitado.

Variables admitidas: `SPRING_PROFILES_ACTIVE`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y, en producción, `SERVER_PORT`.

## Oracle y datos semilla

La configuración local predeterminada usa `jdbc:oracle:thin:@//localhost:1521/XEPDB1`, usuario `eduandes` y contraseña `eduandes`. Un administrador puede preparar el esquema así:

```sql
CREATE USER eduandes IDENTIFIED BY eduandes;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO eduandes;
ALTER USER eduandes QUOTA UNLIMITED ON USERS;
```

Arranque primero la API para que Hibernate cree las tablas y luego ejecute [datos_semilla.sql](datos_semilla.sql) conectado como `eduandes`. El script limpia los datos funcionales, carga 3 carreras, 12 cursos y 6 estudiantes, y deja los identificadores requeridos por los casos de demostración.

## Ejecución y verificación

Con Oracle disponible:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell use `./mvnw.cmd spring-boot:run`. Después del arranque:

- Health: `http://localhost:8081/api/v1/health`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Pruebas automatizadas: `./mvnw test` (o `./mvnw.cmd test` en Windows)

Importe [postman/EduAndes.postman_collection.json](postman/EduAndes.postman_collection.json) en Postman y ejecute la colección completa, en orden, sobre una base recién cargada. La variable `baseUrl` ya apunta a `http://localhost:8081`; CP06 guarda el identificador de la matrícula que CP13 anula.

Para producción:

```bash
SPRING_PROFILES_ACTIVE=prod DB_URL='jdbc:oracle:thin:@//host:1521/servicio' \
DB_USERNAME='usuario' DB_PASSWORD='secreto' ./mvnw spring-boot:run
```
