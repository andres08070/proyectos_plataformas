/* ========================================================
   ARCHIVO: wizard.js
   DESCRIPCIÓN: Lógica Dinámica Completa + Validaciones Pro
======================================================== */

let activeModalities = {}; 

document.addEventListener('DOMContentLoaded', function() {
    
    // 1. INICIALIZAR CALENDARIOS
    flatpickr(".flatpickr-input", {
        locale: "es", altInput: true, altFormat: "j F, Y", dateFormat: "Y-m-d", minDate: "today", disableMobile: "true"
    });

    // 2. Bloqueo de Enter global
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Enter' && event.target.tagName !== 'TEXTAREA' && event.target.type !== 'submit') {
            event.preventDefault(); return false;
        }
    });
    // ==========================================
    // 1. ALERTA DE ÉXITO (Con temporizador)
    // ==========================================
    const mensajeExito = /*[[${mensajeExito}]]*/ null;
    
    if (mensajeExito) {
        Swal.fire({
            title: '¡Éxito!',
            text: mensajeExito,
            icon: 'success',
            confirmButtonText: 'Ver mis campeonatos',
            confirmButtonColor: '#b71c1c',
            allowOutsideClick: false,
            allowEscapeKey: false,
            
            // --- NUEVAS LÍNEAS AGREGADAS ---
            timer: 3000, // 3000ms = 3 segundos
            timerProgressBar: true
            // -------------------------------

        }).then((result) => {
            // Verificamos si se cerró por el botón "Ver" O por el temporizador
            if (result.isConfirmed || result.dismiss === Swal.DismissReason.timer) {
                // Recargamos la página
                window.location.reload();
            }
        });
    }
    
    // ==========================================
    // 2. ALERTA DE ERROR (Con temporizador)
    // ==========================================
    const mensajeError = /*[[${mensajeError}]]*/ null;
    
    if (mensajeError) {
        Swal.fire({
            title: 'Error',
            text: mensajeError,
            icon: 'error',
            confirmButtonText: 'Entendido',
            confirmButtonColor: '#b71c1c',
            allowOutsideClick: false,
            allowEscapeKey: false,

            // --- NUEVAS LÍNEAS AGREGADAS ---
            timer: 4000, // Le damos un segundo más al error para leerlo bien
            timerProgressBar: true
            // -------------------------------
        });
    }

    // ==========================================
    // 3. LÓGICA DE ELIMINACIÓN (Sin cambios)
    // ==========================================
    const deleteButtons = document.querySelectorAll('.btn-delete-title');
    
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();
            
            const idCampeonato = this.getAttribute('data-id');
            const nombreCampeonato = this.getAttribute('data-nombre');
            
            confirmarEliminacion(idCampeonato, nombreCampeonato);
        });
    });
    
    function confirmarEliminacion(idCampeonato, nombreCampeonato) {
        const nombreEscapado = nombreCampeonato.replace(/'/g, "\\'").replace(/"/g, '\\"');
        
        Swal.fire({
            title: '¿Eliminar campeonato?',
            html: `
                <div style="text-align: left;">
                    <p>Estás a punto de eliminar: <strong>${nombreEscapado}</strong></p>
                    <p style="color: #b71c1c; font-size: 0.9em;">
                        <i class="fas fa-exclamation-triangle"></i> 
                        Esta acción borrará todas las inscripciones y datos asociados. No se puede deshacer.
                    </p>
                </div>
            `,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                eliminarCampeonato(idCampeonato);
            }
        });
    }
    
    function eliminarCampeonato(idCampeonato) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/campeonato/eliminar/${idCampeonato}`;
        
        document.body.appendChild(form);
        form.submit();
    }
});

/* ========================================================
   VALIDACIONES DE ENTRADA (NO + - e)
======================================================== */
// Esta función impide que el usuario presione teclas inválidas en inputs numéricos
function blockInvalidChars(e) {
    // Permitir: backspace, delete, tab, escape, enter y flechas
    if ([46, 8, 9, 27, 13, 110].indexOf(e.keyCode) !== -1 ||
        // Permitir: Ctrl+A, Command+A
        (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) || 
        // Permitir: home, end, left, right, down, up
        (e.keyCode >= 35 && e.keyCode <= 40)) {
             return;
    }
    // Bloquear 'e', 'E', '+', '-'
    if (['e', 'E', '+', '-'].includes(e.key)) {
        e.preventDefault();
    }
}

/* ========================================================
   GESTIÓN DE MODALIDADES DINÁMICAS
======================================================== */
function addNewModality() {
    const container = document.getElementById('modalities-container-dynamic');
    const uniqueId = 'mod_' + Date.now(); 
    
    activeModalities[uniqueId] = { name: '', desc: '', peso: [], rango: [], edad: [], genero: null };

    // NOTA: Se agregan onkeydown="blockInvalidChars(event)" y min="0" a todos los inputs numéricos
    const cardHTML = `
        <div class="modality-card" id="card-${uniqueId}">
            <div class="modality-header-edit">
                <div class="modality-inputs">
                    <input type="text" class="modality-name-input" placeholder="Nombre de Modalidad (Ej: Kickboxing)" 
                           oninput="updateModName('${uniqueId}', this.value)" required>
                    <input type="text" class="modality-desc-input" placeholder="Descripción breve"
                           oninput="updateModDesc('${uniqueId}', this.value)">
                </div>
                <button type="button" class="btn-delete-modality" onclick="removeModality('${uniqueId}')" title="Eliminar"><i class="fas fa-trash"></i></button>
            </div>
            
            <div class="modality-toggle-wrapper">
                <label class="switch"><input type="checkbox" onchange="toggleConfigPanel('${uniqueId}', this)"><span class="slider round"></span></label>
                <span style="font-weight:600; color:#444;">Configurar Categorías</span>
            </div>

            <div class="config-panel" id="panel-${uniqueId}">
                
                <div class="criteria-wrapper">
                    <div class="criteria-header" onclick="triggerSwitch(this)">
                        <span>Dividir por Peso</span>
                        <label class="switch"><input type="checkbox" onchange="toggleCriteriaBody(this)"><span class="slider round"></span></label>
                    </div>
                    <div class="criteria-body">
                        <div class="intuitive-row">
                            <select id="op-peso-${uniqueId}" class="text-select">
                                <option value="Menor a">Menor a (-)</option>
                                <option value="Mayor a">Mayor a (+)</option>
                                <option value="Igual a">Exactamente (=)</option>
                            </select>
                            <div class="input-wrapper">
                                <label>Peso (Kg)</label>
                                <input type="number" id="val-peso-${uniqueId}" class="input-with-label" min="1" 
                                       onkeydown="blockInvalidChars(event); handleEnter(event, '${uniqueId}', 'peso')">
                            </div>
                            <button type="button" class="btn-add-item" onclick="addWeight('${uniqueId}')"><i class="fas fa-plus"></i></button>
                        </div>
                        <div class="tags-container" id="list-peso-${uniqueId}"></div>
                    </div>
                </div>

                <div class="criteria-wrapper">
                    <div class="criteria-header" onclick="triggerSwitch(this)">
                        <span>Dividir por Cinturón / Nivel</span>
                        <label class="switch"><input type="checkbox" onchange="toggleCriteriaBody(this)"><span class="slider round"></span></label>
                    </div>
                    <div class="criteria-body">
                        <div class="radio-group">
                            <label class="radio-label"><input type="radio" name="belt-type-${uniqueId}" value="single" checked onchange="toggleBeltMode('${uniqueId}', 'single')"> Único</label>
                            <label class="radio-label"><input type="radio" name="belt-type-${uniqueId}" value="range" onchange="toggleBeltMode('${uniqueId}', 'range')"> Rango</label>
                        </div>
                        <div class="intuitive-row" id="belt-single-row-${uniqueId}">
                            <div class="input-wrapper"><label>Nombre</label><input type="text" id="val-belt-single-${uniqueId}" class="input-with-label" placeholder="Ej: Negro" onkeydown="handleEnter(event, '${uniqueId}', 'rango')"></div>
                            <button type="button" class="btn-add-item" onclick="addBelt('${uniqueId}')"><i class="fas fa-plus"></i></button>
                        </div>
                        <div class="intuitive-row" id="belt-range-row-${uniqueId}" style="display:none;">
                            <div class="input-wrapper"><label>Desde</label><input type="text" id="val-belt-from-${uniqueId}" class="input-with-label" placeholder="Ej: Blanco"></div>
                            <span style="font-weight:bold; color:#888;">&mdash;</span>
                            <div class="input-wrapper"><label>Hasta</label><input type="text" id="val-belt-to-${uniqueId}" class="input-with-label" placeholder="Ej: Verde"></div>
                            <button type="button" class="btn-add-item" onclick="addBelt('${uniqueId}')"><i class="fas fa-plus"></i></button>
                        </div>
                        <div class="tags-container" id="list-rango-${uniqueId}"></div>
                    </div>
                </div>

                <div class="criteria-wrapper">
                    <div class="criteria-header" onclick="triggerSwitch(this)">
                        <span>Dividir por Edad</span>
                        <label class="switch"><input type="checkbox" onchange="toggleCriteriaBody(this)"><span class="slider round"></span></label>
                    </div>
                    <div class="criteria-body">
                        <div class="radio-group">
                            <label class="radio-label"><input type="radio" name="age-type-${uniqueId}" value="single" checked onchange="toggleAgeMode('${uniqueId}', 'single')"> Única</label>
                            <label class="radio-label"><input type="radio" name="age-type-${uniqueId}" value="range" onchange="toggleAgeMode('${uniqueId}', 'range')"> Rango</label>
                        </div>
                        
                        <div class="intuitive-row" id="age-single-row-${uniqueId}">
                            <div class="input-wrapper">
                                <label>Edad (Años)</label>
                                <input type="number" id="val-age-single-${uniqueId}" class="input-with-label" min="1" placeholder="Ej: 18"
                                       onkeydown="blockInvalidChars(event); handleEnter(event, '${uniqueId}', 'edad')">
                            </div>
                            <button type="button" class="btn-add-item" onclick="addAge('${uniqueId}')"><i class="fas fa-plus"></i></button>
                        </div>

                        <div class="intuitive-row" id="age-range-row-${uniqueId}" style="display:none;">
                            <div class="input-wrapper">
                                <label>Mínima</label>
                                <input type="number" id="val-age-min-${uniqueId}" class="input-with-label" min="1" placeholder="Ej: 14" onkeydown="blockInvalidChars(event)">
                            </div>
                            <span style="font-weight:bold; color:#888;">&mdash;</span>
                            <div class="input-wrapper">
                                <label>Máxima</label>
                                <input type="number" id="val-age-max-${uniqueId}" class="input-with-label" min="1" placeholder="Ej: 17" onkeydown="blockInvalidChars(event)">
                            </div>
                            <button type="button" class="btn-add-item" onclick="addAge('${uniqueId}')"><i class="fas fa-plus"></i></button>
                        </div>
                        <div class="tags-container" id="list-edad-${uniqueId}"></div>
                    </div>
                </div>

                <div class="criteria-wrapper">
                    <div class="criteria-header" onclick="triggerSwitch(this)">
                        <span>Dividir por Género</span>
                        <label class="switch"><input type="checkbox" onchange="toggleCriteriaBody(this)"><span class="slider round"></span></label>
                    </div>
                    <div class="criteria-body" style="padding-bottom:15px;">
                        <select id="val-genero-${uniqueId}" class="text-select" style="width:100%" onchange="updateGender('${uniqueId}', this.value)">
                            <option value="">Seleccione opción...</option>
                            <option value="Separado">Masculino y Femenino (Separados)</option>
                            <option value="Mixto">Mixto (Compiten juntos)</option>
                        </select>
                    </div>
                </div>

            </div>
        </div>
    `;
    container.insertAdjacentHTML('beforeend', cardHTML);
}

function removeModality(id) {
    Swal.fire({
        title: '¿Eliminar modalidad?', text: "Esta acción no se puede deshacer.", icon: 'warning',
        showCancelButton: true, confirmButtonColor: '#b71c1c', cancelButtonColor: '#777', confirmButtonText: 'Sí, eliminar', cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) { document.getElementById(`card-${id}`).remove(); delete activeModalities[id]; }
    });
}

function updateModName(id, val) { activeModalities[id].name = val; }
function updateModDesc(id, val) { activeModalities[id].desc = val; }

// --- TOGGLES VISUALES ---
function toggleConfigPanel(id, checkbox) { const panel = document.getElementById(`panel-${id}`); checkbox.checked ? panel.classList.add('open') : panel.classList.remove('open'); }
function toggleCriteriaBody(checkbox) { const wrapper = checkbox.closest('.criteria-wrapper'); const body = wrapper.querySelector('.criteria-body'); checkbox.checked ? body.classList.add('open') : body.classList.remove('open'); }
function triggerSwitch(header) { if (event.target.closest('.switch')) return; const checkbox = header.querySelector('input[type="checkbox"]'); checkbox.checked = !checkbox.checked; checkbox.dispatchEvent(new Event('change')); }
function toggleBeltMode(id, mode) { const rS = document.getElementById(`belt-single-row-${id}`); const rR = document.getElementById(`belt-range-row-${id}`); if(mode==='single'){rS.style.display='flex';rR.style.display='none';}else{rS.style.display='none';rR.style.display='flex';} }
function toggleAgeMode(id, mode) { const rS = document.getElementById(`age-single-row-${id}`); const rR = document.getElementById(`age-range-row-${id}`); if(mode==='single'){rS.style.display='flex';rR.style.display='none';}else{rS.style.display='none';rR.style.display='flex';} }

/* ========================================================
   LÓGICA DE AGREGAR ITEMS (CON VALIDACIÓN DE RANGOS)
======================================================== */
function handleEnter(e, id, type) { if(e.key === 'Enter') { e.preventDefault(); if(type==='peso') addWeight(id); if(type==='rango') addBelt(id); if(type==='edad') addAge(id); } }

function addWeight(id) {
    const op = document.getElementById(`op-peso-${id}`).value;
    const input = document.getElementById(`val-peso-${id}`);
    const val = parseFloat(input.value);

    if(!input.value.trim()) return;
    
    // Validación de Peso Lógico
    if (val <= 0 || val > 300) {
        Swal.fire({icon:'warning', title:'Peso inválido', text:'Ingresa un peso entre 1 y 300 Kg.', confirmButtonColor: '#b71c1c'});
        return;
    }

    saveItem(id, 'peso', `${op} ${val}kg`);
    input.value = ""; input.focus();
}

function addBelt(id) {
    const mode = document.querySelector(`input[name="belt-type-${id}"]:checked`).value;
    let text = "";
    if (mode === 'single') {
        const input = document.getElementById(`val-belt-single-${id}`);
        if (!input.value.trim()) return;
        text = input.value.trim();
        input.value = ""; input.focus();
    } else {
        const from = document.getElementById(`val-belt-from-${id}`);
        const to = document.getElementById(`val-belt-to-${id}`);
        if (!from.value.trim() || !to.value.trim()) return;
        text = `${from.value.trim()} - ${to.value.trim()}`;
        from.value = ""; to.value = ""; from.focus();
    }
    saveItem(id, 'rango', text);
}

function addAge(id) {
    const mode = document.querySelector(`input[name="age-type-${id}"]:checked`).value;
    let text = "";
    
    if (mode === 'single') {
        const input = document.getElementById(`val-age-single-${id}`);
        const val = parseInt(input.value);
        if (!input.value) return;
        
        // Validación Edad Lógica
        if (val <= 0 || val > 120) {
            Swal.fire({icon:'warning', title:'Edad inválida', text:'Ingresa una edad realista.', confirmButtonColor: '#b71c1c'});
            return;
        }
        text = `${val} años`;
        input.value = ""; input.focus();
    } else {
        const minInput = document.getElementById(`val-age-min-${id}`);
        const maxInput = document.getElementById(`val-age-max-${id}`);
        const min = parseInt(minInput.value);
        const max = parseInt(maxInput.value);
        
        if(!minInput.value || !maxInput.value) return;

        // Validaciones Rango
        if(min <= 0 || max > 120) {
            Swal.fire({icon:'warning', title:'Edad fuera de rango', text:'Edades deben ser entre 1 y 120.', confirmButtonColor: '#b71c1c'});
            return; 
        }
        if(min > max) { 
            Swal.fire({icon: 'error', title: 'Error lógico', text: 'La edad mínima no puede ser mayor a la máxima', confirmButtonColor: '#b71c1c'});
            return; 
        }
        text = `${min} - ${max} años`;
        minInput.value = ""; maxInput.value = ""; minInput.focus();
    }
    saveItem(id, 'edad', text);
}

function updateGender(id, val) { activeModalities[id].genero = val; }
function saveItem(id, type, text) { if(!activeModalities[id][type].includes(text)) { activeModalities[id][type].push(text); renderTags(id, type); } }
function renderTags(id, type) {
    const container = document.getElementById(`list-${type}-${id}`);
    container.innerHTML = "";
    activeModalities[id][type].forEach((item, index) => {
        const tag = document.createElement('div');
        tag.className = 'tag-item';
        tag.innerHTML = `${item} <span class="tag-remove" onclick="removeTag('${id}', '${type}', ${index})">&times;</span>`;
        container.appendChild(tag);
    });
}
function removeTag(id, type, index) { activeModalities[id][type].splice(index, 1); renderTags(id, type); }

/* ========================================================
   NAVEGACIÓN
======================================================== */
function showStep(step) {
    document.querySelectorAll('.wizard-step').forEach(el => el.classList.remove('active-step'));
    document.querySelectorAll('.step').forEach(el => el.classList.remove('active'));
    document.getElementById('step-' + step).classList.add('active-step');
    for(let i=1; i<=step; i++) document.getElementById('indicator-'+i).classList.add('active');
}

function nextStep(target) {
    if (target === 2 && !document.getElementById('nombre').value) { Swal.fire({icon: 'warning', title: 'Atención', text: 'Falta el nombre del campeonato', confirmButtonColor: '#b71c1c'}); return; }
    if (target === 3) {
        if(Object.keys(activeModalities).length === 0) { Swal.fire({icon: 'warning', title: 'Sin Modalidades', text: 'Agrega al menos una modalidad', confirmButtonColor: '#b71c1c'}); return; }
        for(let id in activeModalities) if(!activeModalities[id].name) { Swal.fire({icon: 'warning', title: 'Incompleto', text: 'Todas las modalidades deben tener nombre', confirmButtonColor: '#b71c1c'}); return; }
    }
    if(target === 4) generateSummary();
    showStep(target);
}
function prevStep(step) { showStep(step); }

// Generate Summary: Mismo de antes (ya está correcto)
function generateSummary() {
    const div = document.getElementById('summary-content');
    const nombre = document.getElementById('nombre').value || "Sin Nombre";
    const fechaIni = document.getElementById('fechaInicio').value || "--";
    const fechaFin = document.getElementById('fechaFin').value || "--";
    const ubicacion = document.getElementById('ubicacion').value || "No especificada";
    const areas = document.getElementById('numAreas').value || "1";

    let html = `
        <div class="summary-container">
            <div class="summary-header-block">
                <div class="summary-row"><span class="summary-label">Campeonato:</span> <span>${nombre}</span></div>
                <div class="summary-row"><span class="summary-label">Fechas:</span> <span>${fechaIni} al ${fechaFin}</span></div>
                <div class="summary-row"><span class="summary-label">Sede:</span> <span>${ubicacion}</span></div>
                <div class="summary-row"><span class="summary-label">Áreas:</span> <span>${areas}</span></div>
            </div>
            <h4 style="margin-bottom:15px; color:#444;">Modalidades:</h4>
    `;

    if(Object.keys(activeModalities).length === 0) {
        html += "<p><em>Sin modalidades.</em></p>";
    } else {
        for(let id in activeModalities) {
            const mod = activeModalities[id];
            html += `<div class="summary-modality-item"><span class="summary-modality-title">${mod.name}</span><ul class="summary-cat-list">`;
            if(mod.peso.length) html += `<li><span class="summary-cat-label">Pesos:</span> ${mod.peso.join(", ")}</li>`;
            if(mod.rango.length) html += `<li><span class="summary-cat-label">Niveles:</span> ${mod.rango.join(", ")}</li>`;
            if(mod.edad.length) html += `<li><span class="summary-cat-label">Edades:</span> ${mod.edad.join(", ")}</li>`;
            if(mod.genero) html += `<li><span class="summary-cat-label">Género:</span> ${mod.genero}</li>`;
            html += `</ul></div>`;
        }
    }
    html += `</div>`;
    
    let jsonInput = document.getElementById('jsonModalidades');
    if(!jsonInput) {
        jsonInput = document.createElement('input'); jsonInput.type = 'hidden'; jsonInput.name = 'jsonModalidades'; jsonInput.id = 'jsonModalidades';
        document.getElementById('campeonatoForm').appendChild(jsonInput);
    }
    jsonInput.value = JSON.stringify(activeModalities);
    div.innerHTML = html;
}

