import { Button, Modal } from "react-bootstrap";
import { formatDate, formatTime, formatDoctorName, formatFee } from "../../utils/format";

const SuccessTick = () => (
  <svg className="success-tick" viewBox="0 0 52 52">
    <circle className="success-tick-circle" cx="26" cy="26" r="24" />
    <path className="success-tick-check" d="M14 27l8 8 16-16" />
  </svg>
);

const PaymentSuccessModal = ({ show, appointment, amount, onClose }) => {
  return (
    <Modal show={show} onHide={onClose} centered backdrop="static">
      <Modal.Body className="payment-success">
        <SuccessTick />

        <h2 className="payment-success-title">Payment Successful</h2>
        <p className="text-muted-custom">Your appointment is confirmed.</p>

        {appointment && (
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
            {amount != null && (
              <div>
                <dt>Amount Paid</dt>
                <dd>{formatFee(amount)}</dd>
              </div>
            )}
          </dl>
        )}

        <Button className="w-100" onClick={onClose}>
          View My Appointments
        </Button>
      </Modal.Body>
    </Modal>
  );
};

export default PaymentSuccessModal;
