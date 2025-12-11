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

// Variable global para almacenar las categorías
// Estructura: categories['combate']['peso'] = ['-60kg', '-70kg']
let categories = {
    combate: { peso: [], rango: [], edad: [] },
    figuras: { rango: [], edad: [] },
    defensa: { nivel: [] },
    demo: { cat: [] }
};

/* ================= NAVEGACIÓN Y VALIDACIÓN ================= */
function showStep(stepNumber) {
    // 1. Ocultar todos los pasos
    document.querySelectorAll('.wizard-step').forEach(el => el.classList.remove('active-step'));
    document.querySelectorAll('.step').forEach(el => el.classList.remove('active'));
    
    // 2. Mostrar paso actual
    document.getElementById('step-' + stepNumber).classList.add('active-step');
    
    // 3. Activar bolitas
    for(let i = 1; i <= stepNumber; i++) {
        document.getElementById('indicator-' + i).classList.add('active');
    }
}

function nextStep(targetStep) {
    let currentStepNum = targetStep - 1;
    
    // VALIDACIÓN DEL PASO ACTUAL
    if (!validateStep(currentStepNum)) {
        Swal.fire({
            icon: 'warning',
            title: 'Faltan datos',
            text: 'Por favor completa los campos obligatorios antes de continuar.',
            confirmButtonColor: '#b71c1c'
        });
        return; // Detener si falla
    }

    // Si vamos al paso 4 (Resumen), generamos el HTML
    if (targetStep === 4) {
        generateSummary();
    }

    showStep(targetStep);
}

function prevStep(step) {
    showStep(step);
}

function validateStep(stepNum) {
    let isValid = true;
    let currentStepDiv = document.getElementById('step-' + stepNum);
    
    // Buscar inputs requeridos VISIBLES en el paso actual
    let requiredInputs = currentStepDiv.querySelectorAll('input[required]');
    
    requiredInputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.classList.add('input-error'); // Poner rojo
        } else {
            input.classList.remove('input-error');
        }
    });

    // Validación Específica Paso 2: Al menos una modalidad
    if (stepNum === 2) {
        let checkedModalities = document.querySelectorAll('input[name="modalidades"]:checked');
        if (checkedModalities.length === 0) {
            isValid = false;
        }
    }

    return isValid;
}

/* ================= GESTIÓN DE CATEGORÍAS (ADD/REMOVE) ================= */

// Función para añadir item (ej: 50kg) a la lista
function addItem(modalidad, tipo) {
    let inputId = `input-${modalidad}-${tipo}`;
    let input = document.getElementById(inputId);
    let value = input.value.trim();

    if (value === "") return; // No agregar vacíos

    // Agregar al array en memoria
    categories[modalidad][tipo].push(value);

    // Limpiar input
    input.value = "";
    input.focus(); // Mantener foco para añadir rápido

    // Actualizar la vista (render)
    renderList(modalidad, tipo);
}

// Función para pintar la lista en HTML y actualizar el input oculto
function renderList(modalidad, tipo) {
    let listId = `list-${modalidad}-${tipo}`;
    let hiddenId = `hidden-${modalidad}-${tipo}`;
    
    let container = document.getElementById(listId);
    let hiddenInput = document.getElementById(hiddenId);

    // Limpiar contenedor visual
    container.innerHTML = "";

    // Recorrer array y crear etiquetas
    categories[modalidad][tipo].forEach((item, index) => {
        let tag = document.createElement("div");
        tag.classList.add("tag-item");
        tag.innerHTML = `
            ${item} 
            <span class="tag-remove" onclick="removeItem('${modalidad}', '${tipo}', ${index})">&times;</span>
        `;
        container.appendChild(tag);
    });

    // Actualizar input oculto (separado por comas) para el Backend
    hiddenInput.value = categories[modalidad][tipo].join(",");
}

// Función para borrar item
function removeItem(modalidad, tipo, index) {
    categories[modalidad][tipo].splice(index, 1);
    renderList(modalidad, tipo);
}

/* ================= GENERAR RESUMEN (PASO 4) ================= */
function generateSummary() {
    let summaryDiv = document.getElementById('summary-content');
    let html = "";

    // 1. Datos Generales
    let nombre = document.getElementById('nombre').value;
    let fecha = document.getElementById('fechaInicio').value;
    let lugar = document.getElementById('ubicacion').value;

    html += `
        <div class="summary-section">
            <h4>${nombre}</h4>
            <p><i class="fas fa-calendar"></i> ${fecha} &nbsp;|&nbsp; <i class="fas fa-map-marker-alt"></i> ${lugar}</p>
        </div>
    `;

    // 2. Modalidades Seleccionadas
    let checkedModalities = document.querySelectorAll('input[name="modalidades"]:checked');
    
    html += `<div class="summary-section"><h4>Modalidades Configuradas:</h4>`;
    
    if (checkedModalities.length === 0) {
        html += `<p style="color:red">No has seleccionado ninguna modalidad.</p>`;
    } else {
        checkedModalities.forEach(chk => {
            let modName = chk.value.toLowerCase(); // combate, figuras...
            html += `<div style="margin-bottom:15px"><strong>• ${chk.value}</strong><ul class="summary-list">`;
            
            // Buscar en el objeto 'categories' los datos de esta modalidad
            let modData = categories[modName]; 
            if (modData) {
                // Iterar sobre tipos (peso, rango...)
                for (let tipo in modData) {
                    if (modData[tipo].length > 0) {
                        html += `<li>Wait for it... <strong>${tipo.toUpperCase()}:</strong> ${modData[tipo].join(", ")}</li>`;
                    }
                }
            }
            html += `</ul></div>`;
        });
    }
    html += `</div>`;

    summaryDiv.innerHTML = html;
}