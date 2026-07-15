import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { Button, Form } from "react-bootstrap";
import { toast } from "react-toastify";
import AuthLayout from "../../components/common/AuthLayout";
import PasswordInput from "../../components/common/PasswordInput";
import { resetPassword } from "../../api/authApi";
import { emailRules, passwordRules } from "../../utils/validators";
import { getApiErrorMessage } from "../../utils/apiError";

const ResetPasswordPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (values) => {
    setSubmitting(true);

    try {
      await resetPassword({
        email: values.email,
        new_password: values.new_password,
      });
      toast.success("Password reset successfully. Please log in.");
      navigate("/login");
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Password reset failed."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout
      title="Reset Password"
      subtitle="Enter your email and a new password.">
      <Form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Form.Group className="mb-3">
          <Form.Label>Email</Form.Label>
          <Form.Control
            type="email"
            placeholder="you@gmail.com"
            isInvalid={!!errors.email}
            {...register("email", emailRules)}
          />
          <Form.Control.Feedback type="invalid">
            {errors.email?.message}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3">
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

        <Button type="submit" className="w-100 mb-3" disabled={submitting}>
          {submitting ? "Resetting..." : "Reset Password"}
        </Button>
      </Form>
    </AuthLayout>
  );
};

export default ResetPasswordPage;
