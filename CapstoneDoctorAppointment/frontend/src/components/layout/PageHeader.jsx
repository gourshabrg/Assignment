const PageHeader = ({ title, badge }) => {
  return (
    <header className="page-header-bar">
      <h1 className="page-title">{title}</h1>
      {badge && <span className="count-pill">{badge}</span>}
    </header>
  );
};

export default PageHeader;
