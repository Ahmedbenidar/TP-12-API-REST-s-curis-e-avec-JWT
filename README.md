# TP 12 — API REST sécurisée avec JWT


## Données de démonstration

Créées automatiquement au premier démarrage par `InitialisationDonnees`.

| Identifiant | Mot de passe | Rôles | Dossiers |
|---|---|---|---|
| `a.benidar` | `admin123` | `ROLE_ADMIN`, `ROLE_MEMBRE` | 0 |
| `s.idalen` | `membre123` | `ROLE_MEMBRE` | 3 |
| `k.ouazzani` | `membre123` | `ROLE_MEMBRE`, `ROLE_OBSERVATEUR` | 2 |

Cinq dossiers `DOS-2024-001` à `DOS-2024-005` sont également insérés
(2 validés, 2 en attente, 1 refusé).

---

## Prérequis

- JDK 17 ou plus récent
- Maven 3.9+
- MySQL ou MariaDB accessible sur `localhost:3306` avec l'utilisateur `root` sans mot de passe
  (la base `portail_jeton_db` est créée automatiquement)

---

## Lancer le projet

```bash
mvn clean package
java -jar target/portail-jeton-api-1.0.0.jar
```

L'application écoute sur **http://localhost:8099**. La racine `/` ouvre la console de test.

---

## Résultats obtenus

### 1. Console au démarrage

La console se charge et interroge immédiatement la seule route publique :
le bandeau affiche `service operationnel · 5 dossiers`, ce qui confirme que la base est joignable
et que le jeu de données a bien été inséré.

![Console initiale](docs/screenshots/01-console-initiale.png)

### 2. Obtention d'un jeton

Connexion avec `s.idalen` : la réponse est un `200` contenant un jeton JWT, le type `Bearer`,
la durée de validité en secondes et la liste des rôles. Le jeton est conservé côté navigateur
et sera renvoyé dans l'en-tête `Authorization` des appels suivants.

![Jeton obtenu](docs/screenshots/02-jeton-obtenu.png)

### 3. Accès à l'espace membre

Avec le jeton du membre, `GET /api/membre/tableau-de-bord` répond `200` et renvoie les
3 dossiers déposés par `s.idalen`. Le serveur ne consulte aucune session : il reconstruit
l'identité à partir des *claims* du jeton à chaque requête.

![Espace membre](docs/screenshots/03-espace-membre.png)

### 4. Accès refusé à l'administration

Le même jeton, utilisé sur `GET /api/administration/statistiques`, donne un `403`.
L'utilisateur est bien authentifié mais il ne possède pas `ROLE_ADMIN` : c'est
l'autorisation qui échoue, pas l'authentification.

![Accès refusé 403](docs/screenshots/04-acces-refuse-403.png)

### 5. Requête sans jeton

Sans en-tête `Authorization`, la requête n'atteint jamais le contrôleur : le point d'entrée
personnalisé renvoie un `401` au format JSON avec un message explicite et le chemin demandé.

![Sans jeton 401](docs/screenshots/05-sans-jeton-401.png)

### 6. Jeton falsifié

Un jeton dont la signature a été modifiée est rejeté par le filtre : la vérification HMAC échoue
et la réponse est également un `401`. Impossible donc de s'inventer le rôle `ROLE_ADMIN`
en éditant le contenu du jeton.

![Jeton falsifié 401](docs/screenshots/06-jeton-falsifie-401.png)

### 7. Statistiques d'administration

Après connexion avec `a.benidar` (porteur de `ROLE_ADMIN`), la route réservée répond `200`
et agrège les données : 3 comptes, 5 dossiers au total, 2 en attente, 2 validés.

![Statistiques administration](docs/screenshots/07-administration.png)

### 8. Liste des comptes

Toujours avec le jeton d'administration, `GET /api/administration/comptes` renvoie `200`
et la liste des comptes avec leurs rôles respectifs.

![Liste des comptes](docs/screenshots/08-comptes-admin.png)

---

