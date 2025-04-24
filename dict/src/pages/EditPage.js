import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { fetchTerm, updateTerm } from "../api/terms";

export default function EditPage() {
  const { term } = useParams();
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const previousQuery = location.state && location.state.searchQuery ? location.state.searchQuery : "";

  useEffect(() => {
    if (location.state && location.state.termData) {
      setDescription(location.state.termData.description || "");
    } else {
      fetchTerm(term)
        .then((data) => setDescription(data.description || ""))
        .catch(() => setMessage("Failed to load term"));
    }
  }, [term, location.state]);

  const handleSave = () => {
    updateTerm(term, description)
      .then(() => {
        setMessage("Updated successfully");
        navigate(`/?q=${encodeURIComponent(previousQuery || term)}`);
      })
      .catch((error) => {
        if (error.response && error.response.status === 404) {
          setMessage("Term not found (cannot update)");
        } else {
          setMessage("Update failed");
        }
      });
  };

  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") {
        navigate(`/?q=${encodeURIComponent(previousQuery || term)}`);
      }
    };
    window.addEventListener("keydown", handleEsc);
    return () => window.removeEventListener("keydown", handleEsc);
  }, [navigate, previousQuery, term]);

  return (
    <div style={{ padding: "2rem" }}>
      <h2>Edit Term: {term}</h2>
      <textarea
        rows="6"
        cols="50"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <br />
      <button onClick={handleSave}>Update</button>
      <button style={{ marginLeft: "0.5rem" }} onClick={() => navigate(`/?q=${encodeURIComponent(previousQuery || term)}`)}>
        Cancel
      </button>
      {message && <p>{message}</p>}
    </div>
  );
}
