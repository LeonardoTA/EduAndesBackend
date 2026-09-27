# EduAndes Backend

API REST para la matrícula académica de EduAndes, desarrollada con Java 21, Spring Boot 4, Maven y Oracle.

## Componentes

- CRUD de carreras, cursos y estudiantes.
- Matrículas con detalle, validación de vacantes, carrera, periodo y límite de 20 créditos.
- Búsqueda de cursos y reporte de matriculados por curso.
- DTO, mappers, validación, transacciones, manejo uniforme de errores, CORS y OpenAPI.

## Ejecución

El perfil `dev` es el predeterminado. Usa el puerto `8081` y Oracle en `jdbc:oracle:thin:@//localhost:1522/FREEPDB1` con las variables `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

## Recursos

- Health: `GET /api/v1/health`
- Carreras: `/api/v1/carreras`
- Cursos: `/api/v1/cursos` y `/api/v1/cursos/buscar`
- Estudiantes: `/api/v1/estudiantes`
- Matrículas: `/api/v1/matriculas`
- Reporte: `GET /api/v1/reportes/matriculados-por-curso`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

Ejecuta [datos_semilla.sql](datos_semilla.sql) sobre Oracle antes de probar la colección de [Postman](postman/EduAndes.postman_collection.json).

## Pruebas

```bash
./mvnw test
```

En producción, activa el perfil `prod`; requiere `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y, opcionalmente, `SERVER_PORT`.
