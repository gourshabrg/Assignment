import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { Button, ButtonGroup, Col, Form, Row } from "react-bootstrap";
import { toast } from "react-toastify";
import AuthLayout from "../../components/common/AuthLayout";
import PasswordInput from "../../components/common/PasswordInput";
import { registerPatient, registerDoctor } from "../../api/authApi";
import {
  ROLES,
  GENDER_OPTIONS,
  SPECIALIZATION_OPTIONS,
} from "../../utils/constants";
import {
  nameRules,
  emailRules,
  phoneRules,
  passwordRules,
  dobRules,
  qualificationRules,
  experienceRules,
  feeRules,
  requiredRule,
} from "../../utils/validators";
import { getApiErrorMessage } from "../../utils/apiError";

const RegisterPage = () => {
  const [role, setRole] = useState(ROLES.PATIENT);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ shouldUnregister: true });

  const isPatient = role === ROLES.PATIENT;

  const switchRole = (nextRole) => {
    if (nextRole !== role) {
      setRole(nextRole);
      reset();
    }
  };

  const onSubmit = async (values) => {
    setSubmitting(true);

    try {
      if (isPatient) {
        await registerPatient(values);
        toast.success("Registration successful. Please log in.");
      } else {
        await registerDoctor(values);
        toast.success(
          "Registration successful. Your account is pending admin approval.",
        );
      }

      navigate("/login");
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Registration failed."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout
      title="Create Account"
      subtitle="Register as a patient or doctor.">
      <ButtonGroup className="w-100 mb-4">
        <Button
          variant={isPatient ? "primary" : "outline-primary"}
          onClick={() => switchRole(ROLES.PATIENT)}>
          Patient
        </Button>
        <Button
          variant={!isPatient ? "primary" : "outline-primary"}
          onClick={() => switchRole(ROLES.DOCTOR)}>
          Doctor
        </Button>
      </ButtonGroup>

      <Form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Form.Group className="mb-3">
          <Form.Label>Full Name</Form.Label>
          <Form.Control
            placeholder="John Doe"
            isInvalid={!!errors.full_name}
            {...register("full_name", nameRules)}
          />
          <Form.Control.Feedback type="invalid">
            {errors.full_name?.message}
          </Form.Control.Feedback>
        </Form.Group>

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

        <Row>
          <Col md={6}>
            <Form.Group className="mb-3">
              <Form.Label>Phone</Form.Label>
              <Form.Control
                placeholder="9876543210"
                isInvalid={!!errors.phone}
                {...register("phone", phoneRules)}
              />
              <Form.Control.Feedback type="invalid">
                {errors.phone?.message}
              </Form.Control.Feedback>
            </Form.Group>
          </Col>
          <Col md={6}>
            <Form.Group className="mb-3">
              <Form.Label>Password</Form.Label>
              <PasswordInput
                placeholder="8-12 characters"
                isInvalid={!!errors.password}
                {...register("password", passwordRules)}
              />
              <Form.Control.Feedback type="invalid" className="d-block">
                {errors.password?.message}
              </Form.Control.Feedback>
            </Form.Group>
          </Col>
        </Row>

        {isPatient && (
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Gender</Form.Label>
                <Form.Select
                  isInvalid={!!errors.gender}
                  defaultValue=""
                  {...register("gender", requiredRule("Gender"))}>
                  <option value="" disabled>
                    Select gender
                  </option>
                  {GENDER_OPTIONS.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Form.Select>
                <Form.Control.Feedback type="invalid">
                  {errors.gender?.message}
                </Form.Control.Feedback>
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Date of Birth</Form.Label>
                <Form.Control
                  type="date"
                  isInvalid={!!errors.dob}
                  {...register("dob", dobRules)}
                />
                <Form.Control.Feedback type="invalid">
                  {errors.dob?.message}
                </Form.Control.Feedback>
              </Form.Group>
            </Col>
          </Row>
        )}

        {!isPatient && (
          <>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Qualification</Form.Label>
                  <Form.Control
                    placeholder="MBBS, MD"
                    isInvalid={!!errors.qualification}
                    {...register("qualification", qualificationRules)}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.qualification?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Specialization</Form.Label>
                  <Form.Select
                    isInvalid={!!errors.specialization}
                    defaultValue=""
                    {...register(
                      "specialization",
                      requiredRule("Specialization"),
                    )}>
                    <option value="" disabled>
                      Select specialization
                    </option>
                    {SPECIALIZATION_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </Form.Select>
                  <Form.Control.Feedback type="invalid">
                    {errors.specialization?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Experience (years)</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    isInvalid={!!errors.experience}
                    {...register("experience", {
                      ...experienceRules,
                      valueAsNumber: true,
                    })}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.experience?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Consultation Fee (₹)</Form.Label>
                  <Form.Control
                    type="number"
                    min="1"
                    isInvalid={!!errors.consultation_fee}
                    {...register("consultation_fee", {
                      ...feeRules,
                      valueAsNumber: true,
                    })}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.consultation_fee?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>License Number</Form.Label>
              <Form.Control
                placeholder="Medical license number"
                isInvalid={!!errors.license_number}
                {...register("license_number", requiredRule("License number"))}
              />
              <Form.Control.Feedback type="invalid">
                {errors.license_number?.message}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Clinic Address</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                placeholder="Clinic address"
                isInvalid={!!errors.clinic_address}
                {...register("clinic_address", requiredRule("Clinic address"))}
              />
              <Form.Control.Feedback type="invalid">
                {errors.clinic_address?.message}
              </Form.Control.Feedback>
            </Form.Group>
          </>
        )}

        <Button type="submit" className="w-100 mb-3" disabled={submitting}>
          {submitting ? "Creating account..." : "Register"}
        </Button>
      </Form>
    </AuthLayout>
  );
};

export default RegisterPage;
