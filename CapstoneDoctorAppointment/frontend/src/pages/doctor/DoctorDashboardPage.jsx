import { useEffect, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import StatCard from "../../components/common/StatCard";
import AppointmentCard from "../../components/appointment/AppointmentCard";
import {
  CalendarIcon,
  ClipboardIcon,
  DashboardIcon,
  UsersIcon
} from "../../components/common/Icons";
import { getDoctorAppointments } from "../../api/appointmentApi";
import { APPOINTMENT_STATUS } from "../../utils/constants";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/appointment.css";

const todayDate = () => new Date().toISOString().split("T")[0];

// Each card owns the filter it applies to the appointment list below.
const CARDS = [
  {
    key: "today",
    label: "Today's Appointments",
    Icon: CalendarIcon,
    filter: (item) =>
      item.appointment_date === todayDate() &&
      item.status === APPOINTMENT_STATUS.BOOKED
  },
  {
    key: "upcoming",
    label: "Upcoming",
    Icon: DashboardIcon,
    filter: (item) => item.status === APPOINTMENT_STATUS.BOOKED
  },
  {
    key: "completed",
    label: "Completed",
    Icon: ClipboardIcon,
    filter: (item) => item.status === APPOINTMENT_STATUS.COMPLETED
  },
  {
    key: "cancelled",
    label: "Cancelled",
    Icon: UsersIcon,
    filter: (item) => item.status === APPOINTMENT_STATUS.CANCELLED
  }
];

const DoctorDashboardPage = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeKey, setActiveKey] = useState(CARDS[0].key);

  useEffect(() => {
    const load = async () => {
      try {
        const response = await getDoctorAppointments();
        setAppointments(response.data.data);
      } catch (error) {
        toast.error(getApiErrorMessage(error, "Could not load dashboard."));
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  const activeCard = CARDS.find((card) => card.key === activeKey);
  const visible = appointments.filter(activeCard.filter);

  return (
    <>
      <PageHeader title="Dashboard" />

      <div className="page-content">
        {loading && <Loader />}

        {!loading && (
          <>
            <Row className="g-4">
              {CARDS.map((card) => (
                <Col md={6} xl={3} key={card.key}>
                  <StatCard
                    label={card.label}
                    Icon={card.Icon}
                    value={appointments.filter(card.filter).length}
                    active={card.key === activeKey}
                    onClick={() => setActiveKey(card.key)}
                  />
                </Col>
              ))}
            </Row>

            <h2 className="section-heading dashboard-list-heading">
              {activeCard.label}
            </h2>

            {visible.length === 0 ? (
              <p className="text-center text-muted-custom py-5">
                No {activeCard.label.toLowerCase()}.
              </p>
            ) : (
              visible.map((appointment) => (
                <AppointmentCard
                  key={appointment.id}
                  appointment={appointment}
                  name={appointment.patient_name}
                />
              ))
            )}
          </>
        )}
      </div>
    </>
  );
};

export default DoctorDashboardPage;
