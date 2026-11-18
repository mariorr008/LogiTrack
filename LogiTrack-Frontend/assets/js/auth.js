
document.addEventListener("DOMContentLoaded", () => {

    // Si ya existe una sesión, entrar al dashboard.
    if (getToken()) {
        location.href = "index.html";
        return;
    }

    // SECCIONES
    const loginSection = document.getElementById("loginSection");
    const registerSection = document.getElementById("registerSection");

    const showRegister = document.getElementById("showRegister");
    const showLogin = document.getElementById("showLogin");

    // Cambiar entre login y registro.
    showRegister.addEventListener("click", () => {
        loginSection.style.display = "none";
        registerSection.style.display = "block";
    });

    showLogin.addEventListener("click", () => {
        registerSection.style.display = "none";
        loginSection.style.display = "block";
    });

    // =====================================
    // INICIO DE SESIÓN
    // =====================================

    const loginForm = document.getElementById("loginForm");
    const password = document.getElementById("password");
    const toggle = document.getElementById("togglePassword");

    toggle.addEventListener("click", () => {
        const visible = password.type === "text";

        password.type = visible ? "password" : "text";

        toggle.innerHTML = `<i class="bi bi-${visible ? "eye" : "eye-slash"}"></i>`;
    });

    loginForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const correo = document.getElementById("correo").value.trim();
        const pass = password.value;

        if (!correo || !pass) {
            showAlert("Completa el correo y la contraseña.", "warning", "loginAlert");
            return;
        }

        const button = document.getElementById("loginSubmit");

        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Validando...';

        try {

            const result = await API.login({
                correo,
                password: pass
            });

            if (!result || !result.token) {
                throw new Error("El backend no devolvió un token JWT válido.");
            }

            localStorage.setItem("logitrack_token", result.token);
            localStorage.setItem("logitrack_email", correo);

            location.href = "index.html";

        } catch (error) {

            showAlert(
                error.message,
                error.status === 401 ? "danger" : "warning",
                "loginAlert"
            );

        } finally {

            button.disabled = false;
            button.innerHTML = 'Ingresar <i class="bi bi-arrow-right"></i>';

        }
    });

    // =====================================
    // REGISTRO DE EMPLEADOS
    // =====================================

    const registerForm = document.getElementById("registerForm");

    registerForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const nombre = document.getElementById("registerNombre").value.trim();
        const correo = document.getElementById("registerCorreo").value.trim();
        const telefono = document.getElementById("registerTelefono").value.trim();
        const cedula = document.getElementById("registerCedula").value.trim();
        const password = document.getElementById("registerPassword").value;

        if (!nombre || !correo || !telefono || !cedula || !password) {
            showAlert(
                "Completa todos los campos del formulario.",
                "warning",
                "registerAlert"
            );
            return;
        }

        const button = document.getElementById("registerSubmit");

        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Registrando...';

        try {

            const response = await fetch("http://localhost:8080/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    nombre,
                    correo,
                    telefono,
                    cedula,
                    password
                })
            });

            const result = await response.json().catch(() => ({}));

            if (!response.ok) {
                throw new Error(
                    result.message ||
                    result.mensaje ||
                    "No fue posible registrar la cuenta."
                );
            }

            showAlert(
                result.message ||
                result.mensaje ||
                "¡Registro exitoso! Tu cuenta está pendiente de activación por un administrador.",
                "success",
                "registerAlert"
            );

            registerForm.reset();

            // Regresar al login después del registro.
            setTimeout(() => {
                registerSection.style.display = "none";
                loginSection.style.display = "block";
            }, 2500);

        } catch (error) {

            showAlert(
                error.message,
                "danger",
                "registerAlert"
            );

        } finally {

            button.disabled = false;
            button.innerHTML = 'Crear cuenta <i class="bi bi-arrow-right"></i>';

        }
    });

});
