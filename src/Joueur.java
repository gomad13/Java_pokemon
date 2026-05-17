import java.util.Scanner;

// Un joueur a un nom et une equipe de Pokemon.
// Pendant le combat, il choisit ses attaques et son prochain pokemon
// en tapant ses choix au clavier.
public class Joueur {

    protected String nom;
    protected Equipe equipe;
    protected Scanner clavier;

    public Joueur(String nom, Equipe equipe, Scanner clavier) {
        this.nom = nom;
        this.equipe = equipe;
        this.clavier = clavier;
    }

    // Demande au joueur quelle attaque utiliser parmi celles du pokemon
    public Attaque choisirAttaque(Pokemon p) {
        System.out.println("\nAttaques de " + p.getNom() + " :");
        for (int i = 0; i < p.getNombreAttaques(); i++) {
            System.out.println((i + 1) + " - " + p.getListeAttaques().get(i));
        }

        int choix = -1;
        while (choix < 1 || choix > p.getNombreAttaques()) {
            System.out.print("Votre choix : ");
            try {
                choix = Integer.parseInt(clavier.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide, entrez un nombre.");
                choix = -1;
            }
        }

        return p.getListeAttaques().get(choix - 1);
    }

    // Quand le joueur veut changer de pokemon en plein milieu du combat.
    // Refuse le pokemon deja en combat et les KO.
    public Pokemon changerPokemonVolontaire() {
        int indexActuel = equipe.getIndexActif();
        System.out.println("\nVotre equipe :");
        for (int i = 0; i < equipe.getNombrePokemon(); i++) {
            Pokemon p = equipe.getPokemon(i);
            String etat = "";
            if (p.estKO()) {
                etat = " (KO)";
            } else if (i == indexActuel) {
                etat = " (en combat)";
            }
            System.out.println((i + 1) + " - " + p + etat);
        }

        int choix = -1;
        while (choix < 1 || choix > equipe.getNombrePokemon()
                || equipe.getPokemon(choix - 1).estKO()
                || (choix - 1) == indexActuel) {
            System.out.print("Quel Pokemon envoyer ? ");
            try {
                choix = Integer.parseInt(clavier.nextLine());
                if (choix >= 1 && choix <= equipe.getNombrePokemon()) {
                    if (equipe.getPokemon(choix - 1).estKO()) {
                        System.out.println("Ce Pokemon est KO, choisissez en un autre.");
                    } else if ((choix - 1) == indexActuel) {
                        System.out.println("Ce Pokemon est deja en combat.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide.");
                choix = -1;
            }
        }

        equipe.setIndexActif(choix - 1);
        return equipe.getPokemonActif();
    }

    // Quand le pokemon actif est KO, le joueur en envoie un autre
    public Pokemon choisirPokemonSuivant() {
        System.out.println("\nVotre equipe :");
        for (int i = 0; i < equipe.getNombrePokemon(); i++) {
            Pokemon p = equipe.getPokemon(i);
            String etat = "";
            if (p.estKO()) {
                etat = " (KO)";
            }
            System.out.println((i + 1) + " - " + p + etat);
        }

        int choix = -1;
        while (choix < 1 || choix > equipe.getNombrePokemon()
                || equipe.getPokemon(choix - 1).estKO()) {
            System.out.print("Quel Pokemon envoyer ? ");
            try {
                choix = Integer.parseInt(clavier.nextLine());
                if (choix >= 1 && choix <= equipe.getNombrePokemon()
                        && equipe.getPokemon(choix - 1).estKO()) {
                    System.out.println("Ce Pokemon est KO, choisissez en un autre.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide.");
                choix = -1;
            }
        }

        equipe.setIndexActif(choix - 1);
        return equipe.getPokemonActif();
    }

    public String getNom() {
        return nom;
    }

    public Equipe getEquipe() {
        return equipe;
    }
}
