
package es.mariana.dweb.model.entities;

import java.util.Comparator;

public class PokemonNameComparator implements Comparator<Pokemon> {

    @Override
    public int compare(Pokemon p1, Pokemon p2) {
        return p1.getName().compareToIgnoreCase(p2.getName());
    }
}
