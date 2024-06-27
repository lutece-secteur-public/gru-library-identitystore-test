# gru-library-identitystore-test

## Objet
Ce module a pour objectif de tester les interfaces REST de l'identity store.

## Prérequis
### Environnement
Il est nécessaire de démarrer un environnement complet : IDS + quality.
Avec le plugin + module démarrés, un E/S et une BDD.

### Configuration
Il est nécessaire de configurer le contexte de vos tests via [le fichier de properties](src/test/resources/library-identitystore-test.properties).

## Run

ATTENTION : Lors de l'exécution des tests, des manipulations réelles sont effectuées sur les données de la base. Il faut donc sauvegarder au préalable cette base pour pouvoir la restaurer.