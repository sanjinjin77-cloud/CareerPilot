from pydantic import BaseModel


class CareerPlanCreate(BaseModel):
    stage: str = ""
    target_position: str = ""


class CareerPlanRead(BaseModel):
    id: int
    user_id: int
    stage: str
    target_position: str
    plan_content: str
    short_term_goals: list[str]
    mid_term_goals: list[str]
    long_term_goals: list[str]
    weekly_tasks: list[str]

    model_config = {"from_attributes": True}


class AbilityReportCreate(BaseModel):
    target_position: str = ""


class AbilityReportRead(BaseModel):
    id: int
    user_id: int
    target_position: str
    match_score: int
    strengths: list[str]
    weaknesses: list[str]
    suggestions: list[str]

    model_config = {"from_attributes": True}


class ResumeMaterialCreate(BaseModel):
    type: str
    title: str = ""
    original_content: str
    tags: list[str] = []


class ResumeMaterialUpdate(BaseModel):
    type: str = ""
    title: str = ""
    original_content: str = ""
    optimized_content: str = ""
    tags: list[str] = []


class ResumeMaterialRead(BaseModel):
    id: int
    user_id: int
    type: str
    title: str
    original_content: str
    optimized_content: str
    tags: list[str]

    model_config = {"from_attributes": True}


class JDAnalysisCreate(BaseModel):
    job_title: str = ""
    company_name: str = ""
    jd_content: str


class JDAnalysisRead(BaseModel):
    id: int
    user_id: int
    job_title: str
    company_name: str
    jd_content: str
    keywords: list[str]
    requirements: list[str]
    match_score: int
    recommended_materials: list[str]
    suggestions: list[str]

    model_config = {"from_attributes": True}


class InterviewQuestionCreate(BaseModel):
    job_title: str = ""
    jd_content: str = ""


class InterviewAnswerCreate(BaseModel):
    job_title: str = ""
    question: str
    answer: str


class InterviewRecordRead(BaseModel):
    id: int
    user_id: int
    job_title: str
    question: str
    answer: str
    feedback: str
    scores: dict

    model_config = {"from_attributes": True}
