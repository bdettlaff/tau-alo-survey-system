package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.dto.*;
import com.edu.tau.alo.tau_survey_system.model.*;
import com.edu.tau.alo.tau_survey_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyResultRepository surveyResultRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final SurveyRepository surveyRepository;
    private final ClassRepository classRepository;
    private final QuestionRepository questionRepository;
    private final TeacherAssignmentRepository assignmentRepository;

    public SurveyService(SurveyResultRepository surveyResultRepository, TeacherRepository teacherRepository,
                         SubjectRepository subjectRepository, SurveyRepository surveyRepository,
                         ClassRepository classRepository, QuestionRepository questionRepository,
                         TeacherAssignmentRepository assignmentRepository) {
        this.surveyResultRepository = surveyResultRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.surveyRepository = surveyRepository;
        this.classRepository = classRepository;
        this.questionRepository = questionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<Question> getQuestions(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .map(Survey::getQuestions)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ankiety o ID: " + surveyId));
    }

    @Transactional
    public void saveCompositeSurveyStructure(CompositeSurveyRequest request) {
        Classes schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klasy o ID: " + request.getClassId()));

        if (schoolClass.getAccessCode() == null || schoolClass.getAccessCode().isEmpty()) {
            String code = schoolClass.getName()
                    .toUpperCase()
                    .replaceAll("\\s+", "")
                    .replaceAll("[^A-Z0-9]", "")
                    + "-"
                    + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            schoolClass.setAccessCode(code);
            classRepository.save(schoolClass);
        }

        LocalDate parsedStartDate = LocalDate.parse(request.getStartDate());
        LocalDate parsedEndDate = LocalDate.parse(request.getEndDate());

        if (request.getTeacherSurveys() != null) {
            for (TeacherSurveySelection selection : request.getTeacherSurveys()) {
                Teacher teacher = teacherRepository.findById(selection.getTeacherId())
                        .orElseThrow(() -> new IllegalArgumentException("Brak nauczyciela ID: " + selection.getTeacherId()));

                Subject subject = subjectRepository.findById(selection.getSubjectId())
                        .orElseThrow(() -> new IllegalArgumentException("Brak przedmiotu ID: " + selection.getSubjectId()));

                List<Question> questions = questionRepository.findAllById(selection.getQuestionIds());

                Survey survey = new Survey();
                survey.setClasses(schoolClass);
                survey.setTeacher(teacher);
                survey.setSubject(subject);
                survey.setQuestions(questions);
                survey.setStartDate(parsedStartDate);
                survey.setEndDate(parsedEndDate);
                survey.setActive(true);
                survey.setSchoolSurvey(false);
                surveyRepository.save(survey);
            }
        }

        if (request.getSchoolQuestionIds() != null && !request.getSchoolQuestionIds().isEmpty()) {
            List<Question> schoolQuestions = questionRepository.findAllById(request.getSchoolQuestionIds());

            Survey schoolSurvey = new Survey();
            schoolSurvey.setClasses(schoolClass);
            schoolSurvey.setTeacher(null);
            schoolSurvey.setSubject(null);
            schoolSurvey.setQuestions(schoolQuestions);
            schoolSurvey.setStartDate(parsedStartDate);
            schoolSurvey.setEndDate(parsedEndDate);
            schoolSurvey.setActive(true);
            schoolSurvey.setSchoolSurvey(true);
            surveyRepository.save(schoolSurvey);
        }
    }

    @Transactional
    public void saveAnswers(Long surveyId, Map<String, Object> answers, String studentId) {
        if (surveyResultRepository.existsBySurveyIdAndStudentId(surveyId, studentId)) {
            throw new IllegalStateException("Już wypełniłeś tę ankietę!");
        }

        Survey survey = surveyRepository.findById(surveyId).orElseThrow();

        if (survey.getClasses() == null || survey.getClasses().getName() == null) {
            throw new IllegalStateException("Błąd: Ankieta nie jest poprawnie przypisana do żadnej klasy!");
        }

        SurveyResult result = new SurveyResult();
        result.setSurvey(survey);
        result.setTeacher(survey.getTeacher());
        result.setSubject(survey.getSubject());
        result.setStudentId(studentId);
        result.setClassName(survey.getClasses().getName());

        Map<String, Double> scores = new HashMap<>();
        Map<String, String> commentsMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : answers.entrySet()) {
            if (entry.getValue() instanceof Number) {
                scores.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
            } else if (entry.getValue() instanceof String && (entry.getKey().equals("A+") || entry.getKey().equals("A-"))) {
                commentsMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        result.setQuestionScores(scores);
        result.setComments(commentsMap);
        surveyResultRepository.save(result);
    }

    @Transactional
    public void saveGroupAnswers(Map<Long, Map<String, Object>> answers, String studentId) {
        for (Map.Entry<Long, Map<String, Object>> entry : answers.entrySet()) {
            Long surveyId = entry.getKey();
            Map<String, Object> sectionAnswers = entry.getValue();

            if (surveyResultRepository.existsBySurveyIdAndStudentId(surveyId, studentId)) {
                continue;
            }

            Survey survey = surveyRepository.findById(surveyId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ankiety ID: " + surveyId));

            SurveyResult result = new SurveyResult();
            result.setSurvey(survey);
            result.setTeacher(survey.getTeacher());
            result.setSubject(survey.getSubject());
            result.setStudentId(studentId);
            result.setClassName(survey.getClasses().getName());

            Map<String, Double> scores = new HashMap<>();
            Map<String, String> commentsMap = new HashMap<>();

            for (Map.Entry<String, Object> ans : sectionAnswers.entrySet()) {
                if (ans.getValue() instanceof Number) {
                    scores.put(ans.getKey(), ((Number) ans.getValue()).doubleValue());
                } else if (ans.getValue() instanceof String) {
                    commentsMap.put(ans.getKey(), (String) ans.getValue());
                }
            }

            result.setQuestionScores(scores);
            result.setComments(commentsMap);
            surveyResultRepository.save(result);
        }
    }

    private String buildTeacherLabel(Survey survey) {
        if (survey.getTeacher() == null) return "Ocena szkoły";
        String name = survey.getTeacher().getFirstName() + " " + survey.getTeacher().getLastName();
        String subject = (survey.getSubject() != null) ? survey.getSubject().getName() : null;
        return (subject != null && !subject.isEmpty()) ? name + " – " + subject : name;
    }

    @Transactional(readOnly = true)
    public List<ActiveSurveyOverviewDTO.SurveyEntryDTO> getActiveSurveysForAdmin() {
        return surveyRepository.findByIsActiveTrue().stream().map(survey ->
                new ActiveSurveyOverviewDTO.SurveyEntryDTO(
                        survey.getId(),
                        buildTeacherLabel(survey),
                        survey.getClasses().getName(),
                        survey.getStartDate().toString(),
                        survey.getEndDate().toString(),
                        survey.getClasses().getAccessCode(),
                        survey.isSchoolSurvey()
                )
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActiveSurveyOverviewDTO.SurveyEntryDTO> getActiveSurveysByCode(String accessCode) {
        Classes schoolClass = classRepository.findByAccessCode(accessCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klasy o kodzie: " + accessCode));

        return surveyRepository.findByIsActiveTrue().stream()
                .filter(s -> s.getClasses() != null && s.getClasses().getId().equals(schoolClass.getId()))
                .map(survey ->
                        new ActiveSurveyOverviewDTO.SurveyEntryDTO(
                                survey.getId(),
                                buildTeacherLabel(survey),
                                survey.getClasses().getName(),
                                survey.getStartDate().toString(),
                                survey.getEndDate().toString(),
                                survey.getClasses().getAccessCode(),
                                survey.isSchoolSurvey()
                        )
                ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherSurveyGroupDTO getGroupedSurveysForTeacher(Long teacherId, String accessCode) {
        Classes schoolClass = classRepository.findByAccessCode(accessCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klasy o kodzie: " + accessCode));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono nauczyciela ID: " + teacherId));

        List<Survey> surveys = surveyRepository.findByIsActiveTrue().stream()
                .filter(s -> s.getClasses() != null
                        && s.getClasses().getId().equals(schoolClass.getId())
                        && s.getTeacher() != null
                        && s.getTeacher().getId().equals(teacherId))
                .collect(Collectors.toList());

        if (surveys.isEmpty()) {
            throw new IllegalArgumentException("Brak aktywnych ankiet dla tego nauczyciela w tej klasie.");
        }

        List<TeacherSurveyGroupDTO.SurveySection> sections = surveys.stream()
                .map(s -> {
                    String subjectName = s.getSubject() != null ? s.getSubject().getName() : "Ogólna";
                    List<TeacherSurveyGroupDTO.QuestionDTO> questions = s.getQuestions().stream()
                            .map(q -> new TeacherSurveyGroupDTO.QuestionDTO(q.getId(), q.getContent(), q.getType()))
                            .collect(Collectors.toList());
                    return new TeacherSurveyGroupDTO.SurveySection(s.getId(), subjectName, questions);
                })
                .collect(Collectors.toList());

        Survey first = surveys.get(0);
        String teacherName = teacher.getFirstName() + " " + teacher.getLastName();

        return new TeacherSurveyGroupDTO(
                teacherId,
                teacherName,
                schoolClass.getName(),
                first.getStartDate().toString(),
                first.getEndDate().toString(),
                sections
        );
    }

    @Transactional(readOnly = true)
    public boolean isGroupCompleted(Long teacherId, String accessCode, String studentId) {
        Classes schoolClass = classRepository.findByAccessCode(accessCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klasy o kodzie: " + accessCode));

        List<Survey> surveys = surveyRepository.findByIsActiveTrue().stream()
                .filter(s -> s.getClasses() != null
                        && s.getClasses().getId().equals(schoolClass.getId())
                        && s.getTeacher() != null
                        && s.getTeacher().getId().equals(teacherId))
                .collect(Collectors.toList());

        if (surveys.isEmpty()) return false;

        return surveys.stream().allMatch(s ->
                surveyResultRepository.existsBySurveyIdAndStudentId(s.getId(), studentId));
    }

    @Transactional(readOnly = true)
    public TeacherSurveyGroupDTO getGroupedSurveysByIds(List<Long> surveyIds, String accessCode) {
        List<Survey> surveys = surveyIds.stream()
                .map(id -> surveyRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ankiety ID: " + id)))
                .collect(Collectors.toList());

        if (surveys.isEmpty()) {
            throw new IllegalArgumentException("Brak ankiet dla podanych ID.");
        }

        Survey first = surveys.get(0);
        Teacher teacher = first.getTeacher();
        String teacherName = teacher != null
                ? teacher.getFirstName() + " " + teacher.getLastName()
                : "Ogólna";

        List<TeacherSurveyGroupDTO.SurveySection> sections = surveys.stream()
                .map(s -> {
                    String subjectName = s.getSubject() != null ? s.getSubject().getName() : "Ogólna";
                    List<TeacherSurveyGroupDTO.QuestionDTO> questions = s.getQuestions().stream()
                            .map(q -> new TeacherSurveyGroupDTO.QuestionDTO(q.getId(), q.getContent(), q.getType()))
                            .collect(Collectors.toList());
                    return new TeacherSurveyGroupDTO.SurveySection(s.getId(), subjectName, questions);
                })
                .collect(Collectors.toList());

        return new TeacherSurveyGroupDTO(
                teacher != null ? teacher.getId() : null,
                teacherName,
                first.getClasses().getName(),
                first.getStartDate().toString(),
                first.getEndDate().toString(),
                sections
        );
    }

    @Transactional(readOnly = true)
    public boolean isGroupCompletedByIds(List<Long> surveyIds, String studentId) {
        return surveyIds.stream().allMatch(id ->
                surveyResultRepository.existsBySurveyIdAndStudentId(id, studentId));
    }

    public List<Teacher> getAllTeachers() { return teacherRepository.findAll(); }

    public List<String> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(Subject::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    // ─── WYNIKI ────────────────────────────────────────────────────────────────

    /**
     * Zwraca listę DTO per nauczyciel (nie per nauczyciel+przedmiot).
     * Jeden nauczyciel uczący wielu przedmiotów → jedna karta ze scalonymi wynikami.
     * subjectName = lista przedmiotów oddzielona przecinkiem.
     */
    @Transactional(readOnly = true)
    public List<SurveySummaryDTO> getAllTeacherSummaries() {
        Map<String, String> questionTextsMap = questionRepository.findAll().stream()
                .collect(Collectors.toMap(Question::getId, Question::getContent, (a, b) -> a));

        List<SurveyResult> allResults = surveyResultRepository.findAll().stream()
                .filter(r -> r.getTeacher() != null)
                .collect(Collectors.toList());

        // Grupuj per teacherId
        Map<Long, List<SurveyResult>> grouped = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getTeacher().getId()));

        return grouped.values().stream()
                .map(results -> buildTeacherDTO(results, questionTextsMap))
                .collect(Collectors.toList());
    }

    private SurveySummaryDTO buildTeacherDTO(List<SurveyResult> results,
                                             Map<String, String> questionTextsMap) {
        SurveyResult first = results.get(0);
        Teacher teacher = first.getTeacher();

        SurveySummaryDTO dto = new SurveySummaryDTO();
        dto.setTeacherId(teacher.getId());
        dto.setTeacherName(teacher.getFirstName() + " " + teacher.getLastName());

        // Lista unikalnych przedmiotów jako jeden string
        String subjects = results.stream()
                .map(r -> r.getSubject() != null ? r.getSubject().getName() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
        dto.setSubjectName(subjects.isEmpty() ? null : subjects);
        dto.setQuestionTexts(questionTextsMap);

        // Unikalni uczniowie globalnie
        long uniqueStudents = results.stream()
                .map(SurveyResult::getStudentId).distinct().count();
        dto.setTotalVotes(uniqueStudents);

        dto.setClassNames(results.stream()
                .map(SurveyResult::getClassName)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toSet()));

        // Średnie globalne
        Map<String, Double> averages = results.stream()
                .flatMap(r -> r.getQuestionScores().entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.averagingDouble(Map.Entry::getValue)));
        Map<String, Double> formattedAverages = new HashMap<>();
        averages.forEach((key, val) -> formattedAverages.put("avg" + key, val));
        dto.setAverages(formattedAverages);

        // Średnie per klasa
        Map<String, Map<String, Double>> averagesPerClass = results.stream()
                .filter(r -> r.getClassName() != null)
                .collect(Collectors.groupingBy(
                        SurveyResult::getClassName,
                        Collectors.flatMapping(
                                r -> r.getQuestionScores().entrySet().stream(),
                                Collectors.groupingBy(
                                        e -> "avg" + e.getKey(),
                                        Collectors.averagingDouble(Map.Entry::getValue)
                                )
                        )
                ));
        dto.setAveragesPerClass(averagesPerClass);

        // Głosy per klasa
        Map<String, Long> votesPerClass = results.stream()
                .filter(r -> r.getClassName() != null && r.getStudentId() != null)
                .collect(Collectors.groupingBy(
                        SurveyResult::getClassName,
                        Collectors.collectingAndThen(
                                Collectors.mapping(SurveyResult::getStudentId, Collectors.toSet()),
                                set -> (long) set.size()
                        )
                ));
        dto.setTotalVotesPerClass(votesPerClass);

        // Komentarze globalne (deduplikowane per student)
        Set<String> seenStudents = new HashSet<>();
        List<CommentDTO> comments = results.stream()
                .filter(r -> seenStudents.add(r.getStudentId()))
                .flatMap(r -> r.getComments().entrySet().stream())
                .map(e -> new CommentDTO(
                        e.getValue(),
                        e.getKey().equals("A+") ? "POZYTYWNA" : "KONSTRUKTYWNA",
                        questionTextsMap.getOrDefault(e.getKey(), e.getKey())
                ))
                .collect(Collectors.toList());
        dto.setComments(comments);

        // Komentarze per klasa
        Map<String, List<CommentDTO>> commentsPerClass = new HashMap<>();
        results.stream()
                .filter(r -> r.getClassName() != null)
                .collect(Collectors.groupingBy(SurveyResult::getClassName))
                .forEach((className, classResults) -> {
                    Set<String> seenInClass = new HashSet<>();
                    List<CommentDTO> classComments = classResults.stream()
                            .filter(r -> seenInClass.add(r.getStudentId()))
                            .flatMap(r -> r.getComments().entrySet().stream())
                            .map(e -> new CommentDTO(
                                    e.getValue(),
                                    e.getKey().equals("A+") ? "POZYTYWNA" : "KONSTRUKTYWNA",
                                    questionTextsMap.getOrDefault(e.getKey(), e.getKey())
                            ))
                            .collect(Collectors.toList());
                    commentsPerClass.put(className, classComments);
                });
        dto.setCommentsPerClass(commentsPerClass);

        return dto;
    }

    @Transactional(readOnly = true)
    public SurveySummaryDTO getSchoolSummary() {
        Map<String, String> questionTextsMap = questionRepository.findAll().stream()
                .collect(Collectors.toMap(Question::getId, Question::getContent, (a, b) -> a));

        List<SurveyResult> results = surveyResultRepository.findAll().stream()
                .filter(r -> r.getTeacher() == null)
                .collect(Collectors.toList());

        if (results.isEmpty()) return null;

        SurveySummaryDTO dto = new SurveySummaryDTO();
        dto.setTeacherId(null);
        dto.setTeacherName("Ocena szkoły");
        dto.setSubjectName(null);
        dto.setQuestionTexts(questionTextsMap);

        dto.setClassNames(results.stream()
                .map(SurveyResult::getClassName)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toSet()));

        Map<String, Long> votesPerClass = results.stream()
                .filter(r -> r.getClassName() != null && r.getStudentId() != null)
                .collect(Collectors.groupingBy(
                        SurveyResult::getClassName,
                        Collectors.collectingAndThen(
                                Collectors.mapping(SurveyResult::getStudentId, Collectors.toSet()),
                                set -> (long) set.size()
                        )
                ));
        dto.setTotalVotesPerClass(votesPerClass);

        long totalVotes = results.stream()
                .map(SurveyResult::getStudentId)
                .distinct()
                .count();
        dto.setTotalVotes(totalVotes);

        Map<String, Double> averages = results.stream()
                .flatMap(r -> r.getQuestionScores().entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.averagingDouble(Map.Entry::getValue)));
        Map<String, Double> formattedAverages = new HashMap<>();
        averages.forEach((key, val) -> formattedAverages.put("avg" + key, val));
        dto.setAverages(formattedAverages);

        Map<String, Map<String, Double>> averagesPerClass = results.stream()
                .filter(r -> r.getClassName() != null)
                .collect(Collectors.groupingBy(
                        SurveyResult::getClassName,
                        Collectors.flatMapping(
                                r -> r.getQuestionScores().entrySet().stream(),
                                Collectors.groupingBy(
                                        e -> "avg" + e.getKey(),
                                        Collectors.averagingDouble(Map.Entry::getValue)
                                )
                        )
                ));
        dto.setAveragesPerClass(averagesPerClass);

        Map<String, List<CommentDTO>> commentsPerClass = new HashMap<>();
        results.stream()
                .filter(r -> r.getClassName() != null)
                .collect(Collectors.groupingBy(SurveyResult::getClassName))
                .forEach((className, classResults) -> {
                    Set<String> seenInClass = new HashSet<>();
                    List<CommentDTO> classComments = classResults.stream()
                            .filter(r -> seenInClass.add(r.getStudentId()))
                            .flatMap(r -> r.getComments().entrySet().stream())
                            .map(e -> new CommentDTO(
                                    e.getValue(),
                                    "SCHOOL_OPEN",
                                    questionTextsMap.getOrDefault(e.getKey(), e.getKey())
                            ))
                            .collect(Collectors.toList());
                    commentsPerClass.put(className, classComments);
                });
        dto.setCommentsPerClass(commentsPerClass);

        List<CommentDTO> globalComments = commentsPerClass.values().stream()
                .flatMap(List::stream)
                .filter(c -> {
                    String b3Text = questionTextsMap.getOrDefault("B3", "");
                    return !c.getQuestionText().equals(b3Text);
                })
                .collect(Collectors.toList());
        dto.setComments(globalComments);

        return dto;
    }
}