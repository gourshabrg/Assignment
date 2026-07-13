import { useState } from "react";
import { Button } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import AppointmentCard from "../../components/appointment/AppointmentCard";
import {
  getCancellationRequests,
  approveCancellation,
  rejectCancellation
} from "../../api/adminApi";
import { useFetch } from "../../hooks/useFetch";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/appointment.css";

const AdminCancellationsPage = () => {
  const {
    data: requests,
    loading,
    refetch
  } = useFetch(getCancellationRequests, "Could not load requests.");

  const [busyId, setBusyId] = useState(null);

  const handleAction = async (appointmentId, action) => {
    setBusyId(appointmentId);

    try {
      const response =
        action === "approve"
          ? await approveCancellation(appointmentId)
          : await rejectCancellation(appointmentId);

      toast.success(response.data.message);
      await refetch();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not update the request."));
    } finally {
      setBusyId(null);
    }
  };

  const all = requests ?? [];

  return (
    <>
      <PageHeader
        title="Cancellation Requests"
        badge={loading ? null : `${all.length} pending`}
      />

      <div className="page-content">
        {loading && <Loader />}

        {!loading && all.length === 0 && (
          <p className="text-center text-muted-custom py-5">
            No pending cancellation requests.
          </p>
        )}

        {!loading &&
          all.map((appointment) => {
            const busy = busyId === appointment.id;

            return (
              <AppointmentCard
                key={appointment.id}
                appointment={appointment}
                name={`Dr ${appointment.doctor_name}`}
                actions={
                  <>
                    <Button
                      size="sm"
                      disabled={busy}
                      onClick={() => handleAction(appointment.id, "approve")}
                    >
                      Approve
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      disabled={busy}
                      onClick={() => handleAction(appointment.id, "reject")}
                    >
                      Reject
                    </Button>
                  </>
                }
              />
            );
          })}
      </div>
    </>
  );
};

export default AdminCancellationsPage;
