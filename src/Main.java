import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

// Point d'entree du jeu.
// Menu principal en boucle : jouer, voir scores, gerer scores, quitter.
public class Main {

    public static void main(String[] args) {
        Scanner clavier = new Scanner(System.in);

        // connexion a la base de donnees
        BaseDeDonnees bdd = new BaseDeDonnees();
        try {
            bdd.seConnecter();
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC SQLite introuvable : " + e.getMessage());
            System.out.println("Verifiez que sqlite-jdbc.jar est bien dans le classpath.");
            return;
        } catch (SQLException e) {
            System.out.println("Impossible de se connecter a la base : " + e.getMessage());
            return;
        }

        // creation des tables et insertion des pokemon de depart si besoin
        bdd.initialiserBase();

        // boucle du menu principal
        boolean quitter = false;
        while (!quitter) {
            System.out.println("\n====== JEU POKEMON - COMBAT 3 vs 3 ======");
            System.out.println();
            System.out.println("Que voulez-vous faire ?");
            System.out.println("1 - Jouer une partie");
            System.out.println("2 - Voir le tableau des scores");
            System.out.println("3 - Gerer les scores");
            System.out.println("4 - Quitter");
            System.out.print("\nVotre choix : ");

            String saisie = clavier.nextLine().trim();
            int choix;
            try {
                choix = Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide.");
                continue;
            }

            switch (choix) {
                case 1:
                    sousMenuPartie(clavier, bdd);
                    break;
                case 2:
                    afficherScores(clavier, bdd);
                    break;
                case 3:
                    gererScores(clavier, bdd);
                    break;
                case 4:
                    quitter = true;
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }

        System.out.println("\nA bientot !");
        bdd.fermerConnexion();
        clavier.close();
    }

    // Sous-menu : match unique, tournoi, ou retour
    private static void sousMenuPartie(Scanner clavier, BaseDeDonnees bdd) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n====== TYPE DE PARTIE ======");
            System.out.println();
            System.out.println("1 - Match unique (1 vs 1 IA)");
            System.out.println("2 - Tournoi (3 combats a la suite)");
            System.out.println("3 - Retour au menu principal");
            System.out.print("\nVotre choix : ");

            String saisie = clavier.nextLine().trim();
            int choix;
            try {
                choix = Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide.");
                continue;
            }

            if (choix == 1) {
                jouerPartie(clavier, bdd);
                retour = true;
            } else if (choix == 2) {
                jouerTournoi(clavier, bdd);
                retour = true;
            } else if (choix == 3) {
                retour = true;
            } else {
                System.out.println("Choix invalide.");
            }
        }
    }

    // Demande au joueur le niveau de difficulte de l'IA
    private static NiveauDifficulte demanderDifficulte(Scanner clavier) {
        System.out.println("\n====== CHOISIR LA DIFFICULTE ======");
        System.out.println();
        System.out.println("1 - Facile (l'IA joue au hasard)");
        System.out.println("2 - Moyen (l'IA choisit ses meilleures attaques)");
        System.out.println("3 - Difficile (l'IA change strategiquement de Pokemon)");
        System.out.println("4 - Impossible (IA optimale + Pokemon boostes)");

        int choix = -1;
        while (choix < 1 || choix > 4) {
            System.out.print("\nVotre choix : ");
            try {
                choix = Integer.parseInt(clavier.nextLine().trim());
                if (choix < 1 || choix > 4) {
                    System.out.println("Choisissez un nombre entre 1 et 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide.");
                choix = -1;
            }
        }

        if (choix == 1) return NiveauDifficulte.FACILE;
        if (choix == 2) return NiveauDifficulte.MOYEN;
        if (choix == 3) return NiveauDifficulte.DIFFICILE;
        return NiveauDifficulte.IMPOSSIBLE;
    }

    // lance une partie complete : choix equipe + difficulte + combat
    private static void jouerPartie(Scanner clavier, BaseDeDonnees bdd) {
        ArrayList<Pokemon> tousLesPokemon = bdd.chargerTousLesPokemon();

        if (tousLesPokemon.size() < 3) {
            System.out.println("Il n'y a pas assez de Pokemon dans la base pour jouer.");
            return;
        }

        // saisie du nom du joueur
        System.out.print("\nEntrez votre nom : ");
        String nom = clavier.nextLine();
        if (nom.trim().equals("")) {
            nom = "Joueur";
        }

        // composition de l'equipe du joueur (avec pagination)
        Equipe equipeJoueur = new Equipe();
        choisirEquipeJoueur(clavier, tousLesPokemon, equipeJoueur);

        // choix de la difficulte
        NiveauDifficulte niveau = demanderDifficulte(clavier);

        // composition aleatoire de l'equipe de l'IA
        Equipe equipeIA = creerEquipeIA(tousLesPokemon, niveau);

        System.out.println("\nL'IA a compose son equipe :");
        for (int i = 0; i < equipeIA.getNombrePokemon(); i++) {
            System.out.println(" - " + equipeIA.getPokemon(i).getNom());
        }

        // creation des joueurs et lancement du combat
        Joueur joueur = new Joueur(nom, equipeJoueur, clavier);
        JoueurIA ia = new JoueurIA("Dresseur IA", equipeIA, clavier, niveau);

        Combat combat = new Combat(joueur, ia, clavier, bdd);
        combat.lancerCombat();
    }

    // Lance un tournoi : 3 combats successifs contre 3 IA differentes.
    // Le joueur garde la meme equipe (soignee entre les combats).
    private static void jouerTournoi(Scanner clavier, BaseDeDonnees bdd) {
        ArrayList<Pokemon> tousLesPokemon = bdd.chargerTousLesPokemon();

        if (tousLesPokemon.size() < 3) {
            System.out.println("Il n'y a pas assez de Pokemon dans la base pour jouer.");
            return;
        }

        // saisie du nom du joueur
        System.out.print("\nEntrez votre nom : ");
        String nom = clavier.nextLine();
        if (nom.trim().equals("")) {
            nom = "Joueur";
        }

        // composition de l'equipe du joueur (une seule fois pour tout le tournoi)
        Equipe equipeJoueur = new Equipe();
        choisirEquipeJoueur(clavier, tousLesPokemon, equipeJoueur);

        // choix de la difficulte (applique a TOUTES les IA du tournoi)
        NiveauDifficulte niveau = demanderDifficulte(clavier);

        Joueur joueur = new Joueur(nom, equipeJoueur, clavier);

        int totalTours = 0;
        boolean elimine = false;
        int combatAtteint = 0;

        for (int i = 1; i <= 3; i++) {
            combatAtteint = i;
            System.out.println("\n=== COMBAT " + i + "/3 contre Dresseur IA " + i + " ===");

            // nouvelle equipe IA a chaque combat
            Equipe equipeIA = creerEquipeIA(tousLesPokemon, niveau);
            System.out.println("\nDresseur IA " + i + " a compose son equipe :");
            for (int j = 0; j < equipeIA.getNombrePokemon(); j++) {
                System.out.println(" - " + equipeIA.getPokemon(j).getNom());
            }

            JoueurIA ia = new JoueurIA("Dresseur IA " + i, equipeIA, clavier, niveau);

            // on n'enregistre pas le score combat par combat, juste un score consolide a la fin
            Combat combat = new Combat(joueur, ia, clavier, bdd, false);
            boolean joueurGagne = combat.lancerCombat();
            totalTours += combat.getNbTours();

            if (!joueurGagne) {
                elimine = true;
                break;
            }

            // soin complet + resurrection entre les combats (sauf apres le dernier)
            if (i < 3) {
                equipeJoueur.soignerEquipe();
                System.out.println("\nVos Pokemon sont soignes avant le prochain combat.");
            }
        }

        // recap du tournoi et enregistrement d'un seul score consolide
        System.out.println("\n======= FIN DU TOURNOI =======");
        String resultat;
        if (elimine) {
            System.out.println("Vous avez ete elimine au combat " + combatAtteint + "/3.");
            resultat = "Defaite (tournoi)";
        } else {
            System.out.println("Felicitations, vous etes le champion du tournoi !");
            resultat = "Victoire (tournoi)";
        }
        System.out.println("Total de tours joues : " + totalTours);

        bdd.ajouterScore(nom, resultat, totalTours, LocalDate.now().toString());
        System.out.println("Score enregistre.");
    }

    // Cree une equipe aleatoire de 3 pokemon pour l'IA.
    // En mode IMPOSSIBLE, les pokemon ont +30% d'attaque et de defense
    // (PV inchanges, arrondi a l'entier superieur).
    private static Equipe creerEquipeIA(ArrayList<Pokemon> tousLesPokemon, NiveauDifficulte niveau) {
        Equipe equipeIA = new Equipe();
        Random random = new Random();
        ArrayList<Pokemon> disponibles = new ArrayList<Pokemon>(tousLesPokemon);
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(disponibles.size());
            Pokemon original = disponibles.get(index);
            if (niveau == NiveauDifficulte.IMPOSSIBLE) {
                equipeIA.ajouterPokemon(copierPokemonBooste(original));
            } else {
                equipeIA.ajouterPokemon(copierPokemon(original));
            }
            disponibles.remove(index);
        }
        return equipeIA;
    }

    // affiche le tableau des scores et attend une touche pour revenir
    private static void afficherScores(Scanner clavier, BaseDeDonnees bdd) {
        ArrayList<String> scores = bdd.chargerTousLesScores();
        System.out.println("\n====== TABLEAU DES SCORES ======");
        if (scores.size() == 0) {
            System.out.println("Aucun score enregistre pour le moment.");
        } else {
            for (int i = 0; i < scores.size(); i++) {
                System.out.println(scores.get(i));
            }
        }
        System.out.print("\nAppuyez sur Entree pour revenir au menu.");
        clavier.nextLine();
    }

    // sous-menu de gestion des scores (modifier, supprimer, tout effacer)
    private static void gererScores(Scanner clavier, BaseDeDonnees bdd) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n====== GERER LES SCORES ======");
            System.out.println("1 - Modifier le nom d'un joueur dans le tableau");
            System.out.println("2 - Supprimer un score");
            System.out.println("3 - Effacer tout l'historique");
            System.out.println("4 - Retour au menu principal");
            System.out.print("\nVotre choix : ");

            String saisie = clavier.nextLine().trim();
            int choix;
            try {
                choix = Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide.");
                continue;
            }

            if (choix == 4) {
                retour = true;
            } else if (choix == 1) {
                modifierUnScore(clavier, bdd);
            } else if (choix == 2) {
                supprimerUnScore(clavier, bdd);
            } else if (choix == 3) {
                effacerTout(clavier, bdd);
            } else {
                System.out.println("Choix invalide.");
            }
        }
    }

    private static void modifierUnScore(Scanner clavier, BaseDeDonnees bdd) {
        ArrayList<String> scores = bdd.chargerScoresAvecId();
        if (scores.size() == 0) {
            System.out.println("Aucun score enregistre pour le moment.");
            return;
        }
        System.out.println("\n--- Liste des scores ---");
        for (int i = 0; i < scores.size(); i++) {
            System.out.println(scores.get(i));
        }
        System.out.print("ID du score a modifier : ");
        int id;
        try {
            id = Integer.parseInt(clavier.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Saisie invalide.");
            return;
        }
        System.out.print("Nouveau nom : ");
        String nouveauNom = clavier.nextLine().trim();
        if (nouveauNom.equals("")) {
            System.out.println("Nom vide, modification annulee.");
            return;
        }
        bdd.modifierNomJoueurScore(id, nouveauNom);
    }

    private static void supprimerUnScore(Scanner clavier, BaseDeDonnees bdd) {
        ArrayList<String> scores = bdd.chargerScoresAvecId();
        if (scores.size() == 0) {
            System.out.println("Aucun score enregistre pour le moment.");
            return;
        }
        System.out.println("\n--- Liste des scores ---");
        for (int i = 0; i < scores.size(); i++) {
            System.out.println(scores.get(i));
        }
        System.out.print("ID du score a supprimer : ");
        int id;
        try {
            id = Integer.parseInt(clavier.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Saisie invalide.");
            return;
        }
        System.out.print("Confirmer la suppression du score " + id + " ? (O/N) : ");
        String conf = clavier.nextLine().trim();
        if (conf.equalsIgnoreCase("O")) {
            bdd.supprimerScore(id);
        } else {
            System.out.println("Suppression annulee.");
        }
    }

    private static void effacerTout(Scanner clavier, BaseDeDonnees bdd) {
        System.out.println("Etes-vous sur ? Cela supprimera TOUS les scores.");
        System.out.print("Confirmer ? (O/N) : ");
        String conf = clavier.nextLine().trim();
        if (conf.equalsIgnoreCase("O")) {
            bdd.supprimerTousLesScores();
        } else {
            System.out.println("Suppression annulee.");
        }
    }

    // fait une copie pour pas que joueur et ia partagent le meme pokemon
    private static Pokemon copierPokemon(Pokemon original) {
        Pokemon copie = new Pokemon(original.getNom(), original.getType(),
                original.getPvMax(), original.getAttaque(), original.getDefense());
        for (int i = 0; i < original.getNombreAttaques(); i++) {
            copie.ajouterAttaque(original.getListeAttaques().get(i));
        }
        return copie;
    }

    // copie avec bonus +30% attaque et defense (mode IMPOSSIBLE).
    // PV inchanges, arrondi a l'entier superieur via Math.ceil.
    private static Pokemon copierPokemonBooste(Pokemon original) {
        int newAtt = (int) Math.ceil(original.getAttaque() * 1.3);
        int newDef = (int) Math.ceil(original.getDefense() * 1.3);
        Pokemon copie = new Pokemon(original.getNom(), original.getType(),
                original.getPvMax(), newAtt, newDef);
        for (int i = 0; i < original.getNombreAttaques(); i++) {
            copie.ajouterAttaque(original.getListeAttaques().get(i));
        }
        return copie;
    }

    // Affichage de la liste des pokemon page par page (20 par page).
    // Commandes : D = page suivante, G = page precedente, numero = choisir.
    private static void choisirEquipeJoueur(Scanner clavier, ArrayList<Pokemon> tousLesPokemon, Equipe equipeJoueur) {
        int total = tousLesPokemon.size();
        int parPage = 20;
        int nbPages = (total + parPage - 1) / parPage; // arrondi superieur
        int page = 1;
        ArrayList<Integer> dejaChoisis = new ArrayList<Integer>();
        int nbChoisis = 0;

        while (nbChoisis < 3) {
            int debut = (page - 1) * parPage + 1;
            int fin = debut + parPage - 1;
            if (fin > total) {
                fin = total;
            }

            System.out.println("\n====== Page " + page + "/" + nbPages + " - " + total + " Pokemon disponibles ======");
            for (int i = debut; i <= fin; i++) {
                Pokemon p = tousLesPokemon.get(i - 1);
                System.out.println(i + " - " + p.getNom() + " [" + p.getType()
                        + "] PV:" + p.getPvMax() + " ATT:" + p.getAttaque() + " DEF:" + p.getDefense());
            }

            System.out.println("\nCommandes : D = page suivante | G = page precedente | Numero = choisir");

            // construction de la liste des deja choisis (pour l'affichage)
            String noms = "";
            for (int i = 0; i < dejaChoisis.size(); i++) {
                if (i > 0) {
                    noms += ", ";
                }
                noms += tousLesPokemon.get(dejaChoisis.get(i) - 1).getNom();
            }
            System.out.println("Pokemon " + nbChoisis + "/3 deja choisis : " + noms);
            System.out.print("Votre choix : ");

            String saisie = clavier.nextLine().trim();

            if (saisie.equalsIgnoreCase("D")) {
                if (page < nbPages) {
                    page++;
                } else {
                    System.out.println("Vous etes deja a la derniere page.");
                }
            } else if (saisie.equalsIgnoreCase("G")) {
                if (page > 1) {
                    page--;
                } else {
                    System.out.println("Vous etes deja a la premiere page.");
                }
            } else {
                // on essaie de lire un numero
                try {
                    int choix = Integer.parseInt(saisie);
                    if (choix < 1 || choix > total) {
                        System.out.println("Numero invalide.");
                    } else if (dejaChoisis.contains(choix)) {
                        System.out.println("Vous avez deja choisi ce Pokemon.");
                    } else {
                        Pokemon original = tousLesPokemon.get(choix - 1);
                        equipeJoueur.ajouterPokemon(copierPokemon(original));
                        dejaChoisis.add(choix);
                        nbChoisis++;
                        System.out.println(original.getNom() + " ajoute a votre equipe.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Commande invalide.");
                }
            }
        }
    }
}
