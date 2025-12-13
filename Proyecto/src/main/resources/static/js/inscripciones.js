const gestionarInscripcion = {
    ids: {
        cardCompetidor: 'card-competidor',
        cardJuez: 'card-juez',
        formCompetidor: 'form-competidor',
        formJuez: 'form-juez'
    },

    seleccionarRol: function(rol) {
        const cCompetidor = document.getElementById(this.ids.cardCompetidor);
        const cJuez = document.getElementById(this.ids.cardJuez);
        const fCompetidor = document.getElementById(this.ids.formCompetidor);
        const fJuez = document.getElementById(this.ids.formJuez);

        cCompetidor.classList.remove('active-competitor');
        cJuez.classList.remove('active-judge');

        fCompetidor.style.display = 'none';
        fJuez.style.display = 'none';

        if (rol === 'competidor') {
            cCompetidor.classList.add('active-competitor');
            fCompetidor.style.display = 'block';
            this.scrollSuave(fCompetidor);
        } else if (rol === 'juez') {
            cJuez.classList.add('active-judge');
            fJuez.style.display = 'block';
            this.scrollSuave(fJuez);
        }
    },

    scrollSuave: function(elemento) {
        setTimeout(() => {
            elemento.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 100);
    }
};