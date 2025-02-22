import { useEffect, useState } from "react";

const root = "http://localhost:8080";

function FetchData(path) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(root + path, {
          headers: { "Accept": "application/json" },
        });

        if (!response.ok) {
          const text = await response.text();
          throw new Error(`HTTP ${response.status}: ${text}`);
        }

        const jsonData = await response.json();
        setData(jsonData);
      } catch (err) {
        setError(`Fetch error: ${err.message}`);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [path]);

  return { data, loading, error };
}

export default FetchData;
