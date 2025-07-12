# 📚 Book Management API

Une API REST de gestion de livres développée avec Spring Boot et Kotlin, suivant l'architecture hexagonale.

## 🏗️ Architecture

Ce projet utilise l'architecture hexagonale (ports et adaptateurs) avec :
- **Domain** : Logique métier et règles de gestion
- **Infrastructure** : Adaptateurs pour la persistance et les API
- **Tests** : Couverture complète avec 4 types de tests

## 🧪 Types de tests

- **Tests unitaires** : Tests du domaine métier
- **Tests d'architecture** : Validation de l'architecture hexagonale
- **Tests de composants** : Tests BDD avec Cucumber
- **Tests d'intégration** : Tests avec base de données réelle

## 📋 Prérequis

- Java 21
- Docker et Docker Compose
- Git

## 🚀 Démarrage rapide

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd demoBookManagement
```

### 2. Démarrer la base de données PostgreSQL

```bash
cd docker/book-management-local
docker-compose up -d
```

Cette commande démarre un conteneur PostgreSQL avec :
- **Port** : 5432
- **Base de données** : bookmanagement
- **Utilisateur** : bookmanagement
- **Mot de passe** : bookmanagement

### 3. Vérifier que Docker fonctionne

```bash
# Vérifier que le conteneur PostgreSQL est démarré
docker ps

# Vous devriez voir un conteneur postgres en cours d'exécution
```

### 4. Donner les permissions d'exécution (Linux/Mac uniquement)

```bash
chmod +x gradlew
```

### 5. Exécuter tous les tests

```bash
# Lancer tous les types de tests
./gradlew test testArchitecture testComponent testIntegration

# Alternative : forcer la ré-exécution des tests
./gradlew test testArchitecture testComponent testIntegration --rerun-tasks
```

## 🧪 Commandes de test détaillées

### Tests unitaires
```bash
./gradlew test
```
- Tests du domaine métier (Book, BookUseCase)
- Tests de propriétés avec Kotest
- Couverture : ~6-8 tests

### Tests d'architecture
```bash
./gradlew testArchitecture
```
- Validation de l'architecture hexagonale
- Vérification des dépendances entre couches
- Couverture : 2 tests

### Tests de composants (BDD)
```bash
./gradlew testComponent
```
- Tests comportementaux avec Cucumber
- Tests API avec RestAssured
- Base de données en mémoire avec Testcontainers

### Tests d'intégration
```bash
./gradlew testIntegration
```
- Tests avec base de données PostgreSQL réelle
- Tests des contrôleurs et DAO
- Couverture : 2 tests

## 📊 Rapports de tests

Après l'exécution des tests, les rapports sont disponibles dans :

```
build/reports/tests/
├── test/              # Rapports des tests unitaires
├── testArchitecture/  # Rapports des tests d'architecture
├── testComponent/     # Rapports des tests de composants
└── testIntegration/   # Rapports des tests d'intégration
```

Ouvrez `build/reports/tests/[type-test]/index.html` dans votre navigateur pour voir les détails.

## 🏃‍♂️ Démarrer l'application

```bash
# Démarrer l'application Spring Boot
./gradlew bootRun
```

L'API sera accessible sur : `http://localhost:8080`

### Documentation API
- Swagger UI : `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON : `http://localhost:8080/v3/api-docs`

## 🛠️ Commandes utiles

### Nettoyage et reconstruction
```bash
./gradlew clean build
```

### Générer le rapport de couverture de code
```bash
./gradlew jacocoTestReport
```
Rapport disponible dans : `build/reports/jacoco/test/html/index.html`

### Tests de mutation avec PIT
```bash
./gradlew pitest
```

### Arrêter Docker
```bash
cd docker/book-management-local
docker-compose down
```

## 🐛 Dépannage

### Erreur de connexion à la base de données
1. Vérifiez que Docker est démarré : `docker ps`
2. Redémarrez PostgreSQL : 
   ```bash
   cd docker/book-management-local
   docker-compose down
   docker-compose up -d
   ```

### Tests qui échouent
1. Nettoyez le cache Gradle : `./gradlew clean`
2. Vérifiez que PostgreSQL est accessible : `docker logs <container-id>`
3. Relancez les tests : `./gradlew test testArchitecture testComponent testIntegration --rerun-tasks`

### Problèmes de permissions (Linux/Mac)
```bash
chmod +x gradlew
```

## 📁 Structure du projet

```
src/
├── main/kotlin/com/ajessondaniel/bookmanagement/
│   ├── domain/                    # Logique métier
│   ├── infrastructure/            # Adaptateurs
│   └── BookManagementApplication.kt
├── test/kotlin/                   # Tests unitaires
├── testArchitecture/kotlin/       # Tests d'architecture
├── testComponent/                 # Tests BDD/Cucumber
│   ├── kotlin/
│   └── resources/features/
└── testIntegration/kotlin/        # Tests d'intégration
```

## 🔧 Technologies utilisées

- **Kotlin** - Langage de programmation
- **Spring Boot 3.1.3** - Framework web
- **PostgreSQL** - Base de données
- **Liquibase** - Migration de base de données
- **Kotest** - Framework de tests
- **Cucumber** - Tests BDD
- **RestAssured** - Tests API
- **Testcontainers** - Tests avec conteneurs
- **ArchUnit** - Tests d'architecture
- **Docker** - Conteneurisation

## ✅ Validation finale

Pour valider que tout fonctionne correctement :

1. ✅ PostgreSQL démarré : `docker ps`
2. ✅ Compilation réussie : `./gradlew compileKotlin`
3. ✅ Tous les tests passent : `./gradlew test testArchitecture testComponent testIntegration`
4. ✅ Application démarre : `./gradlew bootRun`

Si toutes ces étapes réussissent, votre environnement est correctement configuré ! 🎉

## 📝 Notes importantes

- Les tests d'intégration et de composants utilisent Testcontainers et nécessitent Docker
- La base de données PostgreSQL doit être démarrée avant les tests d'intégration
- Les tests unitaires et d'architecture n'ont pas besoin de Docker
- Le projet utilise Java 21 - assurez-vous d'avoir la bonne version installée
