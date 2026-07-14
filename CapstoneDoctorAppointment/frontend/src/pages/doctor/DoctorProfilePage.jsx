import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Button, Card, Col, Form, Row } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import { getMyProfile, updateMyProfile } from "../../api/doctorApi";
import { useFetch } from "../../hooks/useFetch";
import {
  feeRules,
  qualificationRules,
  requiredRule
} from "../../utils/validators";
import { formatDoctorName, getInitials } from "../../utils/format";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/doctor.css";

const DoctorProfilePage = () => {
  const { data: profile, loading, refetch } = useFetch(
    getMyProfile,
    "Could not load your profile."
  );

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm();
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (profile) {
      reset({
        qualification: profile.qualification,
        consultation_fee: profile.consultation_fee,
        clinic_address: profile.clinic_address
      });
    }
  }, [profile, reset]);

  const onSubmit = async (values) => {
    setSubmitting(true);

    try {
      const response = await updateMyProfile({
        qualification: values.qualification,
        consultation_fee: Number(values.consultation_fee),
        clinic_address: values.clinic_address
      });

      toast.success(response.data.message);
      await refetch();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not update your profile."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <PageHeader title="My Profile" />

      <div className="page-content">
        {loading && <Loader />}

        {!loading && !profile && (
          <p className="text-center text-muted-custom py-5">
            No profile found.
          </p>
        )}

        {!loading && profile && (
          <Card className="form-card">
            <Card.Body>
              <div className="profile-head">
                <div className="doctor-avatar">
                  {getInitials(profile.full_name)}
                </div>
                <div>
                  <h2 className="doctor-name">
                    {formatDoctorName(profile.full_name)}
                  </h2>
                  <p className="text-muted-custom mb-0">
                    {profile.specialization} · {profile.experience} yrs
                    experience
                  </p>
                </div>
              </div>

              <Row className="profile-readonly">
                <Col md={6}>
                  <p className="profile-label">Email</p>
                  <p className="profile-value">{profile.email}</p>
                </Col>
                <Col md={6}>
                  <p className="profile-label">Phone</p>
                  <p className="profile-value">{profile.phone}</p>
                </Col>
                <Col md={6}>
                  <p className="profile-label">License Number</p>
                  <p className="profile-value">{profile.license_number}</p>
                </Col>
                <Col md={6}>
                  <p className="profile-label">Specialization</p>
                  <p className="profile-value">{profile.specialization}</p>
                </Col>
              </Row>

              <Form onSubmit={handleSubmit(onSubmit)} noValidate>
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

                <Form.Group className="mb-3">
                  <Form.Label>Consultation Fee</Form.Label>
                  <Form.Control
                    type="number"
                    placeholder="500"
                    isInvalid={!!errors.consultation_fee}
                    {...register("consultation_fee", feeRules)}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.consultation_fee?.message}
                  </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label>Clinic Address</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={2}
                    placeholder="Clinic name, area, city"
                    isInvalid={!!errors.clinic_address}
                    {...register("clinic_address", requiredRule("Clinic address"))}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.clinic_address?.message}
                  </Form.Control.Feedback>
                </Form.Group>

                <Button type="submit" disabled={submitting}>
                  {submitting ? "Saving..." : "Save Changes"}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        )}
      </div>
    </>
  );
};

export default DoctorProfilePage;
