import { Col, Container, Row } from "react-bootstrap";
import "../styles/home.css";

const SearchIcon = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none">
    <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="2" />
    <path
      d="M21 21l-4.3-4.3"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
    />
  </svg>
);

const CalendarIcon = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none">
    <rect
      x="3"
      y="5"
      width="18"
      height="16"
      rx="2"
      stroke="currentColor"
      strokeWidth="2"
    />
    <path
      d="M3 9h18M8 3v4M16 3v4"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
    />
    <path
      d="M9 14l2 2 4-4"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

const ClipboardIcon = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none">
    <rect
      x="5"
      y="4"
      width="14"
      height="17"
      rx="2"
      stroke="currentColor"
      strokeWidth="2"
    />
    <path
      d="M9 4h6v3H9z"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinejoin="round"
    />
    <path
      d="M8 12h8M8 16h5"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
    />
  </svg>
);

const HeroArt = () => (
  <svg viewBox="0 0 400 320" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="200" cy="160" r="140" fill="#e3f5f2" />
    <rect
      x="120"
      y="90"
      width="160"
      height="150"
      rx="16"
      fill="#ffffff"
      stroke="#16a596"
      strokeWidth="3"
    />
    <rect x="120" y="90" width="160" height="34" rx="16" fill="#16a596" />
    <circle cx="140" cy="107" r="5" fill="#ffffff" />
    <circle cx="158" cy="107" r="5" fill="#ffffff" />
    <path
      d="M200 140v56M172 168h56"
      stroke="#16a596"
      strokeWidth="10"
      strokeLinecap="round"
    />
    <path
      d="M150 252c0 18 20 32 44 32s44-14 44-32"
      stroke="#0f7a6f"
      strokeWidth="6"
      strokeLinecap="round"
    />
    <circle cx="238" cy="252" r="13" fill="#16a596" />
  </svg>
);

const HomePage = () => {
  return (
    <div>
      <section className="hero">
        <Container>
          <Row className="align-items-center g-4">
            <Col md={6}>
              <h1 className="hero-title">
                Your Health, <span className="accent">One Click Away</span>
              </h1>
              <p className="hero-subtitle">
                Find trusted doctors, view their availability, and book
                appointments online in just a few steps.
              </p>
            </Col>
            <Col md={6}>
              <div className="hero-art">
                <HeroArt />
              </div>
            </Col>
          </Row>
        </Container>
      </section>

      <section className="features">
        <Container>
          <h2 className="section-title">How It Works</h2>
          <Row className="g-4">
            <Col md={4}>
              <div className="feature-card">
                <div className="feature-icon">
                  <SearchIcon />
                </div>
                <h3 className="feature-title">Search Doctors</h3>
                <p className="feature-text">
                  Browse doctors by specialization and find the right expert for
                  your needs.
                </p>
              </div>
            </Col>
            <Col md={4}>
              <div className="feature-card">
                <div className="feature-icon">
                  <CalendarIcon />
                </div>
                <h3 className="feature-title">Book Appointments</h3>
                <p className="feature-text">
                  Pick an available time slot and confirm your appointment with
                  a quick, secure booking.
                </p>
              </div>
            </Col>
            <Col md={4}>
              <div className="feature-card">
                <div className="feature-icon">
                  <ClipboardIcon />
                </div>
                <h3 className="feature-title">Manage Visits</h3>
                <p className="feature-text">
                  Track your upcoming, completed, and cancelled appointments all
                  in one place.
                </p>
              </div>
            </Col>
          </Row>
        </Container>
      </section>

      <Container>
        <section className="cta-band">
          <h2>Ready to see a doctor?</h2>
          <p>Create your account and book your first appointment today.</p>
        </section>
      </Container>
    </div>
  );
};

export default HomePage;
