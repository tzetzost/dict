import React, { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { fetchTerm } from "../api/terms";
import axios from "axios";

export default function SearchPage() {
  const [term, setTerm] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(() => {
    const stored = localStorage.getItem('searchPageSize');
    return stored ? parseInt(stored) : 50;
  });
  const [totalCount, setTotalCount] = useState(0);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const fetchSuggestions = React.useCallback((query, page = 0, pageSizeOverride = pageSize) => {
    if (query.length > 0) {
      axios
        .get(`/api/terms/search?q=${query}`, {
          headers: {
            'X-Page': page,
            'X-Page-Size': pageSizeOverride
          }
        })
        .then((res) => {
          setSuggestions(res.data);
          const total = res.headers['x-total-count'];
          setTotalCount(total ? parseInt(total) : 0);
        })
        .catch(() => {
          setSuggestions([]);
          setTotalCount(0);
        });
    } else {
      setSuggestions([]);
      setTotalCount(0);
    }
  }, [pageSize]);

  useEffect(() => {
    const q = searchParams.get('q') || '';
    setTerm(q);
    setError(null);
    setPage(0);
  }, [searchParams]);

  useEffect(() => {
    const delayDebounce = setTimeout(() => {
      fetchSuggestions(term, page, pageSize);
    }, 300);
    return () => clearTimeout(delayDebounce);
  }, [term, page, pageSize, fetchSuggestions]);

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
        setPage(0);
      }}>Clear</button>
      {error && <p style={{ color: "red" }}>{error}</p>}

      {}
      {suggestions.length > 0 && (
        <>
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
        <div style={{ marginTop: '1rem' }}>
          <button onClick={() => setPage(page - 1)} disabled={page === 0}>Prev</button>
          {Array.from({ length: Math.ceil(totalCount / pageSize) }, (_, i) => (
            <button
              key={i}
              style={{ margin: '0 2px', fontWeight: i === page ? 'bold' : 'normal' }}
              onClick={() => setPage(i)}
              disabled={i === page}
            >
              {i + 1}
            </button>
          ))}
          <button onClick={() => setPage(page + 1)} disabled={(page + 1) * pageSize >= totalCount}>Next</button>
        </div>
        </>
      )}
      {suggestions.length === 0 && term.length > 0 && (
        <button style={{ marginTop: "1rem" }} onClick={() => navigate("/create", { state: { term } })}>
          Create
        </button>
      )}
      <select
        style={{ marginTop: "1rem" }}
        value={pageSize}
        onChange={e => {
          const newSize = Number(e.target.value);
          setPageSize(newSize);
          localStorage.setItem('searchPageSize', newSize);
          setPage(0);
        }}
      >
        {[25, 50, 100].map(size => (
          <option key={size} value={size}>{size} per page</option>
        ))}
      </select>
    </div>
  );
}
