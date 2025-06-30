package com.ptit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FavoriteController {

    @RequestMapping("/favorites")
    public String favorites() {
        return "favorite/list";
    }
}
