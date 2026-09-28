document.addEventListener("DOMContentLoaded", () => {

    const offcanvas = document.getElementById("carrinhoOffcanvas");

    if (!offcanvas) {
        return;
    }

    const body = document.getElementById("carrinhoOffcanvasBody");
    const footer = document.getElementById("carrinhoOffcanvasFooter");

    const csrfToken =
        document.querySelector('meta[name="_csrf"]')?.content;

    const csrfHeader =
        document.querySelector('meta[name="_csrf_header"]')?.content;


    function headersComCsrf() {

        const headers = {};

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        return headers;
    }


    // =====================================
    // ABRIR CARRINHO
    // =====================================

    offcanvas.addEventListener("show.bs.offcanvas", () => {
        carregarCarrinho();
    });


    // =====================================
    // CARREGAR CARRINHO
    // =====================================

    async function carregarCarrinho() {

        body.innerHTML = `
            <div class="text-center py-5">

                <div class="spinner-border" role="status"></div>

                <p class="text-muted mt-3 mb-0">
                    Carregando carrinho...
                </p>

            </div>
        `;

        footer.innerHTML = "";


        try {

            const response = await fetch("/carrinho");


            // =====================================
            // USUÁRIO NÃO LOGADO
            // =====================================

            if (
                response.status === 401 ||
                (
                    response.redirected &&
                    response.url.includes("/login")
                )
            ) {

                mostrarLoginNecessario();

                return;
            }


            if (!response.ok) {

                throw new Error(
                    `Erro ao carregar carrinho: ${response.status}`
                );

            }


            const itens = await response.json();


            if (!Array.isArray(itens)) {

                throw new Error(
                    "Resposta do carrinho não é uma lista."
                );

            }


            // =====================================
            // CARRINHO VAZIO
            // =====================================

            if (itens.length === 0) {

                mostrarCarrinhoVazio();

                return;
            }


            // =====================================
            // MONTAR CARRINHO
            // =====================================

            let total = 0;


            body.innerHTML = itens.map(item => {

                const preco = Number(item.preco);

                const quantidade =
                    Number(item.quantidade);

                const subtotal =
                    preco * quantidade;


                total += subtotal;


                return `
                    <div class="border-bottom py-3">

                        <div class="d-flex justify-content-between">

                            <div class="flex-grow-1">

                                <h6 class="mb-1">
                                    ${item.nome}
                                </h6>

                                <small class="text-muted">
                                    R$ ${formatarMoeda(preco)} cada
                                </small>

                            </div>


                            <button
                                type="button"
                                class="btn btn-sm btn-outline-danger btn-remover"
                                data-item-id="${item.itemId}"
                                title="Remover item">

                                <i class="bi bi-trash"></i>

                            </button>

                        </div>


                        <div class="d-flex justify-content-between align-items-center mt-3">

                            <div class="d-flex align-items-center">

                                <button
                                    type="button"
                                    class="btn btn-sm btn-light border btn-diminuir"
                                    data-item-id="${item.itemId}"
                                    data-quantidade="${quantidade}">

                                    <i class="bi bi-dash"></i>

                                </button>


                                <span class="mx-3">
                                    ${quantidade}
                                </span>


                                <button
                                    type="button"
                                    class="btn btn-sm btn-light border btn-aumentar"
                                    data-item-id="${item.itemId}"
                                    data-quantidade="${quantidade}">

                                    <i class="bi bi-plus"></i>

                                </button>

                            </div>


                            <strong>
                                R$ ${formatarMoeda(subtotal)}
                            </strong>

                        </div>

                    </div>
                `;

            }).join("");


            // =====================================
            // FOOTER
            // =====================================

            footer.innerHTML = `
                <div>

                    <div class="d-flex justify-content-between mb-3">

                        <span class="fw-semibold">
                            Total
                        </span>

                        <span class="fw-bold fs-5">
                            R$ ${formatarMoeda(total)}
                        </span>

                    </div>


                    <div class="d-grid gap-2">

                        <button
                            type="button"
                            class="btn btn-outline-danger"
                            id="esvaziarCarrinho">

                            <i class="bi bi-trash me-2"></i>

                            Esvaziar carrinho

                        </button>


                        <a
                            href="/checkout"
                            class="btn btn-red">

                            Finalizar compra

                        </a>

                    </div>

                </div>
            `;


            configurarBotoes();

        } catch (error) {

            console.error(
                "Erro ao carregar carrinho:",
                error
            );


            body.innerHTML = `
                <div class="alert alert-danger mb-0">
                    Não foi possível carregar o carrinho.
                </div>
            `;

        }

    }


    // =====================================
    // LOGIN NECESSÁRIO
    // =====================================

    function mostrarLoginNecessario() {

        body.innerHTML = `
            <div class="text-center py-5 px-3">

                <i class="bi bi-person-lock fs-1 text-muted"></i>

                <h5 class="mt-3">
                    Você precisa realizar o login para acessar o carrinho
                </h5>

                <p class="text-muted">
                    Faça login para visualizar e gerenciar seus produtos.
                </p>

                <a
                    href="/login"
                    class="btn btn-red mt-2">

                    <i class="bi bi-box-arrow-in-right me-2"></i>

                    Fazer login

                </a>

            </div>
        `;

        footer.innerHTML = "";
    }


    // =====================================
    // CARRINHO VAZIO
    // =====================================

    function mostrarCarrinhoVazio() {

        body.innerHTML = `
            <div class="text-center py-5">

                <i class="bi bi-cart-x fs-1 text-muted"></i>

                <h5 class="mt-3">
                    Seu carrinho está vazio
                </h5>

                <p class="text-muted mb-0">
                    Adicione produtos para começar sua compra.
                </p>

            </div>
        `;

        footer.innerHTML = "";
    }


    // =====================================
    // CONFIGURAR BOTÕES
    // =====================================

    function configurarBotoes() {


        // DIMINUIR

        document.querySelectorAll(".btn-diminuir")
            .forEach(botao => {

                botao.addEventListener("click", () => {

                    const itemId =
                        botao.dataset.itemId;

                    const quantidade =
                        Number(botao.dataset.quantidade);


                    if (quantidade > 1) {

                        alterarQuantidade(
                            itemId,
                            quantidade - 1
                        );

                    }

                });

            });


        // AUMENTAR

        document.querySelectorAll(".btn-aumentar")
            .forEach(botao => {

                botao.addEventListener("click", () => {

                    const itemId =
                        botao.dataset.itemId;

                    const quantidade =
                        Number(botao.dataset.quantidade);


                    alterarQuantidade(
                        itemId,
                        quantidade + 1
                    );

                });

            });


        // REMOVER

        document.querySelectorAll(".btn-remover")
            .forEach(botao => {

                botao.addEventListener("click", () => {

                    const itemId =
                        botao.dataset.itemId;


                    removerItem(itemId);

                });

            });


        // ESVAZIAR

        const botaoEsvaziar =
            document.getElementById(
                "esvaziarCarrinho"
            );


        if (botaoEsvaziar) {

            botaoEsvaziar.addEventListener(
                "click",
                esvaziarCarrinho
            );

        }

    }


    // =====================================
    // ALTERAR QUANTIDADE
    // =====================================

    async function alterarQuantidade(itemId, quantidade) {

        try {

            const response = await fetch(
                `/carrinho/item/${itemId}?quantidade=${quantidade}`,
                {
                    method: "PATCH",
                    headers: headersComCsrf()
                }
            );


            if (
                response.status === 401 ||
                (
                    response.redirected &&
                    response.url.includes("/login")
                )
            ) {

                window.location.href = "/login";

                return;
            }


            if (!response.ok) {

                throw new Error(
                    `Não foi possível alterar a quantidade. Status: ${response.status}`
                );

            }


            await carregarCarrinho();

        } catch (error) {

            console.error(error);

        }

    }


    // =====================================
    // REMOVER ITEM
    // =====================================

    async function removerItem(itemId) {

        try {

            const response = await fetch(
                `/carrinho/item/${itemId}`,
                {
                    method: "DELETE",
                    headers: headersComCsrf()
                }
            );


            if (
                response.status === 401 ||
                (
                    response.redirected &&
                    response.url.includes("/login")
                )
            ) {

                window.location.href = "/login";

                return;
            }


            if (!response.ok) {

                throw new Error(
                    `Não foi possível remover o item. Status: ${response.status}`
                );

            }


            await carregarCarrinho();

        } catch (error) {

            console.error(error);

        }

    }


    // =====================================
    // ESVAZIAR CARRINHO
    // =====================================

    async function esvaziarCarrinho() {

        try {

            const response = await fetch(
                "/carrinho",
                {
                    method: "DELETE",
                    headers: headersComCsrf()
                }
            );


            if (
                response.status === 401 ||
                (
                    response.redirected &&
                    response.url.includes("/login")
                )
            ) {

                window.location.href = "/login";

                return;
            }


            if (!response.ok) {

                throw new Error(
                    `Não foi possível esvaziar o carrinho. Status: ${response.status}`
                );

            }


            await carregarCarrinho();

        } catch (error) {

            console.error(error);

        }

    }


    // =====================================
    // ADICIONAR PRODUTO
    // =====================================

    const formAdicionarCarrinho =
        document.getElementById("formAdicionarCarrinho");


    if (formAdicionarCarrinho &&
        !formAdicionarCarrinho.dataset.listenerAdicionado) {

        formAdicionarCarrinho.dataset.listenerAdicionado = "true";

        formAdicionarCarrinho.addEventListener(
            "submit",
            async event => {

                event.preventDefault();

                const botao =
                    formAdicionarCarrinho.querySelector(
                        'button[type="submit"]'
                    );

                // Evita múltiplos cliques
                if (botao.disabled) {
                    return;
                }

                botao.disabled = true;

                try {

                    const formData =
                        new FormData(formAdicionarCarrinho);


                    const response = await fetch(
                        formAdicionarCarrinho.action,
                        {
                            method: "POST",
                            headers: headersComCsrf(),
                            body: formData
                        }
                    );


                    // =====================================
                    // NÃO LOGADO
                    // =====================================

                    if (
                        response.status === 401 ||
                        (
                            response.redirected &&
                            response.url.includes("/login")
                        )
                    ) {

                        window.location.href = "/login";

                        return;
                    }


                    if (!response.ok) {

                        throw new Error(
                            `Não foi possível adicionar o produto. Status: ${response.status}`
                        );

                    }


                    // =====================================
                    // ABRIR CARRINHO
                    // =====================================

                    const offcanvasInstance =
                        bootstrap.Offcanvas.getOrCreateInstance(
                            offcanvas
                        );

                    offcanvasInstance.show();


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
    }


    // =====================================
    // FORMATAR MOEDA
    // =====================================

    function formatarMoeda(valor) {

        return Number(valor).toLocaleString(
            "pt-BR",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }
        );

    }

});