import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// classe qui gere la base SQLite des pokemon et de leurs attaques
public class BaseDeDonnees {

    private static final String URL = "jdbc:sqlite:data/pokemon.db";

    private Connection connexion;

    // charge le driver sqlite
    public void seConnecter() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connexion = DriverManager.getConnection(URL);
    }

    public void fermerConnexion() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Erreur a la fermeture de la base : " + e.getMessage());
        }
    }

    // cree les tables et remplit la base si elle est vide
    public void initialiserBase() {
        try {
            Statement st = connexion.createStatement();

            // creation des tables
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pokemon ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "nom TEXT NOT NULL, "
                    + "type TEXT NOT NULL, "
                    + "pv_max INTEGER NOT NULL, "
                    + "attaque INTEGER NOT NULL, "
                    + "defense INTEGER NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS attaque ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "nom TEXT NOT NULL, "
                    + "type TEXT NOT NULL, "
                    + "puissance INTEGER NOT NULL, "
                    + "id_pokemon INTEGER NOT NULL, "
                    + "FOREIGN KEY(id_pokemon) REFERENCES pokemon(id))");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS score ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "nom_joueur TEXT NOT NULL, "
                    + "resultat TEXT NOT NULL, "
                    + "nombre_tours INTEGER NOT NULL, "
                    + "date TEXT NOT NULL)");

            // on regarde si la base contient deja des pokemon
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM pokemon");
            int nb = 0;
            if (rs.next()) {
                nb = rs.getInt(1);
            }
            rs.close();
            st.close();

            if (nb == 0) {
                System.out.println("Base vide, chargement des Pokemon depuis le fichier...");
                chargerPokemonsDepuisFichier();
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de l'initialisation : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier de Pokemon : " + e.getMessage());
        }
    }

    // lit le fichier csv des pokemon et les insere dans la base
    private void chargerPokemonsDepuisFichier() throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader("data/pokemon_data.txt"));
        String ligne;
        int idPokemon = 1;
        int compteur = 0;
        boolean entete = true;

        while ((ligne = reader.readLine()) != null) {
            if (ligne.trim().equals("")) {
                continue;
            }
            // on saute les commentaires
            if (ligne.startsWith("#")) {
                continue;
            }
            // la premiere ligne non vide est l'entete, on la saute aussi
            if (entete) {
                entete = false;
                continue;
            }

            String[] parts = ligne.split(";");
            String nom = parts[0].trim();
            Type type = Type.valueOf(parts[1].trim());
            int pvMax = Integer.parseInt(parts[2].trim());
            int attaque = Integer.parseInt(parts[3].trim());
            int defense = Integer.parseInt(parts[4].trim());

            String req = "INSERT INTO pokemon (id, nom, type, pv_max, attaque, defense) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connexion.prepareStatement(req);
            ps.setInt(1, idPokemon);
            ps.setString(2, nom);
            ps.setString(3, type.name());
            ps.setInt(4, pvMax);
            ps.setInt(5, attaque);
            ps.setInt(6, defense);
            ps.executeUpdate();
            ps.close();

            insererAttaquesPourPokemon(idPokemon, type);

            idPokemon++;
            compteur++;
        }
        reader.close();

        System.out.println(compteur + " Pokemon charges depuis pokemons.csv.");
    }

    // donne 4 attaques a un pokemon selon son type
    private void insererAttaquesPourPokemon(int idPokemon, Type type) throws SQLException {
        if (type == Type.NORMAL) {
            ajouterAttaqueInterne(idPokemon, "Charge", Type.NORMAL, 30);
            ajouterAttaqueInterne(idPokemon, "Vive Attaque", Type.NORMAL, 40);
            ajouterAttaqueInterne(idPokemon, "Plaquage", Type.NORMAL, 55);
            ajouterAttaqueInterne(idPokemon, "Mega Coup", Type.NORMAL, 75);
            return;
        }

        // attaque de base pour tout le monde
        ajouterAttaqueInterne(idPokemon, "Charge", Type.NORMAL, 30);

        switch (type) {
            case FEU:
                ajouterAttaqueInterne(idPokemon, "Flammeche", Type.FEU, 40);
                ajouterAttaqueInterne(idPokemon, "Lance-Flammes", Type.FEU, 60);
                ajouterAttaqueInterne(idPokemon, "Deflagration", Type.FEU, 75);
                break;
            case EAU:
                ajouterAttaqueInterne(idPokemon, "Pistolet a O", Type.EAU, 40);
                ajouterAttaqueInterne(idPokemon, "Surf", Type.EAU, 60);
                ajouterAttaqueInterne(idPokemon, "Hydrocanon", Type.EAU, 75);
                break;
            case PLANTE:
                ajouterAttaqueInterne(idPokemon, "Fouet Lianes", Type.PLANTE, 40);
                ajouterAttaqueInterne(idPokemon, "Tranch Herbe", Type.PLANTE, 60);
                ajouterAttaqueInterne(idPokemon, "Lance-Soleil", Type.PLANTE, 75);
                break;
            case ELECTRIK:
                ajouterAttaqueInterne(idPokemon, "Eclair", Type.ELECTRIK, 40);
                ajouterAttaqueInterne(idPokemon, "Tonnerre", Type.ELECTRIK, 60);
                ajouterAttaqueInterne(idPokemon, "Fatal-Foudre", Type.ELECTRIK, 75);
                break;
            case GLACE:
                ajouterAttaqueInterne(idPokemon, "Vent Glace", Type.GLACE, 40);
                ajouterAttaqueInterne(idPokemon, "Laser Glace", Type.GLACE, 60);
                ajouterAttaqueInterne(idPokemon, "Blizzard", Type.GLACE, 75);
                break;
            case COMBAT:
                ajouterAttaqueInterne(idPokemon, "Poing Karate", Type.COMBAT, 40);
                ajouterAttaqueInterne(idPokemon, "Double Pied", Type.COMBAT, 60);
                ajouterAttaqueInterne(idPokemon, "Ultimapoing", Type.COMBAT, 75);
                break;
            case POISON:
                ajouterAttaqueInterne(idPokemon, "Dard Venin", Type.POISON, 40);
                ajouterAttaqueInterne(idPokemon, "Acide", Type.POISON, 60);
                ajouterAttaqueInterne(idPokemon, "Bomb-Beurk", Type.POISON, 75);
                break;
            case SOL:
                ajouterAttaqueInterne(idPokemon, "Jet de Sable", Type.SOL, 40);
                ajouterAttaqueInterne(idPokemon, "Tunnel", Type.SOL, 60);
                ajouterAttaqueInterne(idPokemon, "Seisme", Type.SOL, 75);
                break;
            case VOL:
                ajouterAttaqueInterne(idPokemon, "Cru-Aile", Type.VOL, 40);
                ajouterAttaqueInterne(idPokemon, "Aeropique", Type.VOL, 60);
                ajouterAttaqueInterne(idPokemon, "Tornade", Type.VOL, 75);
                break;
            case PSY:
                ajouterAttaqueInterne(idPokemon, "Choc Mental", Type.PSY, 40);
                ajouterAttaqueInterne(idPokemon, "Psyko", Type.PSY, 60);
                ajouterAttaqueInterne(idPokemon, "Telekinesie", Type.PSY, 75);
                break;
            case INSECTE:
                ajouterAttaqueInterne(idPokemon, "Piqure", Type.INSECTE, 40);
                ajouterAttaqueInterne(idPokemon, "Damocles", Type.INSECTE, 60);
                ajouterAttaqueInterne(idPokemon, "Megacorne", Type.INSECTE, 75);
                break;
            case ROCHE:
                ajouterAttaqueInterne(idPokemon, "Jet Pierres", Type.ROCHE, 40);
                ajouterAttaqueInterne(idPokemon, "Lance Pierres", Type.ROCHE, 60);
                ajouterAttaqueInterne(idPokemon, "Eboulement", Type.ROCHE, 75);
                break;
            case SPECTRE:
                ajouterAttaqueInterne(idPokemon, "Lechouille", Type.SPECTRE, 40);
                ajouterAttaqueInterne(idPokemon, "Ball Ombre", Type.SPECTRE, 60);
                ajouterAttaqueInterne(idPokemon, "Hantise", Type.SPECTRE, 75);
                break;
            case DRAGON:
                ajouterAttaqueInterne(idPokemon, "Colere", Type.DRAGON, 40);
                ajouterAttaqueInterne(idPokemon, "Dracosouffle", Type.DRAGON, 60);
                ajouterAttaqueInterne(idPokemon, "Draco-Rage", Type.DRAGON, 75);
                break;
        }
    }

    // ajoute une attaque dans la base
    private void ajouterAttaqueInterne(int idPokemon, String nom, Type type, int puissance) throws SQLException {
        Statement st = connexion.createStatement();
        st.executeUpdate("INSERT INTO attaque (nom, type, puissance, id_pokemon) VALUES ('"
                + nom + "', '" + type.name() + "', " + puissance + ", " + idPokemon + ")");
        st.close();
    }

    // recupere tous les pokemon avec leurs attaques
    public ArrayList<Pokemon> chargerTousLesPokemon() {
        ArrayList<Pokemon> liste = new ArrayList<Pokemon>();
        String requete = "SELECT id, nom, type, pv_max, attaque, defense FROM pokemon";

        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(requete);

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                Type type = Type.valueOf(rs.getString("type"));
                int pvMax = rs.getInt("pv_max");
                int attaque = rs.getInt("attaque");
                int defense = rs.getInt("defense");

                Pokemon p = new Pokemon(nom, type, pvMax, attaque, defense);

                // on charge ses attaques
                ArrayList<Attaque> attaques = chargerAttaquesDePokemon(id);
                for (int i = 0; i < attaques.size(); i++) {
                    p.ajouterAttaque(attaques.get(i));
                }

                liste.add(p);
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des Pokemon : " + e.getMessage());
        }

        return liste;
    }

    // recupere les attaques d'un pokemon
    public ArrayList<Attaque> chargerAttaquesDePokemon(int idPokemon) {
        ArrayList<Attaque> liste = new ArrayList<Attaque>();
        String requete = "SELECT nom, type, puissance FROM attaque WHERE id_pokemon = ?";

        try {
            PreparedStatement ps = connexion.prepareStatement(requete);
            ps.setInt(1, idPokemon);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nom = rs.getString("nom");
                Type type = Type.valueOf(rs.getString("type"));
                int puissance = rs.getInt("puissance");
                liste.add(new Attaque(nom, type, puissance));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des attaques : " + e.getMessage());
        }

        return liste;
    }

    // ajoute un pokemon dans la base
    public void ajouterPokemon(String nom, Type type, int pvMax, int attaque, int defense) {
        String requete = "INSERT INTO pokemon(nom, type, pv_max, attaque, defense) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connexion.prepareStatement(requete);
            ps.setString(1, nom);
            ps.setString(2, type.name());
            ps.setInt(3, pvMax);
            ps.setInt(4, attaque);
            ps.setInt(5, defense);
            ps.executeUpdate();
            ps.close();
            System.out.println("Pokemon " + nom + " ajoute.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    // ajoute une attaque liee a un pokemon
    public void ajouterAttaque(String nom, Type type, int puissance, int idPokemon) {
        String requete = "INSERT INTO attaque(nom, type, puissance, id_pokemon) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connexion.prepareStatement(requete);
            ps.setString(1, nom);
            ps.setString(2, type.name());
            ps.setInt(3, puissance);
            ps.setInt(4, idPokemon);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'attaque : " + e.getMessage());
        }
    }

    // modifie les stats d'un pokemon
    public void modifierPokemon(int id, int pvMax, int attaque, int defense) {
        String requete = "UPDATE pokemon SET pv_max = ?, attaque = ?, defense = ? WHERE id = ?";
        try {
            PreparedStatement ps = connexion.prepareStatement(requete);
            ps.setInt(1, pvMax);
            ps.setInt(2, attaque);
            ps.setInt(3, defense);
            ps.setInt(4, id);
            int n = ps.executeUpdate();
            ps.close();
            if (n == 0) {
                System.out.println("Aucun pokemon avec l'id " + id);
            } else {
                System.out.println("Pokemon " + id + " modifie.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    // supprime un pokemon et ses attaques
    public void supprimerPokemon(int id) {
        // on supprime d'abord les attaques sinon la fk fait planter
        String reqAttaques = "DELETE FROM attaque WHERE id_pokemon = ?";
        String reqPokemon = "DELETE FROM pokemon WHERE id = ?";
        try {
            PreparedStatement ps1 = connexion.prepareStatement(reqAttaques);
            ps1.setInt(1, id);
            ps1.executeUpdate();
            ps1.close();

            PreparedStatement ps2 = connexion.prepareStatement(reqPokemon);
            ps2.setInt(1, id);
            int n = ps2.executeUpdate();
            ps2.close();

            if (n == 0) {
                System.out.println("Aucun pokemon avec l'id " + id);
            } else {
                System.out.println("Pokemon " + id + " supprime.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // ajoute un score dans la base
    public void ajouterScore(String nomJoueur, String resultat, int nbTours, String date) {
        String requete = "INSERT INTO score(nom_joueur, resultat, nombre_tours, date) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connexion.prepareStatement(requete);
            ps.setString(1, nomJoueur);
            ps.setString(2, resultat);
            ps.setInt(3, nbTours);
            ps.setString(4, date);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du score : " + e.getMessage());
        }
    }

    // charge tous les scores formattes avec un rang pour l'affichage du tableau
    public ArrayList<String> chargerTousLesScores() {
        ArrayList<String> liste = new ArrayList<String>();
        String requete = "SELECT nom_joueur, resultat, nombre_tours, date FROM score ORDER BY id";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(requete);
            int rang = 1;
            while (rs.next()) {
                String nom = rs.getString("nom_joueur");
                String resultat = rs.getString("resultat");
                int tours = rs.getInt("nombre_tours");
                String date = rs.getString("date");
                liste.add(rang + " - " + nom + " - " + resultat + " - " + tours + " tours - " + date);
                rang++;
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des scores : " + e.getMessage());
        }
        return liste;
    }

    // charge les scores avec leur id (pour la gestion : modif/suppression)
    public ArrayList<String> chargerScoresAvecId() {
        ArrayList<String> liste = new ArrayList<String>();
        String requete = "SELECT id, nom_joueur, resultat, nombre_tours, date FROM score ORDER BY id";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom_joueur");
                String resultat = rs.getString("resultat");
                int tours = rs.getInt("nombre_tours");
                String date = rs.getString("date");
                liste.add("ID " + id + " - " + nom + " - " + resultat + " - " + tours + " tours - " + date);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des scores : " + e.getMessage());
        }
        return liste;
    }

    // modifie le nom du joueur dans un score
    public void modifierNomJoueurScore(int idScore, String nouveauNom) {
        String requete = "UPDATE score SET nom_joueur = ? WHERE id = ?";
        try {
            PreparedStatement ps = connexion.prepareStatement(requete);
            ps.setString(1, nouveauNom);
            ps.setInt(2, idScore);
            int n = ps.executeUpdate();
            ps.close();
            if (n == 0) {
                System.out.println("Aucun score avec l'id " + idScore);
            } else {
                System.out.println("Score " + idScore + " modifie.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du score : " + e.getMessage());
        }
    }

    // supprime un score precis
    public void supprimerScore(int idScore) {
        String requete = "DELETE FROM score WHERE id = ?";
        try {
            PreparedStatement ps = connexion.prepareStatement(requete);
            ps.setInt(1, idScore);
            int n = ps.executeUpdate();
            ps.close();
            if (n == 0) {
                System.out.println("Aucun score avec l'id " + idScore);
            } else {
                System.out.println("Score " + idScore + " supprime.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du score : " + e.getMessage());
        }
    }

    // supprime tous les scores
    public void supprimerTousLesScores() {
        String requete = "DELETE FROM score";
        try {
            Statement st = connexion.createStatement();
            int n = st.executeUpdate(requete);
            st.close();
            System.out.println(n + " scores supprimes.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des scores : " + e.getMessage());
        }
    }

}
