import { Link } from "react-router-dom";

const NotFoundPage = () => {
  return (
    <div className="text-center py-5">
      <h1>404</h1>
      <p>Page not found.</p>
      <Link to="/">Back to home</Link>
    </div>
  );
};

export default NotFoundPage;
