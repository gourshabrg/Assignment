import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import PaymentSuccessModal from "../../components/appointment/PaymentSuccessModal";
import { getMyAppointments, payForAppointment } from "../../api/appointmentApi";
import { formatDate, formatTime, formatDoctorName } from "../../utils/format";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/appointment.css";

const PaymentPage = () => {
  const { appointmentId } = useParams();
  const navigate = useNavigate();

  const [appointment, setAppointment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [paying, setPaying] = useState(false);
  const [paidAmount, setPaidAmount] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        const response = await getMyAppointments();
        const match = response.data.data.find(
          (item) => item.id === appointmentId
        );

        setAppointment(match ?? null);
      } catch (error) {
        toast.error(getApiErrorMessage(error, "Could not load appointment."));
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [appointmentId]);

  const handlePay = async () => {
    setPaying(true);

    try {
      const response = await payForAppointment(appointmentId);

      setPaidAmount(response.data.data.amount);
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Payment failed."));
    } finally {
      setPaying(false);
    }
  };

  if (loading) {
    return (
      <>
        <PageHeader title="Payment" />
        <div className="page-content">
          <Loader />
        </div>
      </>
    );
  }

  if (!appointment) {
    return (
      <>
        <PageHeader title="Payment" />
        <div className="page-content">
          <p className="text-center text-muted-custom py-5">
            Appointment not found.
          </p>
        </div>
      </>
    );
  }

  return (
    <>
      <PageHeader title="Payment" />

      <div className="page-content">
        <section className="detail-card">
          <h2 className="section-heading">Appointment Summary</h2>

          <dl className="payment-summary">
            <div>
              <dt>Doctor</dt>
              <dd>{formatDoctorName(appointment.doctor_name)}</dd>
            </div>
            <div>
              <dt>Date</dt>
              <dd>{formatDate(appointment.appointment_date)}</dd>
            </div>
            <div>
              <dt>Time</dt>
              <dd>
                {formatTime(appointment.start_time)} -{" "}
                {formatTime(appointment.end_time)}
              </dd>
            </div>
          </dl>

          <p className="text-muted-custom">
            Your slot is reserved. Complete the payment to confirm your
            appointment.
          </p>

          <Button className="btn-book" onClick={handlePay} disabled={paying}>
            {paying ? "Processing..." : "Complete Payment"}
          </Button>
        </section>
      </div>

      <PaymentSuccessModal
        show={paidAmount !== null}
        appointment={appointment}
        amount={paidAmount}
        onClose={() => navigate("/my-appointments")}
      />
    </>
  );
};

export default PaymentPage;
