import axios from "axios";

const API_BASE = "/api/terms";

export const fetchTerm = (term) =>
  axios.get(`${API_BASE}/${term}`).then((res) => res.data);

export const createTerm = (termData) =>
  axios.post(API_BASE, termData).then((res) => res.data);

export const updateTerm = (term, description) =>
  axios.put(`${API_BASE}/${encodeURIComponent(term)}`, { term, description }).then((res) => res.data);

