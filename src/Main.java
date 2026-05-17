import java.sql.SQLException;
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
                    jouerPartie(clavier, bdd);
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

    // lance une partie complete : choix equipe + combat
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

        // composition aleatoire de l'equipe de l'IA
        Equipe equipeIA = new Equipe();
        Random random = new Random();
        ArrayList<Pokemon> disponibles = new ArrayList<Pokemon>(tousLesPokemon);
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(disponibles.size());
            Pokemon original = disponibles.get(index);
            equipeIA.ajouterPokemon(copierPokemon(original));
            disponibles.remove(index);
        }

        System.out.println("\nL'IA a compose son equipe :");
        for (int i = 0; i < equipeIA.getNombrePokemon(); i++) {
            System.out.println(" - " + equipeIA.getPokemon(i).getNom());
        }

        // creation des joueurs et lancement du combat
        Joueur joueur = new Joueur(nom, equipeJoueur, clavier);
        JoueurIA ia = new JoueurIA("Dresseur IA", equipeIA, clavier);

        Combat combat = new Combat(joueur, ia, clavier, bdd);
        combat.lancerCombat();
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
