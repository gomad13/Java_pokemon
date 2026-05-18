import java.util.ArrayList;

// Represente un Pokemon avec ses stats et sa liste d'attaques.
// Un Pokemon peut avoir jusqu'a 4 attaques (comme dans le vrai jeu).
public class Pokemon {

    private String nom;
    private Type type;
    private int pvMax;
    private int pv;
    private int attaque;
    private int defense;
    private ArrayList<Attaque> listeAttaques;

    public Pokemon(String nom, Type type, int pvMax, int attaque, int defense) {
        this.nom = nom;
        this.type = type;
        this.pvMax = pvMax;
        this.pv = pvMax; // au depart le pokemon est en pleine forme
        this.attaque = attaque;
        this.defense = defense;
        this.listeAttaques = new ArrayList<Attaque>();
    }

    // Ajoute une attaque au pokemon (max 4)
    public void ajouterAttaque(Attaque a) {
        if (listeAttaques.size() < 4) {
            listeAttaques.add(a);
        }
    }

    public boolean estKO() {
        return pv <= 0;
    }

    // Le pokemon se prend des degats, ses pv ne peuvent pas descendre en dessous de 0
    public void subirDegats(int degats) {
        pv -= degats;
        if (pv < 0) {
            pv = 0;
        }
    }

    // Remet les pv au maximum (sert entre deux combats du tournoi)
    public void soigner() {
        this.pv = pvMax;
    }

    public int getNombreAttaques() {
        return listeAttaques.size();
    }

    public ArrayList<Attaque> getListeAttaques() {
        return listeAttaques;
    }

    public String getNom() {
        return nom;
    }

    public Type getType() {
        return type;
    }

    public int getPv() {
        return pv;
    }

    public int getPvMax() {
        return pvMax;
    }

    public int getAttaque() {
        return attaque;
    }

    public int getDefense() {
        return defense;
    }

    @Override
    public String toString() {
        return nom + " [" + type + "] " + pv + "/" + pvMax + " PV";
    }
}
