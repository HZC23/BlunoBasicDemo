# Instructions pour le Développement de l'Application Android "Nono Controller"

**Titre :** Mise à jour de l'application Android "Nono Controller" pour intégrer les nouvelles fonctionnalités du firmware.

**Objectif :** Mettre à jour l'interface utilisateur et la logique de l'application Android pour qu'elle soit compatible avec les dernières commandes et modes du robot NoNo, en utilisant le protocole de communication BLE et série défini.

---

## 1. Protocole de Communication BLE

L'application doit se connecter au robot NoNo via Bluetooth Low Energy (BLE) en utilisant les UUIDs suivants :

*   **Service UUID :** `0000dfb0-0000-1000-8000-00805f9b34fb`
*   **Caractéristique (Commandes / Écriture) :** `0000dfb2-0000-1000-8000-00805f9b34fb`
*   **Caractéristique (Télémétrie / Notifications) :** `0000dfb1-0000-1000-8000-00805f9b34fb`

Les données échangées (commandes et télémétrie) sont des chaînes de caractères ASCII, terminées par un caractère de nouvelle ligne (`\n`).

---

## 2. Organisation de l'Interface Utilisateur (UI/UX)

L'application doit être conçue comme un tableau de bord unique, sans défilement ni pages multiples, pour un accès immédiat à toutes les fonctionnalités.

### Principes Directeurs
*   **Écran Unique :** L'ensemble de l'interface doit tenir sur un seul écran en mode portrait.
*   **Clarté et Réactivité :** L'état du robot et la connectivité doivent être visibles en permanence. Les contrôles doivent fournir un retour visuel immédiat.

### Structure du Layout (de haut en bas)

1.  **Bandeau d'État (Header)**
    *   Une zone en haut de l'écran affichant les informations critiques en temps réel.
    *   **Gauche :** Statut de la connexion BLE (`TextView` : "Connecté à NoNo", "Recherche...", "Déconnecté").
    *   **Centre :** État actuel du robot, reçu de la télémétrie (`TextView` : "IDLE", "FOLLOW_HEADING", etc.). Cet élément doit être très visible.
    *   **Droite :** Niveau de la batterie (`ProgressBar` et/ou `TextView` en %).

2.  **Panneaux de Contrôle (Corps)**
    *   La zone centrale, idéalement divisée en deux colonnes.
    *   **Colonne de Gauche : Contrôles de Mouvement**
        *   Un pad directionnel (D-Pad) composé de 4 boutons pour `Haut`, `Bas`, `Gauche`, `Droite`.
        *   Un bouton `STOP` proéminent, au centre du D-Pad ou juste en dessous.
    *   **Colonne de Droite : Commandes et Modes**
        *   **Panneau "Modes" :** Un groupe de boutons radio (`RadioGroup`) pour sélectionner un seul mode à la fois : "Manuel", "Évitement", "Sentinelle", "Cap".
        *   **Panneau "Tourelle" :** Deux boutons : "Centrer Tourelle" et "Lancer Scan".
        *   **Panneau "Utilitaires" :** Un bouton pour le phare ("Phare ON/OFF") et un pour la calibration ("Calibrer Compas").

3.  **Zone d'Information (Pied de page)**
    *   Une zone en bas de l'écran pour la télémétrie détaillée et les logs.
    *   **Affichage Télémétrie :** Quelques `TextViews` pour les données secondaires importantes (ex: `Cap: 92°`, `Distance: 45cm`).
    *   **Console de Log :** Un `TextView` de 3-4 lignes (dans un `ScrollView` de hauteur fixe) pour afficher les dernières commandes envoyées et les messages de statut/erreur. C'est essentiel pour le débogage.

---

## 3. Modifications Logiques à Apporter à l'Application

### 3.1. Panneau "Modes Autonomes"

Ce panneau, décrit dans le layout ci-dessus, doit permettre de sélectionner les différents modes de fonctionnement du robot.

*   **Structure :** Créer une section "Modes Autonomes" avec les boutons suivants. Ces boutons doivent fonctionner comme un groupe de boutons radio (un seul actif à la fois). L'état actif doit être clairement visible.
    *   **"Mode Manuel"**: Envoie la commande `CMD:MODE:MANUAL\n`. Ce mode correspond à l'état `IDLE` du robot et doit être sélectionné par défaut au démarrage de l'application.
    *   **"Mode Évitement"**: Envoie la commande `CMD:MODE:AVOID\n`. Ce mode active l'état `SMART_AVOIDANCE` du robot.
    *   **"Mode Sentinelle"**: Envoie la commande `CMD:MODE:SENTRY\n`. Ce mode active l'état `SENTRY_MODE` du robot.
    *   **"Mode Cap"**: Pour activer le mode de suivi de cap, l'application doit envoyer la commande `CMD:GOTO:0\n` (ou un cap initial par défaut, par exemple le cap actuel du robot si disponible). Ce mode active l'état `FOLLOW_HEADING` du robot.

### 3.2. Panneau "Contrôles Manuels" (Comportement Dynamique)

Le comportement des flèches de direction doit s'adapter en fonction de l'état actuel du robot.

*   **Détection de l'état :** L'application doit lire en permanence la clé `"state"` de la télémétrie JSON reçue du robot pour connaître son état actuel.
*   **Logique conditionnelle :**
    *   **Si l'état du robot est `FOLLOW_HEADING` ou `MAINTAIN_HEADING` :**
        *   **Bouton Haut (FWD):** Envoie `CMD:MOVE:FWD\n`. Ce bouton doit alterner entre la reprise du mouvement (`FOLLOW_HEADING`) et la pause (`MAINTAIN_HEADING`). L'icône ou le texte du bouton devrait refléter cette fonctionnalité (ex: "Pause/Reprendre").
        *   **Bouton Bas (BWD):** Envoie `CMD:MOVE:BWD\n`. Ce bouton doit mettre le mouvement en pause (`MAINTAIN_HEADING`). L'icône ou le texte pourrait indiquer "Pause".
        *   **Bouton Gauche (LEFT):** Envoie `CMD:MOVE:LEFT\n`. Ce bouton doit ajuster le cap cible de -5 degrés. L'icône ou le texte pourrait indiquer "Ajuster Cap -".
        *   **Bouton Droite (RIGHT):** Envoie `CMD:MOVE:RIGHT\n`. Ce bouton doit ajuster le cap cible de +5 degrés. L'icône ou le texte pourrait indiquer "Ajuster Cap +".
    *   **Si le robot est dans n'importe quel autre état :** Les boutons conservent leur comportement normal de mouvement continu.
        *   **Bouton Haut (FWD):** Envoie `CMD:MOVE:FWD\n`.
        *   **Bouton Bas (BWD):** Envoie `CMD:MOVE:BWD\n`.
        *   **Bouton Gauche (LEFT):** Envoie `CMD:MOVE:LEFT\n`.
        *   **Bouton Droite (RIGHT):** Envoie `CMD:MOVE:RIGHT\n`.
        *   **Bouton Stop:** Envoie `CMD:MOVE:STOP\n`.

### 3.3. Mise à jour des Commandes de la Tourelle

*   **Bouton "Lancer Scan" :**
    *   Ajouter un seul bouton **"Lancer Scan"**. Ce bouton enverra la commande `CMD:SCAN:START\n`. Ce mode active l'état `SCANNING` du robot.
*   **Bouton "Centrer Tourelle"**:
    *   Ajouter un bouton **"Centrer Tourelle"**. Ce bouton enverra la commande `CMD:TURRET:CENTER\n`.

---

## 4. Télémétrie et Affichage de l'État

*   L'application doit s'abonner aux notifications de la caractéristique de télémétrie pour recevoir les mises à jour de l'état du robot.
*   Le message de télémétrie est un objet JSON contenant, entre autres, la clé `"state"` (ex: `{"state":"IDLE", ...}`).
*   Il est crucial que l'interface utilisateur reflète clairement le mode et l'état actuel du robot. Le `TextView` affichant l'état est maintenant très important.
*   Pensez à utiliser des indicateurs visuels (changement de couleur des boutons, icônes, texte) pour montrer quel mode autonome est actif et pour visualiser le comportement spécial des flèches de direction en mode suivi de cap.

---

## 5. Résumé des Nouvelles Commandes et États Clés

**Commandes à implémenter :**
*   `CMD:MODE:AVOID\n` (Active `SMART_AVOIDANCE`)
*   `CMD:MODE:SENTRY\n` (Active `SENTRY_MODE`)
*   `CMD:MODE:MANUAL\n` (Active `IDLE`)
*   `CMD:GOTO:<0-359>\n` (Active `FOLLOW_HEADING` pour le "Mode Cap")
*   `CMD:SCAN:START\n` (Active `SCANNING`)
*   `CMD:TURRET:CENTER\n`
*   Le comportement contextuel des commandes `CMD:MOVE:*` en fonction des états `FOLLOW_HEADING` et `MAINTAIN_HEADING`.

**États du robot à surveiller via la télémétrie :**
*   `IDLE`
*   `SMART_AVOIDANCE`
*   `SENTRY_MODE`
*   `FOLLOW_HEADING`
*   `MAINTAIN_HEADING`
*   `SCANNING`
*   `CALIBRATING_COMPASS` (pour information, si l'application souhaite afficher un message pendant la calibration)

ne pas modifier les versions sdk ou gradle
