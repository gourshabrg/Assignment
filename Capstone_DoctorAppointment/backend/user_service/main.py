from fastapi import FastAPI
from shared.database.mongodb import database

app = FastAPI(
    title="User Service"
)


@app.get("/")
async def home():

    return {
        "message": "User Service Running"
    }


@app.get("/db-test")
async def db_test():

    collections = await database.list_collection_names()

    return {
        "collections": collections
    }