package es.masanz.proyectodemo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingController {

    @GetMapping("/")
    public String greeting(Model model) {
        // Añadimos un atributo al modelo para enviarlo a la vista HTML
        model.addAttribute("name", "Endika");

        // Retorna el nombre de la plantilla HTML que se va a cargar (index.html)
        return "index";
    }
}