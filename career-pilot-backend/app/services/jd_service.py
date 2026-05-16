from app.services.ai_service import AIService


class JDService:
    def __init__(self, ai_service: AIService | None = None) -> None:
        self.ai_service = ai_service or AIService()
