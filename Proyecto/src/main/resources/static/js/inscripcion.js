/* ========================================================
   ARCHIVO: inscripcion.js
   DESCRIPCIÓN: Renderiza el formulario basado en la config del campeonato
======================================================== */

document.addEventListener('DOMContentLoaded', function() {
    
    const jsonInput = document.getElementById('jsonConfig');
    const selectModalidad = document.getElementById('selectModalidad');
    const container = document.getElementById('dynamic-fields-container');
    const form = document.getElementById('inscripcionForm');

    // 1. PARSEAR LA CONFIGURACIÓN (Evitar errores si está vacío)
    let config = {};
    try {
        if(jsonInput && jsonInput.value) {
            config = JSON.parse(jsonInput.value);
        }
    } catch (e) {
        console.error("Error parseando JSON de modalidades", e);
        Swal.fire({icon:'error', title:'Error', text:'No se pudo cargar la configuración del torneo.'});
        return;
    }

    // 2. LLENAR EL SELECT DE MODALIDADES
    // Iteramos sobre las llaves del objeto (mod_123...)
    for (let id in config) {
        const mod = config[id];
        let option = document.createElement('option');
        option.value = id; // El ID único (ej: mod_173456789)
        option.textContent = mod.name; // Ej: "Kickboxing"
        selectModalidad.appendChild(option);
    }

    // 3. ESCUCHAR CAMBIOS (Lógica Reactiva)
    selectModalidad.addEventListener('change', function() {
        const selectedId = this.value;
        container.innerHTML = ""; // Limpiar campos anteriores
        container.style.opacity = 0; // Animación fade-out

        if (!selectedId || !config[selectedId]) return;

        const modData = config[selectedId];

        // Crear campos dinámicos con una pequeña animación
        setTimeout(() => {
            renderDynamicFields(modData, container);
            container.style.opacity = 1; // Fade-in
        }, 200);
    });

    // 4. VALIDACIÓN AL ENVIAR
    form.addEventListener('submit', function(e) {
        if (!form.checkValidity()) {
            e.preventDefault();
            Swal.fire({icon:'warning', title:'Faltan datos', text:'Por favor completa todos los campos requeridos.'});
        }
        // Aquí podrías agregar lógica extra (ej: confirmar con SweetAlert antes de enviar)
    });
});

// --- FUNCIÓN QUE CONSTRUYE EL HTML ---
function renderDynamicFields(data, container) {
    
    // A. DESCRIPCIÓN (Opcional)
    if(data.desc) {
        let desc = document.createElement('div');
        desc.className = 'alert-info'; // Usar estilo si tienes, o inline
        desc.style.cssText = "background:#f8f9fa; color:#666; padding:10px; border-left:4px solid #b71c1c; margin-bottom:20px; font-style:italic; font-size:0.9rem;";
        desc.innerHTML = `<strong>Nota:</strong> ${data.desc}`;
        container.appendChild(desc);
    }

    // B. GENERAR SELECTS SEGÚN ARRAYS
    // Usamos una función auxiliar para no repetir código
    
    // 1. GÉNERO
    if (data.genero) {
        // Si es "Mixto", quizás no necesites select, o sí para confirmar. 
        // Si es "Separado", necesitamos que elija.
        let options = [];
        if(data.genero === "Separado") {
            options = ["Masculino", "Femenino"];
        } else {
            options = ["Mixto"];
        }
        createSelectField(container, "genero", "Género", options);
    }

    // 2. EDAD (Categorías)
    if (data.edad && data.edad.length > 0) {
        createSelectField(container, "categoriaEdad", "Categoría de Edad", data.edad);
    }

    // 3. PESO (Categorías)
    if (data.peso && data.peso.length > 0) {
        createSelectField(container, "categoriaPeso", "Categoría de Peso", data.peso);
    }

    // 4. CINTURÓN / RANGO
    if (data.rango && data.rango.length > 0) {
        createSelectField(container, "categoriaRango", "Nivel / Cinturón", data.rango);
    }
}

// --- HELPER PARA CREAR SELECTS ---
function createSelectField(container, name, labelText, optionsArray) {
    
    // Wrapper (estilo wizard.css .form-group)
    let wrapper = document.createElement('div');
    wrapper.style.marginBottom = "15px";
    
    // Label
    let label = document.createElement('label');
    label.textContent = labelText;
    label.style.cssText = "display:block; font-weight:600; color:#444; margin-bottom:5px;";
    
    // Select (estilo wizard.css .text-select)
    let select = document.createElement('select');
    select.name = name;
    select.className = "text-select";
    select.style.width = "100%";
    select.required = true;
    
    // Opción default
    let defaultOpt = document.createElement('option');
    defaultOpt.value = "";
    defaultOpt.textContent = "Seleccionar...";
    select.appendChild(defaultOpt);

    // Llenar opciones
    optionsArray.forEach(opt => {
        let option = document.createElement('option');
        option.value = opt; // El valor es el texto (ej: "-60kg")
        option.textContent = opt;
        select.appendChild(option);
    });

    wrapper.appendChild(label);
    wrapper.appendChild(select);
    
    // Animación de entrada
    wrapper.style.animation = "fadeIn 0.5s ease";
    
    container.appendChild(wrapper);
}