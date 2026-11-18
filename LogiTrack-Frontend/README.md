# LogiTrack Frontend

Interfaz inicial de LogiTrack S.A. construida con HTML, CSS, JavaScript vanilla y Bootstrap 5.

## 1. Requisitos

- Backend LogiTrack ejecutándose en `http://localhost:8080`.
- MySQL configurado y con datos de prueba.
- Navegador moderno.
- Visual Studio Code con la extensión Live Server (recomendado).

## 2. Ejecutar

1. Descomprime la carpeta.
2. Abre la carpeta `LogiTrack-Frontend` en Visual Studio Code.
3. Abre `login.html` y selecciona **Open with Live Server**.
4. Live Server debe utilizar el puerto `5500` (por ejemplo `http://127.0.0.1:5500/login.html`).
5. Inicia sesión con un usuario activo creado en el backend.

El `SecurityConfig` del backend debe permitir los orígenes `http://127.0.0.1:5500` y `http://localhost:5500`, como está configurado actualmente en el proyecto.

## 3. Conexión con el backend

La URL base está definida en `assets/js/api.js`:

```js
const API_BASE_URL = "http://localhost:8080";
```

Si ejecutas el backend en otro puerto, modifica esa constante.

El inicio de sesión envía exactamente:

```json
{
  "correo": "usuario@logitrack.com",
  "password": "tu contraseña"
}
```

El token devuelto por `/auth/login` se almacena en `localStorage` y se envía como `Authorization: Bearer <token>` en las peticiones protegidas.

## 4. Módulos incluidos

- Dashboard: conteos reales de bodegas, productos, inventarios y movimientos; alertas de stock bajo.
- Bodegas: listar, crear, consultar visualmente, actualizar y eliminar.
- Productos: listar, crear, actualizar y eliminar.
- Inventario: consultar por registros, consultar stock bajo y crear registro inicial.
- Movimientos: listar, filtrar por tipo, consultar detalles y registrar entrada, salida o transferencia con sus productos.
- Auditorías: listar y consultar detalle.
- Reportes: resumen de stock por bodega y productos más movidos.
- Catálogos: ciudades, marcas y categorías.

## 5. Consideraciones importantes del backend actual

- El login devuelve únicamente el JWT. No existe un endpoint de perfil para obtener desde la interfaz el rol o el ID de la persona. Por eso el frontend no inventa ni decodifica permisos: el backend sigue siendo quien autoriza cada operación. Si el usuario no tiene permiso, se muestra el mensaje HTTP 403.
- El endpoint actual de movimientos exige `usuarioResponsableId` en el JSON. Por eso el formulario solicita el ID del responsable. Más adelante conviene obtener ese dato del usuario autenticado en el backend, evitando que el cliente lo envíe.
- Las marcas y categorías son consultables solo por ADMIN según la configuración actual; la creación/edición de productos requiere esos IDs.
- Bootstrap y Bootstrap Icons se cargan desde CDN, así que se necesita conexión a internet para sus estilos e iconos.

## 6. Estructura

```text
LogiTrack-Frontend/
├── index.html
├── login.html
├── README.md
└── assets/
    ├── css/
    │   └── app.css
    └── js/
        ├── api.js
        ├── auth.js
        └── app.js
```

## 7. Seguridad

Este proyecto es una interfaz de demostración académica. La autorización real permanece en Spring Security. Ocultar botones en el frontend no sustituye las reglas de seguridad del backend. No guardes contraseñas en el navegador; solo se conserva el token de sesión.
