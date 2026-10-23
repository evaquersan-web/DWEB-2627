package es.mariana.dweb.model.repositories;

import es.mariana.dweb.model.entities.Pokemon;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Repository
public class PokemonRepository {

    private static final String POKEMON_API_URL = "https://pokeapi.co/api/v2";
    private static final int MAX_POKEMON_ID = 1025;

    private final RestTemplate restTemplate;
    private final Random random;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PokemonRepository() {
        this(new RestTemplate());
    }

    public PokemonRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.random = new Random();
    }

    public Pokemon findByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String normalizedName = name.trim().toLowerCase(Locale.ROOT);
        return restTemplate.getForObject(POKEMON_API_URL + "/pokemon/" + normalizedName, Pokemon.class);
    }

    public Pokemon findRandom() {
        int randomId = random.nextInt(MAX_POKEMON_ID) + 1;
        return findById(randomId);
    }

    public Pokemon findById(int id) {
        if (id < 1 || id > MAX_POKEMON_ID) {
            throw new IllegalArgumentException("El id del pokémon debe estar entre 1 y " + MAX_POKEMON_ID);
        }

        return restTemplate.getForObject(POKEMON_API_URL + "/pokemon/" + id, Pokemon.class);
    }

    public ArrayList<Pokemon> findRandomList(int limit) {
        ArrayList<Pokemon> result = new ArrayList<>();
        if (limit <= 0) {
            return result;
        }

        Set<Integer> generatedIds = new HashSet<>();
        while (generatedIds.size() < Math.min(limit, MAX_POKEMON_ID)) {
            generatedIds.add(random.nextInt(MAX_POKEMON_ID) + 1);
        }

        for (Integer id : generatedIds) {
            Pokemon pokemon = findById(id);
            if (pokemon != null) {
                result.add(pokemon);
            }
            if (result.size() >= limit) {
                break;
            }
        }

        return result;
    }

    public ArrayList<Pokemon> findAll() {
        return findRandomList(10);
    }

    public ArrayList<Pokemon.Type> findAllTypes() {
        ArrayList<Pokemon.Type> tipos = new ArrayList<>();

        String json = restTemplate.getForObject(
                POKEMON_API_URL + "/type",
                String.class
        );

        JsonNode respuesta = objectMapper.readTree(json);

        if (respuesta != null && respuesta.has("results")) {
            for (JsonNode elemento : respuesta.get("results")) {
                Pokemon.Type tipo = new Pokemon.Type();
                tipo.setName(elemento.get("name").asText());
                tipos.add(tipo);
            }
        }

        return tipos;
    }

    public ArrayList<Pokemon> findByType(String typeName, int limit) {
        ArrayList<Pokemon> pokemons = new ArrayList<>();

        if (typeName == null || typeName.isBlank() || limit <= 0) {
            return pokemons;
        }

        String json = restTemplate.getForObject(
                POKEMON_API_URL + "/type/" + typeName.trim().toLowerCase(Locale.ROOT),
                String.class
        );

        JsonNode respuesta = objectMapper.readTree(json);

        if (respuesta != null && respuesta.has("pokemon")) {
            JsonNode resultados = respuesta.get("pokemon");

            for (JsonNode elemento : resultados) {
                if (pokemons.size() >= limit) {
                    break;
                }

                JsonNode datosPokemon = elemento.get("pokemon");
                String url = datosPokemon.get("url").asText();

                String urlSinBarraFinal = url.endsWith("/")
                        ? url.substring(0, url.length() - 1)
                        : url;

                int ultimaBarra = urlSinBarraFinal.lastIndexOf("/");
                int id = Integer.parseInt(
                        urlSinBarraFinal.substring(ultimaBarra + 1)
                );

                Pokemon pokemon = findById(id);

                if (pokemon != null) {
                    pokemons.add(pokemon);
                }
            }
        }

        return pokemons;
    }


}

