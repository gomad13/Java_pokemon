# Projet Pokemon - Combat

Petit jeu de combat Pokemon en console, fait en Java pour le projet de premiere annee.

## C'est quoi le jeu

On choisit une equipe de 3 Pokemon parmi les 151 de la premiere generation et on affronte une IA qui a aussi 3 Pokemon. Le combat se fait au tour par tour comme dans le vrai jeu, avec les types qui changent les degats (par exemple feu contre plante c'est super efficace).

On peut aussi changer de Pokemon en plein milieu du combat, mais attention ca fait perdre un tour.

## Modes de jeu

Il y a deux modes pour jouer :

- **Match unique** : un seul combat contre une IA
- **Tournoi** : 3 combats a la suite contre 3 IA differentes (l'equipe est soignee entre chaque combat)

Avant chaque partie on choisit la difficulte de l'IA :

- **Facile** : l'IA joue au hasard
- **Moyen** : l'IA choisit ses meilleures attaques
- **Difficile** : l'IA change strategiquement de Pokemon
- **Impossible** : IA optimale + Pokemon boostes (+30% attaque et defense)

## Tableau des scores

A la fin de chaque partie, le score est enregistre dans une base de donnees. On peut consulter le tableau des scores depuis le menu, ou modifier/supprimer des scores.

## Comment lancer

Dans la section Releases, telecharger Java_pokemon.jar et pokemon_data.txt. Mettre pokemon_data.txt dans un dossier "data" a cote du jar, puis lancer :

java -jar Java_pokemon.jar

Il faut avoir Java 17 ou plus.

## Technos

Java, SQLite et JDBC pour la base de donnees.

## Fait par

Hugo Madoumier
