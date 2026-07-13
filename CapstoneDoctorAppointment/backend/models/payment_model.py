from datetime import datetime
from beanie import Document
from pydantic import Field

class Payment(Document):
    """MongoDB 'payments' collection."""

    appointment_id: str

    patient_id: str

    amount: float

    status: str = "SUCCESS"

    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "payments"
