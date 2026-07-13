import pytest
from exceptions import AccessDeniedException
from security.role_checker import (
    patient_required,
    doctor_required,
    admin_required
)


class TestRoleChecker:
    """role-based access control."""

    async def test_allows_matching_role(self, patient):
        assert await patient_required(current_user=patient) is patient

    async def test_allows_doctor(self, doctor):
        assert await doctor_required(current_user=doctor) is doctor

    async def test_allows_admin(self, admin):
        assert await admin_required(current_user=admin) is admin

    @pytest.mark.parametrize("checker", [doctor_required, admin_required])
    async def test_rejects_patient_on_other_roles(self, checker, patient):
        with pytest.raises(AccessDeniedException):
            await checker(current_user=patient)

    async def test_rejects_doctor_on_admin_route(self, doctor):
        with pytest.raises(AccessDeniedException):
            await admin_required(current_user=doctor)
