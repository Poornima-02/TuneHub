package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Song;
import com.example.demo.entities.Users;
import com.example.demo.services.SongService;
import com.example.demo.services.UsersService;

import jakarta.servlet.http.HttpSession;


@Controller
public class UsersController {
	@Autowired
	UsersService service;
	

	@Autowired
	SongService songService; // Injecting SongService

	@PostMapping("/register")
	public String addUsers(@ModelAttribute Users user) {
		boolean userStatus = service.emailExists(user.getEmail());
		if(!userStatus) {
			service.addUser(user);
			System.out.println("user added");
		}
		else {
			System.out.println("user already exists");
		}


		return "login";
	}

	@PostMapping("/validate")
	public String validate(@RequestParam("email")String email,
	        @RequestParam("password") String password,
	        HttpSession session, Model model) {

	    if(service.validateUser(email,password)) {
	        String role = service.getRole(email);

	        session.setAttribute("email", email);

	        if(role.equals("admin")) {
	            return "adminHome";
	        }
	        else {
	            Users user = service.getUser(email);
	            boolean userStatus = user.isPremium();
	            List<Song> songsList = songService.fetchAllSongs();
	            model.addAttribute("songs", songsList);
	            model.addAttribute("isPremium", userStatus);
	            if (userStatus) {
	                model.addAttribute("favorites", user.getFavorites());
	            }
	            return "customerHome";
	        }
	    }
	    else {
	        return "login";
	    }
	}




	@GetMapping("/logout")
	public String logout(HttpSession session) {

		session.invalidate();
		return "login";
	}
	
	@PostMapping("/addFavorite")
	public String addFavorite(@RequestParam("songId") int songId, HttpSession session, Model model) {
	    String email = (String) session.getAttribute("email");
	    Users user = service.getUser(email);
	    Song song = songService.getSongById(songId);

	    if (user != null && song != null) {
	        List<Song> favorites = user.getFavorites();
	        
	        // Check if the song is already in favorites
	        boolean alreadyAdded = favorites.stream().anyMatch(s -> s.getId() == songId);
	        if (!alreadyAdded) {
	            favorites.add(song);
	            user.setFavorites(favorites);
	            service.updateUser(user);
	        }
	        
	        // Redirect to the favorites page after adding the song
	        return "redirect:/favorites";
	    }

	    // If user or song is null, redirect back to customer home
	    return "redirect:/customerHome";
	}



	@GetMapping("/addedSong")
	public String showAddedSong(@RequestParam("id") int songId, Model model) {
	    // Retrieve the added song by its ID
	    Song song = songService.getSongById(songId);
	    
	    // Add the song to the model
	    model.addAttribute("song", song);
	    
	    // Return the name of the view to display the added song
	    return "addedSong";
	}
	@GetMapping("/customerHome")
    public String showCustomerHome(HttpSession session, Model model) {
        // Check if the user is logged in
        String email = (String) session.getAttribute("email");
        if (email == null) {
            // User not logged in, redirect to login page
            return "redirect:/login";
        }
        
        // Get user's role
        String role = service.getRole(email);
        
        // Get user object
        Users user = service.getUser(email);
        
        // Get list of songs
        List<Song> songs = songService.fetchAllSongs();
        
        // Add data to the model
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("user", user);
        model.addAttribute("songs", songs);
        
        // Return the template name
        return "customerHome";
    }









}
