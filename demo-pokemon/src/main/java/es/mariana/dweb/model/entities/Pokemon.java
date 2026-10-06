package es.mariana.dweb.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pokemon {
    private int id;
    private String name;
    private int height;
    private int weight;
    private int order;
    private int baseExperience;
    private boolean isDefault;
    private String imageURL;
    private Sprites sprites;
    private List<AbilityEntry> abilities = new ArrayList<>();
    private List<TypeEntry> types = new ArrayList<>();
    private List<StatEntry> stats = new ArrayList<>();
    private List<MoveEntry> moves = new ArrayList<>();
    private List<FormEntry> forms = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getBaseExperience() {
        return baseExperience;
    }

    @JsonProperty("base_experience")
    public void setBaseExperience(int baseExperience) {
        this.baseExperience = baseExperience;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @JsonProperty("is_default")
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
        if (sprites != null && sprites.getFrontDefault() != null) {
            this.imageURL = sprites.getFrontDefault();
        }
    }

    public List<AbilityEntry> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<AbilityEntry> abilities) {
        this.abilities = abilities;
    }

    public List<TypeEntry> getTypes() {
        return types;
    }

    public void setTypes(List<TypeEntry> types) {
        this.types = types;
    }

    public List<StatEntry> getStats() {
        return stats;
    }

    public void setStats(List<StatEntry> stats) {
        this.stats = stats;
    }

    public List<MoveEntry> getMoves() {
        return moves;
    }

    public void setMoves(List<MoveEntry> moves) {
        this.moves = moves;
    }

    public List<FormEntry> getForms() {
        return forms;
    }

    public void setForms(List<FormEntry> forms) {
        this.forms = forms;
    }

    public static class Sprites {
        @JsonProperty("front_default")
        private String frontDefault;
        @JsonProperty("front_shiny")
        private String frontShiny;
        @JsonProperty("back_default")
        private String backDefault;
        @JsonProperty("back_shiny")
        private String backShiny;

        public String getFrontDefault() {
            return frontDefault;
        }

        public void setFrontDefault(String frontDefault) {
            this.frontDefault = frontDefault;
        }

        public String getFrontShiny() {
            return frontShiny;
        }

        public void setFrontShiny(String frontShiny) {
            this.frontShiny = frontShiny;
        }

        public String getBackDefault() {
            return backDefault;
        }

        public void setBackDefault(String backDefault) {
            this.backDefault = backDefault;
        }

        public String getBackShiny() {
            return backShiny;
        }

        public void setBackShiny(String backShiny) {
            this.backShiny = backShiny;
        }
    }

    public static class AbilityEntry {
        private Ability ability;
        @JsonProperty("is_hidden")
        private boolean hidden;
        private int slot;

        public Ability getAbility() {
            return ability;
        }

        public void setAbility(Ability ability) {
            this.ability = ability;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }
    }

    public static class Ability {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class TypeEntry {
        private int slot;
        private Type type;

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

    public static class Type {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class StatEntry {
        @JsonProperty("base_stat")
        private int baseStat;
        private int effort;
        private Stat stat;

        public int getBaseStat() {
            return baseStat;
        }

        public void setBaseStat(int baseStat) {
            this.baseStat = baseStat;
        }

        public int getEffort() {
            return effort;
        }

        public void setEffort(int effort) {
            this.effort = effort;
        }

        public Stat getStat() {
            return stat;
        }

        public void setStat(Stat stat) {
            this.stat = stat;
        }
    }

    public static class Stat {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class MoveEntry {
        private Move move;

        public Move getMove() {
            return move;
        }

        public void setMove(Move move) {
            this.move = move;
        }
    }

    public static class Move {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class FormEntry {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
