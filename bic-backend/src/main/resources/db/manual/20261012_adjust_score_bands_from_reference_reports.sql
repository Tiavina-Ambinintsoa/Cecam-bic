-- Ajustement provisoire des seuils A/B à partir des intervalles observés dans les rapports
-- de référence fournis. Le centre de la zone non observée entre B max et A min est retenu.
-- Les marges C/D/E restent celles du modèle interne et ne sont pas validées par ces rapports.
UPDATE grille_score
SET score_min = 530,
    score_max = 850,
    categorie_risque = 'Risque tres faible'
WHERE intervalle = 'A';

UPDATE grille_score
SET score_min = 500,
    score_max = 529,
    categorie_risque = 'Risque faible'
WHERE intervalle = 'B';

UPDATE grille_score
SET categorie_risque = 'Risque tresélevé'
WHERE intervalle = 'E';
