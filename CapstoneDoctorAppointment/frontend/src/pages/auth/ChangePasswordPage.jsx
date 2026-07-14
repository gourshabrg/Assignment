import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { Button, Card, Form } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import PasswordInput from "../../components/common/PasswordInput";
import { changePassword } from "../../api/authApi";
import { useAuth } from "../../hooks/useAuth";
import { passwordRules, requiredRule } from "../../utils/validators";
import { getRoleLandingPath } from "../../utils/roleRoutes";
import { getApiErrorMessage } from "../../utils/apiError";

const ChangePasswordPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (values) => {
    setSubmitting(true);

    try {
      const response = await changePassword({
        old_password: values.old_password,
        new_password: values.new_password
      });

      toast.success(response.data.message);
      navigate(getRoleLandingPath(user.role));
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not change password."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <PageHeader title="Change Password" />

      <div className="page-content">
        <Card className="form-card">
          <Card.Body>
            <Form onSubmit={handleSubmit(onSubmit)} noValidate>
              <Form.Group className="mb-3">
                <Form.Label>Current Password</Form.Label>
                <PasswordInput
                  placeholder="Enter your current password"
                  isInvalid={!!errors.old_password}
                  {...register("old_password", requiredRule("Current password"))}
                />
                <Form.Control.Feedback type="invalid" className="d-block">
                  {errors.old_password?.message}
                </Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-4">
                <Form.Label>New Password</Form.Label>
                <PasswordInput
                  placeholder="8-12 characters"
                  isInvalid={!!errors.new_password}
                  {...register("new_password", passwordRules)}
                />
                <Form.Control.Feedback type="invalid" className="d-block">
                  {errors.new_password?.message}
                </Form.Control.Feedback>
              </Form.Group>

              <Button type="submit" disabled={submitting}>
                {submitting ? "Saving..." : "Change Password"}
              </Button>
            </Form>
          </Card.Body>
        </Card>
      </div>
    </>
  );
};

export default ChangePasswordPage;
