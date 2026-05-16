from pydantic import BaseModel


class UserProfileBase(BaseModel):
    grade: str = ""
    education_level: str = ""
    school_type: str = ""
    major: str = ""
    current_stage: str = ""
    target_position: str = ""
    target_city: str = ""
    target_industry: str = ""
    languages: str = ""
    frameworks: str = ""
    projects: str = ""
    competitions: str = ""
    internships: str = ""
    english_level: str = ""
    certificates: str = ""
    preferences: list[str] = []


class UserProfileCreate(UserProfileBase):
    pass


class UserProfileUpdate(UserProfileBase):
    pass


class UserProfileRead(UserProfileBase):
    id: int
    user_id: int

    model_config = {"from_attributes": True}
