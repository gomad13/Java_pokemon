import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

// Point d'entree du jeu.
// On charge les pokemon depuis la BDD, le joueur choisit son equipe,
// l'IA en a une aleatoire, puis on lance le combat.
public class Main {

    public static void main(String[] args) {
        Scanner clavier = new Scanner(System.in);

        System.out.println("==================================");
        System.out.println("    JEU POKEMON - COMBAT 3 vs 3");
        System.out.println("==================================");

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

        ArrayList<Pokemon> tousLesPokemon = bdd.chargerTousLesPokemon();

        if (tousLesPokemon.size() < 3) {
            System.out.println("Il n'y a pas assez de Pokemon dans la base pour jouer.");
            bdd.fermerConnexion();
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

        Combat combat = new Combat(joueur, ia, clavier);
        combat.lancerCombat();

        bdd.fermerConnexion();
        clavier.close();
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
