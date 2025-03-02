import { useEffect, useState } from "react";

const root = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';

function FetchData(path) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(root + path) // Example API
      .then((response) => {
        if (!response.ok) throw new Error("Network response was not ok");
        return response;
      }) 
      .then((data) => setData(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []); // Runs once on mount
  return response
}


export default FetchData;