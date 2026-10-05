package es.mariana.dweb.tienda;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TiendaApplication.class);
    }
    @GetMapping("/home1")
    public String home1() {
        return "home1"; 
    }
}
