# Nono Controller - Application Android

![Langage](https://img.shields.io/badge/langage-Java-orange.svg)
![Plateforme](https://img.shields.io/badge/plateforme-Android-3DDC84.svg)
![API](https://img.shields.io/badge/API-21%2B-brightgreen)

**Nono Controller** est l'application Android compagnon pour le [robot NoNo](https://github.com/HZC23/NoNo). Elle fournit une interface de contrôle complète et intuitive pour piloter le robot, visualiser sa télémétrie et gérer ses modes de fonctionnement via une connexion Bluetooth Low Energy (BLE).

*(Vous pouvez ajouter ici une capture d'écran de l'application)*

## ✨ Interface et Fonctionnalités

L'application est conçue comme un tableau de bord unique, organisé en cartes thématiques pour un accès rapide à toutes les fonctionnalités.

### 1. Connexion et État
*   **Barre d'état** : Affiche l'état de la connexion BLE (Déconnecté, Connexion, Connecté).
*   **Bouton SCAN** : Lance la recherche des appareils BLE à proximité et permet de se connecter au robot NoNo.

### 2. Télémétrie en Temps Réel
*   Affiche les données envoyées par le robot :
    *   **État** : Le mode actuel du robot (ex: `IDLE`, `SMART_AVOIDANCE`).
    *   **Cap** : L'orientation de la boussole en degrés.
    *   **Distance** : Distance mesurée par les capteurs Ultrason et Laser.
    *   **Batterie** : Pourcentage de batterie restant.
    *   **Vitesse Cible** : Vitesse actuelle des moteurs.

### 3. Contrôles Principaux
*   **ARRÊT D'URGENCE** : Un bouton rouge proéminent pour arrêter immédiatement tous les mouvements.
*   **Interrupteur "Phares"** : Allume ou éteint les phares du robot.
*   **Curseur "Vitesse"** : Règle la vitesse cible pour les déplacements.

### 4. Contrôles Manuels
*   **D-Pad (Croix Directionnelle)** : Permet un contrôle manuel direct (Avancer, Reculer, Pivoter Gauche/Droite). Le mouvement est continu tant que le bouton est pressé.

### 5. Modes Autonomes
*   **Mode Exploration** : Active le mode d'exploration et d'évitement d'obstacles intelligent.
*   **Aller au Cap** : Ouvre une boîte de dialogue pour saisir un cap en degrés. Le robot s'oriente et se déplace vers cette direction.
*   **Mode Sentinelle** : Active le mode de surveillance où le robot réagit aux mouvements détectés.

### 6. Système et Calibration
*   **Calibrer Compas** : Lance la procédure de calibration de la boussole sur le robot.
*   **Définir Offset Compas** : Permet d'appliquer une correction manuelle au cap.
*   **Message LCD** : Envoie un message personnalisé à afficher sur l'écran LCD du robot.
*   **Console de Débogage** : Ouvre un écran affichant les messages bruts reçus du robot.

## 🛠️ Technologies et Architecture

*   **Langage** : 100% **Java**.
*   **Architecture** :
    *   **MVVM (Model-View-ViewModel)** : Sépare la logique de l'interface utilisateur.
    *   **Data Binding** : Lie les composants de l'interface utilisateur aux sources de données.
    *   **LiveData** : Notifie l'interface des changements de données de manière réactive.
*   **Interface Utilisateur** : Basée sur **XML** avec les composants Material Design.
*   **Communication BLE** : Gérée via une version adaptée de la `BlunoLibrary` de DFRobot, encapsulée dans un `BluetoothLeService`.

## 📡 Protocole de Communication

L'application communique avec le robot en utilisant le protocole série sur BLE du module Bluno.

*   **Service UUID** : `0000dfb0-0000-1000-8000-00805f9b34fb`
*   **Caractéristique d'Écriture (Commandes)** : `0000dfb2-0000-1000-8000-00805f9b34fb`
*   **Caractéristique de Lecture (Télémétrie)** : `0000dfb1-0000-1000-8000-00805f9b34fb`

Les commandes sont envoyées sous forme de chaînes de caractères (ex: `CMD:MOVE:FWD\n`) et la télémétrie est reçue sous forme d'objets JSON.

## ⚙️ Installation et Build

### Prérequis
*   [Android Studio](https://developer.android.com/studio) (dernière version recommandée).
*   Un appareil Android avec support Bluetooth Low Energy (API 21+).

### Étapes
1.  Clonez ce dépôt.
2.  Ouvrez le projet avec Android Studio.
3.  Laissez Gradle synchroniser les dépendances.
4.  Construisez le projet et lancez-le sur un appareil physique.

## 📄 Licence
Ce projet est sous licence GPLv3. Voir le fichier `LICENSE` pour plus de détails.