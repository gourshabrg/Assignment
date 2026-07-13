import { Col, Row } from "react-bootstrap";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import StatCard from "../../components/common/StatCard";
import StatusBadge from "../../components/common/StatusBadge";
import {
  CalendarIcon,
  ClipboardIcon,
  DashboardIcon,
  StethoscopeIcon,
  UsersIcon
} from "../../components/common/Icons";
import { getDashboard } from "../../api/adminApi";
import { useFetch } from "../../hooks/useFetch";
import { formatDate } from "../../utils/format";
import "../../styles/appointment.css";

const AdminDashboardPage = () => {
  const { data: dashboard, loading } = useFetch(
    getDashboard,
    "Could not load dashboard."
  );

  const stats = dashboard
    ? [
        {
          label: "Total Doctors",
          value: dashboard.total_doctors,
          Icon: StethoscopeIcon
        },
        {
          label: "Total Patients",
          value: dashboard.total_patients,
          Icon: UsersIcon
        },
        {
          label: "Total Appointments",
          value: dashboard.total_appointments,
          Icon: DashboardIcon
        },
        {
          label: "Completed",
          value: dashboard.completed_appointments,
          Icon: ClipboardIcon
        },
        {
          label: "Cancelled",
          value: dashboard.cancelled_appointments,
          Icon: CalendarIcon
        }
      ]
    : [];

  return (
    <>
      <PageHeader title="Dashboard" />

      <div className="page-content">
        {loading && <Loader />}

        {!loading && !dashboard && (
          <p className="text-center text-muted-custom py-5">
            No dashboard data.
          </p>
        )}

        {!loading && dashboard && (
          <>
            <Row className="g-4">
              {stats.map((stat) => (
                <Col md={6} xl={3} key={stat.label}>
                  <StatCard
                    label={stat.label}
                    value={stat.value}
                    Icon={stat.Icon}
                  />
                </Col>
              ))}
            </Row>

            <h2 className="section-heading dashboard-list-heading">
              Recent Appointments
            </h2>

            {dashboard.recent_appointments.length === 0 ? (
              <p className="text-center text-muted-custom py-5">
                No appointments yet.
              </p>
            ) : (
              dashboard.recent_appointments.map((appointment) => (
                <article key={appointment.id} className="appointment-card">
                  <div className="appointment-info">
                    <h3 className="doctor-name">
                      {appointment.patient_name} with Dr{" "}
                      {appointment.doctor_name}
                    </h3>
                    <p className="doctor-line">
                      <CalendarIcon />
                      <span>{formatDate(appointment.appointment_date)}</span>
                    </p>
                  </div>

                  <div className="appointment-actions">
                    <StatusBadge status={appointment.status} />
                  </div>
                </article>
              ))
            )}
          </>
        )}
      </div>
    </>
  );
};

export default AdminDashboardPage;
