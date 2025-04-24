import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchTerm, saveTerm } from "../api/terms";

export default function EditPage() {
  const { term } = useParams();
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetchTerm(term)
      .then((data) => setDescription(data.description || ""))
      .catch(() => setMessage("Failed to load term"));
  }, [term]);

  const handleSave = () => {
    saveTerm({ term, description })
      .then(() => setMessage("Saved successfully"))
      .catch(() => setMessage("Save failed"));
  };

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
      <button onClick={handleSave}>Save</button>
      {message && <p>{message}</p>}
    </div>
  );
}
