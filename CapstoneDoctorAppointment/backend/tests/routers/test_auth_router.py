from schemas.response.api_response import ApiResponse


def _ok(data=None, message="ok"):
    return ApiResponse(success=True, message=message, data=data)


class TestRegisterRoutes:

    def test_register_patient(self, client, mocker):
        mocked = mocker.patch(
            "routers.auth_router.auth_service.register_patient",
            return_value=_ok()
        )

        response = client.post(
            "/auth/register/patient",
            json={
                "full_name": "Ravi Kumar",
                "email": "ravi@gmail.com",
                "password": "Passw0rd!",
                "phone": "9876543210",
                "gender": "MALE",
                "dob": "1995-01-01"
            }
        )

        assert response.status_code == 201
        mocked.assert_awaited_once()

    def test_register_patient_rejects_bad_payload(self, client):
        response = client.post(
            "/auth/register/patient",
            json={"email": "ravi@gmail.com"}
        )

        assert response.status_code == 422

    def test_register_doctor(self, client, mocker):
        mocked = mocker.patch(
            "routers.auth_router.auth_service.register_doctor",
            return_value=_ok()
        )

        response = client.post(
            "/auth/register/doctor",
            json={
                "full_name": "Anita Sharma",
                "email": "anita@gmail.com",
                "password": "Passw0rd!",
                "phone": "9812345678",
                "qualification": "MBBS",
                "specialization": "CARDIOLOGIST",
                "experience": 8,
                "license_number": "LIC-1",
                "consultation_fee": 700,
                "clinic_address": "Pune"
            }
        )

        assert response.status_code == 201
        mocked.assert_awaited_once()


class TestLoginRoute:

    def test_login(self, client, mocker):
        mocked = mocker.patch(
            "routers.auth_router.auth_service.login",
            return_value=_ok()
        )

        response = client.post(
            "/auth/login",
            json={"email": "ravi@gmail.com", "password": "Passw0rd!"}
        )

        assert response.status_code == 200
        mocked.assert_awaited_once()


class TestProtectedAuthRoutes:

    def test_profile(self, client, mocker):
        mocker.patch(
            "routers.auth_router.auth_service.get_profile",
            return_value=_ok()
        )

        assert client.get("/auth/profile").status_code == 200

    def test_logout(self, client, mocker):
        mocker.patch(
            "routers.auth_router.auth_service.logout",
            return_value=_ok()
        )

        assert client.post("/auth/logout").status_code == 200

    def test_change_password(self, client, mocker):
        mocker.patch(
            "routers.auth_router.auth_service.change_password",
            return_value=_ok()
        )

        response = client.post(
            "/auth/change-password",
            json={"old_password": "Passw0rd!", "new_password": "NewPass1!"}
        )

        assert response.status_code == 200

    def test_reset_password(self, client, mocker):
        mocker.patch(
            "routers.auth_router.auth_service.reset_password",
            return_value=_ok()
        )

        response = client.post(
            "/auth/reset-password",
            json={"email": "ravi@gmail.com", "new_password": "NewPass1!"}
        )

        assert response.status_code == 200
