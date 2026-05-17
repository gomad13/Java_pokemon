// Cette classe gere la table d'efficacite entre les types.
// Elle renvoie un multiplicateur de degats selon le type de l'attaque
// et le type du Pokemon qui se prend l'attaque.
// 2.0 = super efficace, 1.0 = neutre, 0.5 = pas tres efficace, 0.0 = aucun effet.
public class TableTypes {

    // methode statique : pas besoin de creer un objet TableTypes
    public static double getMultiplicateur(Type typeAttaque, Type typeDefenseur) {

        switch (typeAttaque) {

            case NORMAL:
                if (typeDefenseur == Type.ROCHE) return 0.5;
                if (typeDefenseur == Type.SPECTRE) return 0.0;
                return 1.0;

            case FEU:
                if (typeDefenseur == Type.PLANTE) return 2.0;
                if (typeDefenseur == Type.GLACE) return 2.0;
                if (typeDefenseur == Type.INSECTE) return 2.0;
                if (typeDefenseur == Type.FEU) return 0.5;
                if (typeDefenseur == Type.EAU) return 0.5;
                if (typeDefenseur == Type.ROCHE) return 0.5;
                if (typeDefenseur == Type.DRAGON) return 0.5;
                return 1.0;

            case EAU:
                if (typeDefenseur == Type.FEU) return 2.0;
                if (typeDefenseur == Type.SOL) return 2.0;
                if (typeDefenseur == Type.ROCHE) return 2.0;
                if (typeDefenseur == Type.EAU) return 0.5;
                if (typeDefenseur == Type.PLANTE) return 0.5;
                if (typeDefenseur == Type.DRAGON) return 0.5;
                return 1.0;

            case PLANTE:
                if (typeDefenseur == Type.EAU) return 2.0;
                if (typeDefenseur == Type.SOL) return 2.0;
                if (typeDefenseur == Type.ROCHE) return 2.0;
                if (typeDefenseur == Type.FEU) return 0.5;
                if (typeDefenseur == Type.PLANTE) return 0.5;
                if (typeDefenseur == Type.POISON) return 0.5;
                if (typeDefenseur == Type.VOL) return 0.5;
                if (typeDefenseur == Type.INSECTE) return 0.5;
                if (typeDefenseur == Type.DRAGON) return 0.5;
                return 1.0;

            case ELECTRIK:
                if (typeDefenseur == Type.EAU) return 2.0;
                if (typeDefenseur == Type.VOL) return 2.0;
                if (typeDefenseur == Type.ELECTRIK) return 0.5;
                if (typeDefenseur == Type.PLANTE) return 0.5;
                if (typeDefenseur == Type.DRAGON) return 0.5;
                if (typeDefenseur == Type.SOL) return 0.0;
                return 1.0;

            case GLACE:
                if (typeDefenseur == Type.PLANTE) return 2.0;
                if (typeDefenseur == Type.SOL) return 2.0;
                if (typeDefenseur == Type.VOL) return 2.0;
                if (typeDefenseur == Type.DRAGON) return 2.0;
                if (typeDefenseur == Type.FEU) return 0.5;
                if (typeDefenseur == Type.EAU) return 0.5;
                if (typeDefenseur == Type.GLACE) return 0.5;
                return 1.0;

            case COMBAT:
                if (typeDefenseur == Type.NORMAL) return 2.0;
                if (typeDefenseur == Type.GLACE) return 2.0;
                if (typeDefenseur == Type.ROCHE) return 2.0;
                if (typeDefenseur == Type.POISON) return 0.5;
                if (typeDefenseur == Type.VOL) return 0.5;
                if (typeDefenseur == Type.PSY) return 0.5;
                if (typeDefenseur == Type.INSECTE) return 0.5;
                if (typeDefenseur == Type.SPECTRE) return 0.0;
                return 1.0;

            case POISON:
                if (typeDefenseur == Type.PLANTE) return 2.0;
                if (typeDefenseur == Type.POISON) return 0.5;
                if (typeDefenseur == Type.SOL) return 0.5;
                if (typeDefenseur == Type.ROCHE) return 0.5;
                if (typeDefenseur == Type.SPECTRE) return 0.5;
                return 1.0;

            case SOL:
                if (typeDefenseur == Type.FEU) return 2.0;
                if (typeDefenseur == Type.ELECTRIK) return 2.0;
                if (typeDefenseur == Type.POISON) return 2.0;
                if (typeDefenseur == Type.ROCHE) return 2.0;
                if (typeDefenseur == Type.PLANTE) return 0.5;
                if (typeDefenseur == Type.INSECTE) return 0.5;
                if (typeDefenseur == Type.VOL) return 0.0;
                return 1.0;

            case VOL:
                if (typeDefenseur == Type.PLANTE) return 2.0;
                if (typeDefenseur == Type.COMBAT) return 2.0;
                if (typeDefenseur == Type.INSECTE) return 2.0;
                if (typeDefenseur == Type.ELECTRIK) return 0.5;
                if (typeDefenseur == Type.ROCHE) return 0.5;
                return 1.0;

            case PSY:
                if (typeDefenseur == Type.COMBAT) return 2.0;
                if (typeDefenseur == Type.POISON) return 2.0;
                if (typeDefenseur == Type.PSY) return 0.5;
                return 1.0;

            case INSECTE:
                if (typeDefenseur == Type.PLANTE) return 2.0;
                if (typeDefenseur == Type.PSY) return 2.0;
                if (typeDefenseur == Type.FEU) return 0.5;
                if (typeDefenseur == Type.COMBAT) return 0.5;
                if (typeDefenseur == Type.POISON) return 0.5;
                if (typeDefenseur == Type.VOL) return 0.5;
                if (typeDefenseur == Type.SPECTRE) return 0.5;
                return 1.0;

            case ROCHE:
                if (typeDefenseur == Type.FEU) return 2.0;
                if (typeDefenseur == Type.GLACE) return 2.0;
                if (typeDefenseur == Type.VOL) return 2.0;
                if (typeDefenseur == Type.INSECTE) return 2.0;
                if (typeDefenseur == Type.COMBAT) return 0.5;
                if (typeDefenseur == Type.SOL) return 0.5;
                return 1.0;

            case SPECTRE:
                if (typeDefenseur == Type.SPECTRE) return 2.0;
                if (typeDefenseur == Type.PSY) return 2.0;
                if (typeDefenseur == Type.NORMAL) return 0.0;
                return 1.0;

            case DRAGON:
                if (typeDefenseur == Type.DRAGON) return 2.0;
                return 1.0;

            default:
                return 1.0;
        }
    }
}
