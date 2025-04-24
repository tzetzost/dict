package org.tstefanov.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/terms")
public class TermsController
{

    @Autowired
    private TermsService termsService;

    @GetMapping("/{term}")
    public ResponseEntity<Term> getTerm(@PathVariable String term)
    {
        Optional<Term> foundTerm = termsService.getTerm(term);
        return foundTerm.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Term> saveTerm(@RequestBody Term term)
    {
        Term savedTerm = termsService.saveTerm(term.getTerm(), term.getDescription());
        return ResponseEntity.ok(savedTerm);
    }

    @GetMapping("/search")
    public List<TermSummary> searchTerms(@RequestParam("q") String query) {
        return termsService.searchTerms(query);
    }

}
