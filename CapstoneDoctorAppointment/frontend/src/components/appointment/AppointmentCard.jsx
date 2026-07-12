import { CalendarIcon } from "../common/Icons";
import { formatDate, formatTime, formatStatus, getInitials } from "../../utils/format";

// Shared by the patient and doctor views: `name` is whichever party the
// viewer cares about, `actions` are the buttons for that view.
const AppointmentCard = ({ appointment, name, actions }) => {
  return (
    <article className="appointment-card">
      <div className="doctor-avatar">{getInitials(name)}</div>

      <div className="appointment-info">
        <h3 className="doctor-name">{name}</h3>

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
        <span
          className={`status-badge status-${appointment.status.toLowerCase()}`}
        >
          {formatStatus(appointment.status)}
        </span>
        {actions}
      </div>
    </article>
  );
};

export default AppointmentCard;
