package com.Cinnamon_Miracle.Cinnamon_Miracle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Cinnamon_Miracle.Cinnamon_Miracle.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	//The Primary Key is now email , so we extend JpaRepository<User , String>
	
	Optional<User> findByMobile(String mobile);
	Optional<User> findByEmailAndPassword(String email , String password);
}
