const btnDiminuir = document.getElementById("btnDiminuir");
const btnAumentar = document.getElementById("btnAumentar");
const quantidadeElement = document.getElementById("quantidade");

let quantidade = 1;


// Diminuir
btnDiminuir.addEventListener("click", () => {

    if (quantidade > 1) {

        quantidade--;

        quantidadeElement.textContent = quantidade;

    }

});


// Aumentar
btnAumentar.addEventListener("click", () => {

    quantidade++;

    quantidadeElement.textContent = quantidade;

});