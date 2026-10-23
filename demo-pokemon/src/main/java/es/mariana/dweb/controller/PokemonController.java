
package es.mariana.dweb.controller;

import es.mariana.dweb.model.entities.Pokemon;
import es.mariana.dweb.model.entities.PokemonNameComparator;
import es.mariana.dweb.model.entities.PokemonHeightComparator;
import es.mariana.dweb.model.entities.PokemonWeightComparator;
import es.mariana.dweb.model.service.PokemonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

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
        model.addAttribute("presentacion",
                "Aquí tienes algunos de los pokemon seleccionados de la pokedex");
        model.addAttribute("fechaActual", LocalDateTime.now());
        model.addAttribute("pokemon", pokemon);

        return "informacion";
    }

    @GetMapping("/pokemon/galeria")
    public String galeria(
            @RequestParam(required = false, defaultValue = "") String tipo,
            @RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false, defaultValue = "") String alturaMin,
            @RequestParam(required = false, defaultValue = "") String pesoMax,
            @RequestParam(required = false, defaultValue = "") String orden,
            @RequestParam(required = false, defaultValue = "asc") String direccion,
            Model model) {

        ArrayList<Pokemon> pokemons;

        if (tipo.isBlank()) {
            pokemons = pokemonService.findRandomList(12);
        } else {
            pokemons = pokemonService.findByType(tipo);
        }

        pokemons = filtrarYOrdenar(
                pokemons, nombre, alturaMin, pesoMax, orden, direccion
        );

        prepararModeloFiltros(
                model, pokemons, tipo, nombre, alturaMin,
                pesoMax, orden, direccion, "/pokemon/galeria"
        );

        model.addAttribute("titulo", "Galería Pokémon");

        return "galeria-pokemon";
    }

    @GetMapping("/pokemon/tabla")
    public String mostrarTabla(
            @RequestParam(required = false, defaultValue = "") String tipo,
            @RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false, defaultValue = "") String alturaMin,
            @RequestParam(required = false, defaultValue = "") String pesoMax,
            @RequestParam(required = false, defaultValue = "") String orden,
            @RequestParam(required = false, defaultValue = "asc") String direccion,
            Model model) {

        ArrayList<Pokemon> pokemons;

        if (tipo.isBlank()) {
            pokemons = pokemonService.findRandomList(10);
        } else {
            pokemons = pokemonService.findByType(tipo);
        }

        pokemons = filtrarYOrdenar(
                pokemons, nombre, alturaMin, pesoMax, orden, direccion
        );

        prepararModeloFiltros(
                model, pokemons, tipo, nombre, alturaMin,
                pesoMax, orden, direccion, "/pokemon/tabla"
        );

        model.addAttribute("titulo", "Tabla de Pokémon");
        model.addAttribute("fechaHora", LocalDateTime.now());

        return "listado-tabla";
    }

    private ArrayList<Pokemon> filtrarYOrdenar(
            ArrayList<Pokemon> pokemons,
            String nombre,
            String alturaMin,
            String pesoMax,
            String orden,
            String direccion) {

        ArrayList<Pokemon> resultado = new ArrayList<>();

        String nombreBuscado = nombre == null
                ? ""
                : nombre.trim().toLowerCase(Locale.ROOT);

        Double altura = convertirNumero(alturaMin);
        Double peso = convertirNumero(pesoMax);

        for (Pokemon pokemon : pokemons) {

            // Filtro por nombre: coincidencia parcial.
            if (!nombreBuscado.isEmpty()
                    && !pokemon.getName().toLowerCase(Locale.ROOT)
                    .contains(nombreBuscado)) {
                continue;
            }

            // La altura de la API está expresada en decímetros.
            if (altura != null && pokemon.getHeight() < altura * 10) {
                continue;
            }

            // El peso de la API está expresado en hectogramos.
            if (peso != null && pokemon.getWeight() > peso * 10) {
                continue;
            }

            resultado.add(pokemon);
        }

        // Ordenación mediante los comparadores Java.
        Comparator<Pokemon> comparador = null;

        if ("nombre".equalsIgnoreCase(orden)) {
            comparador = new PokemonNameComparator();
        } else if ("altura".equalsIgnoreCase(orden)) {
            comparador = new PokemonHeightComparator();
        } else if ("peso".equalsIgnoreCase(orden)) {
            comparador = new PokemonWeightComparator();
        }

        if (comparador != null) {
            if ("desc".equalsIgnoreCase(direccion)) {
                comparador = comparador.reversed();
            }

            Collections.sort(resultado, comparador);
        }

        return resultado;
    }

    private Double convertirNumero(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(valor.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void prepararModeloFiltros(
            Model model,
            ArrayList<Pokemon> pokemons,
            String tipo,
            String nombre,
            String alturaMin,
            String pesoMax,
            String orden,
            String direccion,
            String rutaFormulario) {

        model.addAttribute("pokemons", pokemons);
        model.addAttribute("tipos", pokemonService.findAllTypes());

        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("nombre", nombre);
        model.addAttribute("alturaMin", alturaMin);
        model.addAttribute("pesoMax", pesoMax);
        model.addAttribute("orden", orden);
        model.addAttribute("direccion", direccion);
        model.addAttribute("rutaFormulario", rutaFormulario);
    }

    @GetMapping("/pokemon/{id}")
    public String detallePokemon(
            @PathVariable int id,
            Model model) {

        Pokemon pokemon = pokemonService.findById(id);

        model.addAttribute("titulo", "Detalle del Pokémon");
        model.addAttribute("presentacion", "Información del Pokémon seleccionado");
        model.addAttribute("fechaActual", LocalDateTime.now());
        model.addAttribute("pokemon", pokemon);

        return "informacion";
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
