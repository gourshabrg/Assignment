from datetime import datetime
from pydantic import BaseModel

class PaymentResponse(BaseModel):

    id: str

    appointment_id: str

    patient_id: str

    amount: float

    status: str

    created_at: datetime
