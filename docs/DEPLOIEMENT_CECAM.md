# Lancer et préparer le BIC CECAM

## Démonstration locale sur un autre PC

**Option la plus simple : Docker Desktop.** Copier le dépôt, créer `.env` depuis `.env.example`, puis remplir un utilisateur et un mot de passe PostgreSQL dédiés ainsi qu'un secret JWT aléatoire d'au moins 32 caractères. Ne pas partager le fichier `.env`.

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Ouvrir `http://localhost:8081`. Les données restent dans un volume PostgreSQL nommé. Pour arrêter les services sans supprimer la base : `docker compose down`. Les identifiants et les données de démonstration restent à gérer conformément au prototype local.

**Démarrage manuel sans Docker.** Installer Java 21, PostgreSQL, Maven et Node.js avec npm. Copier le dépôt sur le PC, créer la base PostgreSQL locale attendue par l'application et configurer les variables d'environnement avant de lancer les deux applications.

Dans PowerShell, depuis la racine du dépôt, renseigner les valeurs locales (ne pas enregistrer de vrais secrets dans Git) :

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/bic_cecam"
$env:SPRING_DATASOURCE_USERNAME = "<utilisateur-local-postgresql>"
$env:SPRING_DATASOURCE_PASSWORD = "<mot-de-passe-local>"
$env:APP_JWT_SECRET = "<secret-local-aleatoire-de-32-caracteres-minimum>"
$env:APP_CORS_ALLOWED_ORIGINS = "http://localhost:5173,http://localhost:5174"
```

Terminal backend :

```powershell
cd bic-backend
mvn "-Dmaven.repo.local=$env:USERPROFILE/.m2/repository" clean spring-boot:run
```

Terminal frontend :

```powershell
cd bic-frontend
$env:VITE_API_BASE_URL = "http://localhost:8080/api"
npm install
npm run dev
```

Le frontend Vite et PostgreSQL doivent être démarrés sur le même PC pour cette configuration par défaut. L'application initialise actuellement les données configurées dans `data.sql` et `data-crif.sql` à chaque démarrage ; ces données servent au prototype local et ne doivent pas être utilisées dans une base de production.

## Base partagée sur un serveur CECAM

L'architecture à déployer est : navigateur des agents → frontend web → API Spring Boot → PostgreSQL. Le serveur PostgreSQL doit être joignable uniquement depuis l'API et sauvegardé. L'API doit être joignable depuis les postes CECAM, et la liste `APP_CORS_ALLOWED_ORIGINS` doit contenir exactement l'origine du frontend, par exemple `https://bic.cecam.mg`.

Pour une mise en service réelle, les valeurs de connexion PostgreSQL et `APP_JWT_SECRET` doivent venir du gestionnaire de secrets ou de l'environnement du serveur. Désactiver l'initialisation des données de démonstration (`SPRING_SQL_INIT_MODE=never`), appliquer les migrations SQL contrôlées, puis faire démarrer Hibernate en validation de schéma (`SPRING_JPA_HIBERNATE_DDL_AUTO=validate`). La migration de la file admin est `bic-backend/src/main/resources/db/manual/20261009_create_modification_requests.sql` ; les migrations antérieures listées dans `CODE_GRAPH.md` sont également à examiner et à appliquer selon l'état de la base.

Avant d'utiliser des dossiers réels, il faut encore fournir une procédure approuvée de création du premier compte admin, configurer les comptes des agents, remplacer les jeux de données fictifs par une importation autorisée, définir la sauvegarde/restauration et faire valider les règles de score et les accès par CECAM. Le `ddl-auto=update`, les comptes de démonstration, les valeurs de secours de configuration et l'initialisation SQL actuelle sont adaptés au prototype, pas à une base de production.

## Vercel

Vercel peut héberger le frontend Vite en statique. Définir le répertoire du projet sur `bic-frontend`, construire avec `npm run build`, publier `dist`, et définir `VITE_API_BASE_URL` comme URL publique HTTPS de l'API.

L'API actuelle est une application Java Spring Boot persistante et utilise PostgreSQL. Il faut donc héberger séparément le backend sur un serveur ou une plateforme compatible Java, et héberger PostgreSQL sur un service auquel seul le backend peut accéder. Un frontend Vercel ne peut pas se connecter au PostgreSQL local d'un PC. L'API doit autoriser uniquement le domaine de production dans CORS. Vercel documente Vite comme frontend et ses runtimes de fonctions officiels ne comprennent pas Java ; en pratique, je recommande de garder le backend et la base sur l'infrastructure interne CECAM et de ne mettre le frontend sur Vercel qu'après validation sécurité/DSI.
