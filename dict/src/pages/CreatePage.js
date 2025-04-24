import React, { useState, useRef, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { createTerm } from "../api/terms";

export default function CreatePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const initialTerm = location.state && location.state.term ? location.state.term : "";
  const [term, setTerm] = useState(initialTerm);
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");
  const descInputRef = useRef(null);

  useEffect(() => {
    if (descInputRef.current) {
      descInputRef.current.focus();
    }
  }, []);

  const handleSave = async () => {
    try {
      await createTerm({ term, description });
      navigate(`/?q=${encodeURIComponent(term)}`);
    } catch (error) {
      if (error.response && error.response.status === 409) {
        setMessage("Term already exists");
      } else {
        setMessage("Failed to create term");
      }
    }
  };

  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") {
        navigate(`/?q=${encodeURIComponent(term)}`);
      }
    };
    window.addEventListener("keydown", handleEsc);
    return () => window.removeEventListener("keydown", handleEsc);
  }, [navigate, term]);

  return (
    <div style={{ padding: "2rem" }}>
      <h2>Create New Term</h2>
      <input
        type="text"
        value={term}
        onChange={e => setTerm(e.target.value)}
        style={{ marginBottom: "1rem", width: "300px" }}
      />
      <br />
      <textarea
        ref={descInputRef}
        rows="6"
        cols="50"
        value={description}
        onChange={e => setDescription(e.target.value)}
        placeholder="Enter description"
      />
      <br />
      <button onClick={handleSave}>Save</button>
      <button style={{ marginLeft: "0.5rem" }} onClick={() => navigate(`/?q=${encodeURIComponent(term)}`)}>
        Cancel
      </button>
      {message && <p>{message}</p>}
    </div>
  );
}
