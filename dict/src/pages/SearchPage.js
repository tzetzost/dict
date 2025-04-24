import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchTerm } from "../api/terms";
import axios from "axios";

export default function SearchPage() {
  const [term, setTerm] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const delayDebounce = setTimeout(() => {
      if (term.length > 0) {
        axios
          .get(`/api/terms/search?q=${term}`)
          .then((res) => setSuggestions(res.data))
          .catch(() => setSuggestions([]));
      } else {
        setSuggestions([]);
      }
    }, 300);

    return () => clearTimeout(delayDebounce);
  }, [term]);

  const handleSelect = (selectedTerm) => {
    setTerm(selectedTerm);
    setSuggestions([]);
    navigate(`/edit/${selectedTerm}`);
  };

  const handleSearch = async () => {
    try {
      await fetchTerm(term);
      navigate(`/edit/${term}`);
    } catch (err) {
      setError("Term not found");
    }
  };

  return (
    <div style={{ textAlign: "center", padding: "2rem" }}>
      <h2>Search for a Term</h2>
      <input
        value={term}
        onChange={(e) => {
          setTerm(e.target.value);
          setError(null);
        }}
        placeholder="Enter term"
        autoComplete="off"
      />
      <button onClick={handleSearch}>Search</button>
      {error && <p style={{ color: "red" }}>{error}</p>}

      {}
      {suggestions.length > 0 && (
        <ul style={{ listStyle: "none", padding: 0 }}>
            {suggestions.map((s) => (
              <li
                key={s.term}
                onClick={() => handleSelect(s.term)}
                style={{
                  cursor: "pointer",
                  padding: "0.5rem",
                  background: "#f1f1f1",
                  marginTop: "0.2rem",
                }}
              >
                <strong>{s.term}</strong>
                <br />
                <small>{s.description}</small>
              </li>
            ))}
        </ul>
      )}
    </div>
  );
}
