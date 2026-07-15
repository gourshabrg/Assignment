from datetime import date, timedelta
from schemas.response.api_response import ApiResponse
from tests.conftest import SLOT_ID
FUTURE_DATE = str(date.today() + timedelta(days=10))


def _ok(data=None):
    return ApiResponse(success=True, message="ok", data=data)


class TestCreateSlotRoute:

    def test_create_slot(self, client, mocker):
        mocked = mocker.patch(
            "routers.availability_router.availability_slot_service.create_slot",
            return_value=_ok()
        )

        response = client.post(
            "/availability/slots",
            json={
                "slot_date": FUTURE_DATE,
                "start_time": "10:00:00",
                "end_time": "10:30:00"
            }
        )

        assert response.status_code == 201
        mocked.assert_awaited_once()

    def test_create_rejects_missing_fields(self, client):
        response = client.post(
            "/availability/slots",
            json={"slot_date": FUTURE_DATE}
        )

        assert response.status_code == 422


class TestMySlotsRoute:

    def test_get_my_slots(self, client, mocker):
        mocker.patch(
            "routers.availability_router.availability_slot_service.get_my_slots",
            return_value=_ok(data=[])
        )

        assert client.get("/availability/myslots").status_code == 200


class TestUpdateAndDeleteSlotRoutes:

    def test_update_slot(self, client, mocker):
        mocker.patch(
            "routers.availability_router.availability_slot_service.update_slot",
            return_value=_ok()
        )

        response = client.put(
            f"/availability/slots/{SLOT_ID}",
            json={
                "slot_date": FUTURE_DATE,
                "start_time": "11:00:00",
                "end_time": "11:30:00"
            }
        )

        assert response.status_code == 200

    def test_delete_slot(self, client, mocker):
        mocker.patch(
            "routers.availability_router.availability_slot_service.delete_slot",
            return_value=_ok()
        )

        assert client.delete(f"/availability/slots/{SLOT_ID}").status_code == 200
