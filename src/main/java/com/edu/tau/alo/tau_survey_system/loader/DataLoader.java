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
                "3TP GR 1 TAU", "3TP GR 2 TAU", "3TP-E TAU", "3a ALO", "4TL TAU", "4TP TAU", "4TP-E TAU",
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
                {"Wiktoria Teterycz", "1TL TAU", "język polski"}, // [cite: 78]
                {"Małgorzata Sykacz", "1TL TAU", "język angielski"}, // [cite: 78]
                {"Piotr Leśnikowski", "1TL TAU", "historia"}, // [cite: 78]
                {"Matylda Kołat", "1TL TAU", "biznes i zarządzanie"}, // [cite: 78]
                {"Łukasz Przygodzki", "1TL TAU", "biologia"}, // [cite: 78]
                {"Monika Jurgas-Grudzińska", "1TL TAU", "chemia"}, // [cite: 78]
                {"Kacper Grzybowski", "1TL TAU", "fizyka"}, // [cite: 78]
                {"Kacper Grzybowski", "1TL TAU", "matematyka"}, // [cite: 78]
                {"Janusz Bieńkowski", "1TL TAU", "wychowanie fizyczne"}, // [cite: 78]
                {"Jeremi Boguszewski", "1TL TAU", "informatyka"}, // [cite: 78]
                {"Matylda Kołat", "1TL TAU", "edukacja dla bezpieczeństwa"}, // [cite: 78]
                {"Bartosz Bogacz", "1TL TAU", "podstawy logistyki"}, // [cite: 78]
                {"Sebastian Kręcisz", "1TL TAU", "gospodarka magazynowa"}, // [cite: 78]
                {"Sebastian Kręcisz", "1TL TAU", "obsługiwanie klientów i kontrahentów"}, // [cite: 78]
                {"Bartosz Bogacz", "1TL TAU", "organizowanie pracy magazynów"}, // [cite: 78]
                {"Bartosz Bogacz", "1TL TAU", "organizowanie procesów transportowych"}, // [cite: 78]
                {"Bożena Wdowiak", "1TL TAU", "język rosyjski"}, // [cite: 78]
                {"Aleksandra Gulińska", "1TL TAU", "język niemiecki"}, // [cite: 78]
                {"Witold Ptak", "1TL TAU", "geografia"}, // [cite: 78]

                // ==================== Klasa: 1TP TAU ====================
                {"Małgorzata Cieślak", "1TP TAU", "język niemiecki"}, // [cite: 80]
                {"Wiktoria Teterycz", "1TP TAU", "język polski"}, // [cite: 80]
                {"Majka Cabaj", "1TP TAU", "język angielski"}, // [cite: 80]
                {"Bożena Wdowiak", "1TP TAU", "język rosyjski"}, // [cite: 80]
                {"Piotr Leśnikowski", "1TP TAU", "historia"}, // [cite: 80]
                {"Matylda Kołat", "1TP TAU", "biznes i zarządzanie"}, // [cite: 80]
                {"Witold Ptak", "1TP TAU", "geografia"}, // [cite: 80]
                {"Łukasz Przygodzki", "1TP TAU", "biologia"}, // [cite: 80]
                {"Monika Jurgas-Grudzińska", "1TP TAU", "chemia"}, // [cite: 80]
                {"Kacper Grzybowski", "1TP TAU", "fizyka"}, // [cite: 80]
                {"Kacper Grzybowski", "1TP TAU", "matematyka"}, // [cite: 80]
                {"Jeremi Boguszewski", "1TP TAU", "informatyka"}, // [cite: 80]
                {"Janusz Bieńkowski", "1TP TAU", "wychowanie fizyczne"}, // [cite: 80]
                {"Matylda Kołat", "1TP TAU", "edukacja dla bezpieczeństwa"}, // [cite: 80]
                {"Jeremi Boguszewski", "1TP TAU", "Bezpieczeństwo i higiena pracy"}, // [cite: 80]
                {"Majka Cabaj", "1TP TAU", "język angielski zawodowy"}, // [cite: 80]
                {"Bartłomiej Dettlaff", "1TP TAU", "podstawy informatyki"}, // [cite: 80]
                {"Miłosz Turczak", "1TP TAU", "Bazy danych"}, // [cite: 80]
                {"Krzysztof Gębicz", "1TP TAU", "projektowanie oprogramowania"}, // [cite: 80]
                {"Miłosz Turczak", "1TP TAU", "witryny internetowe"}, // [cite: 80]
                {"Miłosz Turczak", "1TP TAU", "pracownia baz danych"}, // [cite: 80]
                {"Krzysztof Gębicz", "1TP TAU", "Pracownia projektowania oprogramowania"}, // [cite: 80]
                {"Miłosz Turczak", "1TP TAU", "Pracownia witryn internetowych"}, // [cite: 80]

                // ==================== Klasa: 1TP-E TAU ====================
                {"Małgorzata Cieślak", "1TP-E TAU", "język niemiecki"}, // [cite: 82]
                {"Bożena Wdowiak", "1TP-E TAU", "język rosyjski"}, // [cite: 82]
                {"Wiktoria Teterycz", "1TP-E TAU", "język polski"}, // [cite: 82]
                {"Małgorzata Sykacz", "1TP-E TAU", "język angielski"}, // [cite: 82]
                {"Piotr Leśnikowski", "1TP-E TAU", "historia"}, // [cite: 82]
                {"Matylda Kołat", "1TP-E TAU", "biznes i zarządzanie"}, // [cite: 82]
                {"Witold Ptak", "1TP-E TAU", "geografia"}, // [cite: 82]
                {"Łukasz Przygodzki", "1TP-E TAU", "biologia"}, // [cite: 82]
                {"Monika Jurgas-Grudzińska", "1TP-E TAU", "chemia"}, // [cite: 82]
                {"Kacper Grzybowski", "1TP-E TAU", "fizyka"}, // [cite: 82]
                {"Kacper Grzybowski", "1TP-E TAU", "matematyka"}, // [cite: 82]
                {"Jeremi Boguszewski", "1TP-E TAU", "informatyka"}, // [cite: 82]
                {"Marta Ociepa", "1TP-E TAU", "wychowanie fizyczne"}, // [cite: 82]
                {"Matylda Kołat", "1TP-E TAU", "edukacja dla bezpieczeństwa"}, // [cite: 82]
                {"Jeremi Boguszewski", "1TP-E TAU", "Bezpieczeństwo i higiena pracy"}, // [cite: 82]
                {"Małgorzata Sykacz", "1TP-E TAU", "język angielski zawodowy"}, // [cite: 82]
                {"Bartłomiej Dettlaff", "1TP-E TAU", "podstawy informatyki"}, // [cite: 82]
                {"Miłosz Turczak", "1TP-E TAU", "Bazy danych"}, // [cite: 82]
                {"Krzysztof Gębicz", "1TP-E TAU", "projektowanie oprogramowania"}, // [cite: 82]
                {"Miłosz Turczak", "1TP-E TAU", "witryny internetowe"}, // [cite: 82]
                {"Miłosz Turczak", "1TP-E TAU", "pracownia baz danych"}, // [cite: 82]
                {"Krzysztof Gębicz", "1TP-E TAU", "Pracownia projektowania oprogramowania"}, // [cite: 82]
                {"Miłosz Turczak", "1TP-E TAU", "Pracownia witryn internetowych"}, // [cite: 82]

                // ==================== Klasa: 1a ALO ====================
                {"Wiktoria Teterycz", "1a ALO", "język polski"}, // [cite: 84]
                {"Małgorzata Sykacz", "1a ALO", "język angielski"}, // [cite: 84]
                {"Anna Koniarczyk", "1a ALO", "język niemiecki"}, // [cite: 84]
                {"Bożena Wdowiak", "1a ALO", "język rosyjski"}, // [cite: 84]
                {"Adriana Nowicka", "1a ALO", "plastyka"}, // [cite: 84]
                {"Piotr Leśnikowski", "1a ALO", "historia"}, // [cite: 84]
                {"Marzena Mosakowska-Gorzkiewicz", "1a ALO", "biologia"}, // [cite: 84]
                {"Łukasz Przygodzki", "1a ALO", "chemia"}, // [cite: 84]
                {"Kacper Grzybowski", "1a ALO", "fizyka"}, // [cite: 84]
                {"Monika Sobczak", "1a ALO", "matematyka"}, // [cite: 84]
                {"Bartosz Bogacz", "1a ALO", "informatyka"}, // [cite: 84]
                {"Marta Ociepa", "1a ALO", "wychowanie fizyczne"}, // [cite: 84]
                {"Matylda Kołat", "1a ALO", "edukacja dla bezpieczeństwa"}, // [cite: 84]
                {"Kamila Mlonka", "1a ALO", "podstawy kosmetologii"}, // [cite: 84]
                {"Piotr Drzewiecki", "1a ALO", "geografia"}, // [cite: 84]
                {"Agnieszka Kotwicka", "1a ALO", "język angielski"}, // [cite: 84]

                // ==================== Klasa: 2TL TAU ====================
                {"Wiktoria Teterycz", "2TL TAU", "język polski"}, // [cite: 86]
                {"Małgorzata Sykacz", "2TL TAU", "język angielski"}, // [cite: 86]
                {"Anna Koniarczyk", "2TL TAU", "język niemiecki"}, // [cite: 86]
                {"Bożena Wdowiak", "2TL TAU", "język rosyjski"}, // [cite: 86]
                {"Piotr Leśnikowski", "2TL TAU", "historia"}, // [cite: 86]
                {"Matylda Kołat", "2TL TAU", "biznes i zarządzanie"}, // [cite: 86]
                {"Witold Ptak", "2TL TAU", "geografia"}, // [cite: 86]
                {"Łukasz Przygodzki", "2TL TAU", "biologia"}, // [cite: 86]
                {"Monika Jurgas-Grudzińska", "2TL TAU", "chemia"}, // [cite: 86]
                {"Kacper Grzybowski", "2TL TAU", "fizyka"}, // [cite: 86]
                {"Monika Sobczak", "2TL TAU", "matematyka"}, // [cite: 86]
                {"Jeremi Boguszewski", "2TL TAU", "informatyka"}, // [cite: 86]
                {"Janusz Bieńkowski", "2TL TAU", "wychowanie fizyczne"}, // [cite: 86]
                {"Bartosz Bogacz", "2TL TAU", "Bezpieczeństwo i higiena pracy"}, // [cite: 86]
                {"Małgorzata Sykacz", "2TL TAU", "Język angielski w logistyce"}, // [cite: 86]
                {"Bartosz Bogacz", "2TL TAU", "podstawy logistyki"}, // [cite: 86]
                {"Sebastian Kręcisz", "2TL TAU", "gospodarka magazynowa"}, // [cite: 86]
                {"Sebastian Kręcisz", "2TL TAU", "transport"}, // [cite: 86]
                {"Bartosz Bogacz", "2TL TAU", "organizowanie pracy magazynów"}, // [cite: 86]
                {"Bartosz Bogacz", "2TL TAU", "organizowanie procesów transportowych"}, // [cite: 86]

                // ==================== Klasa: 2TP TAU ====================
                {"Bożena Wdowiak", "2TP TAU", "język rosyjski"}, // [cite: 88]
                {"Magdalena Kaczuba", "2TP TAU", "język polski"}, // [cite: 88]
                {"Małgorzata Sykacz", "2TP TAU", "język angielski"}, // [cite: 88]
                {"Aleksandra Gulińska", "2TP TAU", "język niemiecki"}, // [cite: 88]
                {"Piotr Leśnikowski", "2TP TAU", "historia"}, // [cite: 88]
                {"Matylda Kołat", "2TP TAU", "biznes i zarządzanie"}, // [cite: 88]
                {"Witold Ptak", "2TP TAU", "geografia"}, // [cite: 88]
                {"Łukasz Przygodzki", "2TP TAU", "biologia"}, // [cite: 88]
                {"Monika Jurgas-Grudzińska", "2TP TAU", "chemia"}, // [cite: 88]
                {"Kacper Grzybowski", "2TP TAU", "fizyka"}, // [cite: 88]
                {"Monika Sobczak", "2TP TAU", "matematyka"}, // [cite: 88]
                {"Jeremi Boguszewski", "2TP TAU", "informatyka"}, // [cite: 88]
                {"Marta Ociepa", "2TP TAU", "wychowanie fizyczne"}, // [cite: 88]
                {"Jeremi Boguszewski", "2TP TAU", "podstawy programowania"}, // [cite: 88]
                {"Miłosz Turczak", "2TP TAU", "Projektowanie i administrowanie bazami danych"}, // [cite: 88]
                {"Krzysztof Gębicz", "2TP TAU", "projektowanie oprogramowania"}, // [cite: 88]
                {"Bartłomiej Dettlaff", "2TP TAU", "programowanie aplikacji internetowych"}, // [cite: 88]
                {"Miłosz Turczak", "2TP TAU", "pracownia baz danych"}, // [cite: 88]
                {"Bartłomiej Dettlaff", "2TP TAU", "Pracownia aplikacji internetowych"}, // [cite: 88]
                {"Jeremi Boguszewski", "2TP TAU", "Pracownia podstaw programowania"}, // [cite: 88]
                {"Krzysztof Gębicz", "2TP TAU", "Pracownia projektowania oprogramowania"}, // [cite: 88]

                // ==================== Klasa: 2a ALO ====================
                {"Magdalena Kaczuba", "2a ALO", "język polski"}, // [cite: 90]
                {"Małgorzata Sykacz", "2a ALO", "język angielski"}, // [cite: 90]
                {"Anna Koniarczyk", "2a ALO", "język niemiecki"}, // [cite: 90]
                {"Bożena Wdowiak", "2a ALO", "język rosyjski"}, // [cite: 90]
                {"Piotr Leśnikowski", "2a ALO", "historia"}, // [cite: 90]
                {"Matylda Kołat", "2a ALO", "biznes i zarządzanie"}, // [cite: 90]
                {"Piotr Drzewiecki", "2a ALO", "geografia"}, // [cite: 90]
                {"Marzena Mosakowska-Gorzkiewicz", "2a ALO", "biologia"}, // [cite: 90]
                {"Łukasz Przygodzki", "2a ALO", "chemia"}, // [cite: 90]
                {"Kacper Grzybowski", "2a ALO", "fizyka"}, // [cite: 90]
                {"Monika Sobczak", "2a ALO", "matematyka"}, // [cite: 90]
                {"Bartosz Bogacz", "2a ALO", "informatyka"}, // [cite: 90]
                {"Marta Ociepa", "2a ALO", "wychowanie fizyczne"}, // [cite: 90]
                {"Kamila Mlonka", "2a ALO", "podstawy kosmetologii"}, // [cite: 90]
                {"Agnieszka Kotwicka", "2a ALO", "język angielski"}, // [cite: 90]

                // ==================== Klasa: 3TL TAU ====================
                {"Violetta Staniewska-Zapała", "3TL TAU", "język polski"}, // [cite: 92]
                {"Majka Cabaj", "3TL TAU", "język angielski"}, // [cite: 92]
                {"Aleksandra Gulińska", "3TL TAU", "język niemiecki"}, // [cite: 92]
                {"Bożena Wdowiak", "3TL TAU", "język rosyjski"}, // [cite: 92]
                {"Piotr Leśnikowski", "3TL TAU", "historia"}, // [cite: 92]
                {"Piotr Drzewiecki", "3TL TAU", "geografia"}, // [cite: 92]
                {"Monika Solecka", "3TL TAU", "biologia"}, // [cite: 92]
                {"Monika Jurgas-Grudzińska", "3TL TAU", "chemia"}, // [cite: 92]
                {"Kacper Grzybowski", "3TL TAU", "fizyka"}, // [cite: 92]
                {"Kacper Grzybowski", "3TL TAU", "matematyka"}, // [cite: 92]
                {"Jeremi Boguszewski", "3TL TAU", "informatyka"}, // [cite: 92]
                {"Marta Ociepa", "3TL TAU", "wychowanie fizyczne"}, // [cite: 92]
                {"Bartosz Bogacz", "3TL TAU", "Bezpieczeństwo i higiena pracy"}, // [cite: 92]
                {"Majka Cabaj", "3TL TAU", "Język angielski w transporcie"}, // [cite: 92]
                {"Bartosz Bogacz", "3TL TAU", "gospodarka magazynowa"}, // [cite: 92]
                {"Sebastian Kręcisz", "3TL TAU", "transport"}, // [cite: 92]
                {"Bartosz Bogacz", "3TL TAU", "organizowanie pracy magazynów"}, // [cite: 92]
                {"Bartosz Bogacz", "3TL TAU", "organizowanie procesów transportowych"}, // [cite: 92]
                {"Bartosz Bogacz", "3TL TAU", "Menadżer transportu"}, // [cite: 92]
                {"Piotr Leśnikowski", "3TL TAU", "historia i teraźniejszość"}, // [cite: 92]

                // ==================== Klasa: 3TP GR 1 TAU ====================
                {"Bożena Wdowiak", "3TP GR 1 TAU", "język rosyjski"}, // [cite: 94]
                {"Magdalena Kaczuba", "3TP GR 1 TAU", "język polski"}, // [cite: 94]
                {"Marta Kralisz", "3TP GR 1 TAU", "język angielski"}, // [cite: 94]
                {"Aleksandra Gulińska", "3TP GR 1 TAU", "język niemiecki"}, // [cite: 94]
                {"Piotr Leśnikowski", "3TP GR 1 TAU", "historia"}, // [cite: 94]
                {"Piotr Leśnikowski", "3TP GR 1 TAU", "historia i teraźniejszość"}, // [cite: 94]
                {"Witold Ptak", "3TP GR 1 TAU", "geografia"}, // [cite: 94]
                {"Monika Solecka", "3TP GR 1 TAU", "biologia"}, // [cite: 94]
                {"Monika Jurgas-Grudzińska", "3TP GR 1 TAU", "chemia"}, // [cite: 94]
                {"Kacper Grzybowski", "3TP GR 1 TAU", "fizyka"}, // [cite: 94]
                {"Jolanta Adamkiewicz", "3TP GR 1 TAU", "matematyka"}, // [cite: 94]
                {"Jeremi Boguszewski", "3TP GR 1 TAU", "informatyka"}, // [cite: 94]
                {"Janusz Bieńkowski", "3TP GR 1 TAU", "wychowanie fizyczne"}, // [cite: 94]
                {"Marta Kralisz", "3TP GR 1 TAU", "język angielski zawodowy"}, // [cite: 94]
                {"Bartłomiej Dettlaff", "3TP GR 1 TAU", "programowanie aplikacji internetowych"}, // [cite: 94]
                {"Miłosz Turczak", "3TP GR 1 TAU", "programowanie aplikacji mobilnych"}, // [cite: 94]
                {"Krzysztof Gębicz", "3TP GR 1 TAU", "programowanie obiektowe"}, // [cite: 94]
                {"Miłosz Turczak", "3TP GR 1 TAU", "Pracownia programowania aplikacji mobilnych"}, // [cite: 94]
                {"Krzysztof Gębicz", "3TP GR 1 TAU", "Pracownia programowania obiektowego"}, // [cite: 94]

                // ==================== Klasa: 3TP GR 2 TAU ====================
                {"Bożena Wdowiak", "3TP GR 2 TAU", "język rosyjski"}, // [cite: 96]
                {"Magdalena Kaczuba", "3TP GR 2 TAU", "język polski"}, // [cite: 96]
                {"Marta Kralisz", "3TP GR 2 TAU", "język angielski"}, // [cite: 96]
                {"Aleksandra Gulińska", "3TP GR 2 TAU", "język niemiecki"}, // [cite: 96]
                {"Piotr Leśnikowski", "3TP GR 2 TAU", "historia"}, // [cite: 96]
                {"Piotr Leśnikowski", "3TP GR 2 TAU", "historia i teraźniejszość"}, // [cite: 96]
                {"Witold Ptak", "3TP GR 2 TAU", "geografia"}, // [cite: 96]
                {"Monika Solecka", "3TP GR 2 TAU", "biologia"}, // [cite: 96]
                {"Monika Jurgas-Grudzińska", "3TP GR 2 TAU", "chemia"}, // [cite: 96]
                {"Kacper Grzybowski", "3TP GR 2 TAU", "fizyka"}, // [cite: 96]
                {"Kacper Grzybowski", "3TP GR 2 TAU", "matematyka"}, // [cite: 96]
                {"Jeremi Boguszewski", "3TP GR 2 TAU", "informatyka"}, // [cite: 96]
                {"Janusz Bieńkowski", "3TP GR 2 TAU", "wychowanie fizyczne"}, // [cite: 96]
                {"Marta Kralisz", "3TP GR 2 TAU", "język angielski zawodowy"}, // [cite: 96]
                {"Bartłomiej Dettlaff", "3TP GR 2 TAU", "programowanie aplikacji internetowych"}, // [cite: 96]
                {"Miłosz Turczak", "3TP GR 2 TAU", "programowanie aplikacji mobilnych"}, // [cite: 96]
                {"Krzysztof Gębicz", "3TP GR 2 TAU", "programowanie obiektowe"}, // [cite: 96]
                {"Miłosz Turczak", "3TP GR 2 TAU", "Pracownia programowania aplikacji mobilnych"}, // [cite: 96]
                {"Krzysztof Gębicz", "3TP GR 2 TAU", "Pracownia programowania obiektowego"}, // [cite: 96]

                // ==================== Klasa: 3TP-E TAU ====================
                {"Bożena Wdowiak", "3TP-E TAU", "język rosyjski"}, // [cite: 98]
                {"Magdalena Kaczuba", "3TP-E TAU", "język polski"}, // [cite: 98]
                {"Majka Cabaj", "3TP-E TAU", "język angielski"}, // [cite: 98]
                {"Aleksandra Gulińska", "3TP-E TAU", "język niemiecki"}, // [cite: 98]
                {"Piotr Leśnikowski", "3TP-E TAU", "historia"}, // [cite: 98]
                {"Piotr Leśnikowski", "3TP-E TAU", "historia i teraźniejszość"}, // [cite: 98]
                {"Witold Ptak", "3TP-E TAU", "geografia"}, // [cite: 98]
                {"Łukasz Przygodzki", "3TP-E TAU", "biologia"}, // [cite: 98]
                {"Monika Jurgas-Grudzińska", "3TP-E TAU", "chemia"}, // [cite: 98]
                {"Kacper Grzybowski", "3TP-E TAU", "fizyka"}, // [cite: 98]
                {"Jolanta Adamkiewicz", "3TP-E TAU", "matematyka"}, // [cite: 98]
                {"Jeremi Boguszewski", "3TP-E TAU", "informatyka"}, // [cite: 98]
                {"Janusz Bieńkowski", "3TP-E TAU", "wychowanie fizyczne"}, // [cite: 98]
                {"Majka Cabaj", "3TP-E TAU", "język angielski zawodowy"}, // [cite: 98]
                {"Bartłomiej Dettlaff", "3TP-E TAU", "programowanie aplikacji internetowych"}, // [cite: 98]
                {"Miłosz Turczak", "3TP-E TAU", "programowanie aplikacji mobilnych"}, // [cite: 98]
                {"Krzysztof Gębicz", "3TP-E TAU", "programowanie obiektowe"}, // [cite: 98]
                {"Bartłomiej Dettlaff", "3TP-E TAU", "Pracownia aplikacji internetowych"}, // [cite: 98]
                {"Miłosz Turczak", "3TP-E TAU", "Pracownia programowania aplikacji mobilnych"}, // [cite: 98]
                {"Krzysztof Gębicz", "3TP-E TAU", "Pracownia programowania obiektowego"}, // [cite: 98]

                // ==================== Klasa: 3a ALO ====================
                {"Magdalena Kaczuba", "3a ALO", "język polski"}, // [cite: 100]
                {"Agnieszka Kotwicka", "3a ALO", "język angielski"}, // [cite: 100]
                {"Anna Koniarczyk", "3a ALO", "język niemiecki"}, // [cite: 100]
                {"Bożena Wdowiak", "3a ALO", "język rosyjski"}, // [cite: 100]
                {"Piotr Leśnikowski", "3a ALO", "historia"}, // [cite: 100]
                {"Piotr Drzewiecki", "3a ALO", "geografia"}, // [cite: 100]
                {"Monika Solecka", "3a ALO", "biologia"}, // [cite: 100]
                {"Łukasz Przygodzki", "3a ALO", "chemia"}, // [cite: 100]
                {"Monika Sobczak", "3a ALO", "matematyka"}, // [cite: 100]
                {"Bartosz Bogacz", "3a ALO", "informatyka"}, // [cite: 100]
                {"Marta Ociepa", "3a ALO", "wychowanie fizyczne"}, // [cite: 100]
                {"Kacper Grzybowski", "3a ALO", "fizyka"}, // [cite: 100]
                {"Matylda Kołat", "3a ALO", "Doradztwo zawodowe"}, // [cite: 100]

                // ==================== Klasa: 4TL TAU ====================
                {"Violetta Staniewska-Zapała", "4TL TAU", "język polski"}, // [cite: 102]
                {"Marta Kralisz", "4TL TAU", "język angielski"}, // [cite: 102]
                {"Anna Koniarczyk", "4TL TAU", "język niemiecki"}, // [cite: 102]
                {"Bożena Wdowiak", "4TL TAU", "język rosyjski"}, // [cite: 102]
                {"Beata Krysztofczyk", "4TL TAU", "historia"}, // [cite: 102]
                {"Piotr Drzewiecki", "4TL TAU", "geografia"}, // [cite: 102]
                {"Monika Solecka", "4TL TAU", "biologia"}, // [cite: 102]
                {"Monika Jurgas-Grudzińska", "4TL TAU", "chemia"}, // [cite: 102]
                {"Kacper Grzybowski", "4TL TAU", "fizyka"}, // [cite: 102]
                {"Kacper Grzybowski", "4TL TAU", "matematyka"}, // [cite: 102]
                {"Janusz Bieńkowski", "4TL TAU", "wychowanie fizyczne"}, // [cite: 102]
                {"Marta Kralisz", "4TL TAU", "Język angielski w transporcie"}, // [cite: 102]
                {"Sebastian Kręcisz", "4TL TAU", "transport"}, // [cite: 102]
                {"Sebastian Kręcisz", "4TL TAU", "organizowanie procesów transportowych"}, // [cite: 102]
                {"Sebastian Kręcisz", "4TL TAU", "Dokumentowanie procesów transportowych"}, // [cite: 102]
                {"Bartosz Bogacz", "4TL TAU", "Menadżer transportu"}, // [cite: 102]

                // ==================== Klasa: 4TP TAU ====================
                {"Bożena Wdowiak", "4TP TAU", "język rosyjski"}, // [cite: 104]
                {"Magdalena Kaczuba", "4TP TAU", "język polski"}, // [cite: 104]
                {"Majka Cabaj", "4TP TAU", "język angielski"}, // [cite: 104]
                {"Marta Kralisz", "4TP TAU", "język angielski"}, // [cite: 104]
                {"Anna Koniarczyk", "4TP TAU", "język niemiecki"}, // [cite: 104]
                {"Beata Krysztofczyk", "4TP TAU", "historia"}, // [cite: 104]
                {"Witold Ptak", "4TP TAU", "geografia"}, // [cite: 104]
                {"Monika Solecka", "4TP TAU", "biologia"}, // [cite: 104]
                {"Monika Jurgas-Grudzińska", "4TP TAU", "chemia"}, // [cite: 104]
                {"Kacper Grzybowski", "4TP TAU", "fizyka"}, // [cite: 104]
                {"Krzysztof Lisiecki", "4TP TAU", "matematyka"}, // [cite: 104]
                {"Janusz Bieńkowski", "4TP TAU", "wychowanie fizyczne"}, // [cite: 104]
                {"Majka Cabaj", "4TP TAU", "język angielski zawodowy"}, // [cite: 104]
                {"Marta Kralisz", "4TP TAU", "język angielski zawodowy"}, // [cite: 104]
                {"Małgorzata Sykacz", "4TP TAU", "język angielski zawodowy"}, // [cite: 104]
                {"Miłosz Turczak", "4TP TAU", "programowanie aplikacji mobilnych"}, // [cite: 104]
                {"Krzysztof Gębicz", "4TP TAU", "programowanie obiektowe"}, // [cite: 104]
                {"Piotr Nowak", "4TP TAU", "Zaawansowane aplikacje webowe"}, // [cite: 104]
                {"Dariusz Doliwa", "4TP TAU", "testowanie i dokumentowanie aplikacji"}, // [cite: 104]
                {"Miłosz Turczak", "4TP TAU", "Pracownia programowania aplikacji mobilnych"}, // [cite: 104]
                {"Piotr Nowak", "4TP TAU", "programowanie zaawansowane aplikacji webowych"}, // [cite: 104]
                {"Krzysztof Gębicz", "4TP TAU", "Pracownia programowania obiektowego"}, // [cite: 104]
                {"Dariusz Doliwa", "4TP TAU", "Pracownia testowania i dokumentowania aplikacji"}, // [cite: 104]
                {"Małgorzata Sykacz", "4TP TAU", "język angielski"}, // [cite: 104]

                // ==================== Klasa: 4TP-E TAU ====================
                {"Bożena Wdowiak", "4TP-E TAU", "język rosyjski"}, // [cite: 106]
                {"Majka Cabaj", "4TP-E TAU", "język angielski"}, // [cite: 106]
                {"Marta Kralisz", "4TP-E TAU", "język angielski"}, // [cite: 106]
                {"Majka Cabaj", "4TP-E TAU", "język angielski zawodowy"}, // [cite: 106]
                {"Marta Kralisz", "4TP-E TAU", "język angielski zawodowy"}, // [cite: 106]
                {"Małgorzata Sykacz", "4TP-E TAU", "język angielski zawodowy"}, // [cite: 106]
                {"Magdalena Kaczuba", "4TP-E TAU", "język polski"}, // [cite: 106]
                {"Małgorzata Sykacz", "4TP-E TAU", "język angielski"}, // [cite: 106]
                {"Anna Koniarczyk", "4TP-E TAU", "język niemiecki"}, // [cite: 106]
                {"Beata Krysztofczyk", "4TP-E TAU", "historia"}, // [cite: 106]
                {"Witold Ptak", "4TP-E TAU", "geografia"}, // [cite: 106]
                {"Monika Solecka", "4TP-E TAU", "biologia"}, // [cite: 106]
                {"Monika Jurgas-Grudzińska", "4TP-E TAU", "chemia"}, // [cite: 106]
                {"Kacper Grzybowski", "4TP-E TAU", "fizyka"}, // [cite: 106]
                {"Kacper Grzybowski", "4TP-E TAU", "matematyka"}, // [cite: 106]
                {"Janusz Bieńkowski", "4TP-E TAU", "wychowanie fizyczne"}, // [cite: 106]
                {"Miłosz Turczak", "4TP-E TAU", "programowanie aplikacji mobilnych"}, // [cite: 106]
                {"Krzysztof Gębicz", "4TP-E TAU", "programowanie obiektowe"}, // [cite: 106]
                {"Piotr Nowak", "4TP-E TAU", "Zaawansowane aplikacje webowe"}, // [cite: 106]
                {"Dariusz Doliwa", "4TP-E TAU", "testowanie i dokumentowanie aplikacji"}, // [cite: 106]
                {"Miłosz Turczak", "4TP-E TAU", "Pracownia programowania aplikacji mobilnych"}, // [cite: 106]
                {"Piotr Nowak", "4TP-E TAU", "programowanie zaawansowane aplikacji webowych"}, // [cite: 106]
                {"Krzysztof Gębicz", "4TP-E TAU", "Pracownia programowania obiektowego"}, // [cite: 106]
                {"Dariusz Doliwa", "4TP-E TAU", "Pracownia testowania i dokumentowania aplikacji"}, // [cite: 106]

                // ==================== Klasa: 4a ALO ====================
                {"Wiktoria Teterycz", "4a ALO", "język polski"}, // [cite: 108]
                {"Agnieszka Kotwicka", "4a ALO", "język angielski"}, // [cite: 108]
                {"Anna Koniarczyk", "4a ALO", "język niemiecki"}, // [cite: 108]
                {"Bożena Wdowiak", "4a ALO", "język rosyjski"}, // [cite: 108]
                {"Piotr Leśnikowski", "4a ALO", "historia"}, // [cite: 108]
                {"Marzena Mosakowska-Gorzkiewicz", "4a ALO", "biologia"}, // [cite: 108]
                {"Monika Sobczak", "4a ALO", "matematyka"}, // [cite: 108]
                {"Marta Ociepa", "4a ALO", "wychowanie fizyczne"}, // [cite: 108]

                // ==================== Klasa: 5TL TAU ====================
                {"Violetta Staniewska-Zapała", "5TL TAU", "język polski"}, // [cite: 110]
                {"Majka Cabaj", "5TL TAU", "język angielski"}, // [cite: 110]
                {"Marta Kralisz", "5TL TAU", "język angielski"}, // [cite: 110]
                {"Anna Koniarczyk", "5TL TAU", "język niemiecki"}, // [cite: 110]
                {"Bożena Wdowiak", "5TL TAU", "język rosyjski"}, // [cite: 110]
                {"Piotr Leśnikowski", "5TL TAU", "historia"}, // [cite: 110]
                {"Piotr Leśnikowski", "5TL TAU", "wiedza o społeczeństwie"}, // [cite: 110]
                {"Piotr Drzewiecki", "5TL TAU", "geografia"}, // [cite: 110]
                {"Monika Sobczak", "5TL TAU", "matematyka"}, // [cite: 110]
                {"Marta Ociepa", "5TL TAU", "wychowanie fizyczne"}, // [cite: 110]
                {"Janusz Bieńkowski", "5TL TAU", "wychowanie fizyczne"}, // [cite: 110]
                {"Majka Cabaj", "5TL TAU", "język angielski zawodowy"}, // [cite: 110]
                {"Marta Kralisz", "5TL TAU", "język angielski zawodowy"}, // [cite: 110]
                {"Sebastian Kręcisz", "5TL TAU", "działalność gospodarcza"}, // [cite: 110]
                {"Sebastian Kręcisz", "5TL TAU", "usługi logistyczno-spedycyjne"}, // [cite: 110]
                {"Sebastian Kręcisz", "5TL TAU", "procesy transportowe"}, // [cite: 110]
                {"Sebastian Kręcisz", "5TL TAU", "procesy magazynowo-spedycyjne"}, // [cite: 110]

                // ==================== Klasa: 5TP TAU ====================
                {"Bożena Wdowiak", "5TP TAU", "język rosyjski"}, // [cite: 112]
                {"Violetta Staniewska-Zapała", "5TP TAU", "język polski"}, // [cite: 112]
                {"Marta Kralisz", "5TP TAU", "język angielski"}, // [cite: 112]
                {"Anna Koniarczyk", "5TP TAU", "język niemiecki"}, // [cite: 112]
                {"Piotr Leśnikowski", "5TP TAU", "historia"}, // [cite: 112]
                {"Piotr Leśnikowski", "5TP TAU", "wiedza o społeczeństwie"}, // [cite: 112]
                {"Krzysztof Lisiecki", "5TP TAU", "matematyka"}, // [cite: 112]
                {"Marta Ociepa", "5TP TAU", "wychowanie fizyczne"}, // [cite: 112]
                {"Dariusz Doliwa", "5TP TAU", "programowanie aplikacji desktopowych"}, // [cite: 112]
                {"Piotr Nowak", "5TP TAU", "programowanie zaawansowane aplikacji webowych"}, // [cite: 112]
                {"Dariusz Doliwa", "5TP TAU", "Pracownia programowania aplikacji desktopowych"}, // [cite: 112]
                {"Piotr Nowak", "5TP TAU", "Pracownia programowania zaawansowanych aplikacji webowych"}, // [cite: 112]

                // ==================== Klasa: 5TP-E TAU ====================
                {"Bożena Wdowiak", "5TP-E TAU", "język rosyjski"}, // [cite: 114]
                {"Anna Koniarczyk", "5TP-E TAU", "język niemiecki"}, // [cite: 114]
                {"Magdalena Cząstka", "5TP-E TAU", "język polski"}, // [cite: 114]
                {"Majka Cabaj", "5TP-E TAU", "język angielski"}, // [cite: 114]
                {"Piotr Leśnikowski", "5TP-E TAU", "historia"}, // [cite: 114]
                {"Piotr Leśnikowski", "5TP-E TAU", "wiedza o społeczeństwie"}, // [cite: 114]
                {"Krzysztof Lisiecki", "5TP-E TAU", "matematyka"}, // [cite: 114]
                {"Marta Ociepa", "5TP-E TAU", "wychowanie fizyczne"}, // [cite: 114]
                {"Dariusz Doliwa", "5TP-E TAU", "programowanie aplikacji desktopowych"}, // [cite: 114]
                {"Piotr Nowak", "5TP-E TAU", "programowanie zaawansowane aplikacji webowych"}, // [cite: 114]
                {"Dariusz Doliwa", "5TP-E TAU", "Pracownia programowania aplikacji desktopowych"}, // [cite: 114]
                {"Piotr Nowak", "5TP-E TAU", "Pracownia programowania zaawansowanych aplikacji webowych"} // [cite: 114]
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