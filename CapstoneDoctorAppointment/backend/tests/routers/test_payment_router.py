from schemas.response.api_response import ApiResponse
from tests.conftest import APPOINTMENT_ID


class TestPaymentRoute:

    def test_pay_for_appointment(self, client, mocker):
        mocked = mocker.patch(
            "routers.payment_router.payment_service.pay_for_appointment",
            return_value=ApiResponse(success=True, message="ok", data=None)
        )

        response = client.post(f"/appointments/{APPOINTMENT_ID}/pay")

        assert response.status_code == 201
        mocked.assert_awaited_once()
