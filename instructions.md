# Prompt pour le Développement de l'Application Android "Nono Controller"

**Titre :** Mise à jour de l'application Android "Nono Controller" pour intégrer les nouvelles fonctionnalités du firmware.

**Objectif :** Mettre à jour l'interface utilisateur et la logique de l'application Android pour qu'elle soit compatible avec les dernières commandes et modes du robot NoNo.

**Fichier de référence :** Le fichier `docs/commands.md` du projet firmware décrit le nouveau protocole de commande à implémenter.

---

## Modifications à Apporter

### 1. Mise à jour du Panneau 3 : "Commandes Avancées"

Le panneau des commandes avancées doit être réorganisé pour inclure les nouveaux modes autonomes.

*   **Supprimer le menu de navigation existant.**
*   **Ajouter une nouvelle section "Modes Autonomes" avec 3 boutons :**
    *   **"Mode Manuel"**: Envoie la commande `CMD:MODE:MANUAL\n`. Ce bouton doit être sélectionné par défaut au démarrage.
    *   **"Mode Évitement"**: Envoie la commande `CMD:MODE:AVOID\n`.
    *   **"Mode Sentinelle"**: Envoie la commande `CMD:MODE:SENTRY\n`.
    *   **Mode Cap** : Envoie la commande `CMD:MODE:FOLLOW_HEADING\n`.


*   **Comportement :** Ces 3 boutons doivent fonctionner comme un groupe de boutons radio (un seul peut être actif à la fois). L'état actif doit être clairement visible dans l'interface.

### 2. Mise à jour du Panneau 2 : "Contrôles Manuels" (Comportement Dynamique)

Le comportement des flèches de direction doit changer lorsque le robot est en mode "Suivi de Cap".

*   **Détection de l'état :** L'application doit lire en permanence la clé `"state"` de la télémétrie JSON pour connaître l'état actuel du robot.
*   **Logique conditionnelle :**
    *   **Si l'état est `FOLLOW_HEADING` ou `MAINTAIN_HEADING` :**
        *   **Bouton Haut (`FWD`):** Doit envoyer `CMD:MOVE:FWD\n`. L'icône pourrait changer pour indiquer "Pause/Reprendre".
        *   **Bouton Bas (`BWD`):** Doit envoyer `CMD:MOVE:BWD\n`. L'icône pourrait changer pour indiquer "Pause".
        *   **Bouton Gauche (`LEFT`):** Doit envoyer `CMD:MOVE:LEFT\n`. L'icône pourrait changer pour indiquer "Ajuster Cap -".
        *   **Bouton Droite (`RIGHT`):** Doit envoyer `CMD:MOVE:RIGHT\n`. L'icône pourrait changer pour indiquer "Ajuster Cap +".
    *   **Si le robot est dans n'importe quel autre état :** Les boutons conservent leur comportement normal (mouvement continu, changement d'état vers `MOVING_FORWARD`, `TURNING_LEFT`, etc.).

### 3. Mise à jour des Commandes de la Tourelle

*   **Un seul bouton de Scan :**
    *   Supprimer les anciens boutons/commandes de scan (`SCAN:H`, `SCAN:V`).
    *   Ajouter un seul bouton **"Lancer Scan"**. Ce bouton enverra la commande `CMD:SCAN:START\n`.
*   **Ajouter un bouton "Centrer Tourelle"**: 
    *   Ce bouton enverra la commande `CMD:TURRET:CENTER\n`.

---


### Résumé des Nouvelles Commandes à Implémenter

*   `CMD:MODE:AVOID\n`
*   `CMD:MODE:SENTRY\n`
*   `CMD:MODE:MANUAL\n`
*   `CMD:SCAN:START\n`
*   `CMD:TURRET:CENTER\n`
*   Le comportement contextuel des commandes `CMD:MOVE:*` en fonction de l'état du robot.

### Interface Utilisateur (Suggestions)

*   Il est crucial que l'interface reflète clairement le mode actuel du robot. Le `TextView` affichant l'état est maintenant très important.
*   Pensez à utiliser des indicateurs visuels (changement de couleur des boutons, icônes, texte) pour montrer quel mode autonome est actif et pour visualiser le comportement spécial des flèches de direction en mode suivi de cap.