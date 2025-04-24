// src/App.test.js
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import axios from 'axios';
import AxiosMockAdapter from 'axios-mock-adapter';
import TermSearch from './TermSearch';

const mock = new AxiosMockAdapter(axios);

describe('TermSearch Component', () => {
  beforeEach(() => {
    mock.reset();
  });

  test('renders search input and button', () => {
    render(<TermSearch />);
    expect(screen.getByPlaceholderText('Enter term to search')).toBeInTheDocument();
    expect(screen.getByText('Search')).toBeInTheDocument();
  });

  test('displays term and description when found', async () => {
    const term = 'IFG';
    const description = 'Interesting Field Guide';
    mock.onGet(`/api/terms/${term}`).reply(200, { term, description });

    render(<TermSearch />);

    fireEvent.change(screen.getByPlaceholderText('Enter term to search'), {
      target: { value: term },
    });
    fireEvent.click(screen.getByText('Search'));

    await waitFor(() => {
      expect(screen.getByText(`Term: ${term}`)).toBeInTheDocument();
      expect(screen.getByText(`Description: ${description}`)).toBeInTheDocument();
    });
  });

  test('shows form to add a new term when term is not found', async () => {
    const term = 'UnknownTerm';
    mock.onGet(`/api/terms/${term}`).reply(404);

    render(<TermSearch />);

    fireEvent.change(screen.getByPlaceholderText('Enter term to search'), {
      target: { value: term },
    });
    fireEvent.click(screen.getByText('Search'));

    await waitFor(() => {
      expect(screen.getByText('Term not found. Add a new term:')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('Enter new term')).toHaveValue(term);
    });
  });

  test('saves a new term and displays it', async () => {
    const term = 'NewTerm';
    const description = 'New Term Description';
    mock.onGet(`/api/terms/${term}`).reply(404);
    mock.onPost('/api/terms').reply(200, { term, description });

    render(<TermSearch />);

    fireEvent.change(screen.getByPlaceholderText('Enter term to search'), {
      target: { value: term },
    });
    fireEvent.click(screen.getByText('Search'));

    await waitFor(() => {
      expect(screen.getByText('Term not found. Add a new term:')).toBeInTheDocument();
    });

    fireEvent.change(screen.getByPlaceholderText('Enter term to search'), {
      target: { value: term },
    });
    fireEvent.change(screen.getByPlaceholderText('Enter term description'), {
      target: { value: description },
    });
    fireEvent.click(screen.getByText('Save'));

    await waitFor(() => {
      expect(screen.getByText(`Term: ${term}`)).toBeInTheDocument();
      expect(screen.getByText(`Description: ${description}`)).toBeInTheDocument();
    });
  });
});
