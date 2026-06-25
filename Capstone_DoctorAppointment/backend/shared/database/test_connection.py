from shared.database.mongodb import database


async def test_connection():

    collections = await database.list_collection_names()

    print(collections)