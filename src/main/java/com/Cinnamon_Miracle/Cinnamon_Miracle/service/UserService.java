package com.Cinnamon_Miracle.Cinnamon_Miracle.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Cinnamon_Miracle.Cinnamon_Miracle.model.User;

import com.Cinnamon_Miracle.Cinnamon_Miracle.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public String signup(User user) {
		// check if a user with the given email already exists
		if(userRepository.existsById(user.getEmail())) {
			return "User Already Exist";
		}
		userRepository.save(user);
		return "User Signed Up Successfully";
	}
	// Login
	public Optional<User> login(String email , String password){
		Optional<User> userOptional = userRepository.findById(email);
		if(userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
			//Return the user object if login successful
			return userOptional;
		}
		return Optional.empty();
	}
}
