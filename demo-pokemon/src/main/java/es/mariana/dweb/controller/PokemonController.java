package es.mariana.dweb.controller;

import es.mariana.dweb.model.entities.Pokemon;
import es.mariana.dweb.model.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @GetMapping("/")
    public String home(Model model) {
        ArrayList<Pokemon> pokemons = pokemonService.obtenerPokemons();
        model.addAttribute("pokemons", pokemons);

        return "home";
    }
    @GetMapping("/informacion")
    public String informacion(Model model) {
        Pokemon pokemon = pokemonService.findRandom();

        model.addAttribute("titulo", "Pokémon del Día");
        model.addAttribute("presentacion", "Aquí tienes algunos de los pokemon selecionados de la pokedex");
        model.addAttribute("fechaActual", LocalDateTime.now());
        model.addAttribute("pokemon", pokemon);

        return "informacion";
    }
    @GetMapping("/pokemon/galeria")
    public String galeria(Model model){
        model.addAttribute("pokemons",pokemonService.findRandomList(12));
        model.addAttribute("titulo","galeria pokemon");
        return "galeria-pokemon";
    }
    @GetMapping("/pokemon/tabla")
    public String mostrarTabla(Model model) {
        model.addAttribute("pokemons",pokemonService.findRandomList(10) );
        model.addAttribute("titulo", "tabla de pokemons");
        model.addAttribute("fechaHora", LocalDateTime.now());

        return "listado-tabla";
    }

    @GetMapping("/pokemon/batalla")
    public String batalla(Model model) {

        Pokemon pokemon1 = pokemonService.findRandom();
        Pokemon pokemon2 = pokemonService.findRandom();
        model.addAttribute("pokemon1", pokemon1);
        model.addAttribute("pokemon2", pokemon2);

        return "batalla-pokemon";
    }

}
