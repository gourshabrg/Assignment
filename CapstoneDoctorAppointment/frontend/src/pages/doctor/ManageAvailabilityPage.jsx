import { useCallback, useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Button, Col, Form, Row } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import {
  createSlot,
  getMySlots,
  updateSlot,
  deleteSlot
} from "../../api/availabilityApi";
import { formatDate, formatTime } from "../../utils/format";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/appointment.css";

const today = () => new Date().toISOString().split("T")[0];

const MyAvailabilityPage = () => {
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm();

  const fetchSlots = useCallback(async () => {
    try {
      const response = await getMySlots();
      setSlots(response.data.data);
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not load slots."));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const load = async () => {
      await fetchSlots();
    };

    load();
  }, [fetchSlots]);

  const cancelEdit = () => {
    setEditingId(null);
    reset({ slot_date: "", start_time: "", end_time: "" });
  };

  const onSubmit = async (values) => {
    setSaving(true);

    try {
      const response = editingId
        ? await updateSlot(editingId, values)
        : await createSlot(values);

      toast.success(response.data.message);
      cancelEdit();
      await fetchSlots();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not save the slot."));
    } finally {
      setSaving(false);
    }
  };

  const startEdit = (slot) => {
    setEditingId(slot.id);
    reset({
      slot_date: slot.slot_date,
      start_time: slot.start_time,
      end_time: slot.end_time
    });
  };

  const handleDelete = async (slotId) => {
    try {
      const response = await deleteSlot(slotId);

      toast.success(response.data.message);
      await fetchSlots();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not delete the slot."));
    }
  };

  return (
    <>
      <PageHeader
        title="Manage Availability"
        badge={loading ? null : `${slots.length} slots`}
      />

      <div className="page-content">
        <Form onSubmit={handleSubmit(onSubmit)} className="filter-bar" noValidate>
          <h2 className="section-heading">
            {editingId ? "Update Slot" : "Add Slot"}
          </h2>

          <Row className="g-3">
            <Col md={4}>
              <Form.Label>Date</Form.Label>
              <Form.Control
                type="date"
                min={today()}
                isInvalid={!!errors.slot_date}
                {...register("slot_date", {
                  required: "Date is required.",
                  validate: (value) =>
                    value >= today() || "Date cannot be in the past."
                })}
              />
              <Form.Control.Feedback type="invalid">
                {errors.slot_date?.message}
              </Form.Control.Feedback>
            </Col>
            <Col md={4}>
              <Form.Label>Start time</Form.Label>
              <Form.Control
                type="time"
                step="1"
                isInvalid={!!errors.start_time}
                {...register("start_time", {
                  required: "Start time is required."
                })}
              />
              <Form.Control.Feedback type="invalid">
                {errors.start_time?.message}
              </Form.Control.Feedback>
            </Col>
            <Col md={4}>
              <Form.Label>End time</Form.Label>
              <Form.Control
                type="time"
                step="1"
                isInvalid={!!errors.end_time}
                {...register("end_time", {
                  required: "End time is required."
                })}
              />
              <Form.Control.Feedback type="invalid">
                {errors.end_time?.message}
              </Form.Control.Feedback>
            </Col>
          </Row>

          <div className="filter-actions">
            <Button type="submit" disabled={saving}>
              {saving ? "Saving..." : editingId ? "Update Slot" : "Add Slot"}
            </Button>
            {editingId && (
              <Button variant="outline-primary" onClick={cancelEdit}>
                Cancel
              </Button>
            )}
          </div>
        </Form>

        {loading && <Loader />}

        {!loading && slots.length === 0 && (
          <p className="text-center text-muted-custom py-5">
            No slots yet. Add one above.
          </p>
        )}

        {!loading &&
          slots.map((slot) => (
            <article key={slot.id} className="appointment-card">
              <div className="appointment-info">
                <h3 className="doctor-name">{formatDate(slot.slot_date)}</h3>
                <p className="doctor-line">
                  <span>
                    {formatTime(slot.start_time)} - {formatTime(slot.end_time)}
                  </span>
                </p>
              </div>

              <div className="appointment-actions">
                <span
                  className={`status-badge ${
                    slot.is_booked ? "status-booked" : "status-completed"
                  }`}
                >
                  {slot.is_booked ? "Booked" : "Available"}
                </span>

                {!slot.is_booked && (
                  <>
                    <Button
                      variant="outline-primary"
                      size="sm"
                      onClick={() => startEdit(slot)}
                    >
                      Edit
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => handleDelete(slot.id)}
                    >
                      Delete
                    </Button>
                  </>
                )}
              </div>
            </article>
          ))}
      </div>
    </>
  );
};

export default MyAvailabilityPage;
