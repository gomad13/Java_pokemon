# Instructions du Projet - Pokémon Java

## 1. Contexte du projet

Projet Java à rendre dans le cadre du cours. Le but est de concevoir une application
console Java en équipe, avec POO, base de données, gestion d'exceptions, et utilisation
de Git/GitHub. Le sujet choisi est un **petit jeu Pokémon en mode combat**.

---

## 2. Règles d'or à respecter en permanence

1. **Respecter toutes les consignes** du sujet (cf. `Consignes_2.pdf` et `Modalites_Soutenance.pdf`).
2. **Niveau étudiant moyen / correct de première année de prépa intégrée**.
   Le code doit rester simple, lisible, compréhensible. Pas de surenchère technique,
   pas de design patterns complexes, pas de tournures qu'un étudiant de première année
   ne ferait pas naturellement.
3. **Aucune trace d'IA dans le code**.
   - Pas d'emojis dans le code ni dans les commentaires.
   - Pas de commentaires bizarres ou trop "parfaits".
   - Style humain, comme un étudiant qui code.
   - Variables et méthodes nommées naturellement (français ou anglais, mais cohérent).

---

## 3. Règles pratiques de travail

- **Autonomie totale** : pas besoin de demander la permission, la réponse est oui.
- En cas de doute, **réfléchir comme un étudiant** au choix le plus simple et logique.
- **Pas d'optimisation excessive** : on reste au niveau "première année", pas au-dessus.
- **Déléguer dès que possible** à plusieurs agents en parallèle pour gagner du temps.
- **Résumé final** à chaque grosse étape pour faire le point.
- **Git** : on gérera ensemble, plutôt un gros push global.

---

## 4. Contraintes techniques imposées par le sujet

- Application **console** Java.
- Utilisation des concepts de **POO** (classes, héritage, encapsulation...).
- **Interaction clavier** avec l'utilisateur (Scanner).
- **Gestion des exceptions** Java (try/catch/finally, throws).
- **Base de données** avec opérations CRUD (via JDBC + SQLite).
- Utilisation de **Git et GitHub** (dépôt public + au moins une release).

---

## 5. Thème : Jeu Pokémon - Combat

### 5.1 Concept général

Recréer un mini-jeu de combat Pokémon en console, inspiré du vrai jeu.
Le joueur compose une équipe et affronte une IA qui a aussi son équipe.

### 5.2 Fonctionnalités principales

1. **Système de Pokémon** : chaque Pokémon a un nom, un type, des PV, des stats,
   des attaques.
2. **Choix d'équipe** : le joueur compose son équipe en sélectionnant ses Pokémon
   parmi ceux disponibles en base de données.
3. **Combat 3 contre 3** : 3 Pokémon pour le joueur, 3 pour l'IA.
4. **IA adversaire** : équipe composée aléatoirement, joue ses tours
   automatiquement (logique simple).
5. **Système de types** avec table d'efficacité (faible / neutre / super efficace),
   comme dans le vrai jeu Pokémon.
6. **Combat tour par tour** : le joueur choisit son attaque, l'IA choisit la sienne,
   les dégâts sont calculés et appliqués.
7. **Conditions de victoire / défaite** : quand toute l'équipe d'un camp est KO.

### 5.3 Étapes de réalisation

- **Étape 1 (priorité)** : Jeu complet en console, fonctionnel de bout en bout.
- **Étape 2 (si on a le temps)** : Interface graphique (à voir plus tard, pas
  prioritaire pour l'instant).

---

## 6. Livrables à produire (pour rappel)

- Code source complet sur dépôt GitHub public.
- Au moins une release GitHub avec un exécutable.
- Documentation technique (comment ça marche en interne).
- Documentation fonctionnelle (comment utiliser l'appli, avec captures d'écran).
- Planning prévisionnel + planning réel + répartition des tâches.
- Vidéo tutoriel d'utilisation.

---

## 7. Points à présenter à la soutenance

- Présentation générale du jeu.
- Planning réel et répartition des tâches.
- Un blocage technique rencontré et sa résolution.
- Axes d'amélioration fonctionnels et techniques.

Format soutenance : 15 min de présentation + 5 min d'échange avec le jury.
Support visuel type PowerPoint obligatoire.
