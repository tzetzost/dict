import React, { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { fetchTerm } from "../api/terms";
import axios from "axios";

export default function SearchPage() {
  const [term, setTerm] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const fetchSuggestions = (query) => {
    if (query.length > 0) {
      axios
        .get(`/api/terms/search?q=${query}`)
        .then((res) => setSuggestions(res.data))
        .catch(() => setSuggestions([]));
    } else {
      setSuggestions([]);
    }
  };

  useEffect(() => {
    const q = searchParams.get('q') || '';
    setTerm(q);
    setError(null);
  }, [searchParams]);

  useEffect(() => {
    const delayDebounce = setTimeout(() => {
      fetchSuggestions(term);
    }, 300);
    return () => clearTimeout(delayDebounce);
  }, [term]);

  const handleSelect = (selectedTerm) => {
    const selectedSuggestion = suggestions.find(s => s.term === selectedTerm);
    setTerm(selectedTerm);
    setSuggestions([]);
    navigate(`/edit/${selectedTerm}`, { state: { searchQuery: term, termData: selectedSuggestion } });
  };

  const handleSearch = async () => {
    try {
      const found = suggestions.find(s => s.term === term);
      if (found) {
        navigate(`/edit/${term}`, { state: { searchQuery: term, termData: found } });
      } else {
        await fetchTerm(term);
        navigate(`/edit/${term}`, { state: { searchQuery: term } });
      }
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
      <button style={{ marginLeft: "0.5rem" }} onClick={() => {
        setTerm("");
        setSuggestions([]);
        setError(null);
      }}>Clear</button>
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
      {suggestions.length === 0 && term.length > 0 && (
        <button style={{ marginTop: "1rem" }} onClick={() => navigate("/create", { state: { term } })}>
          Create
        </button>
      )}
    </div>
  );
}
