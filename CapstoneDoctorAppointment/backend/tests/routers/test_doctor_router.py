from schemas.response.api_response import ApiResponse
from tests.conftest import DOCTOR_ID


def _ok(data=None):
    return ApiResponse(success=True, message="ok", data=data)


class TestDoctorProfileRoutes:

    def test_get_my_profile(self, client, mocker):
        mocker.patch(
            "routers.doctor_router.doctor_service.get_my_profile",
            return_value=_ok()
        )

        assert client.get("/doctors/profile").status_code == 200

    def test_update_my_profile(self, client, mocker):
        mocker.patch(
            "routers.doctor_router.doctor_service.update_my_profile",
            return_value=_ok()
        )

        response = client.put(
            "/doctors/profile",
            json={"consultation_fee": 900}
        )

        assert response.status_code == 200

    def test_update_rejects_invalid_fee(self, client):
        response = client.put(
            "/doctors/profile",
            json={"consultation_fee": -1}
        )

        assert response.status_code == 422


class TestDoctorSearchRoute:

    def test_search_without_filters(self, client, mocker):
        mocked = mocker.patch(
            "routers.doctor_router.doctor_service.search_doctors",
            return_value=_ok(data=[])
        )

        assert client.get("/doctors/search").status_code == 200
        mocked.assert_awaited_once()

    def test_search_passes_filters_from_schema(self, client, mocker):
        mocked = mocker.patch(
            "routers.doctor_router.doctor_service.search_doctors",
            return_value=_ok(data=[])
        )

        response = client.get(
            "/doctors/search",
            params={
                "name": "Anita",
                "specialization": "CARDIOLOGIST",
                "location": "Pune",
                "min_experience": 5,
                "max_fee": 1000
            }
        )

        assert response.status_code == 200
        assert mocked.call_args.kwargs["name"] == "Anita"
        assert mocked.call_args.kwargs["min_experience"] == 5

    def test_search_rejects_negative_experience(self, client):
        response = client.get(
            "/doctors/search",
            params={"min_experience": -5}
        )

        assert response.status_code == 422


class TestDoctorDetailsRoute:

    def test_get_doctor_by_id(self, client, mocker):
        mocker.patch(
            "routers.doctor_router.doctor_service.get_doctor_by_id",
            return_value=_ok()
        )

        assert client.get(f"/doctors/{DOCTOR_ID}").status_code == 200
