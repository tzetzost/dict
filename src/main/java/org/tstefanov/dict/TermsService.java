package org.tstefanov.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TermsService
{

    @Autowired
    private TermsRepository termsRepository;

    public Optional<Term> getTerm(String term)
    {
        return termsRepository.findByTerm(term);
    }

    public Term createTerm(String term, String description) {
        if (termsRepository.findByTerm(term).isPresent()) {
            throw new IllegalArgumentException("Term already exists");
        }
        return termsRepository.save(new Term(term, description));
    }

    public Term updateTerm(String term, String description) {
        Optional<Term> existing = termsRepository.findByTerm(term);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Term not found");
        }
        Term t = existing.get();
        t.setDescription(description);
        return termsRepository.save(t);
    }

    public List<TermSummary> searchTerms(String query) {
        return termsRepository.findByTermStartingWithIgnoreCase(query)
                .stream()
                .map(term -> new TermSummary(term.getTerm(),
                        Optional.ofNullable(term.getDescription()).orElse("No description")))
                .toList();
    }

}
