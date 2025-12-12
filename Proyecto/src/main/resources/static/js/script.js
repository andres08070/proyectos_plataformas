/* ========================================================
   1. INICIALIZACIÓN Y EVENTOS DEL DOM
======================================================== */
document.addEventListener('DOMContentLoaded', function() {
    
    // ----------------------------------------------------
    // A. LÓGICA DE MENÚS DESPLEGABLES (DROPDOWNS - REGISTRO)
    // ----------------------------------------------------
    const customDropdowns = document.querySelectorAll('.custom-dropdown');

    customDropdowns.forEach(dropdown => {
        const select = dropdown.querySelector('select');
        const selectedDisplay = dropdown.querySelector('.dropdown-selected span');
        const optionsList = dropdown.querySelector('.dropdown-options');
        
        // Crear lista visual basada en el select original
        Array.from(select.options).forEach(option => {
            if (option.disabled) return;

            const li = document.createElement('li');
            li.textContent = option.textContent;
            li.setAttribute('data-value', option.value);
            
            if (option.selected) {
                li.classList.add('selected');
                selectedDisplay.textContent = option.textContent;
                selectedDisplay.style.color = '#2c3e50'; 
            }
            
            li.addEventListener('click', function() {
                selectedDisplay.textContent = this.textContent;
                selectedDisplay.style.color = '#2c3e50'; 
                select.value = this.getAttribute('data-value');
                
                optionsList.querySelectorAll('li').forEach(item => item.classList.remove('selected'));
                this.classList.add('selected');
                dropdown.classList.remove('active');
            });
            
            optionsList.appendChild(li);
        });

        dropdown.querySelector('.dropdown-selected').addEventListener('click', function(e) {
            customDropdowns.forEach(d => { if (d !== dropdown) d.classList.remove('active'); });
            dropdown.classList.toggle('active');
            e.stopPropagation(); 
        });
    });

    // Cerrar dropdown si se hace clic fuera
    document.addEventListener('click', function(e) {
        customDropdowns.forEach(dropdown => {
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove('active');
            }
        });
    });


    // ----------------------------------------------------
    // B. VALIDACIÓN DEL DOCUMENTO (FORMULARIO REGISTRO)
    // ----------------------------------------------------
    const formulario = document.getElementById('formularioRegistro');
    
    if (formulario) {
        formulario.addEventListener('submit', function(event) {
            const docInput = document.getElementById('docInput');
            const docError = document.getElementById('docError');
            
            if (docInput && docInput.value.length < 6) {
                event.preventDefault(); // Evitar envío
                if(docError) docError.style.display = 'block';
                docInput.style.borderColor = '#b71c1c'; // Rojo alerta
                docInput.focus();
            } else {
                if(docError) docError.style.display = 'none';
                if(docInput) docInput.style.borderColor = '#e0e0e0';
            }
        });
    }

    // ----------------------------------------------------
    // C. LÓGICA DE SWITCHES (CREAR CAMPEONATO)
    // "Preguntar primero antes de mostrar inputs"
    // ----------------------------------------------------
    const toggles = document.querySelectorAll('.toggle-control');
    
    toggles.forEach(toggle => {
        // Al cargar, verificar estado inicial
        togglePanel(toggle);

        // Escuchar cambios
        toggle.addEventListener('change', function() {
            togglePanel(this);
        });
    });

    function togglePanel(checkbox) {
        const wrapper = checkbox.closest('.criteria-wrapper');
        // Protección: si no estamos en el wizard, no hacemos nada
        if (!wrapper) return; 

        const body = wrapper.querySelector('.criteria-body');
        
        if (checkbox.checked) {
            // MOSTRAR PANEL
            body.style.maxHeight = "500px"; 
            body.style.opacity = "1";
            body.style.padding = "15px";
            body.style.marginTop = "10px";
            body.style.pointerEvents = "all"; 
        } else {
            // OCULTAR PANEL
            body.style.maxHeight = "0"; 
            body.style.opacity = "0";
            body.style.padding = "0 15px";
            body.style.marginTop = "0";
            body.style.pointerEvents = "none"; 
        }
    }
});


/* ========================================================
   2. FUNCIONES GLOBALES DE VALIDACIÓN (REGISTRO)
======================================================== */
function validarContrasenas() {
    const pass1 = document.getElementById('contraseña');
    const pass2 = document.getElementById('Confirmar');
    const mensaje = document.getElementById('mensajeError');
    const boton = document.getElementById('botonRegistrar');

    if (!pass1 || !pass2 || !mensaje || !boton) return;

    if (pass2.value.length > 0) {
        if (pass1.value !== pass2.value) {
            mensaje.style.display = 'block';
            pass2.style.borderColor = '#b71c1c';
            boton.disabled = true;
            boton.style.opacity = '0.5';
        } else {
            mensaje.style.display = 'none';
            pass2.style.borderColor = '#2ecc71'; // Verde
            boton.disabled = false;
            boton.style.opacity = '1';
        }
    } else {
        mensaje.style.display = 'none';
        pass2.style.borderColor = '#e0e0e0';
        boton.disabled = false;
        boton.style.opacity = '1';
    }
}


/* ========================================================
   3. GESTIÓN DEL WIZARD (ARRAYS Y PASOS)
======================================================== */

// OBJETO PRINCIPAL: Estructura estandarizada para las 4 modalidades
let categories = {
    combate: { peso: [], rango: [], edad: [], genero: null },
    figuras: { peso: [], rango: [], edad: [], genero: null },
    defensa: { peso: [], rango: [], edad: [], genero: null },
    demo:    { peso: [], rango: [], edad: [], genero: null }
};

// --- NAVEGACIÓN ---
function showStep(stepNumber) {
    // 1. Ocultar todos
    document.querySelectorAll('.wizard-step').forEach(el => el.classList.remove('active-step'));
    document.querySelectorAll('.step').forEach(el => el.classList.remove('active'));
    
    // 2. Mostrar actual
    const stepDiv = document.getElementById('step-' + stepNumber);
    if(stepDiv) stepDiv.classList.add('active-step');
    
    // 3. Actualizar barra superior
    for(let i = 1; i <= stepNumber; i++) {
        const ind = document.getElementById('indicator-' + i);
        if(ind) ind.classList.add('active');
    }
}

function nextStep(targetStep) {
    let currentStepNum = targetStep - 1;
    
    // Validar antes de avanzar
    if (!validateStep(currentStepNum)) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'warning',
                title: 'Atención',
                text: 'Completa los campos obligatorios.',
                confirmButtonColor: '#b71c1c'
            });
        } else {
            alert('Faltan datos obligatorios.');
        }
        return; 
    }

    // Si es el paso final, generar resumen
    if (targetStep === 4) {
        generateSummary();
    }

    showStep(targetStep);
}

function prevStep(step) {
    showStep(step);
}

// --- VALIDACIÓN DE PASOS ---
function validateStep(stepNum) {
    let isValid = true;
    let currentStepDiv = document.getElementById('step-' + stepNum);
    
    if(!currentStepDiv) return true; 

    // 1. Validar inputs visibles marcados como required
    let requiredInputs = currentStepDiv.querySelectorAll('input[required]');
    requiredInputs.forEach(input => {
        // Solo validar si es visible (offsetParent != null)
        if (input.offsetParent !== null && !input.value.trim()) {
            isValid = false;
            input.classList.add('input-error');
            input.addEventListener('input', function() {
                if(this.value.trim()) this.classList.remove('input-error');
            });
        } else {
            input.classList.remove('input-error');
        }
    });

    // 2. Validación específica Paso 2 (Debe haber al menos 1 modalidad)
    if (stepNum === 2) {
        let checkedModalities = document.querySelectorAll('input[name="modalidades"]:checked');
        if (checkedModalities.length === 0) {
            isValid = false;
        }
    }

    return isValid;
}


/* ========================================================
   4. AÑADIR/ELIMINAR CATEGORÍAS Y GÉNERO
======================================================== */

// --- AÑADIR ETIQUETAS (Tags) ---
function addItem(modalidad, tipo) {
    let inputId = `input-${modalidad}-${tipo}`;
    let input = document.getElementById(inputId);
    
    if (!input) return;

    let value = input.value.trim();
    if (value === "") return; 

    // Añadir al array si no existe
    if (!categories[modalidad][tipo].includes(value)) {
        categories[modalidad][tipo].push(value);
        input.value = "";
        input.focus();
        renderList(modalidad, tipo);
    } else {
        // Aviso de duplicado
        input.value = "";
        input.focus();
    }
}

// --- PINTAR LISTA ---
function renderList(modalidad, tipo) {
    let listId = `list-${modalidad}-${tipo}`;
    let hiddenId = `hidden-${modalidad}-${tipo}`;
    
    let container = document.getElementById(listId);
    let hiddenInput = document.getElementById(hiddenId);

    if (!container || !hiddenInput) return;

    container.innerHTML = ""; 

    categories[modalidad][tipo].forEach((item, index) => {
        let tag = document.createElement("div");
        tag.classList.add("tag-item");
        tag.innerHTML = `
            ${item} 
            <span class="tag-remove" onclick="removeItem('${modalidad}', '${tipo}', ${index})">&times;</span>
        `;
        container.appendChild(tag);
    });

    // ACTUALIZAR EL HIDDEN INPUT (Para enviar a BD)
    hiddenInput.value = categories[modalidad][tipo].join(",");
}

// --- ELIMINAR ETIQUETA ---
function removeItem(modalidad, tipo, index) {
    categories[modalidad][tipo].splice(index, 1);
    renderList(modalidad, tipo); 
}

// --- LÓGICA DE GÉNERO (SELECT) ---
// Se activa desde el switch onchange="toggleGender(...)"
function toggleGender(modalidad, checkbox) {
    const select = document.getElementById(`select-${modalidad}-genero`);
    
    if(checkbox.checked) {
        // Guardar valor inicial
        categories[modalidad].genero = select.value;
        
        // Escuchar cambios en el select
        select.addEventListener('change', function() {
            categories[modalidad].genero = this.value;
        });
    } else {
        // Si se apaga, limpiar
        categories[modalidad].genero = null;
    }
}


/* ========================================================
   5. GENERAR RESUMEN FINAL
======================================================== */
function generateSummary() {
    let summaryDiv = document.getElementById('summary-content');
    let html = "";

    // Datos Básicos
    let nombre = document.getElementById('nombre').value;
    let fecha = document.getElementById('fechaInicio').value;
    let lugar = document.getElementById('ubicacion').value;
    let numAreas = document.getElementById('numAreas').value;

    html += `
        <div class="summary-section" style="border-bottom:1px solid #eee; padding-bottom:15px; margin-bottom:15px;">
            <h4 style="color: #b71c1c; margin: 0 0 5px 0;">${nombre}</h4>
            <p style="color: #555; font-size: 0.9rem; margin:0;">
                <i class="fas fa-calendar"></i> ${fecha} &nbsp;|&nbsp; 
                <i class="fas fa-map-marker-alt"></i> ${lugar} &nbsp;|&nbsp;
                <i class="fas fa-layer-group"></i> ${numAreas} Áreas
            </p>
        </div>
    `;

    // Modalidades
    let checkedModalities = document.querySelectorAll('input[name="modalidades"]:checked');
    
    html += `<div class="summary-section"><h5 style="margin-bottom:15px; color:#333;">Configuración del Torneo:</h5>`;
    
    if (checkedModalities.length === 0) {
        html += `<p style="color:red">Error: No hay modalidades seleccionadas.</p>`;
    } else {
        checkedModalities.forEach(chk => {
            let modKey = chk.value.toLowerCase(); // ej: 'combate'
            
            html += `<div style="background:#fff; border:1px solid #ddd; border-left: 4px solid #b71c1c; padding:15px; border-radius:8px; margin-bottom:15px;">
                        <strong style="display:block; font-size:1.1rem; margin-bottom:10px; color:#1a1a1a;">
                            <i class="fas fa-check-circle" style="color:#b71c1c"></i> ${chk.value}
                        </strong>
                        <ul style="list-style:none; padding:0; margin:0; color:#555;">`;
            
            let modData = categories[modKey];
            let hayDatos = false;

            if (modData) {
                // 1. Arrays (Peso, Rango, Edad)
                ['peso', 'rango', 'edad'].forEach(tipo => {
                    if (modData[tipo] && modData[tipo].length > 0) {
                        html += `<li style="margin-bottom:5px; border-bottom:1px dashed #eee; padding-bottom:5px;">
                                    <strong style="color:#333;">${tipo.toUpperCase()}:</strong> 
                                    ${modData[tipo].join(", ")}
                                 </li>`;
                        hayDatos = true;
                    }
                });

                // 2. Género (Valor único)
                if (modData.genero) {
                    html += `<li style="margin-bottom:5px;">
                                <strong style="color:#333;">GÉNERO:</strong> ${modData.genero}
                             </li>`;
                    hayDatos = true;
                }
            }
            
            if (!hayDatos) {
                html += `<li><em style="color:#999;">Configuración estándar (sin filtros adicionales)</em></li>`;
            }

            html += `</ul></div>`;
        });
    }
    html += `</div>`;

    summaryDiv.innerHTML = html;
}