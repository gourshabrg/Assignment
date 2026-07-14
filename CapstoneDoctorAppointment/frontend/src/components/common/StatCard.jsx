const StatCard = ({ label, value, Icon, onClick, active }) => {
  return (
    <button
      type="button"
      className={`stat-card ${active ? "active" : ""}`}
      onClick={onClick}
    >
      <div className="stat-icon">
        <Icon />
      </div>
      <div className="stat-text">
        <p className="stat-value">{value}</p>
        <p className="stat-label">{label}</p>
      </div>
    </button>
  );
};

export default StatCard;
