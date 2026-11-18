# 📦 LogiTrack S.A. — Sistema de Gestión de Bodegas e Inventarios

**LogiTrack** es un sistema de gestión de bodegas e inventarios desarrollado con Java y Spring Boot. Su objetivo es centralizar la administración de productos, bodegas y movimientos de inventario, proporcionando trazabilidad de las operaciones y control de acceso mediante autenticación JWT.

El proyecto busca reemplazar los procesos manuales de control de inventario por una solución backend organizada, segura y escalable, con una API REST que permite administrar la información y consultar reportes.

---

## 🎯 Objetivo del proyecto

Desarrollar un sistema que permita gestionar las operaciones de inventario de diferentes bodegas, registrar entradas, salidas y transferencias de productos, y mantener un historial auditable de los cambios realizados por los usuarios.

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje de programación |
| Spring Boot | Desarrollo del backend |
| Spring Security | Seguridad y autorización |
| JWT | Autenticación mediante tokens |
| Spring Data JPA | Persistencia y acceso a datos |
| Hibernate | Mapeo objeto-relacional |
| MySQL | Base de datos relacional |
| Swagger / OpenAPI 3 | Documentación de la API |
| HTML5 | Estructura del frontend |
| CSS3 | Estilos e interfaz |
| JavaScript | Consumo de la API y lógica del frontend |
| Bootstrap 5 | Diseño de componentes e interfaz |
| Postman | Pruebas de endpoints REST |
| Git y GitHub | Control de versiones |

## ⚙️ Funcionalidades principales

### 1. Gestión de bodegas
- Registrar, consultar, actualizar y eliminar bodegas.
- Asociar bodegas con sus respectivas ciudades y encargados.
- Controlar la capacidad de almacenamiento.

### 2. Gestión de productos
- CRUD completo de productos.
- Administración de marcas y categorías.
- Consulta de información y disponibilidad de productos.

### 3. Gestión de inventarios
- Control de existencias por producto y bodega.
- Consulta de inventarios registrados.
- Identificación de productos con stock bajo.
- Control de estados del inventario.

### 4. Movimientos de inventario
- Registro de entradas de productos.
- Registro de salidas.
- Transferencias entre bodegas.
- Registro de cantidades, fechas y responsables.
- Actualización de existencias según el movimiento realizado.

### 5. Auditoría
- Registro de operaciones realizadas en el sistema.
- Identificación del usuario responsable.
- Consulta de auditorías por usuario, tipo de operación y entidad afectada.
- Seguimiento de los cambios realizados en la información.

### 6. Autenticación y autorización
- Registro e inicio de sesión.
- Autenticación mediante JWT.
- Contraseñas protegidas con BCrypt.
- Control de acceso basado en roles:
    - **ADMIN:** administración y control general del sistema.
    - **EMPLEADO:** acceso a las operaciones permitidas para su rol.
- Registro de empleados con estado pendiente de activación por un administrador.

### 7. Consultas y reportes
- Consulta de productos con menos de 10 unidades.
- Búsqueda de movimientos por rangos de fechas.
- Consulta de auditorías.
- Reporte general de existencias por bodega y productos con mayor movimiento.

### 8. Documentación y validaciones
- Documentación de endpoints mediante Swagger/OpenAPI 3.
- Validación de datos de entrada.
- Manejo global de excepciones.
- Respuestas de error estructuradas en formato JSON.

---

## 🏗️ Arquitectura del proyecto

El backend está organizado mediante una arquitectura por capas, separando las responsabilidades para facilitar el mantenimiento, las pruebas y la escalabilidad.

```text
src/
├── main/
│   ├── java/com/proyecto/LogiTrack/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   │       └── impl/
│   │
│   └── resources/
│       ├── application.properties
│       ├── schema.sql
│       └── data.sql
│
└── test/
```

### Responsabilidades de las capas

- **Controller:** recibe las solicitudes HTTP y devuelve las respuestas.
- **Service:** contiene la lógica de negocio.
- **Repository:** gestiona las operaciones de acceso a la base de datos.
- **Model:** contiene las entidades y modelos del sistema.
- **DTO:** define los objetos utilizados para recibir y enviar información.
- **Security:** gestiona JWT, autenticación y autorización.
- **Config:** contiene las configuraciones generales.
- **Exception:** centraliza el manejo de errores.

---

## 🔐 Seguridad

LogiTrack utiliza Spring Security y JSON Web Tokens (JWT) para proteger los recursos de la API.

El proceso de autenticación funciona de la siguiente manera:

1. El usuario envía sus credenciales al endpoint de inicio de sesión.
2. El backend valida el correo y la contraseña.
3. Si las credenciales son correctas y la cuenta está activa, se genera un token JWT.
4. El cliente utiliza el token para acceder a los endpoints protegidos.
5. Spring Security verifica la autenticación y los permisos correspondientes al rol del usuario.

---

## 🚀 Instalación y ejecución

### Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java JDK 21.
- MySQL.
- Maven o el Maven Wrapper incluido en el proyecto.
- Un IDE como IntelliJ IDEA, Eclipse o Visual Studio Code.

### 1. Clonar el repositorio

```bash
git clone https://github.com/mariorr008/LogiTrack.git
```

Ingresar a la carpeta:

```bash
cd LogiTrack
```

### 2. Configurar la base de datos

Crear una base de datos en MySQL y configurar las credenciales de conexión en `application.properties` o mediante las variables de entorno utilizadas por el proyecto.

Ejemplo de configuración:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logitrack
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

El proyecto utiliza scripts SQL para la estructura y los datos iniciales de la base de datos.

### 3. Configurar las variables de entorno

Configurar las variables necesarias para la conexión con MySQL y la clave secreta JWT, de acuerdo con los nombres definidos en `application.properties`.

No se deben publicar contraseñas ni claves secretas en el repositorio.

### 4. Ejecutar el backend

Desde la raíz del proyecto, ejecutar:

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

El backend estará disponible en:

```text
http://localhost:8080
```

### 5. Acceder a la documentación

Con el servidor en ejecución, ingresar a:

```text
http://localhost:8080/swagger-ui/index.html
```

Desde Swagger se pueden consultar y probar los endpoints disponibles.

### 6. Ejecutar el frontend

Abrir la carpeta del frontend con Visual Studio Code y ejecutar `index.html` mediante la extensión Live Server.

La configuración de CORS contempla los siguientes orígenes locales:

```text
http://127.0.0.1:5500
http://localhost:5500
```

---

## 🧪 Pruebas

Durante el desarrollo se utiliza Postman para comprobar el funcionamiento de los endpoints REST.

Las pruebas contemplan:

- Registro e inicio de sesión.
- Validación de JWT.
- Control de permisos según el rol.
- Operaciones CRUD.
- Movimientos de inventario.
- Consultas y reportes.
- Manejo de errores y validaciones.

---

## 📌 Consideraciones del sistema

- Los usuarios registrados como empleados deben ser activados por un administrador antes de iniciar sesión.
- Las operaciones de inventario deben realizarse mediante los movimientos correspondientes para conservar la trazabilidad.
- Los movimientos registrados forman parte del historial de operaciones.
- El acceso a los recursos está condicionado por los permisos del usuario autenticado.

---

## 👨‍💻 Autor

**Mario Alberto Rojas**

Estudiante de desarrollo de software en Campusland.

Interesado en el desarrollo backend con Java, Spring Boot, bases de datos y automatización.

- GitHub: [@mariorr008](https://github.com/mariorr008)
- Proyecto: [LogiTrack](https://github.com/mariorr008/LogiTrack)

---

*LogiTrack S.A. — Gestión, control y trazabilidad de inventarios.*