// Represente une attaque qu'un Pokemon peut utiliser pendant un combat.
// Une attaque a un nom, un type et une puissance (sert au calcul des degats).
public class Attaque {

    private String nom;
    private Type type;
    private int puissance;

    public Attaque(String nom, Type type, int puissance) {
        this.nom = nom;
        this.type = type;
        this.puissance = puissance;
    }

    public String getNom() {
        return nom;
    }

    public Type getType() {
        return type;
    }

    public int getPuissance() {
        return puissance;
    }

    @Override
    public String toString() {
        return nom + " (type " + type + ", puissance " + puissance + ")";
    }
}
