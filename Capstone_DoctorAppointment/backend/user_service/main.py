from contextlib import asynccontextmanager
from fastapi import FastAPI

from user_service.database.database import MongoDatabase
from user_service.routers import (
    auth_router,
    doctor_router,
    availability_router,
    appointment_router
)
from user_service.startup.seed_admin import AdminSeeder



@asynccontextmanager
async def lifespan(app: FastAPI):

    await MongoDatabase.connect()
    await AdminSeeder.seed_admin()

    yield

    await MongoDatabase.close()


app = FastAPI(
    title="Doctor Appointment Booking System",
    lifespan=lifespan
)

app.include_router(auth_router)
app.include_router(doctor_router)
app.include_router(availability_router)
app.include_router(appointment_router)