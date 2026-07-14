import { Card, Container } from "react-bootstrap";

const AuthLayout = ({ title, subtitle, children }) => {
  return (
    <Container className="auth-container">
      <Card className="auth-card card-shadow">
        <Card.Body>
          <h1 className="auth-title">{title}</h1>
          {subtitle && <p className="auth-subtitle">{subtitle}</p>}
          {children}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AuthLayout;
