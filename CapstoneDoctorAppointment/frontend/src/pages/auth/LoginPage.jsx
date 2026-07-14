import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { Button, Form } from "react-bootstrap";
import { toast } from "react-toastify";
import AuthLayout from "../../components/common/AuthLayout";
import PasswordInput from "../../components/common/PasswordInput";
import { login as loginRequest } from "../../api/authApi";
import { useAuth } from "../../hooks/useAuth";
import { emailRules } from "../../utils/validators";
import { getApiErrorMessage } from "../../utils/apiError";
import { getRoleLandingPath } from "../../utils/roleRoutes";
import { decodeJwt } from "../../utils/jwt";

const LoginPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const { login } = useAuth();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (values) => {
    setSubmitting(true);

    try {
      const response = await loginRequest(values);
      const token = response.data.data.access_token;

      login(token);
      toast.success("Login successful.");

      const { role } = decodeJwt(token);
      navigate(getRoleLandingPath(role));
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Login failed."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout title="Login" subtitle="Welcome back. Please sign in.">
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

        <Form.Group className="mb-2">
          <Form.Label>Password</Form.Label>
          <PasswordInput
            placeholder="Enter your password"
            isInvalid={!!errors.password}
            {...register("password", { required: "Password is required." })}
          />
          <Form.Control.Feedback type="invalid" className="d-block">
            {errors.password?.message}
          </Form.Control.Feedback>
        </Form.Group>

        <div className="mb-3 text-end">
          <Link to="/reset-password">Forgot password?</Link>
        </div>

        <Button type="submit" className="w-100 mb-3" disabled={submitting}>
          {submitting ? "Signing in..." : "Login"}
        </Button>
      </Form>
    </AuthLayout>
  );
};

export default LoginPage;
