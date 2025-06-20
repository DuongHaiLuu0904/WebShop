package com.ptit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ptit.entity.Customers;
import com.ptit.service.CustomerService;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/customers")
public class CustomerRestController {

    @Autowired
    CustomerService customerService;

    @GetMapping
    public List<Customers> getCustomers(@RequestParam("admin") Optional<Boolean> admin) {
        if (admin.orElse(false)) {
            return customerService.getAdministrators();
        }
        return customerService.findAll();
    }

    @GetMapping("{id}")
    public Customers getOne(@PathVariable("id") String id) {
        return customerService.findById(id);
    }

    @PostMapping
    public Customers create(@RequestBody Customers customer) {
        return customerService.create(customer);
    }

    @PutMapping("{id}")
    public Customers update(@PathVariable("id") String id, @RequestBody Customers customer) {
        return customerService.update(customer);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        customerService.delete(id);
    }
}
