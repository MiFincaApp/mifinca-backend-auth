# Backend-mifinca
API REST en Spring Boot para MiFincaApp, una app que conecta a campesinos y compradores. Incluye autenticación JWT, control de roles, manejo de usuarios y productos, integración con Wompi y Cloudflare R2. Diseñada para seguridad, rendimiento y despliegue en producción.

# Backend MiFincaApp

Este es el backend de MiFincaApp, una API REST desarrollada con Spring Boot para gestionar usuarios y autenticación mediante JWT. El sistema implementa control de acceso por roles (admin, campesino, comprador), validación de IP, dominios permitidos y cabeceras personalizadas para mayor seguridad.

## Funcionalidades

- Autenticación con JWT
- Registro y login de usuarios
- Validación de roles y acceso por perfil
- Filtros personalizados por IP, dominio y cabeceras
- Seguridad con BCrypt y configuración CORS

## Tecnologías

- Java 21
- Spring Boot
- MySQL
- Seguridad JWT

## Variables de entorno

Este proyecto utiliza un archivo `.env` (excluido del repositorio mediante `.gitignore`) para definir variables sensibles de configuración. A continuación se describen las variables requeridas:

### Configuración de base de datos

- `DB_URL`: URL de conexión a la base de datos MySQL.
- `DB_USERNAME`: Usuario para la base de datos.
- `DB_PASSWORD`: Contraseña del usuario de base de datos.

### Configuración CORS

- `CORS_ALLOWED_ORIGINS`: Lista de dominios permitidos para hacer peticiones (separados por comas).
- `ALLOWED_METHODS`: Métodos HTTP permitidos (GET, POST, etc).

### Validación de cabeceras y origen

- `CUSTOM_HEADER_NAME`: Nombre de la cabecera personalizada que se espera en las peticiones.
- `VALOR_CUSTOM_HEADER`: Valor esperado en la cabecera personalizada.
- `DOMINIOSPERMITIDOS`: Lista de dominios válidos desde donde puede accederse a la API.
- `IP_PERMITIDAS`: Lista de direcciones IP autorizadas para acceder a rutas restringidas.

### Configuración de JWT

- `JWT_SECRET`: Clave secreta usada para firmar los tokens JWT.
- `JWT_EXPIRATION_MS`: Tiempo de expiración del token en milisegundos.

> Estas variables deben estar correctamente configuradas antes del despliegue. Consulta al equipo de desarrollo para más detalles o configuración en entornos de producción.
