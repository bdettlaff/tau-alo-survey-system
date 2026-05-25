package com.edu.tau.alo.tau_survey_system.loader;

import com.edu.tau.alo.tau_survey_system.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        Long countTeachers = (Long) em.createQuery("SELECT COUNT(t) FROM Teacher t").getSingleResult();
        Long countQuestions = (Long) em.createQuery("SELECT COUNT(q) FROM Question q").getSingleResult();

        if (countTeachers > 0 && countQuestions > 0) {
            System.out.println("=== Dane (nauczyciele i pytania) juz istnieja, pomijam ladowanie. ===");
            return;
        }

        System.out.println("=== Ladowanie danych... ===");

        // Mapa przechowująca referencje do obiektów kategorii pytań
        Map<String, QuestionCategory> categories = new HashMap<>();

        // --- PYTANIA I KATEGORIE (INDEKS STAŁY) ---
        if (countQuestions == 0) {
            System.out.println("=== Inicjalizacja stalego indeksu pytan ===");

            Map<String, String> rawCategories = Map.of(
                    "OGOLNE", "Ogólna ocena zajęć",
                    "JEZYKI", "Języki obce",
                    "SCISLE", "Przedmioty ścisłe",
                    "POLSKI", "Język polski",
                    "WF", "Wychowanie fizyczne",
                    "ZAWODOWE", "Przedmioty zawodowe",
                    "SZKOLA", "Ewaluacja szkoły (Sekcja B)"
            );

            for (Map.Entry<String, String> entry : rawCategories.entrySet()) {
                QuestionCategory cat;
                try {
                    cat = em.createQuery("SELECT c FROM QuestionCategory c WHERE c.name = :name", QuestionCategory.class)
                            .setParameter("name", entry.getValue())
                            .getSingleResult();
                } catch (Exception e) {
                    cat = new QuestionCategory();
                    cat.setName(entry.getValue());
                    em.persist(cat);
                }
                categories.put(entry.getKey(), cat);
            }
            em.flush();

            // DODAWANIE PYTAŃ Z JAWNYMI ID
            saveQuestion("A1", "Jak ogólnie oceniasz te zajęcia?", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A2", "Nauczyciel/ka jasno i zrozumiale tłumaczy nowy materiał.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A3", "Tempo zajęć jest dostosowane do możliwości uczniów.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A4", "Ocenianie jest sprawiedliwe i zrozumiałe – wiem, za co dostałem/am daną ocenę.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A5", "Na tych zajęciach czuję się bezpiecznie – mogę pytać, nie boję się popełnić błędu.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A6", "Nauczyciel/ka jest dostępna/y i chętna/y do pomocy poza lekcją.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A7", "Zajęcia są prowadzone w sposób, który angażuje mnie i motywuje do nauki.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A8", "Nauczyciel/ka traktuje wszystkich uczniów jednakowo i z szacunkiem.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A9", "Sprawdziany i kartkówki zapowiadane są z odpowiednim wyprzedzeniem.", "SCALE", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A+", "Co najbardziej cenisz na tych zajęciach?", "OPEN", "wszyscy", null, categories.get("OGOLNE"));
            saveQuestion("A-", "Co chciałbyś/chciałabyś zmienić lub poprawić na tych zajęciach?", "OPEN", "wszyscy", null, categories.get("OGOLNE"));

            saveQuestion("L1", "Na zajęciach mam wystarczająco dużo okazji do mówienia w języku obcym.", "SCALE", "języki obce", null, categories.get("JEZYKI"));
            saveQuestion("L4", "Po tych zajęciach czuję, że mój poziom języka faktycznie się poprawia.", "SCALE", "języki obce", null, categories.get("JEZYKI"));

            saveQuestion("S2", "Gdy czegoś nie rozumiem, nauczyciel/ka tłumaczy na różne sposoby.", "SCALE", "ścisłe — wariant pełny", null, categories.get("SCISLE"));
            saveQuestion("S3", "Ilość zadań domowych z tego przedmiotu jest odpowiednia.", "SCALE", "ścisłe — wariant pełny", null, categories.get("SCISLE"));
            saveQuestion("S1", "Nauczyciel/ka pokazuje praktyczne zastosowanie omawianego materiału.", "SCALE", "ścisłe — wariant skrócony", null, categories.get("SCISLE"));

            saveQuestion("P1", "Na zajęciach mam możliwość wyrażania własnych opinii i interpretacji.", "SCALE", "język polski", null, categories.get("POLSKI"));
            saveQuestion("P2", "Nauczyciel/ka ocenia prace pisemne rzetelnie i dostarcza komentarz, który pozwala mi się poprawić.", "SCALE", "język polski", null, categories.get("POLSKI"));
            saveQuestion("P3", "Nauczyciel/ka zachęca do swobodnej wypowiedzi ustnej i nie obawiam się oceny za wyrażane poglądy.", "SCALE", "język polski", null, categories.get("POLSKI"));

            saveQuestion("W2", "Nauczyciel/ka uwzględnia różny poziom sprawności fizycznej – nikt nie jest wykluczany.", "SCALE", "WF", null, categories.get("WF"));
            saveQuestion("W3", "Zajęcia WF są urozmaicone – nie robimy ciągle tego samego.", "SCALE", "WF", null, categories.get("WF"));

            saveQuestion("Z1", "Zajęcia przygotowują mnie do pracy w zawodzie / do praktyk zawodowych.", "SCALE", "zawodowe — wspólne", null, categories.get("ZAWODOWE"));
            saveQuestion("Z2", "Na zajęciach wykonujemy praktyczne zadania i ćwiczenia, a nie tylko teorię.", "SCALE", "zawodowe — wspólne", null, categories.get("ZAWODOWE"));
            saveQuestion("ZP3", "Sprzęt komputerowy i oprogramowanie dostępne na zajęciach są wystarczające i aktualne.", "SCALE", "technik programista", null, categories.get("ZAWODOWE"));
            saveQuestion("ZP4", "Nauczyciel/ka omawia realne projekty i aktualny rynek IT.", "SCALE", "technik programista", null, categories.get("ZAWODOWE"));
            saveQuestion("ZL3", "Sprzęt i materiały dostępne na zajęciach są wystarczające i sprawne.", "SCALE", "technik logistyk", null, categories.get("ZAWODOWE"));
            saveQuestion("ZL4", "Nauczyciel/ka omawia realne przykłady z branży logistycznej i aktualny rynek pracy.", "SCALE", "technik logistyk", null, categories.get("ZAWODOWE"));

            saveQuestion("Z5a", "Zajęcia przygotowują mnie do egzaminu zawodowego, który zdaję w tym roku.", "SCALE", "zawodowe klasa 3", "3", categories.get("ZAWODOWE"));
            saveQuestion("Z5b", "Zajęcia przygotowały mnie do egzaminów zawodowych (obu kwalifikacji).", "SCALE", "zawodowe klasa 5", "5", categories.get("ZAWODOWE"));

            saveQuestion("B1", "Jak ogólnie oceniasz swoją szkołę?", "SCALE", "sekcja końcowa", null, categories.get("SZKOLA"));
            saveQuestion("B2", "Czuję się w szkole dobrze i bezpiecznie.", "SCALE", "sekcja końcowa", null, categories.get("SZKOLA"));
            saveQuestion("B3", "Z jakich przedmiotów chciałbyś/chciałabyś mieć większe wsparcie lub dodatkowe zajęcia?", "MULTIPLE_CHOICE", "sekcja końcowa", null, categories.get("SZKOLA"));
            saveQuestion("B+", "Czy jest coś ważnego, czego nie było w ankiecie, a chciałbyś/chciałabyś przekazać dyrekcji?", "OPEN", "sekcja końcowa", null, categories.get("SZKOLA"));

            em.flush();
        } else {
            // Bezpiecznik: Odbudowa referencji mapy, jeśli baza nie była pusta
            String[] keys = {"OGOLNE", "JEZYKI", "SCISLE", "POLSKI", "WF", "ZAWODOWE", "SZKOLA"};
            String[] names = {"Ogólna ocena zajęć", "Języki obce", "Przedmioty ścisłe", "Język polski", "Wychowanie fizyczne", "Przedmioty zawodowe", "Ewaluacja szkoły (Sekcja B)"};
            for (int i = 0; i < keys.length; i++) {
                try {
                    QuestionCategory cat = em.createQuery("SELECT c FROM QuestionCategory c WHERE c.name = :name", QuestionCategory.class)
                            .setParameter("name", names[i])
                            .getSingleResult();
                    categories.put(keys[i], cat);
                } catch (Exception ignored) {}
            }
        }

        if (countTeachers > 0) {
            System.out.println("=== Nauczyciele juz istnieli, pętla ładowania pominięta. ===");
            return;
        }

        // --- TEACHERS ---
        String[][] teacherData = {
                {"Adriana", "Nowicka"}, {"Agnieszka", "Kotwicka"}, {"Aleksandra", "Gulińska"}, {"Anna", "Koniarczyk"},
                {"Bartosz", "Bogacz"}, {"Bartłomiej", "Dettlaff"}, {"Bartłomiej", "Ryl"}, {"Beata", "Krysztofczyk"},
                {"Bożena", "Wdowiak"}, {"Dariusz", "Doliwa"}, {"Hanna", "Kądziołka-Sabanty"}, {"Janusz", "Bieńkowski"},
                {"Jeremi", "Boguszewski"}, {"Jolanta", "Adamkiewicz"}, {"Kacper", "Grzybowski"}, {"Kamila", "Mlonka"},
                {"Krzysztof", "Gębicz"}, {"Krzysztof", "Lisiecki"}, {"Magdalena", "Cząstka"}, {"Magdalena", "Kaczuba"},
                {"Majka", "Cabaj"}, {"Marta", "Kralisz"}, {"Marta", "Ociepa"}, {"Marzena", "Mosakowska-Gorzkiewicz"},
                {"Matylda", "Kołat"}, {"Małgorzata", "Cieślak"}, {"Małgorzata", "Sykacz"}, {"Miłosz", "Kozieł"},
                {"Miłosz", "Turczak"}, {"Monika", "Jurgas-Grudzińska"}, {"Monika", "Sobczak"}, {"Monika", "Solecka"},
                {"Piotr", "Drzewiecki"}, {"Piotr", "Leśnikowski"}, {"Piotr", "Nowak"}, {"Sebastian", "Kręcisz"},
                {"Violetta", "Staniewska-Zapała"}, {"Wiktoria", "Teterycz"}, {"Witold", "Ptak"}, {"Łukasz", "Przygodzki"},
        };
        for (String[] t : teacherData) {
            Teacher teacher = new Teacher();
            teacher.setFirstName(t[0]);
            teacher.setLastName(t[1]);
            em.persist(teacher);
        }

        // --- CLASSES ---
        String[] classNames = {
                "1TL TAU", "1TP TAU", "1TP-E TAU", "1a ALO", "2TL TAU", "2TP TAU", "2a ALO", "3TL TAU",
                "3TP GR 1 TAU", "3TP GR 2 TAU", "3TP-E TAU", "3a ALO", "4TL TAU", "4TP TAU", "4TP- E TAU",
                "4a ALO", "5TL TAU", "5TP TAU", "5TP-E TAU",
        };
        for (String name : classNames) {
            Classes c = new Classes();
            c.setName(name);
            em.persist(c);
        }

        // --- SUBJECTS (NOWA STRUKTURA TRÓJKOLUMNOWA) ---
        // Format: { Nazwa przedmiotu, Wyświetlana rola modułu we froncie, Klucz powiązanej kategorii pytań }
        String[][] subjectData = {
                {"język polski", "Język Polski", "POLSKI"},
                {"Zajęcia dodatkowe z języka polskiego", "Dodatkowy Polonistyczny", "POLSKI"},
                {"Zajecia z j.polskiego dla obcokrajowców", "Dodatkowy Polonistyczny", "POLSKI"},
                {"Język polski dla obcokrajowców", "Dodatkowy Polonistyczny", "POLSKI"},

                {"język angielski", "Język Obcy", "JEZYKI"},
                {"język niemiecki", "Język Obcy", "JEZYKI"},
                {"język rosyjski", "Język Obcy", "JEZYKI"},
                {"Język angielski w logistyce", "Zawodowy Językowy", "JEZYKI"},
                {"Język angielski w transporcie", "Zawodowy Językowy", "JEZYKI"},
                {"język angielski zawodowy", "Zawodowy Językowy", "JEZYKI"},
                {"Angielski dla Maturzystów", "Zajęcia Dodatkowe", "JEZYKI"},
                {"Niemiecki dla Maturzystów", "Zajęcia Dodatkowe", "JEZYKI"},

                {"matematyka", "Przedmiot Ścisły", "SCISLE"},
                {"biologia", "Przedmiot Ścisły", "SCISLE"},
                {"chemia", "Przedmiot Ścisły", "SCISLE"},
                {"fizyka", "Przedmiot Ścisły", "SCISLE"},
                {"informatyka", "Przedmiot Ścisły", "SCISLE"},
                {"Matematyka dla Maturzystów", "Zajęcia Dodatkowe", "SCISLE"},

                {"wychowanie fizyczne", "Wychowanie Fizyczne", "WF"},
                {"SKS", "Zajęcia Sportowe", "WF"},

                {"podstawy logistyki", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"gospodarka magazynowa", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"obsługiwanie klientów i kontrahentów", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"organizowanie pracy magazynów", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"organizowanie procesów transportowych", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"transport", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"Dokumentowanie procesów transportowych", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"usługi logistyczno-spedycyjne", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"procesy transportowe", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"procesy magazynowo-spedycyjne", "Zawodowy (Logistyka)", "ZAWODOWE"},
                {"Menadżer transportu", "Zawodowy (Logistyka)", "ZAWODOWE"},

                {"podstawy informatyki", "Zawodowy (IT)", "ZAWODOWE"},
                {"Bazy danych", "Zawodowy (IT)", "ZAWODOWE"},
                {"projektowanie oprogramowania", "Zawodowy (IT)", "ZAWODOWE"},
                {"witryny internetowe", "Zawodowy (IT)", "ZAWODOWE"},
                {"pracownia baz danych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia projektowania oprogramowania", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia witryn internetowych", "Zawodowy (IT)", "ZAWODOWE"},
                {"podstawy programowania", "Zawodowy (IT)", "ZAWODOWE"},
                {"Projektowanie i administrowanie bazami danych", "Zawodowy (IT)", "ZAWODOWE"},
                {"programowanie aplikacji internetowych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia aplikacji internetowych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia podstaw programowania", "Zawodowy (IT)", "ZAWODOWE"},
                {"programowanie aplikacji mobilnych", "Zawodowy (IT)", "ZAWODOWE"},
                {"programowanie obiektowe", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia programowania aplikacji mobilnych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia programowania obiektowego", "Zawodowy (IT)", "ZAWODOWE"},
                {"Zaawansowane aplikacje webowe", "Zawodowy (IT)", "ZAWODOWE"},
                {"testowanie i dokumentowanie aplikacji", "Zawodowy (IT)", "ZAWODOWE"},
                {"programowanie zaawansowane aplikacji webowych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia testowania i dokumentowania aplikacji", "Zawodowy (IT)", "ZAWODOWE"},
                {"programowanie aplikacji desktopowych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia programowania aplikacji desktopowych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Pracownia programowania zaawansowanych aplikacji webowych", "Zawodowy (IT)", "ZAWODOWE"},
                {"Zajęcia programistyczne wyrównawcze", "Dodatkowy (IT)", "ZAWODOWE"},

                {"podstawy kosmetologii", "Zawodowy (Kosmetologia)", "ZAWODOWE"},

                {"historia", "Podstawowy", "OGOLNE"},
                {"biznes i zarządzanie", "Podstawowy", "OGOLNE"},
                {"edukacja dla bezpieczeństwa", "Podstawowy", "OGOLNE"},
                {"zajęcia z wychowawcą", "Dodatkowy", "OGOLNE"},
                {"historia i teraźniejszość", "Podstawowy", "OGOLNE"},
                {"wiedza o społeczeństwie", "Podstawowy", "OGOLNE"},
                {"działalność gospodarcza", "Podstawowy", "OGOLNE"},
                {"Polonez", "Dodatkowy", "OGOLNE"},
                {"plastyka", "Podstawowy", "OGOLNE"},
                {"zajęcia z psychologiem", "Dodatkowy", "OGOLNE"},
                {"Doradztwo zawodowe", "Podstawowy", "OGOLNE"},
                {"e-Sport", "Dodatkowy", "OGOLNE"},
                {"edukacja dla zdrowia", "Dodatkowy", "OGOLNE"},
                {"AVT", "Dodatkowy", "OGOLNE"},
                {"Geografia dla Maturzystów", "Zajęcia Dodatkowe", "OGOLNE"},
                {"geografia", "Podstawowy", "OGOLNE"},
                {"korekcyjno-wyrównawcze", "Dodatkowy", "OGOLNE"},
                {"Bezpieczeństwo i higiena pracy", "Podstawowy", "OGOLNE"}
        };

        for (String[] s : subjectData) {
            Subject subject = new Subject();
            subject.setName(s[0]);
            subject.setModuleType(s[1]);

            if (categories.containsKey(s[2])) {
                subject.setQuestionCategory(categories.get(s[2]));
            }
            em.persist(subject);
        }
        em.flush();

        // --- TEACHER ASSIGNMENTS ---
        String[][] assignmentData = {
                // ==================== Klasa: 1TL TAU ====================
                {"Wiktoria Teterycz", "1TL TAU", "język polski"},
                {"Małgorzata Sykacz", "1TL TAU", "język angielski"},
                {"Piotr Leśnikowski", "1TL TAU", "historia"},
                {"Matylda Kołat", "1TL TAU", "biznes i zarządzanie"},
                {"Łukasz Przygodzki", "1TL TAU", "biologia"},
                {"Monika Jurgas-Grudzińska", "1TL TAU", "chemia"},
                {"Kacper Grzybowski", "1TL TAU", "fizyka"},
                {"Kacper Grzybowski", "1TL TAU", "matematyka"},
                {"Janusz Bieńkowski", "1TL TAU", "wychowanie fizyczne"},
                {"Jeremi Boguszewski", "1TL TAU", "informatyka"},
                {"Matylda Kołat", "1TL TAU", "edukacja dla bezpieczeństwa"},
                {"Bartosz Bogacz", "1TL TAU", "podstawy logistyki"},
                {"Sebastian Kręcisz", "1TL TAU", "gospodarka magazynowa"},
                {"Sebastian Kręcisz", "1TL TAU", "obsługiwanie klientów i kontrahentów"},
                {"Bartosz Bogacz", "1TL TAU", "organizowanie pracy magazynów"},
                {"Bartosz Bogacz", "1TL TAU", "organizowanie procesów transportowych"},
                {"Bożena Wdowiak", "1TL TAU", "język rosyjski"},
                {"Aleksandra Gulińska", "1TL TAU", "język niemiecki"},
                {"Witold Ptak", "1TL TAU", "geografia"},

                // ==================== Klasa: 1TP TAU ====================
                {"Małgorzata Cieślak", "1TP TAU", "język niemiecki"},
                {"Wiktoria Teterycz", "1TP TAU", "język polski"},
                {"Majka Cabaj", "1TP TAU", "język angielski"},
                {"Bożena Wdowiak", "1TP TAU", "język rosyjski"},
                {"Piotr Leśnikowski", "1TP TAU", "historia"},
                {"Matylda Kołat", "1TP TAU", "biznes i zarządzanie"},
                {"Witold Ptak", "1TP TAU", "geografia"},
                {"Łukasz Przygodzki", "1TP TAU", "biologia"},
                {"Monika Jurgas-Grudzińska", "1TP TAU", "chemia"},
                {"Kacper Grzybowski", "1TP TAU", "fizyka"},
                {"Kacper Grzybowski", "1TP TAU", "matematyka"},
                {"Jeremi Boguszewski", "1TP TAU", "informatyka"},
                {"Janusz Bieńkowski", "1TP TAU", "wychowanie fizyczne"},
                {"Matylda Kołat", "1TP TAU", "edukacja dla bezpieczeństwa"},
                {"Jeremi Boguszewski", "1TP TAU", "Bezpieczeństwo i higiena pracy"},
                {"Majka Cabaj", "1TP TAU", "język angielski zawodowy"},
                {"Bartłomiej Dettlaff", "1TP TAU", "podstawy informatyki"},
                {"Miłosz Turczak", "1TP TAU", "Bazy danych"},
                {"Krzysztof Gębicz", "1TP TAU", "projektowanie oprogramowania"},
                {"Miłosz Turczak", "1TP TAU", "witryny internetowe"},
                {"Miłosz Turczak", "1TP TAU", "pracownia baz danych"},
                {"Krzysztof Gębicz", "1TP TAU", "Pracownia projektowania oprogramowania"},
                {"Miłosz Turczak", "1TP TAU", "Pracownia witryn internetowych"},

                // ==================== Klasa: 1TP-E TAU ====================
                {"Małgorzata Cieślak", "1TP-E TAU", "język niemiecki"},
                {"Bożena Wdowiak", "1TP-E TAU", "język rosyjski"},
                {"Wiktoria Teterycz", "1TP-E TAU", "język polski"},
                {"Małgorzata Sykacz", "1TP-E TAU", "język angielski"},
                {"Piotr Leśnikowski", "1TP-E TAU", "historia"},
                {"Matylda Kołat", "1TP-E TAU", "biznes i zarządzanie"},
                {"Witold Ptak", "1TP-E TAU", "geografia"},
                {"Łukasz Przygodzki", "1TP-E TAU", "biologia"},
                {"Monika Jurgas-Grudzińska", "1TP-E TAU", "chemia"},
                {"Kacper Grzybowski", "1TP-E TAU", "fizyka"},
                {"Kacper Grzybowski", "1TP-E TAU", "matematyka"},
                {"Jeremi Boguszewski", "1TP-E TAU", "informatyka"},
                {"Marta Ociepa", "1TP-E TAU", "wychowanie fizyczne"},
                {"Matylda Kołat", "1TP-E TAU", "edukacja dla bezpieczeństwa"},
                {"Jeremi Boguszewski", "1TP-E TAU", "Bezpieczeństwo i higiena pracy"},
                {"Małgorzata Sykacz", "1TP-E TAU", "język angielski zawodowy"},
                {"Bartłomiej Dettlaff", "1TP-E TAU", "podstawy informatyki"},
                {"Miłosz Turczak", "1TP-E TAU", "Bazy danych"},
                {"Krzysztof Gębicz", "1TP-E TAU", "projektowanie oprogramowania"},
                {"Miłosz Turczak", "1TP-E TAU", "witryny internetowe"},
                {"Miłosz Turczak", "1TP-E TAU", "pracownia baz danych"},
                {"Krzysztof Gębicz", "1TP-E TAU", "Pracownia projektowania oprogramowania"},
                {"Miłosz Turczak", "1TP-E TAU", "Pracownia witryn internetowych"}
        };

        for (String[] assign : assignmentData) {
            try {
                String[] nameParts = assign[0].split(" ", 2);
                Teacher teacher = em.createQuery("SELECT t FROM Teacher t WHERE t.firstName = :f AND t.lastName = :l", Teacher.class)
                        .setParameter("f", nameParts[0])
                        .setParameter("l", nameParts[1])
                        .getSingleResult();

                Classes cl = em.createQuery("SELECT c FROM Classes c WHERE c.name = :name", Classes.class)
                        .setParameter("name", assign[1])
                        .getSingleResult();

                Subject sub = em.createQuery("SELECT s FROM Subject s WHERE s.name = :name", Subject.class)
                        .setParameter("name", assign[2])
                        .getSingleResult();

                // FIX: Użycie dopasowanej klasy i poprawnego settera
                TeacherAssignment assignment = new TeacherAssignment();
                assignment.setTeacher(teacher);
                assignment.setClazz(cl);
                assignment.setSubject(sub);
                em.persist(assignment);
            } catch (Exception e) {
                System.out.println("Pominięto błędne przypisanie: " + assign[0] + " -> " + assign[2]);
            }
        }
        em.flush();
        System.out.println("=== Dane załadowane pomyślnie. ===");
    }

    private void saveQuestion(String id, String content, String type, String scope, String year, QuestionCategory cat) {
        Question q = new Question();
        q.setId(id);
        q.setContent(content);
        q.setType(type);
        q.setCategory(cat);
        em.persist(q);
    }
}