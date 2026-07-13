import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Button, Col, Row } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import SlotBooking from "../../components/doctor/SlotBooking";
import { LocationIcon, BriefcaseIcon } from "../../components/common/Icons";
import { getDoctorById } from "../../api/doctorApi";
import { bookAppointment } from "../../api/appointmentApi";
import {
  formatSpecialization,
  formatFee,
  formatDoctorName,
  getInitials
} from "../../utils/format";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/doctor.css";

const DoctorDetailsPage = () => {
  const { doctorId } = useParams();
  const navigate = useNavigate();

  const [doctor, setDoctor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedSlotId, setSelectedSlotId] = useState(null);
  const [booking, setBooking] = useState(false);

  useEffect(() => {
    const fetchDoctor = async () => {
      try {
        const response = await getDoctorById(doctorId);
        setDoctor(response.data.data);
      } catch (error) {
        toast.error(getApiErrorMessage(error, "Could not load doctor."));
      } finally {
        setLoading(false);
      }
    };

    fetchDoctor();
  }, [doctorId]);

  const handleBook = async () => {
    if (!selectedSlotId) {
      toast.warning("Please select a time slot first.");

      return;
    }

    setBooking(true);

    try {
      const response = await bookAppointment({ slot_id: selectedSlotId });

      toast.success(response.data.message);
      navigate(`/payment/${response.data.data.id}`);
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not book this slot."));
    } finally {
      setBooking(false);
    }
  };

  if (loading) {
    return (
      <>
        <PageHeader title="Doctor Details" />
        <div className="page-content">
          <Loader />
        </div>
      </>
    );
  }

  if (!doctor) {
    return (
      <>
        <PageHeader title="Doctor Details" />
        <div className="page-content text-center py-5">
          <p className="text-muted-custom">Doctor not found.</p>
          <Link to="/doctors">Back to doctors</Link>
        </div>
      </>
    );
  }

  const availableSlots = doctor.available_slots.filter(
    (slot) => !slot.is_booked
  );
  const initials = getInitials(doctor.full_name);

  return (
    <>
      <PageHeader title="Doctor Details" />

      <div className="page-content">
        <section className="detail-card">
          <Row className="g-4 align-items-center">
            <Col md="auto">
              <div className="doctor-media">
                <div className="doctor-avatar">{initials}</div>
                <span className="doctor-fee">
                  {formatFee(doctor.consultation_fee)}
                </span>
              </div>
            </Col>

            <Col>
              <h2 className="detail-name">
                {formatDoctorName(doctor.full_name)}
              </h2>
              <p className="doctor-specialization">
                {formatSpecialization(doctor.specialization)}
              </p>
              <p className="doctor-qualification">{doctor.qualification}</p>

              <p className="doctor-line">
                <BriefcaseIcon />
                <span>{doctor.experience} yr experience</span>
              </p>
              <p className="doctor-line">
                <LocationIcon />
                <span>{doctor.clinic_address}</span>
              </p>
            </Col>

            <Col md="auto">
              <Button
                className="btn-book"
                onClick={handleBook}
                disabled={booking || availableSlots.length === 0}
              >
                {booking ? "Booking..." : "Book Online"}
              </Button>
            </Col>
          </Row>

          <hr className="booking-divider" />

          <SlotBooking
            slots={availableSlots}
            selectedSlotId={selectedSlotId}
            onSelectSlot={setSelectedSlotId}
          />
        </section>
      </div>
    </>
  );
};

export default DoctorDetailsPage;
