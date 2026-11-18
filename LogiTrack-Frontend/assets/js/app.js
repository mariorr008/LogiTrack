
const state = { view: "dashboard", modal: null, cache: {} };
const root = document.getElementById("viewRoot");
const modalElement = document.getElementById("appModal");
const modal = new bootstrap.Modal(modalElement);

const labels = {
  dashboard: "Dashboard",
  bodegas: "Bodegas",
  productos: "Productos",
  inventarios: "Inventario",
  movimientos: "Movimientos",
  auditorias: "Auditorías",
  reportes: "Reportes",
  usuarios: "Usuarios",
  ciudades: "Ciudades",
  marcas: "Marcas",
  categorias: "Categorías"
};

document.addEventListener("DOMContentLoaded", async () => {
  if (!getToken()) {
    location.href = "login.html";
    return;
  }

  document.getElementById("userEmail").textContent =
    localStorage.getItem("logitrack_email") || "Sesión activa";

  document.getElementById("logoutBtn").addEventListener("click", () => {
    clearSession();
    location.href = "login.html";
  });

  document.getElementById("mobileMenu").addEventListener("click", () => {
    document.getElementById("sidebar").classList.toggle("sidebar-open");
  });

  document.getElementById("sideNav").addEventListener("click", event => {
    const button = event.target.closest("[data-view]");
    if (!button) return;

    navigate(button.dataset.view);
    document.getElementById("sidebar").classList.remove("sidebar-open");
  });

  document.getElementById("modalSaveBtn").addEventListener("click", saveModal);

  await navigate("dashboard");
});

async function navigate(view) {
  state.view = view;

  document.querySelectorAll(".side-nav [data-view]").forEach(el => {
    el.classList.toggle("active", el.dataset.view === view);
  });

  document.getElementById("topbarTitle").textContent =
    labels[view] || "LogiTrack";

  root.innerHTML = `
    <div class="loading-state">
      <span class="spinner-border"></span>
      <span>Cargando información...</span>
    </div>
  `;

  try {
    if (view === "dashboard") return await renderDashboard();
    if (view === "bodegas") return await renderCrud("bodegas");
    if (view === "productos") return await renderCrud("productos");
    if (view === "inventarios") return await renderInventories();
    if (view === "movimientos") return await renderMovements();
    if (view === "auditorias") return await renderAudits();
    if (view === "reportes") return await renderReports();
    if (view === "usuarios") return await renderUsers();

    if (["ciudades", "marcas", "categorias"].includes(view)) {
      return await renderCrud(view);
    }
  } catch (error) {
    root.innerHTML = errorPanel(error);
  }
}

function pageHeading(kicker, title, description, action = "") {
  return `
    <div class="page-heading">
      <div>
        <div class="eyebrow">${kicker}</div>
        <h1>${title}</h1>
        <p>${description}</p>
      </div>
      ${action}
    </div>
  `;
}

function errorPanel(error) {
  const forbidden = error.status === 403;

  return `
    <div class="empty-panel">
      <div class="empty-icon">
        <i class="bi bi-${forbidden ? "lock" : "exclamation-triangle"}"></i>
      </div>
      <h3>${forbidden ? "Acceso restringido" : "No se pudo cargar la información"}</h3>
      <p>${escapeHtml(error.message)}</p>
      ${
        forbidden
          ? '<small>Tu usuario no tiene permisos para consultar este módulo. Los permisos se validan en el backend.</small>'
          : '<button class="btn btn-outline-primary mt-3" onclick="navigate(state.view)">Reintentar</button>'
      }
    </div>
  `;
}

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);

  return Number.isNaN(date.getTime())
    ? escapeHtml(value)
    : date.toLocaleString("es-CO");
}

function money(value) {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    maximumFractionDigits: 2
  }).format(Number(value || 0));
}

function table(headers, rows, empty = "No hay registros para mostrar.") {
  return `
    <div class="table-wrap">
      <table class="table app-table">
        <thead>
          <tr>${headers.map(h => `<th>${h}</th>`).join("")}</tr>
        </thead>
        <tbody>
          ${
            rows.length
              ? rows.join("")
              : `<tr><td colspan="${headers.length}" class="table-empty">${empty}</td></tr>`
          }
        </tbody>
      </table>
    </div>
  `;
}

function pill(text) {
  const value = String(text || "—");

  const cls =
    value === "ACTIVO" || value === "ENTRADA" ? "success" :
    value === "AGOTADO" || value === "SALIDA" ? "warning" :
    value === "INACTIVO" ? "secondary" : "neutral";

  return `<span class="status-pill ${cls}">${escapeHtml(value)}</span>`;
}

function actions(id, canEdit = true) {
  return `
    <div class="row-actions">
      ${
        canEdit
          ? `<button class="icon-btn" title="Editar" data-action="edit" data-id="${id}">
               <i class="bi bi-pencil"></i>
             </button>`
          : ""
      }
      <button class="icon-btn danger" title="Eliminar" data-action="delete" data-id="${id}">
        <i class="bi bi-trash3"></i>
      </button>
    </div>
  `;
}

// DASHBOARD

async function renderDashboard() {
  const [bodegas, productos, inventarios, movimientos] = await Promise.all([
    API.list("bodegas"),
    API.list("productos"),
    API.list("inventarios"),
    API.list("movimientos")
  ]);

  const [lowStock, report] = await Promise.all([
    API.lowStock().catch(() => []),
    API.report().catch(() => null)
  ]);

  state.cache.dashboard = {
    bodegas, productos, inventarios, movimientos, lowStock, report
  };

  const stockTotal = (inventarios || []).reduce(
    (sum, item) => sum + Number(item.stock || 0),
    0
  );

  const cards = [
    ["Bodegas registradas", bodegas.length, "bi-buildings", "blue"],
    ["Productos registrados", productos.length, "bi-box2", "green"],
    ["Unidades en inventario", stockTotal.toLocaleString("es-CO"), "bi-boxes", "gold"],
    ["Movimientos registrados", movimientos.length, "bi-arrow-left-right", "purple"]
  ];

  const lowRows = (lowStock || []).slice(0, 6).map(x => `
    <tr>
      <td>
        <strong>${escapeHtml(x.productoNombre || "Producto")}</strong>
        <small class="d-block text-muted">ID ${escapeHtml(x.productoId)}</small>
      </td>
      <td>${escapeHtml(x.bodegaNombre || "—")}</td>
      <td>${pill(x.estado || "—")}</td>
      <td><strong>${Number(x.stock || 0)}</strong></td>
    </tr>
  `);

  const recent = [...(movimientos || [])]
    .sort((a, b) => new Date(b.fecha) - new Date(a.fecha))
    .slice(0, 5)
    .map(x => `
      <tr>
        <td><span class="table-id">#${escapeHtml(x.id)}</span></td>
        <td>${pill(x.tipoMovimiento)}</td>
        <td>${formatDate(x.fecha)}</td>
        <td>${(x.detalles || []).length} producto(s)</td>
      </tr>
    `);

  root.innerHTML = `
    ${pageHeading(
      "VISTA GENERAL",
      "Resumen operativo",
      "Un vistazo al estado actual de tu operación de inventario.",
      '<button class="btn btn-primary" onclick="navigate(\'movimientos\')"><i class="bi bi-plus-lg me-2"></i>Registrar movimiento</button>'
    )}

    <div class="stats-grid">
      ${cards.map(c => `
        <div class="stat-card">
          <div class="stat-icon ${c[3]}"><i class="bi ${c[2]}"></i></div>
          <div class="stat-label">${c[0]}</div>
          <div class="stat-value">${c[1]}</div>
          <div class="stat-foot">Datos consultados desde la API</div>
        </div>
      `).join("")}
    </div>

    <div class="dashboard-grid">
      <section class="panel-card">
        <div class="panel-heading">
          <div>
            <h3>Stock bajo</h3>
            <p>Productos con menos de 10 unidades</p>
          </div>
          <button class="btn btn-sm btn-outline-primary" onclick="navigate('inventarios')">Ver inventario</button>
        </div>
        ${table(["Producto", "Bodega", "Estado", "Stock"], lowRows, "No se encontraron productos con stock bajo.")}
      </section>

      <section class="panel-card">
        <div class="panel-heading">
          <div>
            <h3>Actividad reciente</h3>
            <p>Últimos movimientos registrados</p>
          </div>
          <button class="btn btn-sm btn-outline-primary" onclick="navigate('movimientos')">Ver todos</button>
        </div>
        ${table(["ID", "Tipo", "Fecha", "Detalles"], recent, "Aún no hay movimientos registrados.")}
      </section>
    </div>

    <div class="info-strip">
      <i class="bi bi-info-circle"></i>
      <div>
        <strong>Conexión en tiempo real</strong>
        <span>Las cifras se obtienen consultando los endpoints de LogiTrack. Actualiza la página para recargar los datos.</span>
      </div>
      <button class="btn btn-sm btn-light" onclick="navigate('dashboard')">
        <i class="bi bi-arrow-clockwise me-1"></i>Actualizar
      </button>
    </div>
  `;
}

// CONFIGURACIÓN CRUD

const crudConfig = {
  bodegas: {
    title: "Bodegas",
    desc: "Administra los espacios de almacenamiento y su capacidad.",
    resource: "bodegas",
    headers: ["ID", "Nombre", "Ciudad ID", "Dirección", "Capacidad", "Encargado ID", "Acciones"],

    map: x => `
      <tr>
        <td class="table-id">${x.id}</td>
        <td><strong>${escapeHtml(x.nombre)}</strong></td>
        <td>${x.ciudadId ?? "—"}</td>
        <td>${escapeHtml(x.direccion)}</td>
        <td>${x.capacidad ?? "—"}</td>
        <td>${x.encargadoId ?? "—"}</td>
        <td>${actions(x.id)}</td>
      </tr>
    `,

    fields: [
      { name: "nombre", label: "Nombre de la bodega", required: true },
      { name: "ciudadId", label: "ID de ciudad", type: "number", required: true },
      { name: "direccion", label: "Dirección", required: true },
      { name: "capacidad", label: "Capacidad (unidades)", type: "number", required: true },
      { name: "encargadoId", label: "ID del encargado (opcional)", type: "number" }
    ],

    body: f => ({
      nombre: f.nombre,
      ciudadId: Number(f.ciudadId),
      direccion: f.direccion,
      capacidad: Number(f.capacidad),
      encargadoId: f.encargadoId ? Number(f.encargadoId) : null
    })
  },

  productos: {
    title: "Productos",
    desc: "Gestiona el catálogo de productos de la organización.",
    resource: "productos",
    headers: ["ID", "Producto", "Marca", "Categoría", "Precio", "Acciones"],

    map: x => `
      <tr>
        <td class="table-id">${x.id}</td>
        <td><strong>${escapeHtml(x.nombre)}</strong></td>
        <td>${escapeHtml(x.marcaNombre || x.marcaId)}</td>
        <td>${escapeHtml(x.categoriaNombre || x.categoriaId)}</td>
        <td>${money(x.precio)}</td>
        <td>${actions(x.id)}</td>
      </tr>
    `,

    fields: [
      { name: "nombre", label: "Nombre del producto", required: true },
      { name: "marcaId", label: "ID de marca", type: "number", required: true },
      { name: "categoriaId", label: "ID de categoría", type: "number", required: true },
      { name: "precio", label: "Precio", type: "number", step: "0.01", required: true }
    ],

    body: f => ({
      nombre: f.nombre,
      marcaId: Number(f.marcaId),
      categoriaId: Number(f.categoriaId),
      precio: Number(f.precio)
    })
  },

  ciudades: {
    title: "Ciudades",
    desc: "Catálogo de ciudades asociadas a las bodegas.",
    resource: "ciudades",
    headers: ["ID", "Nombre", "Acciones"],
    map: x => `
      <tr>
        <td class="table-id">${x.id}</td>
        <td><strong>${escapeHtml(x.nombre)}</strong></td>
        <td>${actions(x.id)}</td>
      </tr>
    `,
    fields: [{ name: "nombre", label: "Nombre de la ciudad", required: true }],
    body: f => ({ nombre: f.nombre })
  },

  marcas: {
    title: "Marcas",
    desc: "Administra las marcas de los productos.",
    resource: "marcas",
    headers: ["ID", "Nombre", "Acciones"],
    map: x => `
      <tr>
        <td class="table-id">${x.id}</td>
        <td><strong>${escapeHtml(x.nombre)}</strong></td>
        <td>${actions(x.id)}</td>
      </tr>
    `,
    fields: [{ name: "nombre", label: "Nombre de la marca", required: true }],
    body: f => ({ nombre: f.nombre })
  },

  categorias: {
    title: "Categorías",
    desc: "Organiza los productos por categoría.",
    resource: "categorias",
    headers: ["ID", "Nombre", "Acciones"],
    map: x => `
      <tr>
        <td class="table-id">${x.id}</td>
        <td><strong>${escapeHtml(x.nombre)}</strong></td>
        <td>${actions(x.id)}</td>
      </tr>
    `,
    fields: [{ name: "nombre", label: "Nombre de la categoría", required: true }],
    body: f => ({ nombre: f.nombre })
  }
};

// CRUD

async function renderCrud(view) {
  const cfg = crudConfig[view];
  const data = await API.list(cfg.resource);

  state.cache[view] = data;

  const canCreate = view !== "ciudades" || true;

  root.innerHTML = `
    ${pageHeading(
      "GESTIÓN DE DATOS",
      cfg.title,
      cfg.desc,
      `<button class="btn btn-primary" onclick="openCrudModal('${view}')"><i class="bi bi-plus-lg me-2"></i>Nuevo registro</button>`
    )}

    <section class="panel-card">
      <div class="panel-heading">
        <div>
          <h3>Listado de ${cfg.title.toLowerCase()}</h3>
          <p>${data.length} registro(s) encontrados</p>
        </div>
        <button class="btn btn-sm btn-outline-primary" onclick="navigate('${view}')">
          <i class="bi bi-arrow-clockwise me-1"></i>Actualizar
        </button>
      </div>

      ${table(cfg.headers, data.map(cfg.map))}
    </section>
  `;

  root.querySelectorAll("[data-action]").forEach(btn => {
    btn.addEventListener("click", () => {
      handleCrudAction(view, Number(btn.dataset.id), btn.dataset.action);
    });
  });
}

async function handleCrudAction(view, id, action) {
  const cfg = crudConfig[view];

  if (action === "edit") {
    const item = (state.cache[view] || []).find(x => x.id === id);
    if (item) openCrudModal(view, item);

  } else if (action === "delete") {
    if (!confirm(`¿Seguro que deseas eliminar el registro #${id}?`)) return;

    try {
      await API.remove(cfg.resource, id);
      showAlert("Registro eliminado correctamente.", "success");
      await navigate(view);
    } catch (e) {
      showAlert(e.message);
    }
  }
}

function openCrudModal(view, item = null) {
  const cfg = crudConfig[view];

  state.modal = {
    type: "crud",
    view,
    id: item?.id
  };

  document.getElementById("modalTitle").textContent =
    item ? `Editar ${cfg.title.slice(0, -1).toLowerCase()}` : "Nuevo registro";

  document.getElementById("modalBody").innerHTML = `
    <form id="entityForm" class="row g-3">
      ${cfg.fields.map(field => {
        const val = item?.[field.name] ?? "";

        return `
          <div class="col-md-6">
            <label class="form-label">${field.label}</label>
            <input
              class="form-control"
              name="${field.name}"
              type="${field.type || "text"}"
              ${field.step ? `step="${field.step}"` : ""}
              ${field.required ? "required" : ""}
              value="${escapeHtml(val)}"
            >
          </div>
        `;
      }).join("")}
    </form>
  `;

  modal.show();
}

// INVENTARIOS

async function renderInventories() {
  const [data, low] = await Promise.all([
    API.list("inventarios"),
    API.lowStock().catch(() => [])
  ]);

  state.cache.inventarios = data;

  const rows = data.map(x => `
    <tr>
      <td class="table-id">${x.id}</td>
      <td>${escapeHtml(x.productoNombre)} <small class="d-block text-muted">#${x.productoId}</small></td>
      <td>${escapeHtml(x.bodegaNombre)} <small class="d-block text-muted">#${x.bodegaId}</small></td>
      <td><strong>${x.stock}</strong></td>
      <td>${pill(x.estado)}</td>
    </tr>
  `);

  const lowRows = low.map(x => `
    <tr>
      <td>${escapeHtml(x.productoNombre)}</td>
      <td>${escapeHtml(x.bodegaNombre)}</td>
      <td>${x.stock}</td>
      <td>${pill(x.estado)}</td>
    </tr>
  `);

  root.innerHTML = `
    ${pageHeading(
      "CONTROL DE EXISTENCIAS",
      "Inventario por bodega",
      "Consulta las existencias registradas y detecta niveles bajos.",
      '<button class="btn btn-primary" onclick="openInventoryModal()"><i class="bi bi-plus-lg me-2"></i>Crear registro</button>'
    )}

    <div class="mini-stats">
      <div><span>Registros</span><strong>${data.length}</strong></div>
      <div><span>Stock total</span><strong>${data.reduce((s, x) => s + Number(x.stock || 0), 0).toLocaleString("es-CO")}</strong></div>
      <div><span>Stock bajo</span><strong>${low.length}</strong></div>
    </div>

    <section class="panel-card">
      <div class="panel-heading">
        <div>
          <h3>Existencias</h3>
          <p>Stock por producto y bodega</p>
        </div>
        <button class="btn btn-sm btn-outline-primary" onclick="navigate('inventarios')">Actualizar</button>
      </div>
      ${table(["ID", "Producto", "Bodega", "Stock", "Estado"], rows)}
    </section>

    <section class="panel-card mt-4">
      <div class="panel-heading">
        <div>
          <h3>Alertas de stock bajo</h3>
          <p>El backend identifica registros con menos de 10 unidades</p>
        </div>
      </div>
      ${table(["Producto", "Bodega", "Stock", "Estado"], lowRows, "No hay alertas de stock bajo.")}
    </section>
  `;
}

function openInventoryModal() {
  state.modal = { type: "inventory" };

  document.getElementById("modalTitle").textContent =
    "Crear registro de inventario";

  document.getElementById("modalBody").innerHTML = `
    <form id="entityForm" class="row g-3">
      <div class="col-md-6">
        <label class="form-label">ID de bodega</label>
        <input class="form-control" name="bodegaId" type="number" min="1" required>
      </div>

      <div class="col-md-6">
        <label class="form-label">ID de producto</label>
        <input class="form-control" name="productoId" type="number" min="1" required>
      </div>

      <div class="col-md-6">
        <label class="form-label">Stock inicial</label>
        <input class="form-control" name="stock" type="number" min="1" required>
      </div>

      <div class="col-12 form-hint">
        El stock inicial debe ser mayor que cero. Los cambios posteriores se realizan mediante movimientos.
      </div>
    </form>
  `;

  modal.show();
}

// MOVIMIENTOS

async function renderMovements() {
  const data = await API.list("movimientos");
  state.cache.movimientos = data;

  const rows = data.map(x => `
    <tr>
      <td class="table-id">#${x.id}</td>
      <td>${pill(x.tipoMovimiento)}</td>
      <td>${formatDate(x.fecha)}</td>
      <td>${x.bodegaOrigenId ?? "—"}</td>
      <td>${x.bodegaDestinoId ?? "—"}</td>
      <td>${(x.detalles || []).length} producto(s)</td>
      <td>
        <button class="icon-btn" title="Ver detalles" onclick="showMovement(${x.id})">
          <i class="bi bi-eye"></i>
        </button>
      </td>
    </tr>
  `);

  root.innerHTML = `
    ${pageHeading(
      "TRAZABILIDAD",
      "Movimientos",
      "Registra entradas, salidas y transferencias de inventario.",
      '<button class="btn btn-primary" onclick="openMovementModal()"><i class="bi bi-plus-lg me-2"></i>Registrar movimiento</button>'
    )}

    <section class="panel-card">
      <div class="panel-heading">
        <div>
          <h3>Historial de movimientos</h3>
          <p>${data.length} movimiento(s) registrados</p>
        </div>

        <div class="d-flex gap-2">
          <select class="form-select form-select-sm" id="movementFilter" style="width:170px">
            <option value="">Todos los tipos</option>
            <option>ENTRADA</option>
            <option>SALIDA</option>
            <option>TRANSFERENCIA</option>
          </select>

          <button class="btn btn-sm btn-outline-primary" onclick="filterMovements()">Filtrar</button>
        </div>
      </div>

      ${table(["ID", "Tipo", "Fecha", "Bodega origen", "Bodega destino", "Detalles", ""], rows)}
    </section>
  `;
}

async function filterMovements() {
  const type = document.getElementById("movementFilter").value;

  try {
    const data = type
      ? await API.movementsByType(type)
      : await API.list("movimientos");

    const rows = data.map(x => `
      <tr>
        <td class="table-id">#${x.id}</td>
        <td>${pill(x.tipoMovimiento)}</td>
        <td>${formatDate(x.fecha)}</td>
        <td>${x.bodegaOrigenId ?? "—"}</td>
        <td>${x.bodegaDestinoId ?? "—"}</td>
        <td>${(x.detalles || []).length} producto(s)</td>
        <td>
          <button class="icon-btn" onclick="showMovement(${x.id})">
            <i class="bi bi-eye"></i>
          </button>
        </td>
      </tr>
    `);

    document.querySelector("#viewRoot .app-table tbody").innerHTML =
      rows.length
        ? rows.join("")
        : '<tr><td colspan="7" class="table-empty">Sin resultados.</td></tr>';

  } catch (e) {
    showAlert(e.message);
  }
}

function openMovementModal() {
  state.modal = { type: "movement" };

  document.getElementById("modalTitle").textContent =
    "Registrar movimiento";

  document.getElementById("modalBody").innerHTML = `
    <form id="entityForm" class="row g-3">

      <div class="col-md-6">
        <label class="form-label">Tipo de movimiento</label>
        <select class="form-select" name="tipoMovimiento" id="movementType" required>
          <option value="ENTRADA">Entrada</option>
          <option value="SALIDA">Salida</option>
          <option value="TRANSFERENCIA">Transferencia</option>
        </select>
      </div>

      <div class="col-md-6">
        <label class="form-label">ID usuario responsable</label>
        <input class="form-control" name="usuarioResponsableId" type="number" min="1" required>
        <div class="form-text">El endpoint actual solicita este ID en el cuerpo.</div>
      </div>

      <div class="col-md-6">
        <label class="form-label">ID bodega origen</label>
        <input class="form-control" name="bodegaOrigenId" type="number" min="1" placeholder="Vacío para entrada">
      </div>

      <div class="col-md-6">
        <label class="form-label">ID bodega destino</label>
        <input class="form-control" name="bodegaDestinoId" type="number" min="1" placeholder="Vacío para salida">
      </div>

      <div class="col-12">
        <div class="d-flex align-items-center justify-content-between">
          <label class="form-label mb-0">Detalle de productos</label>
          <button type="button" class="btn btn-sm btn-outline-primary" id="addDetail">
            <i class="bi bi-plus"></i> Agregar producto
          </button>
        </div>

        <div id="movementDetails" class="movement-details mt-2">
          ${detailRow()}
        </div>
      </div>

      <div class="col-12 form-hint">
        Entrada: bodega destino. Salida: bodega origen. Transferencia: ambas bodegas.
      </div>
    </form>
  `;

  document.getElementById("addDetail").addEventListener("click", () => {
    document.getElementById("movementDetails").insertAdjacentHTML(
      "beforeend",
      detailRow()
    );
  });

  modal.show();
}

function detailRow() {
  return `
    <div class="detail-row">
      <input class="form-control" name="productoId" type="number" min="1" placeholder="ID producto" required>
      <input class="form-control" name="cantidad" type="number" min="1" placeholder="Cantidad" required>
      <button type="button" class="icon-btn danger" onclick="this.parentElement.remove()">
        <i class="bi bi-x-lg"></i>
      </button>
    </div>
  `;
}

async function showMovement(id) {
  try {
    const x = await API.get("movimientos", id);

    document.getElementById("modalTitle").textContent =
      `Movimiento #${id}`;

    document.getElementById("modalBody").innerHTML = `
      <div class="detail-summary">
        <div><span>Tipo</span>${pill(x.tipoMovimiento)}</div>
        <div><span>Fecha</span><strong>${formatDate(x.fecha)}</strong></div>
        <div><span>Bodega origen</span><strong>${x.bodegaOrigenId ?? "—"}</strong></div>
        <div><span>Bodega destino</span><strong>${x.bodegaDestinoId ?? "—"}</strong></div>
      </div>

      ${table(
        ["Producto", "ID", "Cantidad"],
        (x.detalles || []).map(d => `
          <tr>
            <td>${escapeHtml(d.productoNombre)}</td>
            <td>${d.productoId}</td>
            <td>${d.cantidad}</td>
          </tr>
        `)
      )}
    `;

    document.getElementById("modalSaveBtn").style.display = "none";
    modal.show();

  } catch (e) {
    showAlert(e.message);
  }
}

// AUDITORÍAS

async function renderAudits() {
  const data = await API.list("auditorias");
  state.cache.auditorias = data;

  const rows = data.map(x => `
    <tr>
      <td class="table-id">#${x.id}</td>
      <td>${formatDate(x.fechaHora)}</td>
      <td>${pill(x.tipoOperacion)}</td>
      <td>${escapeHtml(x.usuarioNombre || x.usuario?.nombre || x.usuarioId || "—")}</td>
      <td>${escapeHtml(x.entidadAfectada)}</td>
      <td>${escapeHtml(x.registroAfectadoId)}</td>
      <td>
        <button class="icon-btn" onclick="showAudit(${x.id})">
          <i class="bi bi-eye"></i>
        </button>
      </td>
    </tr>
  `);

  root.innerHTML = `
    ${pageHeading(
      "SEGUIMIENTO Y CONTROL",
      "Auditorías",
      "Consulta el historial de operaciones registradas por el sistema.",
      '<button class="btn btn-sm btn-outline-primary" onclick="navigate(\'auditorias\')"><i class="bi bi-arrow-clockwise me-1"></i>Actualizar</button>'
    )}

    <section class="panel-card">
      <div class="panel-heading">
        <div>
          <h3>Registro de auditoría</h3>
          <p>${data.length} evento(s) encontrados</p>
        </div>
      </div>

      ${table(["ID", "Fecha", "Operación", "Usuario", "Entidad", "Registro", ""], rows)}
    </section>
  `;
}

async function showAudit(id) {
  try {
    const x = await API.get("auditorias", id);

    document.getElementById("modalTitle").textContent =
      `Auditoría #${id}`;

    document.getElementById("modalBody").innerHTML = `
      <div class="detail-summary">
        <div><span>Fecha</span><strong>${formatDate(x.fechaHora)}</strong></div>
        <div><span>Operación</span>${pill(x.tipoOperacion)}</div>
        <div><span>Entidad</span><strong>${escapeHtml(x.entidadAfectada)}</strong></div>
        <div><span>ID afectado</span><strong>${escapeHtml(x.registroAfectadoId)}</strong></div>
      </div>

      <label class="form-label mt-3">Valores anteriores</label>
      <pre class="json-view">${escapeHtml(prettyJson(x.valoresAnteriores))}</pre>

      <label class="form-label">Valores nuevos</label>
      <pre class="json-view">${escapeHtml(prettyJson(x.valoresNuevos))}</pre>
    `;

    document.getElementById("modalSaveBtn").style.display = "none";
    modal.show();

  } catch (e) {
    showAlert(e.message);
  }
}

function prettyJson(value) {
  if (!value) return "Sin información";

  try {
    return JSON.stringify(
      typeof value === "string" ? JSON.parse(value) : value,
      null,
      2
    );
  } catch {
    return String(value);
  }
}

// REPORTES

async function renderReports() {
  const report = await API.report();

  const stocks = report.stockPorBodega || [];
  const top = report.productosMasMovidos || [];

  const max = Math.max(
    1,
    ...stocks.map(x => Number(x.stockTotal || 0))
  );

  const stockBars = stocks.map(x => `
    <div class="bar-row">
      <div class="bar-label">
        <span>${escapeHtml(x.bodegaNombre)}</span>
        <strong>${x.stockTotal}</strong>
      </div>
      <div class="bar-track">
        <div class="bar-fill" style="width:${Math.max(2, Number(x.stockTotal || 0) / max * 100)}%"></div>
      </div>
    </div>
  `).join("");

  const rows = top.map((x, i) => `
    <tr>
      <td><span class="rank-number">${i + 1}</span></td>
      <td><strong>${escapeHtml(x.productoNombre)}</strong></td>
      <td>${x.productoId}</td>
      <td><strong>${x.cantidadMovida}</strong></td>
    </tr>
  `);

  root.innerHTML = `
    ${pageHeading(
      "ANÁLISIS",
      "Reportes generales",
      "Indicadores consolidados de existencias y rotación de productos.",
      '<button class="btn btn-outline-primary" onclick="navigate(\'reportes\')"><i class="bi bi-arrow-clockwise me-1"></i>Actualizar</button>'
    )}

    <div class="report-grid">
      <section class="panel-card">
        <div class="panel-heading">
          <div>
            <h3>Stock por bodega</h3>
            <p>Unidades registradas en cada bodega</p>
          </div>
        </div>
        <div class="bars-container">
          ${stockBars || '<div class="table-empty">No hay información disponible.</div>'}
        </div>
      </section>

      <section class="panel-card">
        <div class="panel-heading">
          <div>
            <h3>Productos más movidos</h3>
            <p>Ordenados por cantidad acumulada</p>
          </div>
        </div>
        ${table(["Posición", "Producto", "ID", "Cantidad"], rows, "No hay movimientos para calcular el ranking.")}
      </section>
    </div>
  `;
}

// GESTIÓN DE USUARIOS

async function renderUsers() {
  const data = await API.pendingEmployees();
  state.cache.usuarios = data;

  const rows = data.map(user => `
    <tr>
      <td class="table-id">${escapeHtml(user.id)}</td>
      <td><strong>${escapeHtml(user.nombre)}</strong></td>
      <td>${escapeHtml(user.correo)}</td>
      <td>${escapeHtml(user.telefono)}</td>
      <td>${escapeHtml(user.cedula)}</td>
      <td>${pill(user.estado)}</td>
      <td>
        <button
          class="btn btn-sm btn-primary"
          data-action="activate"
          data-id="${escapeHtml(user.id)}">
          <i class="bi bi-check-circle me-1"></i>Activar
        </button>
      </td>
    </tr>
  `);

  root.innerHTML = `
    ${pageHeading(
      "ADMINISTRACIÓN",
      "Gestión de usuarios",
      "Consulta las solicitudes de registro y activa las cuentas de los empleados."
    )}

    <div class="mini-stats">
      <div>
        <span>Usuarios pendientes</span>
        <strong>${data.length}</strong>
      </div>
    </div>

    <section class="panel-card">
      <div class="panel-heading">
        <div>
          <h3>Solicitudes de registro</h3>
          <p>Empleados que esperan activación para acceder al sistema.</p>
        </div>

        <button class="btn btn-sm btn-outline-primary" onclick="navigate('usuarios')">
          <i class="bi bi-arrow-clockwise me-1"></i>Actualizar
        </button>
      </div>

      ${table(
        ["ID", "Nombre", "Correo", "Teléfono", "Cédula", "Estado", "Acciones"],
        rows,
        "No hay empleados pendientes de activación."
      )}
    </section>
  `;

  root.querySelectorAll('[data-action="activate"]').forEach(button => {
    button.addEventListener("click", async () => {
      const id = Number(button.dataset.id);
      const user = data.find(item => Number(item.id) === id);

      if (!user) return;

      const confirmed = confirm(
        `¿Deseas activar la cuenta de ${user.nombre}?`
      );

      if (!confirmed) return;

      button.disabled = true;

      try {
        await API.activateEmployee(id);

        showAlert(
          `La cuenta de ${user.nombre} fue activada correctamente.`,
          "success"
        );

        await renderUsers();

      } catch (error) {
        showAlert(error.message, "danger");
        button.disabled = false;
      }
    });
  });
}

// GUARDAR FORMULARIOS

async function saveModal() {
  const form = document.getElementById("entityForm");

  if (!form) {
    modal.hide();
    return;
  }

  if (!form.reportValidity()) return;

  const values = Object.fromEntries(new FormData(form).entries());

  const button = document.getElementById("modalSaveBtn");

  button.disabled = true;
  button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Guardando...';

  try {
    const cfg = state.modal;

    if (cfg.type === "crud") {
      const config = crudConfig[cfg.view];
      const body = config.body(values);

      if (cfg.id) {
        await API.update(config.resource, cfg.id, body);
      } else {
        await API.create(config.resource, body);
      }

      modal.hide();
      showAlert("Información guardada correctamente.", "success");
      await navigate(cfg.view);

    } else if (cfg.type === "inventory") {
      await API.create("inventarios", {
        bodegaId: Number(values.bodegaId),
        productoId: Number(values.productoId),
        stock: Number(values.stock)
      });

      modal.hide();
      showAlert("Registro de inventario creado.", "success");
      await navigate("inventarios");

    } else if (cfg.type === "movement") {
      const details = [...form.querySelectorAll(".detail-row")].map(row => ({
        productoId: Number(row.querySelector('[name="productoId"]').value),
        cantidad: Number(row.querySelector('[name="cantidad"]').value)
      }));

      const body = {
        tipoMovimiento: values.tipoMovimiento,
        usuarioResponsableId: Number(values.usuarioResponsableId),
        bodegaOrigenId: values.bodegaOrigenId
          ? Number(values.bodegaOrigenId)
          : null,
        bodegaDestinoId: values.bodegaDestinoId
          ? Number(values.bodegaDestinoId)
          : null,
        detalles: details
      };

      await API.create("movimientos", body);

      modal.hide();
      showAlert("Movimiento registrado correctamente.", "success");
      await navigate("movimientos");
    }

  } catch (error) {
    showAlert(error.message, "danger");

  } finally {
    button.disabled = false;
    button.textContent = "Guardar";
  }
}

modalElement.addEventListener("hidden.bs.modal", () => {
  document.getElementById("modalSaveBtn").style.display = "";
});
