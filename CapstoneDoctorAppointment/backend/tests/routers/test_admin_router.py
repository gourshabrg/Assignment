from schemas.response.api_response import ApiResponse
from tests.conftest import APPOINTMENT_ID, DOCTOR_ID


def _ok(data=None):
    return ApiResponse(success=True, message="ok", data=data)


class TestDoctorManagementRoutes:

    def test_list_doctors(self, client, mocker):
        mocker.patch(
            "routers.admin_router.admin_service.list_doctors",
            return_value=_ok(data=[])
        )

        assert client.get("/admin/doctors").status_code == 200

    def test_verify_doctor(self, client, mocker):
        mocked = mocker.patch(
            "routers.admin_router.admin_service.verify_doctor",
            return_value=_ok()
        )

        response = client.patch(f"/admin/doctors/{DOCTOR_ID}/verify")

        assert response.status_code == 200
        mocked.assert_awaited_once()

    def test_reject_doctor(self, client, mocker):
        mocker.patch(
            "routers.admin_router.admin_service.reject_doctor",
            return_value=_ok()
        )

        assert client.patch(
            f"/admin/doctors/{DOCTOR_ID}/reject"
        ).status_code == 200


class TestDashboardRoute:

    def test_get_dashboard(self, client, mocker):
        mocker.patch(
            "routers.admin_router.admin_service.get_dashboard",
            return_value=_ok()
        )

        assert client.get("/admin/dashboard").status_code == 200


class TestCancellationRoutes:

    def test_list_cancellation_requests(self, client, mocker):
        mocker.patch(
            "routers.admin_router.appointment_service"
            ".list_cancellation_requests",
            return_value=_ok(data=[])
        )

        assert client.get("/admin/cancellation-requests").status_code == 200

    def test_approve_cancellation(self, client, mocker):
        mocked = mocker.patch(
            "routers.admin_router.appointment_service.approve_cancellation",
            return_value=_ok()
        )

        response = client.patch(
            f"/admin/cancellation-requests/{APPOINTMENT_ID}/approve"
        )

        assert response.status_code == 200
        mocked.assert_awaited_once()

    def test_reject_cancellation(self, client, mocker):
        mocker.patch(
            "routers.admin_router.appointment_service.reject_cancellation",
            return_value=_ok()
        )

        response = client.patch(
            f"/admin/cancellation-requests/{APPOINTMENT_ID}/reject"
        )

        assert response.status_code == 200
