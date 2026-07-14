import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { getApiErrorMessage } from "../utils/apiError";

// Loads data on mount and exposes refetch. `apiCall` must be a stable
// reference (a module-level api function).
export const useFetch = (apiCall, errorMessage) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  const refetch = useCallback(async () => {
    try {
      const response = await apiCall();
      setData(response.data.data);
    } catch (error) {
      toast.error(getApiErrorMessage(error, errorMessage));
    } finally {
      setLoading(false);
    }
  }, [apiCall, errorMessage]);

  useEffect(() => {
    const load = async () => {
      await refetch();
    };

    load();
  }, [refetch]);

  return { data, loading, refetch };
};
