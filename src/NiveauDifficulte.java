// Les niveaux de difficulte de l'IA.
// FACILE : choisit ses attaques au hasard.
// MOYEN : choisit l'attaque la plus efficace contre le type adverse.
// DIFFICILE : MOYEN + change de Pokemon si le matchup est defavorable.
// IMPOSSIBLE : DIFFICILE + Pokemon boostes (+30% attaque et defense).
public enum NiveauDifficulte {
    FACILE,
    MOYEN,
    DIFFICILE,
    IMPOSSIBLE
}
