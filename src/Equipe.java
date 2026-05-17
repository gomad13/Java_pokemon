import java.util.ArrayList;

// Une equipe contient au maximum 3 Pokemon.
// On garde aussi en memoire le pokemon actuellement en combat
// avec un index (indexActif).
public class Equipe {

    private ArrayList<Pokemon> listePokemon;
    private int indexActif;

    public Equipe() {
        this.listePokemon = new ArrayList<Pokemon>();
        this.indexActif = 0;
    }

    public void ajouterPokemon(Pokemon p) {
        if (listePokemon.size() < 3) {
            listePokemon.add(p);
        }
    }

    // Retourne true si tous les pokemon de l'equipe sont KO
    public boolean estVaincue() {
        for (int i = 0; i < listePokemon.size(); i++) {
            if (!listePokemon.get(i).estKO()) {
                return false;
            }
        }
        return true;
    }

    public Pokemon getPokemonActif() {
        return listePokemon.get(indexActif);
    }

    public void setIndexActif(int index) {
        this.indexActif = index;
    }

    public int getIndexActif() {
        return indexActif;
    }

    public Pokemon getPokemon(int i) {
        return listePokemon.get(i);
    }

    public int getNombrePokemon() {
        return listePokemon.size();
    }

    public ArrayList<Pokemon> getListePokemon() {
        return listePokemon;
    }
}
