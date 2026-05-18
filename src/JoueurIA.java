import java.util.Random;
import java.util.Scanner;

// Joueur controle par l'ordinateur.
// Selon son niveau de difficulte, il joue plus ou moins intelligemment :
//  - FACILE : attaques aleatoires, premier pokemon non KO pour le suivant
//  - MOYEN : choisit l'attaque la plus efficace contre l'adverse
//  - DIFFICILE : MOYEN + change strategiquement de pokemon
//  - IMPOSSIBLE : DIFFICILE (le bonus de stats est applique a la creation des pokemon)
public class JoueurIA extends Joueur {

    private Random random;
    private NiveauDifficulte niveau;

    public JoueurIA(String nom, Equipe equipe, Scanner clavier, NiveauDifficulte niveau) {
        super(nom, equipe, clavier);
        this.random = new Random();
        this.niveau = niveau;
    }

    public NiveauDifficulte getNiveau() {
        return niveau;
    }

    @Override
    public Attaque choisirAttaque(Pokemon p, Pokemon adverse) {
        // niveau FACILE : random sur les attaques
        if (niveau == NiveauDifficulte.FACILE) {
            int index = random.nextInt(p.getNombreAttaques());
            return p.getListeAttaques().get(index);
        }
        // niveau MOYEN/DIFFICILE/IMPOSSIBLE : on prend l'attaque qui fait le plus
        // de degats theoriques (multiplicateur de type * puissance)
        return choisirMeilleureAttaque(p, adverse);
    }

    @Override
    public Pokemon choisirPokemonSuivant() {
        // on prend le premier pokemon non KO trouve dans l'equipe
        for (int i = 0; i < equipe.getNombrePokemon(); i++) {
            if (!equipe.getPokemon(i).estKO()) {
                equipe.setIndexActif(i);
                return equipe.getPokemonActif();
            }
        }
        // ne devrait pas arriver : si tous sont KO, l'equipe est vaincue
        return null;
    }

    // Pour DIFFICILE et IMPOSSIBLE : retourne true si l'IA doit changer
    // de pokemon avant d'attaquer parce que son matchup est defavorable.
    public boolean doitChanger(Pokemon adverse) {
        if (niveau != NiveauDifficulte.DIFFICILE && niveau != NiveauDifficulte.IMPOSSIBLE) {
            return false;
        }
        Pokemon actif = equipe.getPokemonActif();
        double mult = TableTypes.getMultiplicateur(actif.getType(), adverse.getType());
        if (mult >= 1.0) {
            return false;
        }
        // on ne change que si on a un meilleur pokemon dispo
        return trouverMeilleurPokemon(adverse) != -1;
    }

    // Effectue le changement vers le meilleur pokemon contre l'adverse.
    // A appeler seulement apres avoir verifie doitChanger().
    public Pokemon changerVersMeilleur(Pokemon adverse) {
        int index = trouverMeilleurPokemon(adverse);
        if (index == -1) {
            // securite : on ne devrait pas etre la
            return equipe.getPokemonActif();
        }
        equipe.setIndexActif(index);
        return equipe.getPokemonActif();
    }

    // Cherche dans l'equipe le premier pokemon non KO (different de l'actif)
    // qui aurait un multiplicateur >= 1.0 contre le type adverse.
    // Renvoie -1 si aucun candidat.
    private int trouverMeilleurPokemon(Pokemon adverse) {
        int indexActuel = equipe.getIndexActif();
        for (int i = 0; i < equipe.getNombrePokemon(); i++) {
            if (i == indexActuel) {
                continue;
            }
            Pokemon p = equipe.getPokemon(i);
            if (p.estKO()) {
                continue;
            }
            double mult = TableTypes.getMultiplicateur(p.getType(), adverse.getType());
            if (mult >= 1.0) {
                return i;
            }
        }
        return -1;
    }

    // Parcourt les attaques et garde celle qui a le meilleur score
    // (multiplicateur de type * puissance).
    private Attaque choisirMeilleureAttaque(Pokemon monPokemon, Pokemon adverse) {
        Attaque meilleure = monPokemon.getListeAttaques().get(0);
        double meilleurScore = -1.0;
        for (int i = 0; i < monPokemon.getNombreAttaques(); i++) {
            Attaque a = monPokemon.getListeAttaques().get(i);
            double mult = TableTypes.getMultiplicateur(a.getType(), adverse.getType());
            double score = mult * a.getPuissance();
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleure = a;
            }
        }
        return meilleure;
    }
}
