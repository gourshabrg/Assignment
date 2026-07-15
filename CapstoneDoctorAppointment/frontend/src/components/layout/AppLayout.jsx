import { useLocation } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import AppNavbar from "../common/AppNavbar";
import Sidebar from "./Sidebar";
import "../../styles/layout.css";

// Public pages (landing, auth) keep the top navbar; the signed-in app
// pages use the sidebar shell.
const NAVBAR_ROUTES = ["/", "/login", "/register", "/reset-password"];

const AppLayout = ({ children }) => {
  const { user } = useAuth();
  const { pathname } = useLocation();

  if (!user || NAVBAR_ROUTES.includes(pathname)) {
    return (
      <>
        <AppNavbar />
        {children}
      </>
    );
  }

  return (
    <div className="app-shell">
      <Sidebar />
      <main className="app-main">{children}</main>
    </div>
  );
};

export default AppLayout;
