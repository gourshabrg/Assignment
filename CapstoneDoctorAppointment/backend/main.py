from contextlib import asynccontextmanager
from fastapi import FastAPI
from database.database import MongoDatabase
from middleware.cors import add_cors_middleware
from routers import (
    auth_router,
    doctor_router,
    availability_router,
    appointment_router,
    payment_router,
    admin_router
)
from startup.seed_admin import AdminSeeder


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Connects to MongoDB on startup, closes it on shutdown."""

    await MongoDatabase.connect()
    await AdminSeeder.seed_admin()

    yield

    await MongoDatabase.close()


app = FastAPI(
    title="Doctor Appointment Booking System",
    lifespan=lifespan
)

add_cors_middleware(app)

app.include_router(auth_router)
app.include_router(doctor_router)
app.include_router(availability_router)
app.include_router(appointment_router)
app.include_router(payment_router)
app.include_router(admin_router)


@app.get("/")
async def home():
    """Simple health check so you know the API is running."""

    return {
        "message": "Doctor Appointment Booking System API is running.",
        "docs": "/docs"
    }
