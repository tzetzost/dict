package org.tstefanov.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/terms")
public class TermsController
{

    @Autowired
    private TermsService termService;

    @GetMapping("/{term}")
    public ResponseEntity<Term> getTerm(@PathVariable String term)
    {
        Optional<Term> foundTerm = termService.getTerm(term);
        return foundTerm.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Term> saveTerm(@RequestBody Term term)
    {
        Term savedTerm = termService.saveTerm(term.getTerm(), term.getDescription());
        return ResponseEntity.ok(savedTerm);
    }

}
