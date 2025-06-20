package com.ptit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ptit.entity.Authority;
import com.ptit.service.AuthorityService;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/authorities")
public class AuthorityRestController {

    @Autowired
    AuthorityService authorityService;

    @GetMapping
    public List<Authority> findAll(@RequestParam("admin") Optional<Boolean> admin) {
        if (admin.orElse(false)) {
            return authorityService.findAuthoritiesOfAdministrators();
        }
        return authorityService.findAll();
    }

    @PostMapping
    public Authority post(@RequestBody Authority auth) {
        return authorityService.create(auth);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Integer id) {
        authorityService.delete(id);
    }
}
