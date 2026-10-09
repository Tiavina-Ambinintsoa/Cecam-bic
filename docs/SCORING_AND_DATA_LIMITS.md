# Portée des données et du score BIC de démonstration

## Usage

Ce projet sert à l'analyse interne de dossiers individuels pour une présentation et des essais locaux. Le score est une règle métier expérimentale ; il n'est pas calibré sur des historiques de remboursement CECAM réels, ne prédit pas une probabilité de défaut validée et ne prend pas une décision de crédit.

Les rapports CRIF décrivent les informations de crédit des personnes présentées dans ces rapports. Ils ne contiennent pas le portefeuille de prêts CECAM, les échéanciers réellement observés par CECAM ni les résultats de remboursement CECAM. Ils ne permettent donc pas de reconstituer ou de déduire un portefeuille CECAM réel. Les données fictives de démonstration doivent rester identifiées comme telles et ne doivent pas être présentées comme des dossiers réels.

Chaque fiche client et contrat porte maintenant une origine explicite : rapport CRIF, donnée fictive ou origine non renseignée. Les lignes présentes avant l'ajout de ce champ restent « origine non renseignée » ; cette valeur ne permet pas de conclure si elles sont réelles ou fictives. Le rapport de solvabilité affiche l'origine séparément pour la fiche client et chaque contrat.

## Conditions de calcul

- Le score de paiement utilise uniquement les contrats décaissés où le client est emprunteur, avec des échéances échues ou des statuts mensuels interprétables.
- Les contrats où le client est garant ne deviennent pas ses propres crédits.
- Les historiques mensuels utilisent le nombre de jours de retard s'il est connu. Un libellé large comme « impayé » ou « 30+ » ne suffit pas à inventer un bucket précis.
- Le score ne mélange pas des montants dans des devises différentes. Il devient non calculable si la devise ou les montants nécessaires aux ratios sont inconnus.
- L'absence de revenu annuel n'empêche pas le calcul fondé sur les paiements, mais les ratios d'endettement et d'exposition sont alors exclus et signalés dans le rapport.
- Les échéances partiellement payées restent dues jusqu'au paiement intégral.

## Données nécessaires avant toute calibration

Pour évaluer un score prédictif, il faut un échantillon autorisé et anonymisé comprenant des événements datés, les engagements déclarés, les paiements et une définition explicite du défaut ou du retard cible. Il faut ensuite vérifier la qualité et la représentativité des données, tester la calibration hors échantillon, mesurer les erreurs et faire valider la méthode par CECAM. En l'absence de ces éléments, le score reste un indicateur de démonstration pour l'analyse humaine.
