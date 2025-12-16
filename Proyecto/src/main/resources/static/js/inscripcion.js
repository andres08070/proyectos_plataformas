

document.addEventListener('DOMContentLoaded', function() {
    
    const jsonInput = document.getElementById('jsonConfig');
    const selectModalidad = document.getElementById('selectModalidad');
    const container = document.getElementById('dynamic-fields-container');
    const form = document.getElementById('inscripcionForm');

    
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

   
    for (let id in config) {
        const mod = config[id];
        let option = document.createElement('option');
        option.value = id; 
        option.textContent = mod.name; 
        selectModalidad.appendChild(option);
    }

    
    selectModalidad.addEventListener('change', function() {
        const selectedId = this.value;
        container.innerHTML = "";
        container.style.opacity = 0; 

        if (!selectedId || !config[selectedId]) return;

        const modData = config[selectedId];

        
        setTimeout(() => {
            renderDynamicFields(modData, container);
            container.style.opacity = 1; 
        }, 200);
    });

    
    form.addEventListener('submit', function(e) {
        if (!form.checkValidity()) {
            e.preventDefault();
            Swal.fire({icon:'warning', title:'Faltan datos', text:'Por favor completa todos los campos requeridos.'});
        }
        
    });
});


function renderDynamicFields(data, container) {
    
    
    if(data.desc) {
        let desc = document.createElement('div');
        desc.className = 'alert-info'; 
        desc.style.cssText = "background:#f8f9fa; color:#666; padding:10px; border-left:4px solid #b71c1c; margin-bottom:20px; font-style:italic; font-size:0.9rem;";
        desc.innerHTML = `<strong>Nota:</strong> ${data.desc}`;
        container.appendChild(desc);
    }

    
    
    
    if (data.genero) {
       
        let options = [];
        if(data.genero === "Separado") {
            options = ["Masculino", "Femenino"];
        } else {
            options = ["Mixto"];
        }
        createSelectField(container, "genero", "Género", options);
    }

    
    if (data.edad && data.edad.length > 0) {
        createSelectField(container, "categoriaEdad", "Categoría de Edad", data.edad);
    }

   
    if (data.peso && data.peso.length > 0) {
        createSelectField(container, "categoriaPeso", "Categoría de Peso", data.peso);
    }

    
    if (data.rango && data.rango.length > 0) {
        createSelectField(container, "categoriaRango", "Nivel / Cinturón", data.rango);
    }
}


function createSelectField(container, name, labelText, optionsArray) {
    
    
    let wrapper = document.createElement('div');
    wrapper.style.marginBottom = "15px";
    
    
    let label = document.createElement('label');
    label.textContent = labelText;
    label.style.cssText = "display:block; font-weight:600; color:#444; margin-bottom:5px;";
    
    
    let select = document.createElement('select');
    select.name = name;
    select.className = "text-select";
    select.style.width = "100%";
    select.required = true;
    
    
    let defaultOpt = document.createElement('option');
    defaultOpt.value = "";
    defaultOpt.textContent = "Seleccionar...";
    select.appendChild(defaultOpt);

    
    optionsArray.forEach(opt => {
        let option = document.createElement('option');
        option.value = opt; 
        option.textContent = opt;
        select.appendChild(option);
    });

    wrapper.appendChild(label);
    wrapper.appendChild(select);
    
   
    wrapper.style.animation = "fadeIn 0.5s ease";
    
    container.appendChild(wrapper);
}