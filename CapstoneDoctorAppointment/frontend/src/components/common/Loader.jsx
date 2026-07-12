import { Spinner } from "react-bootstrap";

const Loader = () => {
  return (
    <div className="loader">
      <Spinner animation="border" variant="primary" />
    </div>
  );
};

export default Loader;
