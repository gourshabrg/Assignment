import { useState } from "react";
import { Button } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import Tabs from "../../components/common/Tabs";
import StatusBadge from "../../components/common/StatusBadge";
import { MailIcon, PhoneIcon } from "../../components/common/Icons";
import { listDoctors, verifyDoctor, rejectDoctor } from "../../api/adminApi";
import { useFetch } from "../../hooks/useFetch";
import {
  formatSpecialization,
  formatFee,
  getInitials
} from "../../utils/format";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/doctor.css";
import "../../styles/appointment.css";

const TABS = [
  { key: "pending", label: "Pending" },
  { key: "verified", label: "Verified" }
];

const AdminDoctorsPage = () => {
  const {
    data: doctors,
    loading,
    refetch
  } = useFetch(listDoctors, "Could not load doctors.");

  const [activeTab, setActiveTab] = useState(TABS[0].key);
  const [busyId, setBusyId] = useState(null);

  const handleAction = async (doctorId, action) => {
    setBusyId(doctorId);

    try {
      const response =
        action === "verify"
          ? await verifyDoctor(doctorId)
          : await rejectDoctor(doctorId);

      toast.success(response.data.message);
      await refetch();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not update the doctor."));
    } finally {
      setBusyId(null);
    }
  };

  const all = doctors ?? [];
  const visible = all.filter((doctor) =>
    activeTab === "verified" ? doctor.is_verified : !doctor.is_verified
  );

  return (
    <>
      <PageHeader
        title="Doctors"
        badge={loading ? null : `${all.length} total`}
      />

      <div className="page-content">
        <Tabs
          items={TABS}
          activeKey={activeTab}
          onChange={setActiveTab}
          className="appointment-tabs"
        />

        {loading && <Loader />}

        {!loading && visible.length === 0 && (
          <p className="text-center text-muted-custom py-5">
            No {activeTab} doctors.
          </p>
        )}

        {!loading &&
          visible.map((doctor) => {
            const busy = busyId === doctor.doctor_id;

            return (
              <article key={doctor.doctor_id} className="appointment-card">
                <div className="doctor-media">
                  <div className="doctor-avatar">
                    {getInitials(doctor.full_name)}
                  </div>
                  <span className="doctor-fee">
                    {formatFee(doctor.consultation_fee)}
                  </span>
                </div>

                <div className="appointment-info">
                  <h3 className="doctor-name">{doctor.full_name}</h3>
                  <p className="doctor-specialization">
                    {formatSpecialization(doctor.specialization)}
                  </p>
                  <p className="doctor-qualification">{doctor.qualification}</p>

                  <p className="doctor-line">
                    <MailIcon />
                    <span>{doctor.email}</span>
                  </p>
                  <p className="doctor-line">
                    <PhoneIcon />
                    <span>{doctor.phone}</span>
                  </p>
                </div>

                <div className="appointment-actions">
                  <StatusBadge
                    status={doctor.is_verified ? "VERIFIED" : "PENDING"}
                  />

                  {doctor.is_verified ? (
                    <Button
                      variant="outline-danger"
                      size="sm"
                      disabled={busy}
                      onClick={() => handleAction(doctor.doctor_id, "reject")}
                    >
                      Deactivate
                    </Button>
                  ) : (
                    <Button
                      size="sm"
                      disabled={busy}
                      onClick={() => handleAction(doctor.doctor_id, "verify")}
                    >
                      Verify
                    </Button>
                  )}
                </div>
              </article>
            );
          })}
      </div>
    </>
  );
};

export default AdminDoctorsPage;
