# Feuille de Route pour le Développement de l'Application Android "NonoController"

## 1. Objectif du Projet

L'objectif est de développer une application Android native nommée **NonoController**. Cette application servira d'interface de contrôle à distance pour le robot "Nono", basé sur une carte Arduino. La communication entre l'application et le robot s'effectuera via **Bluetooth Low Energy (BLE)**.

L'application doit permettre un contrôle manuel, l'activation de modes autonomes, et la visualisation en temps réel des données des capteurs du robot.

## 2. Spécifications Techniques

- **Plateforme :** Android
- **Langage :** Java
- **Architecture UI :** XML Layouts
- **Architecture de l'application :** MVVM (Model-View-ViewModel) est fortement recommandé pour séparer la logique métier, la gestion de l'état et l'interface utilisateur.
- **Connectivité :** Bluetooth Low Energy (BLE). Le module sur le robot agit comme un pont série transparent.

## 3. Architecture Applicative (MVVM)

Il est crucial de suivre une architecture MVVM pour garantir la maintenabilité et la testabilité du code.

### `data` (Model)
- **`RobotRepository` :** Une classe singleton qui gère la logique de communication BLE. Elle est responsable de :
    - La connexion/déconnexion au module BLE.
    - L'envoi des commandes formatées au robot.
    - La réception, le buffer, et le parsing des données JSON de télémétrie.
    - L'exposition de l'état du robot (télémétrie) via des `LiveData`.
- **`Telemetry.java` :** Une classe Java (POJO) qui représente l'objet JSON reçu du robot.
  ```java
    public class Telemetry {
      public String state = "UNKNOWN";
      public int heading = 0;
      public int distance = 0;
      public int distanceLaser = 0;
      public int battery = 0;
      public int speedTarget = 0;
  }
  ```

### `viewmodel`
- **`MainViewModel.java` :** Il expose l'état de la connexion et les données de télémétrie (provenant du `RobotRepository`) à l'interface utilisateur (View) via des `LiveData`. Il contient également les fonctions que la View appellera en réponse aux interactions de l'utilisateur (ex: `onMoveForward()`, `onSetSpeed(int speed)`). Ces fonctions délègueront l'envoi des commandes au `RobotRepository`.

### `ui` (View)
- **`activity_main.xml` / `MainActivity.java` :** L'activité principale qui observe les `LiveData` du `MainViewModel` et met à jour les vues XML. Elle est composée de plusieurs vues (TextView, Button, etc.).

## 4. Protocole de Communication (Crucial)

La communication est basée sur des chaînes de caractères terminées par un caractère de nouvelle ligne (`\n`).

### 4.1. Commandes (App → Robot)

L'application doit envoyer des commandes au format `CMD:ACTION:VALEUR\n`.

| Action | Valeur | Commande Complète |
| :--- | :--- | :--- |
| `MOVE` | `FWD`, `BWD`, `LEFT`, `RIGHT`, `STOP` | `CMD:MOVE:FWD\n` |
| `SPEED`| `<0-255>` | `CMD:SPEED:150\n` |
| `GOTO` | `<0-359>` | `CMD:GOTO:90\n` |
| `TURN` | `<angle>` | `CMD:TURN:45\n` |
| `LIGHT`| `ON` / `OFF` | `CMD:LIGHT:ON\n` |
| `CALIBRATE`| `COMPASS` | `CMD:CALIBRATE:COMPASS\n` |

### 4.2. Télémétrie (Robot → App)

L'application doit écouter en continu le flux de données BLE. Elle doit bufferiser les octets reçus jusqu'à détecter un caractère `\n`. La chaîne complète obtenue est un objet JSON à parser.

**Exemple de JSON à parser :**
```json
{"state":"FOLLOW_HEADING","heading":92,"distance":45,"distanceLaser":120,"battery":87,"speedTarget":150}
```

Le `RobotRepository` doit gérer les JSON malformés ou incomplets sans faire planter l'application.

## 5. Conception de l'Interface Utilisateur (XML)

L'interface doit être en mode **paysage (landscape)** et immersive. Elle sera divisée en plusieurs panneaux logiques.

### Panneau 1 : État de la Connexion
- **Fonctionnalité :** Afficher le statut de la connexion BLE ("Déconnecté", "Recherche...", "Connecté à Nono"). Un bouton doit permettre de lancer la recherche et la connexion.
- **UI :** Une simple barre de texte en haut de l'écran.

### Panneau 2 : Contrôles Manuels
- **Fonctionnalité :** Permettre le pilotage direct du robot.
- **UI :**
    - Quatre boutons directionnels (Haut, Bas, Gauche, Droite) et un bouton "STOP" central.
    - Les boutons directionnels doivent gérer l'appui long (`press-and-hold`) pour un mouvement continu.
    - **Mapping des commandes :**
        - Haut : `CMD:MOVE:FWD\n`
        - Bas : `CMD:MOVE:BWD\n`
        - Gauche : `CMD:MOVE:LEFT\n`
        - Droite : `CMD:MOVE:RIGHT\n`
        - STOP : `CMD:MOVE:STOP\n`

### Panneau 3 : Commandes Avancées
- **Fonctionnalité :** Envoyer des commandes paramétrées.
- **UI :**
    - Un `SeekBar` pour régler la vitesse (`CMD:SPEED:<valeur>\n`).
    - Un champ de texte (`EditText`) et un bouton "Go" pour le mode "Follow Heading" (`CMD:GOTO:<angle>\n`).
    - Un `Switch` pour allumer/éteindre les phares (`CMD:LIGHT:ON/OFF\n`).
    - Un bouton "Calibrer Compas" (`CMD:CALIBRATE:COMPASS\n`).

### Panneau 4 : Dashboard de Télémétrie
- **Fonctionnalité :** Visualiser les données reçues du robot en temps réel.
- **UI :**
    - **État :** Texte affichant la valeur de la clé `state`.
    - **Cap (Heading) :** Un indicateur de type jauge ou simplement un texte affichant la valeur de `heading`.
    - **Distance :** Une `ProgressBar` (horizontale) qui se remplit à mesure que la distance diminue. La couleur doit changer (ex: vert -> jaune -> rouge) lorsque l'obstacle est proche.
    - **Batterie :** Une icône de batterie qui se vide en fonction de la valeur de `battery`.
    - **Vitesse :** Deux `ProgressBar` (horizontales) pour `speedTarget` et `speedCurrent`.

### Panneau 5 : Moniteur Série (pour le débogage)
- **Fonctionnalité :** Afficher toutes les données brutes (JSON) reçues du robot.
- **UI :** Une `ScrollView` contenant un `TextView`.

## 7. Plan de Développement par Étapes

1.  **Étape 1 : Structure de base.**
    - Adapter la structure existante du projet Java/XML pour suivre l'architecture MVVM.
    - Créer les placeholders pour tous les panneaux UI dans le layout XML.

2.  **Étape 2 : Connectivité BLE.**
    - Implémenter la logique de scan, connexion et déconnexion dans la classe `BlunoLibrary` existante.
    - Afficher la liste des appareils détectés et permettre la connexion.

3.  **Étape 3 : Communication.**
    - Implémenter l'envoi de commandes (commencer avec `CMD:MOVE:STOP\n`).
    - Implémenter la réception et le parsing des messages JSON de télémétrie.
    - Afficher les données brutes dans le moniteur série pour valider la communication.

4.  **Étape 4 : Intégration UI.**
    - Lier les vues XML (boutons, seekbar) aux fonctions du ViewModel pour envoyer des commandes.
    - Lier le Dashboard aux données de télémétrie du ViewModel pour afficher les informations en temps réel.

5.  **Étape 5 : Finalisation et Améliorations.**
    - Gérer les cas d'erreur (déconnexion inattendue, JSON invalide).
    - Peaufiner l'interface utilisateur et l'expérience utilisateur.

## 8. Évolutions Futures (à prévoir dans l'architecture)

L'architecture doit être suffisamment flexible pour intégrer facilement les fonctionnalités suivantes, même si elles ne sont pas dans le MVP.

### Contrôle de la Tourelle
- **UI :** Prévoir un emplacement pour un joystick 2D ou deux `SeekBar` (Horizontal/Vertical).
- **Communication :** L'application devra envoyer de nouvelles commandes :
    - `CMD:TURRET:H:<angle>\n`
    - `CMD:TURRET:V:<angle>\n`

### Affichage du Scan d'Environnement
- **UI :** Prévoir un `GridLayout` ou des `TextView` pour afficher une grille 3x3 de valeurs textuelles.
- **Communication :** Le parser JSON dans le `RobotRepository` devra être capable de gérer une nouvelle clé dans le payload de télémétrie, par exemple :
  ```json
  "scanGrid": [100, 120, 110, 50, 45, 55, 20, 22, 25]
  ```

En suivant cette feuille de route, le développement de l'application NonoController devrait être structuré, efficace et aboutir à un produit robuste et évolutif.

