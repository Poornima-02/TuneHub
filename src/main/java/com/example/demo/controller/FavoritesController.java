package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entities.Song;
import com.example.demo.entities.Users;
import com.example.demo.services.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FavoritesController {
    
    @Autowired
    UsersService userService;

    @GetMapping("/favorites")
    public String favoritesPage(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        Users user = userService.getUser(email);
        
        if (user != null) {
            List<Song> favorites = user.getFavorites();
            model.addAttribute("favorites", favorites);
        }
        
        return "favorites";
    }

}
