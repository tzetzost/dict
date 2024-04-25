package org.tstefanov.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TermsService
{

    @Autowired
    private TermsRepository termRepository;

    public Optional<Term> getTerm(String term)
    {
        return termRepository.findByTerm(term);
    }

    public Term saveTerm(String term, String description)
    {
        return termRepository.save(new Term(term, description));
    }

}
