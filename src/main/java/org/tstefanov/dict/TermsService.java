package org.tstefanov.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TermsService
{

    @Autowired
    private TermsRepository termsRepository;

    public Optional<Term> getTerm(String term)
    {
        return termsRepository.findByTerm(term);
    }

    public Term saveTerm(String term, String description)
    {
        return termsRepository.save(new Term(term, description));
    }

    public List<TermSummary> searchTerms(String query) {
        return termsRepository.findByTermStartingWithIgnoreCase(query)
                .stream()
                .map(term -> new TermSummary(term.getTerm(),
                        Optional.ofNullable(term.getDescription()).orElse("No description")))
                .toList();
    }

}
