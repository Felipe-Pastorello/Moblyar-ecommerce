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

        // Garante que o widget fique dentro do card de entrega.
        if (!container.contains(widget)) {
            container.appendChild(widget);
        }

        // Só configura quando os elementos internos estiverem disponíveis.
        const cepInput = widget.querySelector('input[name="cep_destino"]');
        const widgetForm = widget.querySelector("[data-cc-form]");
        const resultados = widget.querySelector(".cc-results");

        if (cepInput && widgetForm && resultados) {
            widgetObserver.disconnect();
            configurarWidget(widget);
        }
    });

    widgetObserver.observe(document.body, {
        childList: true,
        subtree: true
    });

// Verifica se o widget já existe.
    const widgetExistente = document.querySelector(".cepcerto-frete");

    if (widgetExistente) {
        if (!container.contains(widgetExistente)) {
            container.appendChild(widgetExistente);
        }

        if (
            widgetExistente.querySelector('input[name="cep_destino"]') &&
            widgetExistente.querySelector("[data-cc-form]") &&
            widgetExistente.querySelector(".cc-results")
        ) {
            widgetObserver.disconnect();
            configurarWidget(widgetExistente);
        }
    }
});