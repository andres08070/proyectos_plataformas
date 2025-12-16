

document.addEventListener('DOMContentLoaded', function() {
    
  
    const customDropdowns = document.querySelectorAll('.custom-dropdown');

    customDropdowns.forEach(dropdown => {
        const select = dropdown.querySelector('select');
        const selectedDisplay = dropdown.querySelector('.dropdown-selected span');
        const optionsList = dropdown.querySelector('.dropdown-options');
        
        
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

    
    document.addEventListener('click', function(e) {
        customDropdowns.forEach(dropdown => {
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove('active');
            }
        });
    });


   
    const formulario = document.getElementById('formularioRegistro');
    
    if (formulario) {
        formulario.addEventListener('submit', function(event) {
            const docInput = document.getElementById('docInput');
            const docError = document.getElementById('docError');
            
            if (docInput && docInput.value.length < 6) {
                event.preventDefault();
                if(docError) docError.style.display = 'block';
                docInput.style.borderColor = '#b71c1c'; 
                docInput.focus();
            } else {
                if(docError) docError.style.display = 'none';
                if(docInput) docInput.style.borderColor = '#e0e0e0';
            }
        });
    }
});



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
            pass2.style.borderColor = '#2ecc71'; 
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