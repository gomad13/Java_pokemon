import java.util.Random;
import java.util.Scanner;

// Joueur controle par l'ordinateur.
// Il choisit ses attaques au hasard et envoie toujours
// le premier pokemon non KO de son equipe.
public class JoueurIA extends Joueur {

    private Random random;

    public JoueurIA(String nom, Equipe equipe, Scanner clavier) {
        super(nom, equipe, clavier);
        this.random = new Random();
    }

    @Override
    public Attaque choisirAttaque(Pokemon p) {
        int index = random.nextInt(p.getNombreAttaques());
        return p.getListeAttaques().get(index);
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
}
