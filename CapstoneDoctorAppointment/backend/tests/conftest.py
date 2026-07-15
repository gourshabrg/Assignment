from datetime import date, datetime, timedelta
import pytest
import pytest_asyncio
from beanie import PydanticObjectId, init_beanie
from mongomock_motor import AsyncMongoMockClient
from enums.role_enum import UserRole
from enums.gender_enum import Gender
from enums.specialization_enum import Specialization
from enums.appointment_status_enum import AppointmentStatus
from models.user_model import User
from models.doctor_profile_model import DoctorProfile
from models.availability_slot_model import AvailabilitySlot
from models.appointment_model import Appointment
from models.payment_model import Payment

PATIENT_ID = "6a11111111111111111111a1"
DOCTOR_ID = "6a22222222222222222222b2"
SLOT_ID = "6a33333333333333333333c3"
APPOINTMENT_ID = "6a44444444444444444444d4"


@pytest_asyncio.fixture(autouse=True)
async def init_models():
    """Registers the Beanie models so they can be constructed."""

    client = AsyncMongoMockClient()

    await init_beanie(
        database=client.get_database("test"),
        document_models=[
            User,
            DoctorProfile,
            AvailabilitySlot,
            Appointment,
            Payment
        ]
    )


def _with_id(document, object_id: str):

    document.id = PydanticObjectId(object_id)

    return document


@pytest.fixture
def patient() -> User:
    return _with_id(
        User(
            full_name="Ravi Kumar",
            email="ravi@gmail.com",
            password="hashed",
            phone="9876543210",
            gender=Gender.MALE,
            dob=date(1995, 1, 1),
            role=UserRole.PATIENT,
            is_active=True
        ),
        PATIENT_ID
    )


@pytest.fixture
def doctor() -> User:
    return _with_id(
        User(
            full_name="Anita Sharma",
            email="anita@gmail.com",
            password="hashed",
            phone="9812345678",
            role=UserRole.DOCTOR,
            is_active=True
        ),
        DOCTOR_ID
    )


@pytest.fixture
def pending_doctor() -> User:
    return _with_id(
        User(
            full_name="New Doctor",
            email="new@gmail.com",
            password="hashed",
            phone="9812345679",
            role=UserRole.DOCTOR,
            is_active=False
        ),
        DOCTOR_ID
    )


@pytest.fixture
def admin() -> User:
    return _with_id(
        User(
            full_name="System Admin",
            email="admin@gmail.com",
            password="hashed",
            phone="9999999999",
            role=UserRole.ADMIN,
            is_active=True
        ),
        "6a55555555555555555555e5"
    )


@pytest.fixture
def doctor_profile() -> DoctorProfile:
    return DoctorProfile(
        user_id=DOCTOR_ID,
        qualification="MBBS",
        specialization=Specialization.CARDIOLOGIST,
        experience=8,
        license_number="LIC-1",
        consultation_fee=700,
        clinic_address="Pune"
    )


@pytest.fixture
def future_slot() -> AvailabilitySlot:
    return _with_id(
        AvailabilitySlot(
            doctor_id=DOCTOR_ID,
            slot_date=date.today() + timedelta(days=10),
            start_time="10:00:00",
            end_time="10:30:00",
            is_booked=False
        ),
        SLOT_ID
    )


@pytest.fixture
def booked_slot(future_slot) -> AvailabilitySlot:
    future_slot.is_booked = True

    return future_slot


@pytest.fixture
def appointment() -> Appointment:
    return _with_id(
        Appointment(
            patient_id=PATIENT_ID,
            doctor_id=DOCTOR_ID,
            slot_id=SLOT_ID,
            appointment_date=date.today() + timedelta(days=10),
            start_time="10:00:00",
            end_time="10:30:00",
            status=AppointmentStatus.BOOKED,
            created_at=datetime.utcnow()
        ),
        APPOINTMENT_ID
    )
