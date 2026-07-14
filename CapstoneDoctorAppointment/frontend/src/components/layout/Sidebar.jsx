import { Link, useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../../hooks/useAuth";
import { ROLES } from "../../utils/constants";
import {
  HomeIcon,
  StethoscopeIcon,
  CalendarIcon,
  ClipboardIcon,
  UsersIcon,
  DashboardIcon,
  LogoutIcon
} from "../common/Icons";

const NAV_ITEMS = {
  [ROLES.PATIENT]: [
    { to: "/", label: "Home", Icon: HomeIcon },
    { to: "/doctors", label: "Doctors", Icon: StethoscopeIcon },
    { to: "/my-appointments", label: "My Appointments", Icon: CalendarIcon }
  ],
  [ROLES.DOCTOR]: [
    { to: "/", label: "Home", Icon: HomeIcon },
    { to: "/doctor/dashboard", label: "Dashboard", Icon: DashboardIcon },
    { to: "/doctor/availability", label: "Availability", Icon: CalendarIcon },
    { to: "/doctor/appointments", label: "Appointments", Icon: ClipboardIcon }
  ],
  [ROLES.ADMIN]: [
    { to: "/", label: "Home", Icon: HomeIcon },
    { to: "/admin/dashboard", label: "Dashboard", Icon: DashboardIcon },
    { to: "/admin/doctors", label: "Doctors", Icon: UsersIcon },
    {
      to: "/admin/cancellations",
      label: "Cancellation Requests",
      Icon: ClipboardIcon
    }
  ]
};

const ROLE_SUBTITLE = {
  [ROLES.PATIENT]: "Patient Portal",
  [ROLES.DOCTOR]: "Doctor Portal",
  [ROLES.ADMIN]: "Admin Portal"
};

const Sidebar = () => {
  const { user, logout } = useAuth();
  const { pathname } = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success("Logged out successfully.");
    navigate("/login");
  };

  const items = NAV_ITEMS[user.role] ?? [];
  const initial = user.email[0].toUpperCase();

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-badge">MC</div>
        <div>
          <p className="brand-name">MediCare</p>
          <p className="brand-subtitle">{ROLE_SUBTITLE[user.role]}</p>
        </div>
      </div>

      <nav className="sidebar-nav">
        {items.map(({ to, label, Icon }) => (
          <Link
            key={to}
            to={to}
            className={`sidebar-link ${pathname === to ? "active" : ""}`}
          >
            <Icon />
            <span>{label}</span>
          </Link>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-user">
          <div className="user-badge">{initial}</div>
          <p className="user-email">{user.email}</p>
        </div>
        <button
          type="button"
          className="sidebar-link logout-link"
          onClick={handleLogout}
        >
          <LogoutIcon />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
