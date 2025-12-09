document.addEventListener('DOMContentLoaded', function() {
    
    // ==========================================
    // 1. LÓGICA DE MENÚS DESPLEGABLES (DROPDOWNS)
    // ==========================================
    const customDropdowns = document.querySelectorAll('.custom-dropdown');

    customDropdowns.forEach(dropdown => {
        const select = dropdown.querySelector('select');
        const selectedDisplay = dropdown.querySelector('.dropdown-selected span');
        const optionsList = dropdown.querySelector('.dropdown-options');
        
        // Generar la lista bonita basada en las opciones del <select> original
        Array.from(select.options).forEach(option => {
            if (option.disabled) return; // Ignorar el placeholder

            const li = document.createElement('li');
            li.textContent = option.textContent;
            li.setAttribute('data-value', option.value);
            
            // Si hay algo seleccionado por defecto (ej: tras un error de servidor)
            if (option.selected) {
                li.classList.add('selected');
                selectedDisplay.textContent = option.textContent;
                // Si ya está seleccionado, que se vea oscuro
                selectedDisplay.style.color = '#2c3e50'; 
            }
            
            // --- EVENTO AL HACER CLICK EN UNA OPCIÓN ---
            li.addEventListener('click', function() {
                // 1. Actualizar texto visual
                selectedDisplay.textContent = this.textContent;
                
                // 2. CAMBIO CLAVE: Poner el texto oscuro (ya no gris) para que se vea lleno
                selectedDisplay.style.color = '#2c3e50'; 
                
                // 3. Actualizar valor real del select oculto
                select.value = this.getAttribute('data-value');
                
                // 4. Estilos visuales de la lista
                optionsList.querySelectorAll('li').forEach(item => item.classList.remove('selected'));
                this.classList.add('selected');
                
                // 5. Cerrar menú
                dropdown.classList.remove('active');
            });
            
            optionsList.appendChild(li);
        });

        // Abrir/Cerrar menú al hacer click en la caja
        dropdown.querySelector('.dropdown-selected').addEventListener('click', function(e) {
            customDropdowns.forEach(d => {
                if (d !== dropdown) d.classList.remove('active');
            });
            dropdown.classList.toggle('active');
            e.stopPropagation(); 
        });
    });

    // Cerrar menú si se hace clic fuera
    document.addEventListener('click', function(e) {
        customDropdowns.forEach(dropdown => {
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove('active');
            }
        });
    });


    // ==========================================
    // 2. VALIDACIÓN DEL DOCUMENTO (MIN 6 DÍGITOS)
    // ==========================================
    const formulario = document.getElementById('formularioRegistro');
    
    if (formulario) {
        formulario.addEventListener('submit', function(event) {
            const docInput = document.getElementById('docInput');
            const docError = document.getElementById('docError');
            
            // Validamos solo si el input existe y tiene menos de 6 caracteres
            if (docInput && docInput.value.length < 6) {
                event.preventDefault(); // Detiene el envío del formulario
                if(docError) docError.style.display = 'block';
                docInput.style.borderColor = '#e74c3c'; // Borde rojo
                docInput.focus();
            } else {
                if(docError) docError.style.display = 'none';
                if(docInput) docInput.style.borderColor = '#bdc3c7';
            }
        });
    }
});


// ==========================================
// 3. VALIDACIÓN DE CONTRASEÑAS EN TIEMPO REAL
// (Esta función se llama desde el HTML con onkeyup)
// ==========================================
function validarContrasenas() {
    const pass1 = document.getElementById('password');
    const pass2 = document.getElementById('confirm_password');
    const mensaje = document.getElementById('mensajeError');
    const boton = document.getElementById('botonRegistrar');

    // Seguridad: Si los elementos no existen, no hacemos nada
    if (!pass1 || !pass2 || !mensaje || !boton) return;

    if (pass2.value.length > 0) {
        if (pass1.value !== pass2.value) {
            // No coinciden
            mensaje.style.display = 'block';
            pass2.style.borderColor = '#e74c3c';
            boton.disabled = true;
            boton.style.opacity = '0.5';
        } else {
            // Sí coinciden
            mensaje.style.display = 'none';
            pass2.style.borderColor = '#2ecc71'; // Verde
            boton.disabled = false;
            boton.style.opacity = '1';
        }
    } else {
        // Campo vacío (reseteo)
        mensaje.style.display = 'none';
        pass2.style.borderColor = '#bdc3c7';
        boton.disabled = false;
        boton.style.opacity = '1';
    }
}