import { Button } from "react-bootstrap";
import { Link } from "react-router-dom";
import { CalendarIcon } from "../common/Icons";
import { APPOINTMENT_STATUS } from "../../utils/constants";
import {
  formatDate,
  formatTime,
  formatStatus,
  formatDoctorName,
  getInitials
} from "../../utils/format";

const AppointmentCard = ({ appointment, onCancel, cancelling }) => {
  const { status } = appointment;
  const isPendingPayment = status === APPOINTMENT_STATUS.PENDING_PAYMENT;
  const canCancel =
    status === APPOINTMENT_STATUS.BOOKED || isPendingPayment;

  return (
    <article className="appointment-card">
      <div className="doctor-avatar">
        {getInitials(appointment.doctor_name ?? "Doctor")}
      </div>

      <div className="appointment-info">
        <h3 className="doctor-name">
          {formatDoctorName(appointment.doctor_name)}
        </h3>

        <p className="doctor-line">
          <CalendarIcon />
          <span>
            {formatDate(appointment.appointment_date)} ·{" "}
            {formatTime(appointment.start_time)} -{" "}
            {formatTime(appointment.end_time)}
          </span>
        </p>

        {appointment.cancellation_reason && (
          <p className="cancel-reason">
            Reason: {appointment.cancellation_reason}
          </p>
        )}
      </div>

      <div className="appointment-actions">
        <span className={`status-badge status-${status.toLowerCase()}`}>
          {formatStatus(status)}
        </span>

        {isPendingPayment && (
          <Button as={Link} to={`/payment/${appointment.id}`} size="sm">
            Pay Now
          </Button>
        )}

        {canCancel && (
          <Button
            variant="outline-danger"
            size="sm"
            onClick={() => onCancel(appointment.id)}
            disabled={cancelling}
          >
            {cancelling ? "Cancelling..." : "Cancel"}
          </Button>
        )}
      </div>
    </article>
  );
};

export default AppointmentCard;
