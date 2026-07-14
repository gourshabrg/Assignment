import { useCallback, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import Tabs from "../../components/common/Tabs";
import AppointmentCard from "../../components/appointment/AppointmentCard";
import {
  getDoctorAppointments,
  updateAppointmentStatus,
  requestCancellation
} from "../../api/appointmentApi";
import {
  APPOINTMENT_STATUS,
  APPOINTMENT_TABS
} from "../../utils/constants";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/appointment.css";

const DoctorAppointmentsPage = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState(APPOINTMENT_TABS[0].key);
  const [busyId, setBusyId] = useState(null);
  const [cancelTarget, setCancelTarget] = useState(null);
  const [reason, setReason] = useState("");

  const fetchAppointments = useCallback(async () => {
    try {
      const response = await getDoctorAppointments();
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

  const handleStatus = async (appointmentId, status) => {
    setBusyId(appointmentId);

    try {
      const response = await updateAppointmentStatus(appointmentId, { status });

      toast.success(response.data.message);
      await fetchAppointments();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not update status."));
    } finally {
      setBusyId(null);
    }
  };

  const handleRequestCancellation = async () => {
    setBusyId(cancelTarget);

    try {
      const response = await requestCancellation(cancelTarget, { reason });

      toast.success(response.data.message);
      setCancelTarget(null);
      setReason("");
      await fetchAppointments();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not request cancellation."));
    } finally {
      setBusyId(null);
    }
  };

  const tab = APPOINTMENT_TABS.find((item) => item.key === activeTab);
  const visible = appointments.filter((appointment) =>
    tab.statuses.includes(appointment.status)
  );

  return (
    <>
      <PageHeader
        title="Appointments"
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
            const isBooked = appointment.status === APPOINTMENT_STATUS.BOOKED;
            const busy = busyId === appointment.id;

            return (
              <AppointmentCard
                key={appointment.id}
                appointment={appointment}
                name={appointment.patient_name}
                actions={
                  isBooked && (
                    <>
                      <Button
                        size="sm"
                        disabled={busy}
                        onClick={() =>
                          handleStatus(
                            appointment.id,
                            APPOINTMENT_STATUS.COMPLETED
                          )
                        }
                      >
                        Completed
                      </Button>
                      <Button
                        variant="outline-primary"
                        size="sm"
                        disabled={busy}
                        onClick={() =>
                          handleStatus(
                            appointment.id,
                            APPOINTMENT_STATUS.NOT_ATTENDED
                          )
                        }
                      >
                        Not Attended
                      </Button>
                      <Button
                        variant="outline-danger"
                        size="sm"
                        disabled={busy}
                        onClick={() => setCancelTarget(appointment.id)}
                      >
                        Request Cancel
                      </Button>
                    </>
                  )
                }
              />
            );
          })}
      </div>

      <Modal show={!!cancelTarget} onHide={() => setCancelTarget(null)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Request Cancellation</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p className="text-muted-custom">
            An admin must approve this cancellation. Please give a reason.
          </p>
          <Form.Control
            as="textarea"
            rows={3}
            value={reason}
            onChange={(event) => setReason(event.target.value)}
            placeholder="Reason for cancelling (min 3 characters)"
          />
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="outline-primary"
            onClick={() => setCancelTarget(null)}
          >
            Close
          </Button>
          <Button
            onClick={handleRequestCancellation}
            disabled={reason.trim().length < 3 || busyId === cancelTarget}
          >
            Submit Request
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default DoctorAppointmentsPage;
