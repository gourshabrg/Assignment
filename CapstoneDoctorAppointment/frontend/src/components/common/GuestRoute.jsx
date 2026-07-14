import { Navigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { getRoleLandingPath } from "../../utils/roleRoutes";

const GuestRoute = ({ children }) => {
  const { user } = useAuth();

  if (user) {
    return <Navigate to={getRoleLandingPath(user.role)} replace />;
  }

  return children;
};

export default GuestRoute;
