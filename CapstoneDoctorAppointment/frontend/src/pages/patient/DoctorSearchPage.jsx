import { useCallback, useEffect, useState } from "react";
import { Button, Col, Form, Row } from "react-bootstrap";
import { toast } from "react-toastify";
import PageHeader from "../../components/layout/PageHeader";
import Loader from "../../components/common/Loader";
import DoctorCard from "../../components/doctor/DoctorCard";
import { searchDoctors } from "../../api/doctorApi";
import { SPECIALIZATION_OPTIONS } from "../../utils/constants";
import { getApiErrorMessage } from "../../utils/apiError";
import "../../styles/doctor.css";

const EMPTY_FILTERS = {
  name: "",
  specialization: "",
  location: "",
  min_experience: "",
  max_fee: ""
};

const DoctorSearchPage = () => {
  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchDoctors = useCallback(async (activeFilters) => {
    // Send only the filters the user actually filled in.
    const params = Object.fromEntries(
      Object.entries(activeFilters).filter(([, value]) => value !== "")
    );

    try {
      const response = await searchDoctors(params);
      setDoctors(response.data.data);
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Could not load doctors."));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const loadInitialDoctors = async () => {
      await fetchDoctors(EMPTY_FILTERS);
    };

    loadInitialDoctors();
  }, [fetchDoctors]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleSearch = (event) => {
    event.preventDefault();
    setLoading(true);
    fetchDoctors(filters);
  };

  const handleReset = () => {
    setFilters(EMPTY_FILTERS);
    setLoading(true);
    fetchDoctors(EMPTY_FILTERS);
  };

  return (
    <>
      <PageHeader
        title="Doctors"
        badge={
          loading
            ? null
            : `${doctors.length} doctor${doctors.length === 1 ? "" : "s"} available`
        }
      />

      <div className="page-content">
        <Form onSubmit={handleSearch} className="filter-bar">
          <Row className="g-3">
            <Col md={3}>
              <Form.Label>Name</Form.Label>
              <Form.Control
                name="name"
                value={filters.name}
                onChange={handleChange}
                placeholder="Doctor name"
              />
            </Col>
            <Col md={3}>
              <Form.Label>Specialization</Form.Label>
              <Form.Select
                name="specialization"
                value={filters.specialization}
                onChange={handleChange}
              >
                <option value="">All</option>
                {SPECIALIZATION_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Form.Select>
            </Col>
            <Col md={2}>
              <Form.Label>Location</Form.Label>
              <Form.Control
                name="location"
                value={filters.location}
                onChange={handleChange}
                placeholder="City / area"
              />
            </Col>
            <Col md={2}>
              <Form.Label>Min experience</Form.Label>
              <Form.Control
                type="number"
                min="0"
                name="min_experience"
                value={filters.min_experience}
                onChange={handleChange}
                placeholder="Years"
              />
            </Col>
            <Col md={2}>
              <Form.Label>Max fee</Form.Label>
              <Form.Control
                type="number"
                min="1"
                name="max_fee"
                value={filters.max_fee}
                onChange={handleChange}
                placeholder="₹"
              />
            </Col>
          </Row>

          <div className="filter-actions">
            <Button type="submit">Search</Button>
            <Button variant="outline-primary" onClick={handleReset}>
              Reset
            </Button>
          </div>
        </Form>

        {loading && <Loader />}

        {!loading && doctors.length === 0 && (
          <p className="text-center text-muted-custom py-5">
            No doctors match your search.
          </p>
        )}

        {!loading &&
          doctors.map((doctor) => (
            <DoctorCard key={doctor.doctor_id} doctor={doctor} />
          ))}
      </div>
    </>
  );
};

export default DoctorSearchPage;
