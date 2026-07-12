import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import AppointmentCard from "../../components/appointment/AppointmentCard";
import {
  getMyAppointments,
  cancelAppointment
} from "../../api/appointmentApi";
import { APPOINTMENT_TABS } from "../../utils/constants";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/appointment.css";

const MyAppointmentsPage = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState(APPOINTMENT_TABS[0].key);
  const [cancellingId, setCancellingId] = useState(null);

  const fetchAppointments = useCallback(async () => {
    try {
      const response = await getMyAppointments();
      setAppointments(response.data.data);
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not load appointments."));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const load = async () => {
      await fetchAppointments();
    };

    load();
  }, [fetchAppointments]);

  const handleCancel = async (appointmentId) => {
    setCancellingId(appointmentId);

    try {
      const response = await cancelAppointment(appointmentId);

      toast.success(response.data.message);
      await fetchAppointments();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not cancel appointment."));
    } finally {
      setCancellingId(null);
    }
  };

  const tab = APPOINTMENT_TABS.find((item) => item.key === activeTab);
  const visible = appointments.filter((appointment) =>
    tab.statuses.includes(appointment.status)
  );

  return (
    <>
      <PageHeader
        title="My Appointments"
        badge={loading ? null : `${appointments.length} total`}
      />

      <div className="page-content">
        <div className="period-tabs appointment-tabs">
          {APPOINTMENT_TABS.map((item) => (
            <button
              type="button"
              key={item.key}
              className={`period-tab ${
                item.key === activeTab ? "selected" : ""
              }`}
              onClick={() => setActiveTab(item.key)}
            >
              {item.label}
            </button>
          ))}
        </div>

        {loading && <Loader />}

        {!loading && visible.length === 0 && (
          <p className="text-center text-muted-custom py-5">
            No {tab.label.toLowerCase()} appointments.
          </p>
        )}

        {!loading &&
          visible.map((appointment) => (
            <AppointmentCard
              key={appointment.id}
              appointment={appointment}
              onCancel={handleCancel}
              cancelling={cancellingId === appointment.id}
            />
          ))}
      </div>
    </>
  );
};

export default MyAppointmentsPage;
