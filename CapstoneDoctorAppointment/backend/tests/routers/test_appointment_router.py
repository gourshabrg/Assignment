from schemas.response.api_response import ApiResponse
from tests.conftest import APPOINTMENT_ID, SLOT_ID


def _ok(data=None):
    return ApiResponse(success=True, message="ok", data=data)


class TestBookRoute:

    def test_book_appointment(self, client, mocker):
        mocked = mocker.patch(
            "routers.appointment_router.appointment_service.book_appointment",
            return_value=_ok()
        )

        response = client.post(
            "/appointments/book",
            json={"slot_id": SLOT_ID}
        )

        assert response.status_code == 201
        mocked.assert_awaited_once()

    def test_book_rejects_missing_slot_id(self, client):
        assert client.post("/appointments/book", json={}).status_code == 422


class TestListRoutes:

    def test_patient_appointments(self, client, mocker):
        mocker.patch(
            "routers.appointment_router.appointment_service"
            ".get_my_appointments",
            return_value=_ok(data=[])
        )

        assert client.get("/appointments/patient").status_code == 200

    def test_doctor_appointments(self, client, mocker):
        mocker.patch(
            "routers.appointment_router.appointment_service"
            ".get_doctor_appointments",
            return_value=_ok(data=[])
        )

        assert client.get("/appointments/doctor").status_code == 200


class TestCancelRoutes:

    def test_cancel_appointment(self, client, mocker):
        mocker.patch(
            "routers.appointment_router.appointment_service"
            ".cancel_appointment",
            return_value=_ok()
        )

        response = client.post(f"/appointments/{APPOINTMENT_ID}/cancel")

        assert response.status_code == 200

    def test_request_cancellation(self, client, mocker):
        mocked = mocker.patch(
            "routers.appointment_router.appointment_service"
            ".request_cancellation",
            return_value=_ok()
        )

        response = client.post(
            f"/appointments/{APPOINTMENT_ID}/request-cancellation",
            json={"reason": "Emergency surgery"}
        )

        assert response.status_code == 200
        mocked.assert_awaited_once()

    def test_request_cancellation_rejects_short_reason(self, client):
        response = client.post(
            f"/appointments/{APPOINTMENT_ID}/request-cancellation",
            json={"reason": "x"}
        )

        assert response.status_code == 422


class TestStatusRoute:

    def test_update_status(self, client, mocker):
        mocker.patch(
            "routers.appointment_router.appointment_service.update_status",
            return_value=_ok()
        )

        response = client.patch(
            f"/appointments/{APPOINTMENT_ID}/status",
            json={"status": "COMPLETED"}
        )

        assert response.status_code == 200

    def test_update_status_rejects_unknown_status(self, client):
        response = client.patch(
            f"/appointments/{APPOINTMENT_ID}/status",
            json={"status": "NOT_A_STATUS"}
        )

        assert response.status_code == 422
