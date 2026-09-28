document.addEventListener("DOMContentLoaded", () => {

    // =====================================
    // QUANTIDADE
    // =====================================

    const btnDiminuir =
        document.getElementById("btnDiminuir");

    const btnAumentar =
        document.getElementById("btnAumentar");

    const quantidadeElement =
        document.getElementById("quantidade");

    const quantidadeInput =
        document.getElementById("quantidadeInput");


    let quantidade = 1;


    function atualizarQuantidade() {

        quantidadeElement.textContent = quantidade;

        quantidadeInput.value = quantidade;

    }


    btnDiminuir.addEventListener("click", () => {

        if (quantidade > 1) {

            quantidade--;

            atualizarQuantidade();

        }

    });


    btnAumentar.addEventListener("click", () => {

        quantidade++;

        atualizarQuantidade();

    });


    // =====================================
    // ADICIONAR AO CARRINHO
    // =====================================

    const formAdicionarCarrinho =
        document.getElementById(
            "formAdicionarCarrinho"
        );


    if (!formAdicionarCarrinho) {
        return;
    }


    formAdicionarCarrinho.addEventListener(
        "submit",
        async event => {

            event.preventDefault();


            const botao =
                formAdicionarCarrinho.querySelector(
                    'button[type="submit"]'
                );


            // Impede múltiplos cliques

            if (botao.disabled) {
                return;
            }


            botao.disabled = true;


            try {

                // =====================================
                // CSRF
                // =====================================

                const csrfToken =
                    document.querySelector(
                        'meta[name="_csrf"]'
                    )?.content;


                const csrfHeader =
                    document.querySelector(
                        'meta[name="_csrf_header"]'
                    )?.content;


                const headers = {};


                if (csrfToken && csrfHeader) {

                    headers[csrfHeader] =
                        csrfToken;

                }


                // =====================================
                // DADOS DO FORMULÁRIO
                // =====================================

                const formData =
                    new FormData(
                        formAdicionarCarrinho
                    );


                // =====================================
                // ADICIONAR PRODUTO
                // =====================================

                const response = await fetch(
                    formAdicionarCarrinho.action,
                    {
                        method: "POST",
                        headers: headers,
                        body: formData
                    }
                );


                // =====================================
                // USUÁRIO NÃO LOGADO
                // =====================================

                if (response.status === 401) {

                    window.location.href = "/login";

                    return;
                }


                // =====================================
                // OUTRO ERRO
                // =====================================

                if (!response.ok) {

                    throw new Error(
                        `Não foi possível adicionar o produto. Status: ${response.status}`
                    );

                }


                // =====================================
                // ABRIR CARRINHO
                // =====================================

                const offcanvas =
                    document.getElementById(
                        "carrinhoOffcanvas"
                    );


                if (offcanvas) {

                    const offcanvasInstance =
                        bootstrap.Offcanvas.getOrCreateInstance(
                            offcanvas
                        );


                    offcanvasInstance.show();

                }

            } catch (error) {

                console.error(
                    "Erro ao adicionar produto:",
                    error
                );

            } finally {

                botao.disabled = false;

            }

        }
    );

});