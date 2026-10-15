
package ecommerce.service;

import ecommerce.entity.Pedidos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AsaasService {

    private final RestClient restClient;
    private final String successUrl;
    private final String cancelUrl;
    private final String expiredUrl;

    public AsaasService(
            RestClient.Builder restClientBuilder,
            @Value("${asaas.api-url}") String apiUrl,
            @Value("${asaas.api-key}") String apiKey,
            @Value("${asaas.callback.success-url}") String successUrl,
            @Value("${asaas.callback.cancel-url}") String cancelUrl,
            @Value("${asaas.callback.expired-url}") String expiredUrl) {

        this.restClient = restClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("access_token", apiKey)
                .defaultHeader("User-Agent", "Moblyar")
                .build();

        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.expiredUrl = expiredUrl;
    }

    public String criarCheckout(Pedidos pedido) {

        List<Map<String, Object>> itens = pedido.getItems()
                .stream()
                .map(item -> Map.<String, Object>of(
                        "name", item.getProduto().getNome(),
                        "quantity", item.getQuantidade(),
                        "value", item.getPreco()
                ))
                .toList();

        Map<String, Object> callback = Map.of(
                "successUrl", successUrl,
                "cancelUrl", cancelUrl,
                "expiredUrl", expiredUrl
        );

        Map<String, Object> requisicao = Map.of(
                "billingTypes", List.of("PIX", "CREDIT_CARD"),
                "chargeTypes", List.of("DETACHED"),
                "minutesToExpire", 60,
                "externalReference", "pedido-" + pedido.getId(),
                "callback", callback,
                "items", itens
        );

        Map<?, ?> resposta = restClient.post()
                .uri("/checkouts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requisicao)
                .retrieve()
                .body(Map.class);

        if (resposta == null || resposta.get("id") == null) {
            throw new IllegalStateException(
                    "O Asaas não retornou o identificador do checkout."
            );
        }

        return resposta.get("id").toString();
    }
}
