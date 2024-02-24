package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Song;
import com.example.demo.entities.Users;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UsersRepository;

@Service
public class UsersServiceImplementation 
							implements UsersService{
	@Autowired
	UsersRepository repo;
	@Autowired
	SongRepository songRepo;
	@Override
	public String addUser(Users user) {
		repo.save(user);
		return "user added succesfully";
	}
	@Override
	public boolean emailExists(String email) {
		if(repo.findByEmail(email)==null) {
			return false;
		}
		else {
			return true;
		}
		
	}
	@Override
	public boolean validateUser(String email, String password) {
		Users user = repo.findByEmail(email);
		String db_pass = user.getPassword();
		if(password.equals(db_pass)) {
			return true;
		}
		else {
			return false;
		}
		
	}
	@Override
	public String getRole(String email) {
		Users user = repo.findByEmail(email);
		return user.getRole();
	}
	@Override
	public Users getUser(String email) {
		return repo.findByEmail(email);
	}
	@Override
	public void updateUser(Users user) {
		repo.save(user);
	}
	@Override
    public void addFavorite(int userId, int songId) {
        Users user = repo.findById(userId).orElse(null);
        Song song = songRepo.findById(songId).orElse(null);
        if (user != null && song != null) {
            List<Song> favorites = user.getFavorites();
            favorites.add(song);
            user.setFavorites(favorites);
            repo.save(user);
        }
    }
}
