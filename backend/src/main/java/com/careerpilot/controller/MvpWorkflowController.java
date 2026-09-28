package com.careerpilot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.careerpilot.ai.AiJdAnalysisResult;
import com.careerpilot.ai.AiJdContext;
import com.careerpilot.ai.AiResumeDraft;
import com.careerpilot.ai.AiResumeMaterial;
import com.careerpilot.ai.AiInterviewContext;
import com.careerpilot.ai.AiInterviewEvaluation;
import com.careerpilot.ai.AiInterviewQuestion;
import com.careerpilot.ai.AiResumeMaterialOptimizationResult;
import com.careerpilot.ai.CareerAiProvider;
import com.careerpilot.entity.AbilityAnalysis;
import com.careerpilot.entity.CareerPlan;
import com.careerpilot.entity.InterviewRecord;
import com.careerpilot.entity.JdAnalysis;
import com.careerpilot.entity.ResumeDocument;
import com.careerpilot.entity.ResumeMaterial;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.service.AiUsageService;
import com.careerpilot.service.CareerWorkflowService;
import com.careerpilot.service.InterviewRecordService;
import com.careerpilot.service.JdAnalysisService;
import com.careerpilot.service.ResumeDocumentService;
import com.careerpilot.service.ResumeMaterialService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
public class MvpWorkflowController {

    private final CareerWorkflowService careerWorkflowService;
    private final ResumeMaterialService resumeMaterialService;
    private final ResumeDocumentService resumeDocumentService;
    private final JdAnalysisService jdAnalysisService;
    private final InterviewRecordService interviewRecordService;
    private final AuthSessionService authSessionService;
    private final CareerAiProvider careerAiProvider;
    private final AiUsageService aiUsageService;

    public MvpWorkflowController(CareerWorkflowService careerWorkflowService,
                                 ResumeMaterialService resumeMaterialService,
                                 ResumeDocumentService resumeDocumentService,
                                 JdAnalysisService jdAnalysisService,
                                 InterviewRecordService interviewRecordService,
                                 AuthSessionService authSessionService,
                                 CareerAiProvider careerAiProvider,
                                 AiUsageService aiUsageService) {
        this.careerWorkflowService = careerWorkflowService;
        this.resumeMaterialService = resumeMaterialService;
        this.resumeDocumentService = resumeDocumentService;
        this.jdAnalysisService = jdAnalysisService;
        this.interviewRecordService = interviewRecordService;
        this.authSessionService = authSessionService;
        this.careerAiProvider = careerAiProvider;
        this.aiUsageService = aiUsageService;
    }

    @PostMapping("/api/planning/generate")
    public Map<String, Object> generatePlan(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        String stage = text(payload.get("stage"), "Internship preparation");
        String targetPosition = text(payload.get("target_position"), text(payload.get("targetPosition"), "Frontend Engineer"));
        CareerPlan plan = careerWorkflowService.generatePlan(userId, stage, targetPosition);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", plan.getId());
        data.put("stage", stage);
        data.put("target_position", targetPosition);
        data.put("current_level", value(plan.getCurrentLevel()));
        data.put("short_term_goals", split(plan.getShortTermGoal(), ";"));
        data.put("mid_term_goals", split(plan.getThreeMonthPlan(), ";"));
        data.put("long_term_goals", split(plan.getOneYearPlan(), ";"));
        data.put("weekly_tasks", split(plan.getWeeklyTasks(), ";"));
        data.put("roadmap", split(plan.getRoadmap(), "->"));
        data.put("generation_source", value(plan.getGenerationSource()));
        data.put("model", value(plan.getModel()));
        return data;
    }

    @PostMapping("/api/ability/analyze")
    public Map<String, Object> analyzeAbility(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        String targetPosition = text(payload.get("target_position"), "Frontend Engineer");
        AbilityAnalysis analysis = careerWorkflowService.analyzeAbility(userId, targetPosition);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("target_position", targetPosition);
        data.put("match_score", analysis.getMatchScore());
        data.put("strengths", split(analysis.getStrengths(), ","));
        data.put("weaknesses", split(analysis.getWeaknesses(), ","));
        data.put("suggestions", split(analysis.getPriorities(), ">"));
        data.put("summary", value(analysis.getSuggestions()));
        data.put("skill_scores", parseSkillScores(analysis.getSkillScores()));
        data.put("analysis_source", value(analysis.getAnalysisSource()));
        data.put("model", value(analysis.getModel()));
        return data;
    }

    @GetMapping("/api/resume/materials")
    public List<Map<String, Object>> listMaterials(HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        List<ResumeMaterial> materials = resumeMaterialService.list(new LambdaQueryWrapper<ResumeMaterial>()
                .eq(ResumeMaterial::getUserId, userId)
                .orderByDesc(ResumeMaterial::getCreatedAt)
                .orderByDesc(ResumeMaterial::getId));
        return materials.stream().map(this::toResumeMaterial).toList();
    }

    @PostMapping("/api/resume/materials")
    public Map<String, Object> createMaterial(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeMaterial material = new ResumeMaterial();
        material.setUserId(userId);
        material.setType(text(payload.get("type"), "project"));
        material.setTitle(text(payload.get("title"), material.getType() + " material"));
        material.setRawText(text(payload.get("original_content"), text(payload.get("content"), "")));
        material.setTags(joinList(payload.get("tags")));
        material.setSourceType("manual");
        material.setCreatedAt(LocalDateTime.now());
        material.setUpdatedAt(LocalDateTime.now());
        MaterialOptimization optimization = optimizeMaterialWithSource(material);
        material.setOptimizedText(optimization.content());
        if (material.getTags() == null || material.getTags().isBlank()) {
            material.setTags(String.join(", ", optimization.tags()));
        }
        resumeMaterialService.save(material);
        return toResumeMaterial(material, optimization.source(), optimization.model());
    }

    @PutMapping("/api/resume/materials/{materialId}")
    public Map<String, Object> updateMaterial(@PathVariable Long materialId,
                                              @RequestBody Map<String, Object> payload,
                                              HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeMaterial material = findOwnedMaterial(userId, materialId);
        material.setType(text(payload.get("type"), material.getType()));
        material.setTitle(text(payload.get("title"), material.getTitle()));
        material.setRawText(text(payload.get("original_content"), text(payload.get("content"), material.getRawText())));
        material.setTags(joinList(payload.get("tags")));
        material.setUpdatedAt(LocalDateTime.now());
        resumeMaterialService.updateById(material);
        return toResumeMaterial(material);
    }

    @PostMapping("/api/resume/materials/{materialId}/optimize")
    public Map<String, Object> optimizeMaterial(@PathVariable Long materialId, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeMaterial material = findOwnedMaterial(userId, materialId);
        MaterialOptimization optimization = optimizeMaterialWithSource(material);
        material.setOptimizedText(optimization.content());
        if ((material.getTags() == null || material.getTags().isBlank()) && !optimization.tags().isEmpty()) {
            material.setTags(String.join(", ", optimization.tags()));
        }
        material.setUpdatedAt(LocalDateTime.now());
        resumeMaterialService.updateById(material);
        return toResumeMaterial(material, optimization.source(), optimization.model());
    }

    @DeleteMapping("/api/resume/materials/{materialId}")
    public Map<String, Object> deleteMaterial(@PathVariable Long materialId, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeMaterial material = findOwnedMaterial(userId, materialId);
        resumeMaterialService.removeById(material.getId());
        return Map.of("deleted", true, "id", materialId);
    }

    @PostMapping("/api/jd/analyze")
    public Map<String, Object> analyzeJd(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        String jobTitle = text(payload.get("job_title"), "Frontend Engineer");
        String companyName = text(payload.get("company_name"), "Target Company");
        String jdContent = text(payload.get("jd_content"), "");
        List<ResumeMaterial> materials = resumeMaterialService.list(new LambdaQueryWrapper<ResumeMaterial>()
                .eq(ResumeMaterial::getUserId, userId)
                .orderByDesc(ResumeMaterial::getUpdatedAt)
                .orderByDesc(ResumeMaterial::getId));
        List<String> fallbackKeywords = extractKeywords(jobTitle + " " + jdContent);
        List<ResumeMaterial> fallbackMaterials = rankMaterials(materials, fallbackKeywords);
        AiUsageService.AiExecution<AiJdAnalysisResult> aiExecution = aiUsageService.execute(
                userId,
                "jd_analysis",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.analyzeJd(jdContext(jobTitle, companyName, jdContent, materials)),
                () -> null
        );
        AiJdAnalysisResult aiResult = aiExecution.value();
        boolean analyzedByAi = "llm".equals(aiExecution.source()) && aiResult != null;
        List<String> keywords = analyzedByAi ? aiResult.keywords() : fallbackKeywords;
        List<ResumeMaterial> matchedMaterials = analyzedByAi
                ? materialsByIds(materials, aiResult.recommendedMaterialIds(), fallbackMaterials)
                : fallbackMaterials;
        List<String> recommendedTitles = matchedMaterials.stream()
                .map(ResumeMaterial::getTitle)
                .filter(title -> title != null && !title.isBlank())
                .toList();

        JdAnalysis analysis = new JdAnalysis();
        analysis.setUserId(userId);
        analysis.setJdText(jdContent);
        analysis.setKeywords(String.join(", ", keywords));
        analysis.setMatchScore(analyzedByAi
                ? aiResult.matchScore()
                : jdMatchScore(keywords, matchedMaterials, materials));
        analysis.setRecommendedMaterials(String.join(", ", recommendedTitles));
        List<String> suggestions = analyzedByAi
                ? aiResult.suggestions()
                : split(jdSuggestions(keywords, matchedMaterials, materials), ";");
        analysis.setSuggestions(String.join(";", suggestions));
        analysis.setCreatedAt(LocalDateTime.now());
        analysis.setUpdatedAt(LocalDateTime.now());
        jdAnalysisService.save(analysis);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("job_title", jobTitle);
        data.put("company_name", companyName);
        data.put("jd_content", jdContent);
        data.put("keywords", keywords);
        data.put("requirements", analyzedByAi
                ? aiResult.requirements()
                : (keywords.isEmpty() ? List.of("请补充完整 JD，系统会继续提取岗位要求") : keywords));
        data.put("match_score", analysis.getMatchScore());
        data.put("recommended_materials", recommendedTitles);
        data.put("suggestions", suggestions);
        data.put("analysis_source", aiExecution.source());
        data.put("model", aiExecution.model());
        return data;
    }

    @PostMapping("/api/resume/customize")
    public Map<String, Object> customizeResume(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        String jobTitle = text(payload.get("job_title"), "Frontend Engineer");
        String companyName = text(payload.get("company_name"), "Target Company");
        String jdContent = text(payload.get("jd_content"), "");

        List<ResumeMaterial> materials = resumeMaterialService.list(new LambdaQueryWrapper<ResumeMaterial>()
                .eq(ResumeMaterial::getUserId, userId)
                .orderByDesc(ResumeMaterial::getUpdatedAt)
                .orderByDesc(ResumeMaterial::getId));
        List<String> fallbackKeywords = extractKeywords(jobTitle + " " + jdContent);
        List<ResumeMaterial> fallbackMaterials = rankMaterials(materials, fallbackKeywords);
        AiUsageService.AiExecution<AiResumeDraft> aiExecution = aiUsageService.execute(
                userId,
                "resume_customization",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.customizeResume(jdContext(jobTitle, companyName, jdContent, materials)),
                () -> null
        );
        AiResumeDraft aiDraft = aiExecution.value();
        boolean generatedByAi = "llm".equals(aiExecution.source()) && aiDraft != null;
        List<String> keywords = generatedByAi ? aiDraft.keywords() : fallbackKeywords;
        List<ResumeMaterial> matchedMaterials = generatedByAi
                ? materialsByIds(materials, aiDraft.matchedMaterialIds(), fallbackMaterials)
                : fallbackMaterials;

        ResumeDocument document = new ResumeDocument();
        document.setUserId(userId);
        document.setTitle(companyName + " - " + jobTitle + " 定制简历");
        document.setSourceType("jd_customized");
        document.setContent(generatedByAi
                ? aiDraft.content()
                : buildResumeContent(jobTitle, companyName, keywords, matchedMaterials, false));
        document.setOptimizedContent(generatedByAi
                ? aiDraft.optimizedContent()
                : buildResumeContent(jobTitle, companyName, keywords, matchedMaterials, true));
        document.setStatus("draft");
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        resumeDocumentService.save(document);

        Map<String, Object> data = toResumeDocument(document);
        data.put("matched_materials", matchedMaterials.stream().map(ResumeMaterial::getTitle).toList());
        data.put("keywords", keywords);
        data.put("generation_source", aiExecution.source());
        data.put("model", aiExecution.model());
        return data;
    }

    @GetMapping("/api/resume/documents")
    public List<Map<String, Object>> listResumeDocuments(HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        List<ResumeDocument> documents = resumeDocumentService.list(new LambdaQueryWrapper<ResumeDocument>()
                .eq(ResumeDocument::getUserId, userId)
                .orderByDesc(ResumeDocument::getUpdatedAt)
                .orderByDesc(ResumeDocument::getId));
        return documents.stream().map(this::toResumeDocument).toList();
    }

    @GetMapping("/api/resume/documents/{documentId}")
    public Map<String, Object> getResumeDocument(@PathVariable Long documentId, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        return toResumeDocument(findOwnedResumeDocument(userId, documentId));
    }

    @PutMapping("/api/resume/documents/{documentId}")
    public Map<String, Object> updateResumeDocument(@PathVariable Long documentId,
                                                    @RequestBody Map<String, Object> payload,
                                                    HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeDocument document = findOwnedResumeDocument(userId, documentId);
        document.setTitle(text(payload.get("title"), document.getTitle()));
        document.setContent(text(payload.get("content"), document.getContent()));
        document.setOptimizedContent(text(payload.get("optimized_content"), document.getOptimizedContent()));
        String nextStatus = text(payload.get("status"), document.getStatus());
        if ("final".equals(nextStatus)) {
            demoteOtherFinalResumeDocuments(userId, document.getId());
        }
        document.setStatus(nextStatus);
        document.setUpdatedAt(LocalDateTime.now());
        resumeDocumentService.updateById(document);
        return toResumeDocument(document);
    }

    @DeleteMapping("/api/resume/documents/{documentId}")
    public Map<String, Object> deleteResumeDocument(@PathVariable Long documentId, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeDocument document = findOwnedResumeDocument(userId, documentId);
        resumeDocumentService.removeById(document.getId());
        return Map.of("deleted", true, "id", documentId);
    }

    private AiJdContext jdContext(String jobTitle, String companyName, String jdContent,
                                   List<ResumeMaterial> materials) {
        List<AiResumeMaterial> aiMaterials = materials.stream()
                .map(material -> new AiResumeMaterial(
                        material.getId(),
                        value(material.getTitle()),
                        value(material.getType()),
                        value(material.getOptimizedText()).isBlank()
                                ? value(material.getRawText())
                                : value(material.getOptimizedText()),
                        value(material.getTags())
                ))
                .toList();
        return new AiJdContext(jobTitle, companyName, jdContent, aiMaterials);
    }

    private List<ResumeMaterial> materialsByIds(List<ResumeMaterial> materials, List<Long> ids,
                                                List<ResumeMaterial> fallback) {
        if (ids == null || ids.isEmpty()) {
            return fallback;
        }
        Map<Long, ResumeMaterial> materialMap = new LinkedHashMap<>();
        materials.forEach(material -> materialMap.put(material.getId(), material));
        List<ResumeMaterial> selected = ids.stream()
                .map(materialMap::get)
                .filter(java.util.Objects::nonNull)
                .limit(3)
                .toList();
        return selected.isEmpty() ? fallback : selected;
    }

    @PostMapping("/api/interview/question")
    public Map<String, Object> createQuestion(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeDocument document = selectedResumeDocument(userId, payload.get("resume_document_id"));
        String jobTitle = text(payload.get("job_title"), inferJobTitle(document, "Frontend Engineer"));
        List<String> keywords = extractKeywords(jobTitle + " " + value(document == null ? "" : document.getOptimizedContent()));
        AiInterviewContext aiContext = interviewContext(jobTitle, document, keywords, "", "");
        AiUsageService.AiExecution<AiInterviewQuestion> aiExecution = aiUsageService.execute(
                userId,
                "interview_question",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.generateInterviewQuestion(aiContext),
                () -> null
        );
        AiInterviewQuestion aiQuestion = aiExecution.value();
        boolean generatedByAi = "llm".equals(aiExecution.source()) && aiQuestion != null;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("job_title", jobTitle);
        data.put("resume_document_id", document == null ? null : document.getId());
        data.put("resume_title", document == null ? "" : document.getTitle());
        data.put("keywords", keywords);
        data.put("question", generatedByAi ? aiQuestion.question() : buildInterviewQuestion(jobTitle, document, keywords));
        data.put("generation_source", aiExecution.source());
        data.put("model", aiExecution.model());
        return data;
    }

    @PostMapping("/api/interview/answer")
    public Map<String, Object> scoreAnswer(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        ResumeDocument document = selectedResumeDocument(userId, payload.get("resume_document_id"));
        String jobTitle = text(payload.get("job_title"), inferJobTitle(document, "Frontend Engineer"));
        String question = text(payload.get("question"), "");
        String answer = text(payload.get("answer"), "");
        List<String> keywords = extractKeywords(jobTitle + " " + question + " " + value(document == null ? "" : document.getOptimizedContent()));
        AiUsageService.AiExecution<AiInterviewEvaluation> aiExecution = aiUsageService.execute(
                userId,
                "interview_evaluation",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.evaluateInterviewAnswer(interviewContext(jobTitle, document, keywords, question, answer)),
                () -> null
        );
        AiInterviewEvaluation aiEvaluation = aiExecution.value();
        boolean evaluatedByAi = "llm".equals(aiExecution.source()) && aiEvaluation != null;
        int baseScore = evaluatedByAi ? aiEvaluation.averageScore() : interviewScore(answer, keywords);
        int technicalScore = evaluatedByAi ? aiEvaluation.technical() : baseScore;
        int projectScore = evaluatedByAi
                ? aiEvaluation.project()
                : Math.max(60, baseScore - (answer.toLowerCase(Locale.ROOT).contains("project") || answer.contains("项目") ? 0 : 8));
        int logicScore = evaluatedByAi
                ? aiEvaluation.logic()
                : Math.max(60, baseScore - (answer.contains("因为") || answer.toLowerCase(Locale.ROOT).contains("because") ? 2 : 8));
        int starScore = evaluatedByAi
                ? aiEvaluation.star()
                : Math.max(55, baseScore - (answer.contains("结果") || answer.toLowerCase(Locale.ROOT).contains("result") ? 3 : 12));
        String feedback = evaluatedByAi
                ? aiEvaluation.feedback()
                : buildInterviewFeedback(answer, keywords, baseScore, document);

        InterviewRecord record = new InterviewRecord();
        record.setUserId(userId);
        record.setInterviewType(jobTitle);
        record.setQuestion(question);
        record.setAnswer(answer);
        record.setScore(baseScore);
        record.setFeedback(feedback);
        record.setCreatedAt(LocalDateTime.now());
        interviewRecordService.save(record);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", record.getId());
        data.put("job_title", jobTitle);
        data.put("resume_document_id", document == null ? null : document.getId());
        data.put("resume_title", document == null ? "" : document.getTitle());
        data.put("question", question);
        data.put("answer", answer);
        data.put("feedback", record.getFeedback());
        Map<String, Object> scores = new LinkedHashMap<>();
        scores.put("technical", technicalScore);
        scores.put("project", projectScore);
        scores.put("logic", logicScore);
        scores.put("star", starScore);
        data.put("scores", scores);
        data.put("evaluation_source", aiExecution.source());
        data.put("model", aiExecution.model());
        data.put("created_at", record.getCreatedAt().toString());
        return data;
    }

    @GetMapping("/api/interview/records")
    public List<Map<String, Object>> listInterviewRecords(HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        List<InterviewRecord> records = interviewRecordService.list(new LambdaQueryWrapper<InterviewRecord>()
                .eq(InterviewRecord::getUserId, userId)
                .orderByDesc(InterviewRecord::getCreatedAt)
                .orderByDesc(InterviewRecord::getId)
                .last("LIMIT 20"));
        return records.stream().map(this::toInterviewRecord).toList();
    }

    @DeleteMapping("/api/interview/records/{recordId}")
    public Map<String, Object> deleteInterviewRecord(@PathVariable Long recordId, HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        InterviewRecord record = findOwnedInterviewRecord(userId, recordId);
        interviewRecordService.removeById(record.getId());
        return Map.of("deleted", true, "id", recordId);
    }

    private AiInterviewContext interviewContext(String jobTitle, ResumeDocument document, List<String> keywords,
                                                String question, String answer) {
        return new AiInterviewContext(
                jobTitle,
                document == null ? "" : value(document.getTitle()),
                document == null ? "" : value(document.getOptimizedContent()),
                keywords,
                question,
                answer
        );
    }

    private InterviewRecord findOwnedInterviewRecord(Long userId, Long recordId) {
        InterviewRecord record = interviewRecordService.getOne(new LambdaQueryWrapper<InterviewRecord>()
                .eq(InterviewRecord::getUserId, userId)
                .eq(InterviewRecord::getId, recordId)
                .last("LIMIT 1"));
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview record not found");
        }
        return record;
    }

    private Map<String, Object> toInterviewRecord(InterviewRecord record) {
        int score = record.getScore() == null ? 0 : record.getScore();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", record.getId());
        data.put("job_title", value(record.getInterviewType()));
        data.put("resume_document_id", null);
        data.put("resume_title", "");
        data.put("question", value(record.getQuestion()));
        data.put("answer", value(record.getAnswer()));
        data.put("feedback", value(record.getFeedback()));
        Map<String, Object> scores = new LinkedHashMap<>();
        scores.put("technical", score);
        scores.put("project", score);
        scores.put("logic", score);
        scores.put("star", score);
        data.put("scores", scores);
        data.put("evaluation_source", "history");
        data.put("model", "");
        data.put("created_at", record.getCreatedAt() == null ? "" : record.getCreatedAt().toString());
        return data;
    }

    private ResumeDocument selectedResumeDocument(Long userId, Object documentId) {
        Long selectedId = longValue(documentId);
        if (selectedId != null) {
            return findOwnedResumeDocument(userId, selectedId);
        }
        return latestResumeDocument(userId);
    }

    private ResumeDocument latestResumeDocument(Long userId) {
        List<ResumeDocument> documents = resumeDocumentService.list(new LambdaQueryWrapper<ResumeDocument>()
                .eq(ResumeDocument::getUserId, userId)
                .in(ResumeDocument::getStatus, List.of("final", "draft"))
                .orderByDesc(ResumeDocument::getUpdatedAt)
                .orderByDesc(ResumeDocument::getId));
        return documents.stream()
                .filter(document -> "final".equals(document.getStatus()))
                .findFirst()
                .orElse(documents.isEmpty() ? null : documents.get(0));
    }

    private ResumeDocument findOwnedResumeDocument(Long userId, Long documentId) {
        ResumeDocument document = resumeDocumentService.getOne(new LambdaQueryWrapper<ResumeDocument>()
                .eq(ResumeDocument::getUserId, userId)
                .eq(ResumeDocument::getId, documentId)
                .last("LIMIT 1"));
        if (document == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume document not found");
        }
        return document;
    }

    private void demoteOtherFinalResumeDocuments(Long userId, Long currentDocumentId) {
        List<ResumeDocument> finalDocuments = resumeDocumentService.list(new LambdaQueryWrapper<ResumeDocument>()
                .eq(ResumeDocument::getUserId, userId)
                .eq(ResumeDocument::getStatus, "final")
                .ne(ResumeDocument::getId, currentDocumentId));
        LocalDateTime now = LocalDateTime.now();
        for (ResumeDocument item : finalDocuments) {
            item.setStatus("draft");
            item.setUpdatedAt(now);
            resumeDocumentService.updateById(item);
        }
    }

    private String inferJobTitle(ResumeDocument document, String fallback) {
        if (document == null || document.getTitle() == null || document.getTitle().isBlank()) {
            return fallback;
        }
        String title = document.getTitle();
        int separatorIndex = title.indexOf(" - ");
        int suffixIndex = title.indexOf(" 定制简历");
        if (separatorIndex >= 0 && suffixIndex > separatorIndex) {
            return title.substring(separatorIndex + 3, suffixIndex).trim();
        }
        return fallback;
    }

    private String buildInterviewQuestion(String jobTitle, ResumeDocument document, List<String> keywords) {
        if (document == null) {
            return "请先选择一个与你目标岗位相关的项目经历，说明你的职责、技术选择、实现过程和最终结果。";
        }
        String keywordText = keywords.isEmpty() ? "岗位核心能力" : String.join("、", keywords.subList(0, Math.min(4, keywords.size())));
        return "你正在面试 " + jobTitle + "。请结合简历《" + document.getTitle()
                + "》中最相关的一段经历，说明你如何体现 " + keywordText
                + "，并补充你的具体职责、遇到的问题、解决动作和结果。";
    }

    private int interviewScore(String answer, List<String> keywords) {
        String normalized = value(answer).toLowerCase(Locale.ROOT);
        int score = normalized.length() > 220 ? 84 : normalized.length() > 100 ? 76 : 66;
        for (String keyword : keywords) {
            if (normalized.contains(keyword.toLowerCase(Locale.ROOT))) {
                score += 3;
            }
        }
        if (normalized.contains("result") || normalized.contains("impact") || answer.contains("结果") || answer.contains("提升")) {
            score += 5;
        }
        return Math.min(95, Math.max(55, score));
    }

    private String buildInterviewFeedback(String answer, List<String> keywords, int score, ResumeDocument document) {
        List<String> feedback = new ArrayList<>();
        if (document != null) {
            feedback.add("已基于最近的定制简历草稿进行复盘，回答需要持续贴合简历中的项目证据。");
        }
        if (score >= 82) {
            feedback.add("整体回答较完整，已经覆盖职责、动作和岗位关键词。");
        } else {
            feedback.add("回答还偏概括，建议按 STAR 结构补充背景、任务、行动和结果。");
        }
        if (!keywords.isEmpty()) {
            feedback.add("下一版回答请显式嵌入这些关键词中的 2-3 个：" + String.join("、", keywords.subList(0, Math.min(5, keywords.size()))) + "。");
        }
        if (!(answer.contains("结果") || answer.toLowerCase(Locale.ROOT).contains("result"))) {
            feedback.add("目前结果描述不足，可以补充量化指标、用户价值或工程质量提升。");
        }
        return String.join(" ", feedback);
    }

    private ResumeMaterial findOwnedMaterial(Long userId, Long materialId) {
        ResumeMaterial material = resumeMaterialService.getOne(new LambdaQueryWrapper<ResumeMaterial>()
                .eq(ResumeMaterial::getUserId, userId)
                .eq(ResumeMaterial::getId, materialId)
                .last("LIMIT 1"));
        if (material == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume material not found");
        }
        return material;
    }

    private Map<String, Object> toResumeMaterial(ResumeMaterial material) {
        return toResumeMaterial(material, "", "");
    }

    private Map<String, Object> toResumeMaterial(ResumeMaterial material, String optimizationSource, String model) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", material.getId());
        data.put("type", value(material.getType()));
        data.put("title", value(material.getTitle()));
        data.put("original_content", value(material.getRawText()));
        data.put("optimized_content", value(material.getOptimizedText()));
        data.put("tags", split(value(material.getTags()), ","));
        data.put("source_task_id", material.getSourceTaskId());
        data.put("source_type", value(material.getSourceType()));
        data.put("optimization_source", value(optimizationSource));
        data.put("model", value(model));
        return data;
    }

    private Map<String, Object> toResumeDocument(ResumeDocument document) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", document.getId());
        data.put("title", value(document.getTitle()));
        data.put("source_type", value(document.getSourceType()));
        data.put("content", value(document.getContent()));
        data.put("optimized_content", value(document.getOptimizedContent()));
        data.put("status", value(document.getStatus()));
        data.put("target_offer_id", document.getTargetOfferId());
        data.put("template_id", document.getTemplateId());
        data.put("matched_materials", List.of());
        data.put("keywords", List.of());
        data.put("generation_source", "");
        data.put("model", "");
        data.put("created_at", document.getCreatedAt() == null ? "" : document.getCreatedAt().toString());
        data.put("updated_at", document.getUpdatedAt() == null ? "" : document.getUpdatedAt().toString());
        return data;
    }

    private MaterialOptimization optimizeMaterialWithSource(ResumeMaterial material) {
        AiResumeMaterial aiMaterial = new AiResumeMaterial(
                material.getId(),
                value(material.getTitle()),
                value(material.getType()),
                value(material.getRawText()),
                value(material.getTags())
        );
        AiUsageService.AiExecution<AiResumeMaterialOptimizationResult> aiExecution = aiUsageService.execute(
                material.getUserId(),
                "resume_material_optimization",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.optimizeResumeMaterial(aiMaterial),
                () -> null
        );
        AiResumeMaterialOptimizationResult aiResult = aiExecution.value();
        if (aiResult != null && aiResult.optimizedContent() != null && !aiResult.optimizedContent().isBlank()) {
            return new MaterialOptimization(
                    aiResult.optimizedContent(),
                    aiResult.tags() == null ? List.of() : aiResult.tags(),
                    aiExecution.source(),
                    aiExecution.model()
            );
        }
        return new MaterialOptimization(optimizeMaterial(material.getRawText()), split(value(material.getTags()), ","),
                aiExecution.source(), aiExecution.model());
    }

    private String optimizeMaterial(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "请补充完整经历，建议写清背景、个人职责、技术或方法、解决的问题以及可验证结果。";
        }
        return "围绕该经历完成从需求理解到落地实现的整理，突出个人职责、技术方法、问题解决和结果价值。原始经历：" + rawText;
    }

    private List<String> extractKeywords(String text) {
        String normalized = value(text).toLowerCase(Locale.ROOT);
        List<String> dictionary = List.of(
                "vue", "react", "typescript", "javascript", "java", "spring boot", "mysql", "redis",
                "api", "component", "engineering", "performance", "project", "python", "sql",
                "data", "product", "prototype", "interview", "resume", "frontend", "backend",
                "前端", "后端", "项目", "接口", "组件", "工程化", "简历", "面试", "数据", "产品"
        );
        LinkedHashSet<String> keywords = new LinkedHashSet<>();
        for (String item : dictionary) {
            if (normalized.contains(item.toLowerCase(Locale.ROOT))) {
                keywords.add(item);
            }
        }
        if (keywords.isEmpty()) {
            Arrays.stream(normalized.split("[^a-z0-9\\u4e00-\\u9fa5]+"))
                    .map(String::trim)
                    .filter(word -> word.length() >= 3)
                    .limit(8)
                    .forEach(keywords::add);
        }
        return keywords.stream().limit(10).toList();
    }

    private List<ResumeMaterial> rankMaterials(List<ResumeMaterial> materials, List<String> keywords) {
        List<ResumeMaterial> ranked = materials.stream()
                .sorted(Comparator.comparingInt((ResumeMaterial material) -> materialScore(material, keywords)).reversed()
                        .thenComparing(ResumeMaterial::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        List<ResumeMaterial> matched = ranked.stream()
                .filter(material -> materialScore(material, keywords) > 0)
                .limit(3)
                .toList();
        if (!matched.isEmpty()) {
            return matched;
        }
        return ranked.stream().limit(3).toList();
    }

    private int materialScore(ResumeMaterial material, List<String> keywords) {
        String haystack = String.join(" ",
                value(material.getTitle()),
                value(material.getType()),
                value(material.getTags()),
                value(material.getRawText()),
                value(material.getOptimizedText())
        ).toLowerCase(Locale.ROOT);
        int score = 0;
        for (String keyword : keywords) {
            if (haystack.contains(keyword.toLowerCase(Locale.ROOT))) {
                score += 10;
            }
        }
        if ("project".equals(material.getType())) {
            score += 4;
        }
        if ("learning_task".equals(material.getSourceType())) {
            score += 2;
        }
        return score;
    }

    private int jdMatchScore(List<String> keywords, List<ResumeMaterial> matchedMaterials, List<ResumeMaterial> allMaterials) {
        if (allMaterials.isEmpty()) {
            return 35;
        }
        int score = 45 + Math.min(25, keywords.size() * 4) + Math.min(30, matchedMaterials.size() * 10);
        return Math.min(95, score);
    }

    private String jdSuggestions(List<String> keywords, List<ResumeMaterial> matchedMaterials, List<ResumeMaterial> allMaterials) {
        if (allMaterials.isEmpty()) {
            return "素材库为空，先完成成长任务或手动添加项目素材; 再重新分析 JD 获取匹配建议";
        }
        if (matchedMaterials.isEmpty()) {
            return "当前素材与 JD 关键词重合较少; 补充与 " + String.join(", ", keywords) + " 相关的项目结果和量化表达";
        }
        String titles = String.join(", ", matchedMaterials.stream().map(ResumeMaterial::getTitle).toList());
        return "优先使用素材：" + titles + "; 围绕 JD 关键词强化描述：" + String.join(", ", keywords) + "; 补充职责、动作、技术栈和结果数据";
    }

    private String buildResumeContent(String jobTitle, String companyName, List<String> keywords,
                                      List<ResumeMaterial> materials, boolean optimized) {
        StringBuilder builder = new StringBuilder();
        builder.append("目标岗位：").append(companyName).append(" / ").append(jobTitle).append("\n");
        builder.append("JD 关键词：").append(keywords.isEmpty() ? "暂未提取" : String.join(", ", keywords)).append("\n\n");
        builder.append("匹配经历\n");
        if (materials.isEmpty()) {
            builder.append("- 当前还没有可用简历素材。请先添加项目、技能、实习或学习任务沉淀素材。\n");
        } else {
            for (ResumeMaterial material : materials) {
                String text = optimized ? value(material.getOptimizedText()) : value(material.getRawText());
                builder.append("- ").append(value(material.getTitle())).append(": ").append(text).append("\n");
            }
        }
        builder.append("\n简历定制建议\n");
        if (optimized) {
            builder.append("- 优先使用与 JD 关键词重合度最高的素材。\n");
            builder.append("- 每条经历按照职责、动作、技术方法和可验证结果重写。\n");
            builder.append("- 将岗位关键词对应的证据前置到项目描述开头。\n");
        } else {
            builder.append("- 该草稿基于当前素材库生成，投递前仍需人工检查细节和真实性。\n");
        }
        return builder.toString();
    }

    private String text(Object value, String fallback) {
        String text = value == null ? "" : value.toString().trim();
        return text.isBlank() ? fallback : text;
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private Long longValue(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        try {
            long parsed = Long.parseLong(value.toString().trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private List<String> split(String value, String delimiter) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(value.split(delimiter))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private Map<String, Integer> parseSkillScores(String value) {
        Map<String, Integer> scores = new LinkedHashMap<>();
        split(value, ",").forEach(item -> {
            String[] parts = item.split(":", 2);
            if (parts.length == 2) {
                try {
                    scores.put(parts[0].trim(), Math.max(0, Math.min(100, Integer.parseInt(parts[1].trim()))));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed historical score entries.
                }
            }
        });
        return scores;
    }

    private String joinList(Object value) {
        if (value instanceof List<?> list) {
            return String.join(", ", list.stream().map(Object::toString).toList());
        }
        return text(value, "");
    }

    private record MaterialOptimization(String content, List<String> tags, String source, String model) {
    }
}
