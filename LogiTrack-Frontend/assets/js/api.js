
/* Cliente HTTP centralizado para la API de LogiTrack. */

const API_BASE_URL = "http://localhost:8080";

function getToken() {
    return localStorage.getItem("logitrack_token");
}

function clearSession() {
    localStorage.removeItem("logitrack_token");
    localStorage.removeItem("logitrack_email");
}

async function apiFetch(path, options = {}) {

    const headers = new Headers(options.headers || {});
    const token = getToken();

    if (token) {
        headers.set("Authorization", `Bearer ${token}`);
    }

    if (options.body && !(options.body instanceof FormData)) {
        headers.set("Content-Type", "application/json");
    }

    headers.set("Accept", "application/json");

    let response;

    try {
        response = await fetch(`${API_BASE_URL}${path}`, {
            ...options,
            headers
        });
    } catch (error) {
        throw new Error(
            "No fue posible conectar con el backend. Verifica que Spring Boot esté ejecutándose en localhost:8080 y que CORS permita el origen del frontend."
        );
    }

    if (response.status === 401) {
        clearSession();

        if (!location.pathname.endsWith("login.html")) {
            location.href = "login.html";
        }

        throw new Error(
            "La sesión expiró o no es válida. Inicia sesión nuevamente."
        );
    }

    if (response.status === 204) {
        return null;
    }

    const raw = await response.text();

    let data = null;

    if (raw) {
        try {
            data = JSON.parse(raw);
        } catch {
            data = raw;
        }
    }

    if (!response.ok) {

        const message =
            data && typeof data === "object"
                ? data.message || data.error || data.mensaje
                : data;

        const error = new Error(
            message || `Error HTTP ${response.status}`
        );

        error.status = response.status;
        error.data = data;

        throw error;
    }

    return data;
}

// =====================================
// MÉTODOS DE CONEXIÓN CON LA API
// =====================================

const API = {

    // AUTENTICACIÓN

    login: (body) =>
        apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify(body)
        }),

    register: (body) =>
        apiFetch("/auth/register", {
            method: "POST",
            body: JSON.stringify(body)
        }),

    pendingEmployees: () =>
        apiFetch("/auth/pendientes"),

    activateEmployee: (id) =>
        apiFetch(`/auth/${id}/activar`, {
            method: "PUT"
        }),

    // CRUD GENERAL

    list: (resource) =>
        apiFetch(`/${resource}`),

    get: (resource, id) =>
        apiFetch(`/${resource}/${id}`),

    create: (resource, body) =>
        apiFetch(`/${resource}`, {
            method: "POST",
            body: JSON.stringify(body)
        }),

    update: (resource, id, body) =>
        apiFetch(`/${resource}/${id}`, {
            method: "PUT",
            body: JSON.stringify(body)
        }),

    remove: (resource, id) =>
        apiFetch(`/${resource}/${id}`, {
            method: "DELETE"
        }),

    // INVENTARIO

    lowStock: () =>
        apiFetch("/inventarios/stock-bajo"),

    // MOVIMIENTOS

    movementsByType: (type) =>
        apiFetch(`/movimientos/tipo/${encodeURIComponent(type)}`),

    movementsByDate: (start, end) =>
        apiFetch(
            `/movimientos/fecha?inicio=${encodeURIComponent(start)}&fin=${encodeURIComponent(end)}`
        ),

    movementsByUser: (id) =>
        apiFetch(`/movimientos/usuario/${encodeURIComponent(id)}`),

    // REPORTES

    report: () =>
        apiFetch("/reportes/resumen")
};

// =====================================
// FUNCIONES AUXILIARES
// =====================================

function escapeHtml(value) {

    return String(value ?? "").replace(/[&<>"']/g, char => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[char]);

}

function showAlert(message, type = "danger", targetId = "alertArea") {

    const target = document.getElementById(targetId);

    if (!target) return;

    target.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show app-alert" role="alert">
            ${escapeHtml(message)}
            <button type="button" class="btn-close"
                data-bs-dismiss="alert" aria-label="Cerrar">
            </button>
        </div>
    `;
}
