package com.ptit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptit.entity.Role;
import com.ptit.service.RoleService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/roles")
public class RoleRestController {

    @Autowired
    RoleService roleService;

    @GetMapping
    public List<Role> getAll() {
        return roleService.findAll();
    }

}
