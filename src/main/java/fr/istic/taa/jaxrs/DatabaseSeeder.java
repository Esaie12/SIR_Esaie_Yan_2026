package fr.istic.taa.jaxrs;

import fr.istic.taa.jaxrs.dao.EntityManagerHelper;
import fr.istic.taa.jaxrs.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Seeder — lance-le une seule fois pour peupler la base de données.
 * Usage : exécuter la méthode main() directement depuis votre IDE.
 *
 * Il crée :
 *   - 1 Admin
 *   - 2 Users Physique  (Alice, Bob)
 *   - 1 User Moral      (Acme Corp)
 *   - 10 Clients répartis entre les users
 *   - 4 Groupes (2 par user Physique)
 *   - Associations Client ↔ Groupe
 *   - 6 Messages (user → user et user → groupe)
 */
public class DatabaseSeeder {

    private static final Logger log = Logger.getLogger(DatabaseSeeder.class.getName());

    public static void main(String[] args) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // ── 1. Admin ─────────────────────────────────────────────────────
            Admin admin = new Admin(
                    "admin@istic.fr", "admin123",
                    "Super", "Admin", "superadmin");
            em.persist(admin);
            log.info("Admin créé");

            // ── 2. Users Physique ─────────────────────────────────────────────
            Physique alice = new Physique(
                    "alice@example.com", "alice123",
                    "Alice", "Martin",
                    true, LocalDateTime.now().minusDays(30),
                    "F", LocalDate.of(1990, 3, 15));

            Physique bob = new Physique(
                    "bob@example.com", "bob123",
                    "Bob", "Dupont",
                    true, LocalDateTime.now().minusDays(20),
                    "M", LocalDate.of(1985, 7, 22));

            em.persist(alice);
            em.persist(bob);
            log.info("Users Physique créés : Alice, Bob");

            // ── 3. User Moral ─────────────────────────────────────────────────
            Moral acme = new Moral(
                    "contact@acme.com", "acme123",
                    "Jean", "Directeur",
                    true, LocalDateTime.now().minusDays(10),
                    "Acme Corporation");
            em.persist(acme);
            log.info("User Moral créé : Acme Corp");

            // ── 4. Clients d'Alice (5 clients) ───────────────────────────────
            Client c1 = client("Marie Curie",       "marie@lab.fr",       "0600000001", "Paris",      "France",  "F", alice);
            Client c2 = client("Jean Valjean",      "jean@littleone.fr",  "0600000002", "Lyon",       "France",  "M", alice);
            Client c3 = client("Isabelle Huppert",  "isabelle@films.fr",  "0600000003", "Bordeaux",   "France",  "F", alice);
            Client c4 = client("Carlos Garcia",     "carlos@es.com",      "0600000004", "Madrid",     "Espagne", "M", alice);
            Client c5 = client("Sofia Loren",       "sofia@it.com",       "0600000005", "Rome",       "Italie",  "F", alice);

            // ── 5. Clients de Bob (5 clients) ────────────────────────────────
            Client c6  = client("Paul Dupont",      "paul@free.fr",       "0600000006", "Nantes",     "France",  "M", bob);
            Client c7  = client("Laura Martin",     "laura@gmail.com",    "0600000007", "Marseille",  "France",  "F", bob);
            Client c8  = client("Ahmed Ben Ali",    "ahmed@mail.tn",      "0600000008", "Tunis",      "Tunisie", "M", bob);
            Client c9  = client("Emma Watson",      "emma@uk.co",         "0600000009", "Londres",    "UK",      "F", bob);
            Client c10 = client("Luca Bianchi",     "luca@it.com",        "0600000010", "Milan",      "Italie",  "M", bob);

            for (Client c : List.of(c1,c2,c3,c4,c5,c6,c7,c8,c9,c10)) em.persist(c);
            log.info("10 clients créés");

            // ── 6. Groupes d'Alice ────────────────────────────────────────────
            Groupe gVip    = groupe("VIP",         "#FFD700", alice);
            Groupe gFrance = groupe("France",      "#0055A4", alice);

            // ── 7. Groupes de Bob ─────────────────────────────────────────────
            Groupe gEurope = groupe("Europe",      "#003399", bob);
            Groupe gPromo  = groupe("Promo Été",   "#FF6B35", bob);

            for (Groupe g : List.of(gVip, gFrance, gEurope, gPromo)) em.persist(g);
            log.info("4 groupes créés");

            // flush pour obtenir les IDs générés avant de créer les associations
            em.flush();

            // ── 8. Associations Client ↔ Groupe ──────────────────────────────
            // Alice : VIP → c1, c3 | France → c1, c2, c3
            em.persist(new ClientGroupe(c1, gVip));
            em.persist(new ClientGroupe(c3, gVip));
            em.persist(new ClientGroupe(c1, gFrance));
            em.persist(new ClientGroupe(c2, gFrance));
            em.persist(new ClientGroupe(c3, gFrance));

            // Bob : Europe → c6, c7, c9, c10 | Promo → c6, c8
            em.persist(new ClientGroupe(c6,  gEurope));
            em.persist(new ClientGroupe(c7,  gEurope));
            em.persist(new ClientGroupe(c9,  gEurope));
            em.persist(new ClientGroupe(c10, gEurope));
            em.persist(new ClientGroupe(c6,  gPromo));
            em.persist(new ClientGroupe(c8,  gPromo));
            log.info("Associations Client-Groupe créées");

            // ── 9. Messages ───────────────────────────────────────────────────
            // Alice → Bob (message direct)
            em.persist(new Message(
                    "Bienvenue",
                    "Bonjour Bob, bienvenue sur la plateforme !",
                    LocalDateTime.now().minusDays(5),
                    bob, alice));

            // Alice → groupe VIP
            em.persist(new Message(
                    "Offre exclusive VIP",
                    "Chère clientèle VIP, profitez de -20% ce weekend.",
                    LocalDateTime.now().minusDays(3),
                    gVip, alice));

            // Alice → groupe France
            em.persist(new Message(
                    "Événement Paris",
                    "Rejoignez-nous le 15 mai à Paris pour notre journée portes ouvertes.",
                    LocalDateTime.now().minusDays(1),
                    gFrance, alice));

            // Bob → Alice (message direct)
            em.persist(new Message(
                    "Re: Bienvenue",
                    "Merci Alice, ravi d'être ici !",
                    LocalDateTime.now().minusDays(4),
                    alice, bob));

            // Bob → groupe Europe
            em.persist(new Message(
                    "Soldes Europe",
                    "Promotions spéciales pour tous nos clients européens.",
                    LocalDateTime.now().minusHours(12),
                    gEurope, bob));

            // Bob → groupe Promo Été
            em.persist(new Message(
                    "Promo Été 2025",
                    "Découvrez nos offres estivales en avant-première !",
                    LocalDateTime.now().minusHours(2),
                    gPromo, bob));

            log.info("6 messages créés");

            tx.commit();
            log.info("✅ Seeder terminé avec succès !");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.severe("❌ Erreur durant le seeder : " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static Client client(String name, String email, String phone,
                                 String localisation, String country,
                                 String sexe, Users user) {
        Client c = new Client();
        c.setName(name);
        c.setEmail(email);
        c.setPhone(phone);
        c.setLocalisation(localisation);
        c.setCountry(country);
        c.setSexe(sexe);
        c.setUser(user);
        return c;
    }

    private static Groupe groupe(String libelle, String color, Users user) {
        Groupe g = new Groupe(libelle);
        g.setColor(color);
        g.setUser(user);
        return g;
    }
}