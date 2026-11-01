document.addEventListener("DOMContentLoaded", () => {
    const subtotalElement = document.getElementById("subtotal");
    const freteElement = document.getElementById("valorFrete");
    const totalElement = document.getElementById("totalCheckout");

    const freteValorInput = document.getElementById("freteValor");
    const freteServicoInput = document.getElementById("freteServico");

    const subtotal = Number(subtotalElement.dataset.valor);

    const container = document.getElementById("frete-container");

    function atualizarTotal(valorFrete) {
        const total = subtotal + valorFrete;

        freteElement.textContent = valorFrete.toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL"
        });

        totalElement.textContent = total.toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL"
        });

        freteValorInput.value = valorFrete.toFixed(2);
    }

    function limparFrete() {
        atualizarTotal(0);
        freteElement.textContent = "A calcular";
        freteServicoInput.value = "";
    }

    function configurarWidget(widget) {
        const cepInput = widget.querySelector('input[name="cep_destino"]');
        const widgetForm = widget.querySelector("[data-cc-form]");
        const resultados = widget.querySelector(".cc-results");

        if (!cepInput || !widgetForm || !resultados) return;

        if (widget.dataset.configurado === "true") return;

        widget.dataset.configurado = "true";

        function calcularCep(cep) {
            const cepLimpo = cep.replace(/\D/g, "");

            if (cepLimpo.length !== 8) return;

            limparFrete();

            cepInput.value = cepLimpo.replace(/^(\d{5})(\d{3})$/, "$1-$2");
            cepInput.dataset.raw = cepLimpo;

            widgetForm.requestSubmit();
        }

        function atualizarFreteSelecionado() {
            const selecionado = widget.querySelector(
                'input[name="cepcerto_frete"]:checked'
            );

            if (!selecionado) {
                limparFrete();
                return;
            }

            const precoElement = selecionado
                .closest(".cc-result")
                ?.querySelector(".cc-price");

            if (!precoElement) return;

            const valor = Number(
                precoElement.textContent
                    .replace(/[^\d,]/g, "")
                    .replace(",", ".")
            );

            if (!Number.isFinite(valor)) return;

            atualizarTotal(valor);
            freteServicoInput.value = selecionado.value;
        }

        widget.addEventListener("change", event => {
            if (event.target.matches('input[name="cepcerto_frete"]')) {
                atualizarFreteSelecionado();
            }
        });

        const observer = new MutationObserver(() => {
            atualizarFreteSelecionado();
        });

        observer.observe(resultados, {
            childList: true,
            subtree: true,
            characterData: true
        });

        document.querySelectorAll('input[name="enderecoId"]').forEach(radio => {
            radio.addEventListener("change", () => {
                calcularCep(radio.dataset.cep || "");
            });
        });

        const enderecoSelecionado = document.querySelector(
            'input[name="enderecoId"]:checked'
        );

        if (enderecoSelecionado) {
            calcularCep(enderecoSelecionado.dataset.cep || "");
        }
    }


    // Aguarda o widget ser criado pelo CepCerto.
    const widgetObserver = new MutationObserver(() => {
        const widget = document.querySelector(".cepcerto-frete");

        if (!widget) return;

        const cepInput = widget.querySelector('input[name="cep_destino"]');
        const widgetForm = widget.querySelector("[data-cc-form]");
        const resultados = widget.querySelector(".cc-results");

        if (!cepInput || !widgetForm || !resultados) return;

        // Configura os eventos antes de mover o elemento.
        if (widget.dataset.configurado !== "true") {
            configurarWidget(widget);
        }

        // Posiciona o widget dentro do card.
        if (!container.contains(widget)) {
            container.appendChild(widget);
        }

        widgetObserver.disconnect();
    });

    widgetObserver.observe(document.body, {
        childList: true,
        subtree: true
    });

// Verifica se o widget já existe.
    const widgetExistente = document.querySelector(".cepcerto-frete");

    if (widgetExistente) {
        const cepInput = widgetExistente.querySelector('input[name="cep_destino"]');
        const widgetForm = widgetExistente.querySelector("[data-cc-form]");
        const resultados = widgetExistente.querySelector(".cc-results");

        if (cepInput && widgetForm && resultados) {
            configurarWidget(widgetExistente);

            if (!container.contains(widgetExistente)) {
                container.appendChild(widgetExistente);
            }

            widgetObserver.disconnect();
        }
    }

    const checkoutForm = document.querySelector('form[action*="/checkout/finalizar"]');

    checkoutForm.addEventListener("submit", event => {
        const enderecoSelecionado = document.querySelector(
            'input[name="enderecoId"]:checked'
        );

        const cep = enderecoSelecionado?.dataset.cep?.replace(/\D/g, "") || "";
        const freteServico = freteServicoInput.value.trim();

        if (cep.length !== 8 || !freteServico) {
            event.preventDefault();

            Swal.fire({
                icon: "warning",
                title: "Frete não selecionado",
                text: "Selecione um CEP válido e uma opção de frete antes de continuar.",
                confirmButtonText: "Entendi",
                confirmButtonColor: "#212529"
            });
        }
    });
});