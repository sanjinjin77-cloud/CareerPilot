from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.career import (
    AbilityReport,
    CareerPlan,
    InterviewRecord,
    JDAnalysis,
    ResumeMaterial,
)
from app.schemas.career import (
    AbilityReportCreate,
    CareerPlanCreate,
    InterviewAnswerCreate,
    InterviewQuestionCreate,
    JDAnalysisCreate,
    ResumeMaterialCreate,
    ResumeMaterialUpdate,
)


def create_career_plan(db: Session, user_id: int, payload: CareerPlanCreate) -> CareerPlan:
    target = payload.target_position or "前端开发工程师"
    plan = CareerPlan(
        user_id=user_id,
        stage=payload.stage or "能力建设期",
        target_position=target,
        plan_content=f"围绕{target}目标，优先完成技能补齐、项目积累、简历表达和面试训练。",
        short_term_goals=["补齐 TypeScript 基础", "重构 1 个课程项目", "沉淀 3 条简历素材"],
        mid_term_goals=["完成 1 个综合项目", "参与比赛或开源协作", "建立实习岗位投递清单"],
        long_term_goals=["形成可投递简历", "完成多轮模拟面试", "进入实习/秋招冲刺阶段"],
        weekly_tasks=["整理 1 条 STAR 项目素材", "分析 1 个真实 JD", "完成 1 次模拟面试复盘"],
    )
    db.add(plan)
    db.commit()
    db.refresh(plan)
    return plan


def list_career_plans(db: Session, user_id: int) -> list[CareerPlan]:
    return list(
        db.scalars(
            select(CareerPlan)
            .where(CareerPlan.user_id == user_id)
            .order_by(CareerPlan.created_at.desc())
        )
    )


def create_ability_report(
    db: Session, user_id: int, payload: AbilityReportCreate
) -> AbilityReport:
    target = payload.target_position or "前端开发工程师"
    report = AbilityReport(
        user_id=user_id,
        target_position=target,
        match_score=72,
        strengths=["Vue 基础", "页面开发能力", "课程项目经验"],
        weaknesses=["TypeScript", "工程化", "性能优化", "面试表达"],
        suggestions=[
            "TypeScript > 项目重构 > 简历优化 > 模拟面试",
            "将课程管理系统补充为前后端联调项目",
            "准备 3 个项目难点的 STAR 表达",
        ],
    )
    db.add(report)
    db.commit()
    db.refresh(report)
    return report


def list_ability_reports(db: Session, user_id: int) -> list[AbilityReport]:
    return list(
        db.scalars(
            select(AbilityReport)
            .where(AbilityReport.user_id == user_id)
            .order_by(AbilityReport.created_at.desc())
        )
    )


def optimize_material_content(material_type: str, content: str) -> str:
    trimmed = content.strip().rstrip("。！？")
    return (
        f"围绕{material_type}进行实践，完成{trimmed}，体现了需求理解、"
        "功能实现与问题复盘能力，可作为求职简历中的成长型经历。"
    )


def create_resume_material(
    db: Session, user_id: int, payload: ResumeMaterialCreate
) -> ResumeMaterial:
    material = ResumeMaterial(
        user_id=user_id,
        type=payload.type,
        title=payload.title or f"{payload.type}素材",
        original_content=payload.original_content,
        optimized_content=optimize_material_content(payload.type, payload.original_content),
        tags=payload.tags or [payload.type, "待完善"],
    )
    db.add(material)
    db.commit()
    db.refresh(material)
    return material


def list_resume_materials(db: Session, user_id: int) -> list[ResumeMaterial]:
    return list(
        db.scalars(
            select(ResumeMaterial)
            .where(ResumeMaterial.user_id == user_id)
            .order_by(ResumeMaterial.created_at.desc())
        )
    )


def update_resume_material(
    db: Session, user_id: int, material_id: int, payload: ResumeMaterialUpdate
) -> ResumeMaterial | None:
    material = db.get(ResumeMaterial, material_id)
    if material is None or material.user_id != user_id:
        return None

    data = payload.model_dump()
    for field, value in data.items():
        if value != "" and value != []:
            setattr(material, field, value)

    db.commit()
    db.refresh(material)
    return material


def delete_resume_material(db: Session, user_id: int, material_id: int) -> bool:
    material = db.get(ResumeMaterial, material_id)
    if material is None or material.user_id != user_id:
        return False
    db.delete(material)
    db.commit()
    return True


def analyze_jd(db: Session, user_id: int, payload: JDAnalysisCreate) -> JDAnalysis:
    content = payload.jd_content
    keywords = [
        keyword
        for keyword in ["Vue", "TypeScript", "工程化", "项目表达", "团队协作"]
        if keyword.lower() in content.lower()
    ]
    if not keywords:
        keywords = ["岗位关键词", "能力要求", "项目经验"]

    analysis = JDAnalysis(
        user_id=user_id,
        job_title=payload.job_title or "目标岗位",
        company_name=payload.company_name,
        jd_content=payload.jd_content,
        keywords=keywords,
        requirements=["掌握岗位核心技能", "具备项目实践经验", "能够清晰表达个人职责"],
        match_score=76 if "TypeScript" in keywords else 68,
        recommended_materials=["课程管理系统登录模块", "个人博客前端页面", "前后端联调项目"],
        suggestions=["突出与 JD 关键词相关的经历", "补充技术细节", "用 STAR 结构表达项目结果"],
    )
    db.add(analysis)
    db.commit()
    db.refresh(analysis)
    return analysis


def list_jd_analysis(db: Session, user_id: int) -> list[JDAnalysis]:
    return list(
        db.scalars(
            select(JDAnalysis)
            .where(JDAnalysis.user_id == user_id)
            .order_by(JDAnalysis.created_at.desc())
        )
    )


def generate_interview_question(payload: InterviewQuestionCreate) -> dict[str, str]:
    target = payload.job_title or "目标岗位"
    return {
        "job_title": target,
        "question": f"请介绍一个最能体现你胜任{target}的项目，并说明你解决了什么问题。",
    }


def score_interview_answer(
    db: Session, user_id: int, payload: InterviewAnswerCreate
) -> InterviewRecord:
    length_bonus = 8 if len(payload.answer) > 80 else 0
    scores = {
        "technical": min(92, 70 + length_bonus),
        "project": min(92, 76 + length_bonus),
        "logic": min(88, 72 if "首先" in payload.answer else 66),
        "star": min(86, 70 if "结果" in payload.answer else 63),
    }
    record = InterviewRecord(
        user_id=user_id,
        job_title=payload.job_title,
        question=payload.question,
        answer=payload.answer,
        feedback="回答具备项目背景和个人职责描述，建议继续补充量化结果、技术难点和复盘收获。",
        scores=scores,
    )
    db.add(record)
    db.commit()
    db.refresh(record)
    return record


def list_interview_records(db: Session, user_id: int) -> list[InterviewRecord]:
    return list(
        db.scalars(
            select(InterviewRecord)
            .where(InterviewRecord.user_id == user_id)
            .order_by(InterviewRecord.created_at.desc())
        )
    )
