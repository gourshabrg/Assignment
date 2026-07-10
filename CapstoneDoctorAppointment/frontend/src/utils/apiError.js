export const getApiErrorMessage = (error, fallback = "Something went wrong.") => {
  const detail = error?.response?.data?.detail;

  if (typeof detail === "string") {
    return detail;
  }

  if (Array.isArray(detail) && detail.length > 0) {
    return detail[0].msg || fallback;
  }

  return fallback;
};
