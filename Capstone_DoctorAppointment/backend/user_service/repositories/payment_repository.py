from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from user_service.models.payment_model import Payment


class PaymentRepository:

    async def create(
        self,
        payment: Payment,
        session: AsyncIOMotorClientSession | None = None
    ) -> Payment:

        await payment.insert(session=session)

        return payment

    async def get_by_appointment_id(
        self,
        appointment_id: str
    ) -> Optional[Payment]:

        return await Payment.find_one(
            Payment.appointment_id == appointment_id
        )
