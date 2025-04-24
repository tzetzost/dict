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
    public ResponseEntity<Term> createTerm(@RequestBody Term term) {
        try {
            Term created = termsService.createTerm(term.getTerm(), term.getDescription());
            return ResponseEntity.status(201).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/{term}")
    public ResponseEntity<Term> updateTerm(@PathVariable String term, @RequestBody Term termBody) {
        try {
            Term updated = termsService.updateTerm(term, termBody.getDescription());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<TermSummary> searchTerms(@RequestParam("q") String query) {
        return termsService.searchTerms(query);
    }

}
