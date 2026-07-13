import { formatStatus } from "../../utils/format";

const StatusBadge = ({ status }) => {
  return (
    <span className={`status-badge status-${status.toLowerCase()}`}>
      {formatStatus(status)}
    </span>
  );
};

export default StatusBadge;
