package ecommerce.controller;

import ecommerce.entity.Pedidos;
import ecommerce.entity.StatusPedido;
import ecommerce.service.PedidoService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Arrays;

@Controller
public class AdminController {

    private final PedidoService pedidoService;

    public AdminController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin/produtos";
    }

    @GetMapping("/admin/pedidos")
    public String pedidos(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim,
            @RequestParam(required = false) Double precoMin,
            @RequestParam(required = false) Double precoMax,
            @RequestParam(defaultValue = "0") int pagina,
            Model model,
            RedirectAttributes redirectAttributes) {

        StatusPedido statusSelecionado = null;

        if (status != null && !status.isBlank()) {
            try {
                statusSelecionado = StatusPedido.valueOf(status);
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute(
                        "erro", "O status selecionado é inválido."
                );
                return "redirect:/admin/pedidos";
            }
        }

        LocalDateTime inicio = dataInicio == null ? null : dataInicio.atStartOfDay();

        LocalDateTime fim = dataFim == null ? null : dataFim.atTime(LocalTime.MAX);

        try {Page<Pedidos> paginaPedidos = pedidoService.buscarPedidosAdmin(busca, statusSelecionado, inicio, fim, precoMin, precoMax, pagina, 10);

            model.addAttribute("paginaPedidos", paginaPedidos);
            model.addAttribute("statusDisponiveis",
                    Arrays.asList(StatusPedido.values()));

            model.addAttribute("busca", busca);
            model.addAttribute("statusSelecionado", status);
            model.addAttribute("dataInicio", dataInicio);
            model.addAttribute("dataFim", dataFim);
            model.addAttribute("precoMin", precoMin);
            model.addAttribute("precoMax", precoMax);

            return "admin/pedidos";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }

    @PostMapping("/admin/pedidos/{id}/status")
    public String atualizarStatus(
            @PathVariable Long id,
            @RequestParam String novoStatus,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String precoMin,
            @RequestParam(required = false) String precoMax,
            @RequestParam(defaultValue = "0") int pagina,
            RedirectAttributes redirectAttributes) {

        try {
            StatusPedido statusPedido = StatusPedido.valueOf(novoStatus);

            pedidoService.atualizarStatus(id, statusPedido);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Status do pedido atualizado com sucesso."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não foi possível atualizar o status do pedido."
            );
        }

        return montarRedirect(busca, status, dataInicio, dataFim, precoMin, precoMax, pagina
        );
    }

    private String montarRedirect(String busca, String status, String dataInicio, String dataFim, String precoMin, String precoMax, int pagina) {

        StringBuilder url = new StringBuilder("redirect:/admin/pedidos?");
        url.append("pagina=").append(Math.max(pagina, 0));

        adicionarParametro(url, "busca", busca);
        adicionarParametro(url, "status", status);
        adicionarParametro(url, "dataInicio", dataInicio);
        adicionarParametro(url, "dataFim", dataFim);
        adicionarParametro(url, "precoMin", precoMin);
        adicionarParametro(url, "precoMax", precoMax);

        return url.toString();
    }

    private void adicionarParametro(StringBuilder url, String nome, String valor) {

        if (valor != null && !valor.isBlank()) {
            url.append("&").append(nome).append("=").append(java.net.URLEncoder.encode(valor, java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}