package es.mariana.dweb.model.service;

import es.mariana.dweb.model.entities.Pokemon;
import es.mariana.dweb.model.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PokemonService {

    @Autowired
    private PokemonRepository pokemonRepository;

    public PokemonService() {
        super();
    }

    public Pokemon findByName(String name) {
        return pokemonRepository.findByName(name);
    }

    public Pokemon findRandom() {
        return pokemonRepository.findRandom();
    }

    public Pokemon findById(int id) {
        return pokemonRepository.findById(id);
    }

    public ArrayList<Pokemon> findRandomList(int limit) {
        return pokemonRepository.findRandomList(limit);
    }

    public ArrayList<Pokemon> findAll() {
        return pokemonRepository.findAll();
    }

    /* Se conserva para que el controlador actual siga funcionando. */
    public ArrayList<Pokemon> obtenerPokemons() {
        return findAll();
    }
}
