from datetime import datetime
from beanie import Document
from pydantic import Field

class Payment(Document):
    """MongoDB 'payments' collection. Always SUCCESS since there's no
    real payment gateway -- SRS calls for a simulated payment only.
    """

    appointment_id: str

    patient_id: str

    amount: float

    status: str = "SUCCESS"

    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "payments"
