package ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/checkout/retorno")
public class CheckoutRetornoController {

    @GetMapping("/sucesso")
    public String sucesso() {
        return "redirect:/meus-pedidos";
    }

    @GetMapping("/cancelado")
    public String cancelado() {
        return "redirect:/checkout";
    }

    @GetMapping("/expirado")
    public String expirado() {
        return "redirect:/checkout";
    }
}