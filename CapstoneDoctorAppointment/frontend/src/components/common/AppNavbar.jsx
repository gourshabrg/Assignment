import { Container, Nav, Navbar } from "react-bootstrap";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../../hooks/useAuth";
import { getRoleLandingPath } from "../../utils/roleRoutes";

const AppNavbar = () => {
  const { user, logout } = useAuth();
  const { pathname } = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success("Logged out successfully.");
    navigate("/login");
  };

  return (
    <Navbar expand="lg" className="app-navbar" variant="light" sticky="top">
      <Container>
        <Navbar.Brand as={Link} to="/">
          MediCare
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="main-navbar" />
        <Navbar.Collapse id="main-navbar">
          <Nav className="ms-auto align-items-lg-center" activeKey={pathname}>
            <Nav.Link as={Link} to="/" eventKey="/">
              Home
            </Nav.Link>

            {!user && (
              <>
                <Nav.Link as={Link} to="/login" eventKey="/login">
                  Login
                </Nav.Link>
                <Nav.Link as={Link} to="/register" eventKey="/register">
                  Sign Up
                </Nav.Link>
              </>
            )}

            {user && (
              <>
                <Nav.Link
                  as={Link}
                  to={getRoleLandingPath(user.role)}
                  eventKey={getRoleLandingPath(user.role)}
                >
                  Dashboard
                </Nav.Link>
                <Nav.Link onClick={handleLogout} role="button">
                  Logout
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default AppNavbar;
