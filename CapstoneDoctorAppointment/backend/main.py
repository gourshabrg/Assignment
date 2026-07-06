from fastapi import FastAPI

app = FastAPI(
    title="Doctor Appointment Booking System"
)


@app.get("/")
async def home():
    """Simple health check so you know the API is running."""

    return {
        "message": "Doctor Appointment Booking System API is running.",
        "docs": "/docs"
    }
