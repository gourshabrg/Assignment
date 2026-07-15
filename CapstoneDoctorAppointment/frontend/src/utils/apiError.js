export const getApiErrorMessage = (error, fallback = "Something went wrong.") => {
  if (!error?.response) {
    return "Cannot reach the server. Please try again.";
  }

  const detail = error.response.data?.detail;

  if (typeof detail === "string") {
    return detail;
  }

  // Pydantic validation errors arrive as a list and prefix the message
  // with "Value error, ".
  if (Array.isArray(detail) && detail.length > 0 && detail[0]?.msg) {
    return detail[0].msg.replace(/^Value error,\s*/, "");
  }

  return fallback;
};
