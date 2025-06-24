package com.ptit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException e) {
        return ResponseEntity.badRequest().body("Invalid ID format. ID must be a number.");
    }

    @GetMapping
    public List<Customers> getCustomers(@RequestParam("admin") Optional<Boolean> admin) {
        if (admin.orElse(false)) {
            return customerService.getAdministrators();
        }
        return customerService.findAll();
    }    @GetMapping("{id}")
    public Customers getOne(@PathVariable("id") Integer id) {
        System.out.println("CustomerRestController.getOne() called with id: " + id);
        return customerService.findById(id);
    }

    @GetMapping("/username/{username}")
    public Customers getByUsername(@PathVariable("username") String username) {
        return customerService.findByUsername(username);
    }

    @PostMapping
    public Customers create(@RequestBody Customers customer) {
        return customerService.create(customer);
    }

    @PutMapping("{id}")
    public Customers update(@PathVariable("id") Integer id, @RequestBody Customers customer) {
        customer.setId(id); // Đảm bảo ID đúng
        return customerService.update(customer);
    }    @PatchMapping("{id}")
    public ResponseEntity<Customers> partialUpdate(@PathVariable("id") Integer id, @RequestBody Customers customer) {
        try {
            System.out.println("Attempting to find customer with id: " + id);
            
            // Lấy customer hiện tại từ database
            Customers existingCustomer = customerService.findById(id);
            if (existingCustomer == null) {
                System.err.println("Customer not found with id: " + id);
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("Found customer: " + existingCustomer.getUsername());
            
            // Update các trường được gửi lên
            if (customer.getUsername() != null && !customer.getUsername().trim().isEmpty()) {
                existingCustomer.setUsername(customer.getUsername());
            }
            if (customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
                existingCustomer.setPassword(customer.getPassword());
            }
            if (customer.getFullname() != null && !customer.getFullname().trim().isEmpty()) {
                existingCustomer.setFullname(customer.getFullname());
            }
            if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
                existingCustomer.setEmail(customer.getEmail());
            }
            if (customer.getPhoto() != null && !customer.getPhoto().trim().isEmpty()) {
                existingCustomer.setPhoto(customer.getPhoto());
            }
            
            // Đảm bảo token không bao giờ null
            if (existingCustomer.getToken() == null) {
                existingCustomer.setToken("token");
            }
            
            // Sử dụng saveCustomer để tránh conflict với logic update
            Customers updatedCustomer = customerService.saveCustomer(existingCustomer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Integer id) {
        customerService.delete(id);
    }
}
