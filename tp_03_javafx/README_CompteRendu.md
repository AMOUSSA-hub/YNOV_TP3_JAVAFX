# Compte Rendu - Application de Gestion des Tickets de Support (TP3 JavaFX)

## 1. Architecture Choisie

### 🏗️ Pattern Architectural : MVC + DAO

L'application suit une architecture **Model-View-Controller** enrichie du pattern **Data Access Object (DAO)**, permettant une séparation claire des responsabilités :

```
┌─────────────────────────────────────────────────────────┐
│                    Couche Présentation                   │
│  (PrimaryController, SecondaryController, FXML, CSS)    │
└──────────────────────────┬────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────┐
│              Couche Métier / Service                   │
│         (TicketPersistenceService)                     │
│    - Gestion de la liste observable (UI binding)      │
│    - Orchestration des opérations CRUD                │
└──────────────────────────┬────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────┐
│         Couche Persistance (DAO)                       │
│  - Interface : TicketDao                              │
│  - Implémentation : SQLiteTicketDao                   │
└──────────────────────────┬────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────┐
│     Couche Accès aux Données                          │
│  - DatabaseManager (singleton - gestion connexions)   │
│  - SQLite (base de données locale)                    │
└─────────────────────────────────────────────────────┘
```

### 📊 Modèle de Données

**Classe métier : `SupportTicket`**
- Classe **immuable** (immutability)
- Attributs :
  - `id` : identifiant primaire
  - `title` : titre du ticket
  - `customerName` : nom du client
  - `priority` : niveau de priorité
  - `createdAt` : date de création
  - `description` : description du problème
  - `urgent` : booléen d'urgence
  - `status` : état du ticket

### 🔧 Composants Principaux

| Composant | Responsabilité |
|-----------|----------------|
| `DatabaseManager` | Gestion singleton des connexions SQLite |
| `TicketDao` (interface) | Contrat pour les opérations d'accès aux données |
| `SQLiteTicketDao` | Implémentation concrète du DAO pour SQLite |
| `TicketPersistenceService` | Service métier exposant les opérations CRUD |
| `PrimaryController` / `SecondaryController` | Contrôleurs JavaFX (navigation, actions UI) |
| `TicketExporter` | Exporte les tickets en différents formats |

---

## 2. Rôle du DAO (Data Access Object)

### 🎯 Objectifs du Pattern DAO

Le pattern DAO encapsule la **logique d'accès aux données** et offre une abstraction permettant :
- ✅ **Isolation** : Découplage entre la logique métier et la persistance
- ✅ **Maintenabilité** : Modification facile de la source de données (SQLite → MySQL → PostgreSQL)
- ✅ **Testabilité** : Possibilité de créer des implémentations mock pour les tests
- ✅ **Réutilisabilité** : Interface commune utilisable par plusieurs services

### 📋 Interface `TicketDao`

```java
public interface TicketDao {
    SupportTicket insert(SupportTicket ticket);           // CREATE
    List<SupportTicket> findAll();                        // READ (tous)
    Optional<SupportTicket> findById(long id);            // READ (par id)
    List<SupportTicket> searchByTitleOrCustomer(String); // READ (recherche)
    void update(SupportTicket ticket);                    // UPDATE
    void deleteById(long id);                             // DELETE
    void deleteAll();                                     // DELETE (tous)
}
```

### 🔌 Implémentation `SQLiteTicketDao`

Implémente l'interface `TicketDao` avec :
- **Utilisation de PreparedStatements** : Protection contre les injections SQL
- **try-with-resources** : Gestion automatique des ressources (connexions, statements)
- **Mapping ResultSet** : Conversion des lignes SQL en objets `SupportTicket`
- **Transactions implicites** : Autocommit activé pour chaque opération

---

## 3. Fonctionnement Global du CRUD

### 📝 Opérations Supportées

#### **CREATE : Création d'un ticket**
```java
SupportTicket newTicket = new SupportTicket(
    "Problème de connexion", 
    "Jean Dupont",
    "HAUTE",
    LocalDate.now(),
    "Impossible de se connecter au portail",
    true,
    "OUVERT"
);

SupportTicket created = service.createTicket(newTicket);
// La BD génère un ID et le ticket est ensuite rafraîchi
```

#### **READ : Lecture des tickets**
```java
// Tous les tickets
ObservableList<SupportTicket> tickets = service.getTickets();

// Recherche par ID
Optional<SupportTicket> ticket = dao.findById(5);

// Recherche par mot-clé (titre ou client)
List<SupportTicket> results = service.search("Dupont");
```

#### **UPDATE : Modification d'un ticket**
```java
SupportTicket updated = ticket.withId(5)  // Création d'une copie avec id
                               .withStatus("FERMÉ");
service.updateTicket(updated);
```

#### **DELETE : Suppression**
```java
// Suppression d'un ticket
service.deleteTicket(5);

// Suppression de tous les tickets
service.deleteAllTickets();
```

### 🔄 Flux Général d'une Opération

1. **Interface utilisateur** → Action (bouton, formulaire)
2. **Contrôleur** → Appel du service approprié
3. **Service `TicketPersistenceService`** → Appel de la méthode DAO
4. **DAO `SQLiteTicketDao`** → Exécution de la requête SQL
5. **`DatabaseManager`** → Fourniture de la connexion SQLite
6. **Base de données** → Exécution et retour des données
7. **Service** → Rafraîchissement de `ObservableList` (binding automatique l'UI)
8. **Vue** → Mise à jour automatique via JavaFX FX

### 📊 Flux d'une Insertion Complète

```
Utilisateur crée un formulaire
        ↓
SecondaryController.handleSave()
        ↓
TicketPersistenceService.createTicket(ticket)
        ↓
SQLiteTicketDao.insert(ticket)
        ↓
INSERT INTO support_tickets SQL + RETURN_GENERATED_KEYS
        ↓
BD génère id_auto et retourne la clé
        ↓
Retour ticket.withId(generatedId)
        ↓
Service rafraîchit : refresh()
        ↓
DAO.findAll() récupère toutes les entrées
        ↓
ObservableList mise à jour
        ↓
TableView JavaFX rafraîchit automatiquement ✓
```

---

## 4. Difficultés Rencontrées

### 🚧 Défis Rencontrés et Solutions

#### **1. Gestion des Clés Auto-générées**
- 🔴 **Problème** : Récupération de l'ID généré par SQLite après insertion
- ✅ **Solution** : Utilisation de `Statement.RETURN_GENERATED_KEYS` avec `pstmt.getGeneratedKeys()`

#### **2. Immuabilité et Modification**
- 🔴 **Problème** : `SupportTicket` est immuable, comment mettre à jour un ticket ?
- ✅ **Solution** : Méthode `withId()` et pattern builder pour créer des copies modifiées

#### **3. Binding entre La BD et l'UI**
- 🔴 **Problème** : Synchronisation entre les changements en BD et l'affichage JavaFX
- ✅ **Solution** : Utilisation d'`ObservableList` dans le service + `setAll()` après chaque opération

#### **4. Gestion des Ressources**
- 🔴 **Problème** : Connexions/statements non fermés → fuites mémoire
- ✅ **Solution** : try-with-resources pour fermeture automatique

#### **5. Sérialisation des Date**
- 🔴 **Problème** : SQLite n'a pas de type DATE natif
- ✅ **Solution** : Stockage en STRING (format ISO 8601), conversion avec `LocalDate.parse()`

#### **6. Requêtes Dynamiques de Recherche**
- 🔴 **Problème** : Recherche LIKE sur plusieurs champs (titre ET client)
- ✅ **Solution** : Clause `WHERE title LIKE ? OR customer_name LIKE ?`

---

## 5. Améliorations Possibles

### 🚀 Court Terme (Facile à implémenter)

#### ✨ Améliorations Immédiates

1. **Gestion des Exceptions Centralisée**
   ```java
   // Actuellement : try/catch dans chaque méthode
   // À faire : Custom exceptions + gestion globale au niveau du contrôleur
   public class TicketPersistenceException extends RuntimeException { }
   ```

2. **Logging Professionnel**
   ```java
   // Remplacer les e.printStackTrace() par
   import org.slf4j.Logger;
   Logger logger = LoggerFactory.getLogger(SQLiteTicketDao.class);
   logger.error("Erreur lors de la recherche", e);
   ```

3. **Validation des Données**
   ```java
   // Ajouter des validations :
   if (ticket.getTitle() == null || ticket.getTitle().isEmpty()) {
       throw new IllegalArgumentException("Le titre ne peut pas être vide");
   }
   ```

4. **Pagination**
   ```java
   // Pour les applications avec beaucoup de tickets
   List<SupportTicket> findAll(int page, int pageSize);
   ```

5. **Sorting et Filtering Avancés**
   ```java
   List<SupportTicket> findByStatus(String status);
   List<SupportTicket> findByPriority(String priority);
   List<SupportTicket> findByDateRange(LocalDate start, LocalDate end);
   ```

---

### 🎯 Moyen Terme (Amélioration Architecture)

6. **Lazy Loading et Cache**
   ```java
   // Éviter de recharger ALL les tickets à chaque fois
   // Cache avec invalidation intelligente
   ```

7. **Pool de Connexions**
   ```java
   // HikariCP pour gérer un pool de connexions effiaces
   <dependency>
       <groupId>com.zaxxer</groupId>
       <artifactId>HikariCP</artifactId>
   </dependency>
   ```

8. **Transactions Explicites**
   ```java
   // Actuellement : autocommit à chaque opération
   // À faire : Gestion des transactions multi-pas
   public void transferTicket(long from, long to) 
       throws SQLException {
       try (Connection conn = getConnection()) {
           conn.setAutoCommit(false);
           try {
               // Opérations
               conn.commit();
           } catch (Exception e) {
               conn.rollback();
           }
       }
   }
   ```

---

### 💎 Long Terme (Refactoring Majeur)

9. **ORM (Object-Relational Mapping)**
   ```java
   // Utiliser Hibernate ou JPA à la place du JDBC brut
   // Réduit énormément le code de mapping
   ```

10. **Injection de Dépendances**
    ```java
    // Spring Framework pour gérer les dépendances
    @Autowired
    private TicketDao ticketDao;
    ```

11. **Tests Unitaires**
    ```java
    // JUnit 5 + Mockito pour tester isolément
    @Test
    void testInsertTicket() {
        TicketDao mockDao = mock(SQLiteTicketDao.class);
        // ...
    }
    ```

12. **Migration de BD Versionnée**
    ```java
    // Flyway ou Liquibase pour gérer les versions de schéma
    ```

13. **API REST (Backend)**
    ```java
    // Spring Boot REST + BD serveur
    // Permet d'utiliser l'app sur plusieurs machines
    ```

14. **Base de données Serveur**
    ```java
    // PostgreSQL, MySQL à la place de SQLite
    // Pour application multi-utilisateur
    ```

---

### 🎨 Améliorations UX/UI

15. **Confirmation avant Suppression**
    ```java
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Confirmation");
    confirm.setContentText("Êtes-vous sûr ?");
    confirm.showAndWait();
    ```

16. **Messages d'Erreur Conviviales**
    - Remplacer les stacktraces par des messages utilisateur clairs

17. **Drag & Drop pour Changer de Statut**
    - Déplacer un ticket d'une colonne à une autre

18. **Filtrage en Temps Réel**
    - Recherche au fur et à mesure que l'utilisateur tape

19. **Thème Sombre**
    - CSS moderne et adaptation du contraste

---

### 📊 Métriques et Monitoring

20. **Dashboard de Statistiques**
    ```
    - Nombre de tickets par statut
    - Nombre de tickets urgents
    - Statistiques par priorité
    - Temps moyen de résolution
    ```

---

## Conclusion

Cette application démontre une **architecture bien structurée** avec :
- ✅ Séparation des responsabilités (MVC + DAO)
- ✅ Immuabilité et sécurité thread
- ✅ Binding automatique UI/Données
- ✅ Accès sécurisé aux données (PreparedStatements)

Les **améliorations prioritaires** seraient :
1. **Gestion d'erreurs robuste**
2. **Tests unitaires**
3. **Logging professionnel**
4. **Validation des données**

L'architecture actuelle est **scalable** et peut facilement évoluer vers une solution multi-utilisateur avec base de données sérveuse.

---

**Auteur** : TP3 JavaFX  
**Date** : Mars 2026  
**Technologie** : Java 21 | JavaFX 21 | SQLite | Maven
