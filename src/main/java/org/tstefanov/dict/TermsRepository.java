package org.tstefanov.dict;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermsRepository extends JpaRepository<Term, Long>
{

    Optional<Term> findByTerm(String term);
    List<Term> findByTermStartingWithIgnoreCase(String prefix);

}
