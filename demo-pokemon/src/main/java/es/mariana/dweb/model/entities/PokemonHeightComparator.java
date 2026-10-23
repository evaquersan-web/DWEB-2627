
package es.mariana.dweb.model.entities;

import java.util.Comparator;

public class PokemonHeightComparator implements Comparator<Pokemon> {

    @Override
    public int compare(Pokemon p1, Pokemon p2) {
        return Integer.compare(p1.getHeight(), p2.getHeight());
    }
}
