import { Button } from "react-bootstrap";
import { Link } from "react-router-dom";
import { LocationIcon, BriefcaseIcon } from "../common/Icons";
import {
  formatSpecialization,
  formatFee,
  formatDoctorName,
  getInitials
} from "../../utils/format";

const DoctorCard = ({ doctor }) => {
  const initials = getInitials(doctor.full_name);

  return (
    <article className="doctor-card">
      <div className="doctor-media">
        <div className="doctor-avatar">{initials}</div>
        <span className="doctor-fee">{formatFee(doctor.consultation_fee)}</span>
      </div>

      <div className="doctor-info">
        <h3 className="doctor-name">{formatDoctorName(doctor.full_name)}</h3>
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
      </div>

      <div className="doctor-action">
        <Button as={Link} to={`/doctors/${doctor.doctor_id}`}>
          Book Online
        </Button>
      </div>
    </article>
  );
};

export default DoctorCard;
