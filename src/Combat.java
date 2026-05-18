import java.time.LocalDate;
import java.util.Scanner;

// Gere le deroulement d'un combat entre deux joueurs.
// Le combat continue tant qu'une equipe n'est pas entierement KO.
// Pour rester simple, le joueur 1 attaque toujours en premier dans le tour,
// puis le joueur 2 ripost s'il est encore en vie.
public class Combat {

    private Joueur joueur1;
    private Joueur joueur2;
    private Scanner clavier;
    private BaseDeDonnees bdd;
    private int nbTours = 0;
    private boolean enregistrerScore;

    public Combat(Joueur joueur1, Joueur joueur2, Scanner clavier, BaseDeDonnees bdd) {
        this(joueur1, joueur2, clavier, bdd, true);
    }

    // Constructeur avec controle de l'enregistrement du score.
    // Utile pour le tournoi : on n'enregistre pas chaque combat, mais un seul score a la fin.
    public Combat(Joueur joueur1, Joueur joueur2, Scanner clavier, BaseDeDonnees bdd, boolean enregistrerScore) {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.clavier = clavier;
        this.bdd = bdd;
        this.enregistrerScore = enregistrerScore;
    }

    public int getNbTours() {
        return nbTours;
    }

    // Boucle principale du combat. Retourne true si le joueur 1 a gagne.
    public boolean lancerCombat() {
        System.out.println("\n=== DEBUT DU COMBAT ===");
        System.out.println(joueur1.getNom() + " contre " + joueur2.getNom());

        while (!joueur1.getEquipe().estVaincue() && !joueur2.getEquipe().estVaincue()) {
            afficherEtat();
            tourDeJeu();
        }

        System.out.println("\n=== FIN DU COMBAT ===");
        boolean joueur1Gagne = !joueur1.getEquipe().estVaincue();
        String resultat;
        if (joueur1Gagne) {
            System.out.println(joueur1.getNom() + " a gagne !");
            resultat = "Victoire";
        } else {
            System.out.println(joueur2.getNom() + " a gagne !");
            resultat = "Defaite";
        }

        // recap detaille du combat avant l'enregistrement
        afficherRecap(joueur1Gagne);

        // enregistrement du score en base (sauf si on est dans un tournoi)
        if (enregistrerScore) {
            String date = LocalDate.now().toString();
            bdd.ajouterScore(joueur1.getNom(), resultat, nbTours, date);
            System.out.println("Score enregistre.");
        }

        return joueur1Gagne;
    }

    // Un tour : le joueur 1 choisit (attaquer ou changer), puis l'IA attaque
    // (ou change strategiquement si elle est en DIFFICILE/IMPOSSIBLE).
    private void tourDeJeu() {
        nbTours++;
        Pokemon p1 = joueur1.getEquipe().getPokemonActif();
        Pokemon p2 = joueur2.getEquipe().getPokemonActif();

        int action = demanderAction();

        if (action == 2) {
            // changement de pokemon : l'IA garde son tour d'attaque
            Pokemon nouveau = joueur1.changerPokemonVolontaire();
            System.out.println(joueur1.getNom() + " retire " + p1.getNom()
                    + " et envoie " + nouveau.getNom() + " !");
            p1 = nouveau;

            // l'IA peut changer aussi ou attaquer le nouveau pokemon
            if (jouerTourIA(p2, p1)) {
                return;
            }

            if (p1.estKO()) {
                System.out.println(p1.getNom() + " est KO !");
                if (!joueur1.getEquipe().estVaincue()) {
                    Pokemon suivant = joueur1.choisirPokemonSuivant();
                    System.out.println(joueur1.getNom() + " envoie " + suivant.getNom() + " !");
                }
            }
            return;
        }

        // action 1 : attaque normale
        Attaque a1 = joueur1.choisirAttaque(p1, p2);
        executerAttaque(p1, p2, a1);

        // si on a mis KO le pokemon adverse, il doit en envoyer un autre
        if (p2.estKO()) {
            System.out.println(p2.getNom() + " est KO !");
            if (!joueur2.getEquipe().estVaincue()) {
                Pokemon nouveau = joueur2.choisirPokemonSuivant();
                System.out.println(joueur2.getNom() + " envoie " + nouveau.getNom() + " !");
            }
            return; // le tour s'arrete ici, pas de riposte
        }

        // l'IA decide : changer de pokemon ou attaquer
        jouerTourIA(p2, p1);

        if (p1.estKO()) {
            System.out.println(p1.getNom() + " est KO !");
            if (!joueur1.getEquipe().estVaincue()) {
                Pokemon nouveau = joueur1.choisirPokemonSuivant();
                System.out.println(joueur1.getNom() + " envoie " + nouveau.getNom() + " !");
            }
        }
    }

    // Fait jouer le joueur 2 : s'il est une IA en DIFFICILE/IMPOSSIBLE et
    // que doitChanger renvoie true, il change de pokemon (le tour est consomme).
    // Sinon il attaque normalement.
    // Retourne true si l'IA a change (et donc n'a pas attaque), false sinon.
    private boolean jouerTourIA(Pokemon p2, Pokemon p1) {
        if (joueur2 instanceof JoueurIA) {
            JoueurIA ia = (JoueurIA) joueur2;
            if (ia.doitChanger(p1)) {
                Pokemon nouveau = ia.changerVersMeilleur(p1);
                System.out.println(joueur2.getNom() + " retire " + p2.getNom()
                        + " et envoie " + nouveau.getNom() + " !");
                return true;
            }
        }
        Attaque a2 = joueur2.choisirAttaque(p2, p1);
        executerAttaque(p2, p1, a2);
        return false;
    }

    // Mini menu : 1 = attaquer, 2 = changer de pokemon.
    // Si le joueur n'a plus de pokemon de rechange, on saute le menu.
    private int demanderAction() {
        if (!peutChangerDePokemon(joueur1)) {
            System.out.println("\nVous n'avez plus de Pokemon en reserve, vous etes oblige d'attaquer.");
            return 1;
        }

        System.out.println("\nQue voulez-vous faire ?");
        System.out.println("1 - Attaquer");
        System.out.println("2 - Changer de Pokemon");
        int choix = -1;
        while (choix != 1 && choix != 2) {
            System.out.print("Votre choix : ");
            try {
                choix = Integer.parseInt(clavier.nextLine());
                if (choix != 1 && choix != 2) {
                    System.out.println("Choisissez 1 ou 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide.");
                choix = -1;
            }
        }
        return choix;
    }

    // Renvoie true si le joueur a au moins un pokemon non-KO autre que celui en combat
    private boolean peutChangerDePokemon(Joueur j) {
        Equipe eq = j.getEquipe();
        int actuel = eq.getIndexActif();
        for (int i = 0; i < eq.getNombrePokemon(); i++) {
            if (i != actuel && !eq.getPokemon(i).estKO()) {
                return true;
            }
        }
        return false;
    }

    // Affiche le message d'attaque, calcule les degats et les applique
    private void executerAttaque(Pokemon attaquant, Pokemon defenseur, Attaque attaque) {
        System.out.println("\n" + attaquant.getNom() + " utilise " + attaque.getNom() + " !");

        double multi = TableTypes.getMultiplicateur(attaque.getType(), defenseur.getType());
        int degats = calculerDegats(attaquant, defenseur, attaque);

        if (multi >= 2.0) {
            System.out.println("C'est super efficace !");
        } else if (multi > 0 && multi < 1.0) {
            System.out.println("Ce n'est pas tres efficace...");
        } else if (multi == 0.0) {
            System.out.println("Ca n'a aucun effet sur " + defenseur.getNom() + ".");
        }

        defenseur.subirDegats(degats);
        System.out.println(defenseur.getNom() + " perd " + degats + " PV (il lui reste "
                + defenseur.getPv() + " PV).");
    }

    // Formule de degats simplifiee, inspiree de la formule des vrais jeux Pokemon
    // mais en plus simple : (puissance * attaque / defense) / 5 + 2, multiplie par
    // le coefficient du type.
    private int calculerDegats(Pokemon attaquant, Pokemon defenseur, Attaque attaque) {
        double multi = TableTypes.getMultiplicateur(attaque.getType(), defenseur.getType());
        double base = ((double) attaque.getPuissance() * attaquant.getAttaque()
                / defenseur.getDefense()) / 5.0 + 2.0;
        int degats = (int) (base * multi);
        // on garantit au moins 1 de degats si le type n'est pas immun
        if (degats < 1 && multi > 0) {
            degats = 1;
        }
        return degats;
    }

    // Affiche les pokemon en cours et leurs PV
    private void afficherEtat() {
        System.out.println("\n----------------------------------------");
        Pokemon p1 = joueur1.getEquipe().getPokemonActif();
        Pokemon p2 = joueur2.getEquipe().getPokemonActif();
        System.out.println(joueur1.getNom() + " : " + p1);
        System.out.println(joueur2.getNom() + " : " + p2);
        System.out.println("----------------------------------------");
    }

    // Affiche un recap detaille du combat : vainqueur, nombre de tours,
    // etat de chaque pokemon (vivant + PV ou KO), survivants par equipe.
    private void afficherRecap(boolean joueur1Gagne) {
        System.out.println("\n======= RECAPITULATIF DU COMBAT =======");
        System.out.println();
        if (joueur1Gagne) {
            System.out.println("Vainqueur : " + joueur1.getNom());
        } else {
            System.out.println("Vainqueur : " + joueur2.getNom());
        }
        System.out.println("Nombre de tours : " + nbTours);
        System.out.println();

        System.out.println("Vos Pokemon :");
        int survivantsJ1 = afficherListePokemon(joueur1.getEquipe());

        System.out.println();
        System.out.println("Pokemon adverses :");
        int survivantsJ2 = afficherListePokemon(joueur2.getEquipe());

        System.out.println();
        System.out.println("Pokemon survivants : " + survivantsJ1 + " sur "
                + joueur1.getEquipe().getNombrePokemon() + " pour vous, "
                + survivantsJ2 + " sur " + joueur2.getEquipe().getNombrePokemon()
                + " pour l'adversaire");
        System.out.println("=======================================");
    }

    // Affiche chaque pokemon de l'equipe (vivant ou KO) et compte les survivants
    private int afficherListePokemon(Equipe eq) {
        int survivants = 0;
        for (int i = 0; i < eq.getNombrePokemon(); i++) {
            Pokemon p = eq.getPokemon(i);
            if (p.estKO()) {
                System.out.println(" - " + p.getNom() + " : KO");
            } else {
                System.out.println(" - " + p.getNom() + " : Vivant ("
                        + p.getPv() + "/" + p.getPvMax() + " PV)");
                survivants++;
            }
        }
        return survivants;
    }
}
