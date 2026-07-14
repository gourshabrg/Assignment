import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import { payForAppointment } from "../../api/appointmentApi";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/doctor.css";

const PaymentPage = () => {
  const { appointmentId } = useParams();
  const navigate = useNavigate();
  const [paying, setPaying] = useState(false);

  const handlePay = async () => {
    setPaying(true);

    try {
      const response = await payForAppointment(appointmentId);

      toast.success(response.data.message);
      navigate("/doctors");
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Payment failed."));
    } finally {
      setPaying(false);
    }
  };

  return (
    <>
      <PageHeader title="Payment" />

      <div className="page-content">
        <section className="detail-card">
          <h2 className="section-heading">Complete your payment</h2>
          <p className="text-muted-custom">
            Your slot is reserved. Complete the payment to confirm your
            appointment.
          </p>

          <Button
            className="btn-book"
            onClick={handlePay}
            disabled={paying}
          >
            {paying ? "Processing..." : "Complete Payment"}
          </Button>
        </section>
      </div>
    </>
  );
};

export default PaymentPage;
