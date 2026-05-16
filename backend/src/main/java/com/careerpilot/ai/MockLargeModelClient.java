package com.careerpilot.ai;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockLargeModelClient implements LargeModelClient {

    @Override
    public AiAnalysisResult analyze(String message) {
        String text = message == null ? "" : message;
        AiAnalysisResult result = new AiAnalysisResult();

        String grade = parseGrade(text);
        String major = parseMajor(text);
        String targetRole = parseTargetRole(text);
        String skills = parseSkills(text);
        String weakSkills = parseWeakSkills(text);
        String currentStage = parseCurrentStage(grade);
        List<String> tasks = buildTasks(targetRole, weakSkills, text);

        result.setGrade(grade);
        result.setMajor(major);
        result.setTargetRole(targetRole);
        result.setCurrentStage(currentStage);
        result.setCurrentLevel(parseCurrentLevel(grade));
        result.setSkills(skills);
        result.setWeakSkills(weakSkills);
        result.setInterests(parseInterests(text));
        result.setCareerPreference(parseCareerPreference(text));
        result.setProfileSummary(buildProfileSummary(grade, major, targetRole, skills, weakSkills));
        result.setMatchScore(parseMatchScore(targetRole, skills, weakSkills));
        result.setStrengths(buildStrengths(skills));
        result.setWeaknesses(buildWeaknesses(weakSkills));
        result.setPriorities(String.join("、", tasks));
        result.setSkillScores(buildSkillScores(skills, weakSkills));
        result.setSuggestions(buildSuggestions(targetRole, weakSkills));
        result.setShortTermGoal(buildShortTermGoal(targetRole, weakSkills));
        result.setThreeMonthPlan(buildThreeMonthPlan(targetRole));
        result.setOneYearPlan(buildOneYearPlan(targetRole));
        result.setWeeklyTasks(String.join("、", tasks));
        result.setRoadmap(buildRoadmap(targetRole));
        result.setTasks(tasks);

        if (containsProjectSignal(text)) {
            result.setProjectDetected(true);
            result.setProjectTitle(parseProjectTitle(text));
            result.setProjectText(text);
            result.setProjectTags(buildProjectTags(targetRole, skills));
        }

        result.setReply(buildReply(result));
        return result;
    }

    private String parseGrade(String text) {
        if (text.contains("大一")) {
            return "大一";
        }
        if (text.contains("大二")) {
            return "大二";
        }
        if (text.contains("大三")) {
            return "大三";
        }
        if (text.contains("大四")) {
            return "大四";
        }
        return "";
    }

    private String parseMajor(String text) {
        String[] majors = {"软件工程", "计算机科学与技术", "人工智能", "数据科学与大数据技术", "网络工程", "信息管理与信息系统", "电子信息工程"};
        for (String major : majors) {
            if (text.contains(major)) {
                return major;
            }
        }
        if (text.contains("计算机")) {
            return "计算机类";
        }
        return "";
    }

    private String parseTargetRole(String text) {
        if (text.contains("前端") || text.contains("Vue") || text.contains("React")) {
            return "前端开发工程师";
        }
        if (text.contains("后端") || text.contains("Java") || text.contains("Spring")) {
            return "Java 后端开发工程师";
        }
        if (text.contains("产品经理") || text.contains("产品")) {
            return "产品经理";
        }
        if (text.contains("数据分析")) {
            return "数据分析师";
        }
        if (text.contains("算法")) {
            return "算法工程师";
        }
        if (text.contains("测试")) {
            return "测试开发工程师";
        }
        return "";
    }

    private String parseCurrentStage(String grade) {
        if ("大一".equals(grade)) {
            return "职业认知期";
        }
        if ("大二".equals(grade)) {
            return "能力建设期";
        }
        if ("大三".equals(grade)) {
            return "项目实战期";
        }
        if ("大四".equals(grade)) {
            return "求职冲刺期";
        }
        return "成长探索期";
    }

    private String parseCurrentLevel(String grade) {
        if ("大一".equals(grade)) {
            return "L1 职业探索";
        }
        if ("大二".equals(grade)) {
            return "L2 技能建设";
        }
        if ("大三".equals(grade)) {
            return "L3 项目积累";
        }
        if ("大四".equals(grade)) {
            return "L4 Offer 冲刺";
        }
        return "L1 职业探索";
    }

    private String parseSkills(String text) {
        List<String> skills = new ArrayList<>();
        addIfContains(skills, text, "Vue");
        addIfContains(skills, text, "React");
        addIfContains(skills, text, "TypeScript");
        addIfContains(skills, text, "JavaScript");
        addIfContains(skills, text, "Java");
        addIfContains(skills, text, "Spring Boot");
        addIfContains(skills, text, "MySQL");
        addIfContains(skills, text, "Python");
        addIfContains(skills, text, "数据结构");
        addIfContains(skills, text, "算法");
        return String.join("、", skills);
    }

    private String parseWeakSkills(String text) {
        List<String> weakSkills = new ArrayList<>();
        String[] candidates = {"TypeScript", "JavaScript", "Vue", "React", "Java", "Spring Boot", "MySQL", "算法", "数据结构", "项目工程化", "八股文"};
        for (String candidate : candidates) {
            if (text.contains(candidate) && (text.contains("弱") || text.contains("不熟") || text.contains("不会") || text.contains("欠缺") || text.contains("短板"))) {
                weakSkills.add(candidate);
            }
        }
        return String.join("、", weakSkills);
    }

    private String parseInterests(String text) {
        if (text.contains("前端")) {
            return "交互体验、Web 应用开发、工程化实践";
        }
        if (text.contains("后端")) {
            return "服务端开发、数据库设计、系统稳定性";
        }
        if (text.contains("产品")) {
            return "用户研究、需求分析、产品设计";
        }
        return "";
    }

    private String parseCareerPreference(String text) {
        if (text.contains("实习")) {
            return "优先准备实习机会";
        }
        if (text.contains("秋招") || text.contains("校招")) {
            return "优先准备校招求职";
        }
        return "以能力建设和项目积累为主";
    }

    private Integer parseMatchScore(String targetRole, String skills, String weakSkills) {
        int score = 60;
        if (!targetRole.isBlank()) {
            score += 8;
        }
        if (!skills.isBlank()) {
            score += Math.min(16, skills.split("、").length * 4);
        }
        if (!weakSkills.isBlank()) {
            score -= Math.min(12, weakSkills.split("、").length * 4);
        }
        return Math.max(35, Math.min(88, score));
    }

    private List<String> buildTasks(String targetRole, String weakSkills, String text) {
        List<String> tasks = new ArrayList<>();
        if (!weakSkills.isBlank()) {
            for (String skill : weakSkills.split("、")) {
                tasks.add("补强 " + skill);
            }
        }
        if (targetRole.contains("前端")) {
            tasks.add("整理一个前后端联调项目");
            tasks.add("完善前端工程化表达");
        } else if (targetRole.contains("后端")) {
            tasks.add("补充接口设计与数据库设计项目");
            tasks.add("整理 Spring Boot 项目亮点");
        } else {
            tasks.add("明确目标岗位能力模型");
            tasks.add("补充可写入简历的项目素材");
        }
        if (text.contains("面试")) {
            tasks.add("完成一次模拟面试复盘");
        }
        return tasks;
    }

    private String buildProfileSummary(String grade, String major, String targetRole, String skills, String weakSkills) {
        return "当前学生画像：" + valueOrDefault(grade, "年级待完善") + "，"
                + valueOrDefault(major, "专业待完善") + "，目标岗位为"
                + valueOrDefault(targetRole, "待明确") + "。已掌握能力："
                + valueOrDefault(skills, "待补充") + "；当前短板："
                + valueOrDefault(weakSkills, "待进一步诊断") + "。";
    }

    private String buildStrengths(String skills) {
        return skills.isBlank() ? "已开始进行职业探索，具备继续沉淀成长档案的基础。" : "已具备 " + skills + " 等基础能力。";
    }

    private String buildWeaknesses(String weakSkills) {
        return weakSkills.isBlank() ? "暂未识别到明确短板，建议继续补充课程、项目和求职目标信息。" : "需要重点补强：" + weakSkills + "。";
    }

    private String buildSkillScores(String skills, String weakSkills) {
        return "{\"基础技能\":70,\"项目表达\":62,\"岗位匹配\":68,\"短板补强\":" + (weakSkills.isBlank() ? 70 : 55) + "}";
    }

    private String buildSuggestions(String targetRole, String weakSkills) {
        String role = valueOrDefault(targetRole, "目标岗位");
        if (!weakSkills.isBlank()) {
            return "围绕" + role + "，本周优先补强 " + weakSkills + "，并把学习成果沉淀到项目和简历素材中。";
        }
        return "围绕" + role + "，继续补充项目经历、技能证据和面试表达材料。";
    }

    private String buildShortTermGoal(String targetRole, String weakSkills) {
        String role = valueOrDefault(targetRole, "目标岗位");
        if (!weakSkills.isBlank()) {
            return "完成 " + weakSkills + " 的基础补强，并形成一个能证明能力的项目片段，服务于" + role + "求职。";
        }
        return "明确" + role + "的核心能力要求，并补齐成长档案中的项目与技能证据。";
    }

    private String buildThreeMonthPlan(String targetRole) {
        return "第 1 个月补齐岗位基础能力；第 2 个月完成项目迭代和素材沉淀；第 3 个月进行简历优化、JD 匹配和模拟面试。";
    }

    private String buildOneYearPlan(String targetRole) {
        return "围绕" + valueOrDefault(targetRole, "目标岗位") + "持续完成课程学习、项目积累、实习准备和面试复盘，逐步形成可投递的求职竞争力。";
    }

    private String buildRoadmap(String targetRole) {
        return "职业认知 -> 技能学习 -> 项目积累 -> 简历优化 -> 模拟面试 -> Offer 冲刺";
    }

    private boolean containsProjectSignal(String text) {
        return text.contains("项目") || text.contains("课程设计") || text.contains("比赛") || text.contains("竞赛") || text.contains("实习");
    }

    private String parseProjectTitle(String text) {
        if (text.contains("课程设计")) {
            return "课程设计项目";
        }
        if (text.contains("比赛") || text.contains("竞赛")) {
            return "竞赛项目经历";
        }
        if (text.contains("实习")) {
            return "实习经历素材";
        }
        return "项目经历素材";
    }

    private String buildProjectTags(String targetRole, String skills) {
        List<String> tags = new ArrayList<>();
        if (!targetRole.isBlank()) {
            tags.add(targetRole);
        }
        if (!skills.isBlank()) {
            for (String skill : skills.split("、")) {
                tags.add(skill);
            }
        }
        return String.join("、", tags);
    }

    private String buildReply(AiAnalysisResult result) {
        String role = valueOrDefault(result.getTargetRole(), "目标岗位");
        return "我已经根据你的描述更新了成长档案，并为你生成了面向" + role
                + "的阶段规划。接下来建议优先完成：" + String.join("、", result.getTasks()) + "。";
    }

    private void addIfContains(List<String> values, String text, String keyword) {
        if (text.contains(keyword) && !values.contains(keyword)) {
            values.add(keyword);
        }
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
