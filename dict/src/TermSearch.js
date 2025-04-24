// src/TermSearch.js
import React, { useState } from "react";
import axios from "axios";

const TermSearch = () => {
  const [term, setTerm] = useState("");
  const [definition, setDefinition] = useState("");
  const [searchResult, setSearchResult] = useState(null);
  const [showNewTermForm, setShowNewTermForm] = useState(false);
  const [newTerm, setNewTerm] = useState("");
  const [newDefinition, setNewDefinition] = useState("");

  const backendUrl = `${process.env.REACT_APP_BACKEND_HOST}:${process.env.REACT_APP_BACKEND_PORT}/api/terms`;

  const handleSearch = async () => {
    try {
      const response = await axios.get(`${backendUrl}/${term}`);
      setSearchResult(response.data);
      setShowNewTermForm(false);
    } catch (error) {
      if (error.response && error.response.status === 404) {
        setSearchResult(null);
        setNewTerm(term);
        setNewDefinition("");
        setShowNewTermForm(true);
      } else {
        console.error("An error occurred while searching for the term:", error);
      }
    }
  };

  const handleSave = async () => {
    try {
      const response = await axios.post(backendUrl, {
        term: newTerm,
        description: newDefinition,
      });
      setSearchResult(response.data);
      setShowNewTermForm(false);
    } catch (error) {
      console.error("An error occurred while saving the term:", error);
    }
  };

  return (
    <div>
      <h1>Term Search</h1>
      <div>
        <input
          type="text"
          value={term}
          onChange={(e) => setTerm(e.target.value)}
          placeholder="Enter term to search"
        />
        <button onClick={handleSearch}>Search</button>
      </div>
      {searchResult && (
        <div>
          <h2>Term: {searchResult.term}</h2>
          <p>Description: {searchResult.description}</p>
        </div>
      )}
      {showNewTermForm && (
        <div>
          <h2>Term not found. Add a new term:</h2>
          <input
            type="text"
            value={newTerm}
            onChange={(e) => setNewTerm(e.target.value)}
            placeholder="Enter new term"
          />
          <input
            type="text"
            value={newDefinition}
            onChange={(e) => setNewDefinition(e.target.value)}
            placeholder="Enter term description"
          />
          <button onClick={handleSave}>Save</button>
        </div>
      )}
    </div>
  );
};

export default TermSearch;
