package br.com.leperber.prazoflow.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeWebController {

    @GetMapping({"/", "/painel"})
    public String home() {
        return "home";
    }
}
