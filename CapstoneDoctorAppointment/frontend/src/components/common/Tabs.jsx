const Tabs = ({ items, activeKey, onChange, className = "" }) => {
  return (
    <div className={`tab-strip ${className}`}>
      {items.map((item) => (
        <button
          type="button"
          key={item.key}
          className={`tab-item ${item.key === activeKey ? "selected" : ""}`}
          onClick={() => onChange(item.key)}
        >
          {item.label}
        </button>
      ))}
    </div>
  );
};

export default Tabs;
