import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Button } from "react-bootstrap";
import { Link } from "react-router-dom";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import Tabs from "../../components/common/Tabs";
import AppointmentCard from "../../components/appointment/AppointmentCard";
import { APPOINTMENT_STATUS } from "../../utils/constants";
import { formatDoctorName } from "../../utils/format";
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
        <Tabs
          items={APPOINTMENT_TABS}
          activeKey={activeTab}
          onChange={setActiveTab}
          className="appointment-tabs"
        />

        {loading && <Loader />}

        {!loading && visible.length === 0 && (
          <p className="text-center text-muted-custom py-5">
            No {tab.label.toLowerCase()} appointments.
          </p>
        )}

        {!loading &&
          visible.map((appointment) => {
            const isPending =
              appointment.status === APPOINTMENT_STATUS.PENDING_PAYMENT;
            const canCancel =
              isPending || appointment.status === APPOINTMENT_STATUS.BOOKED;
            const busy = cancellingId === appointment.id;

            return (
              <AppointmentCard
                key={appointment.id}
                appointment={appointment}
                name={formatDoctorName(appointment.doctor_name)}
                actions={
                  <>
                    {isPending && (
                      <Button
                        as={Link}
                        to={`/payment/${appointment.id}`}
                        size="sm"
                      >
                        Pay Now
                      </Button>
                    )}
                    {canCancel && (
                      <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => handleCancel(appointment.id)}
                        disabled={busy}
                      >
                        {busy ? "Cancelling..." : "Cancel"}
                      </Button>
                    )}
                  </>
                }
              />
            );
          })}
      </div>
    </>
  );
};

export default MyAppointmentsPage;
