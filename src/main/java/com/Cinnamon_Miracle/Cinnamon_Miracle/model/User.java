package com.Cinnamon_Miracle.Cinnamon_Miracle.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

 
@Entity
@Table(name = "users")
public class User {
 
	@Id
	@Column(name = "email")
	private String email;
	
	@Column(name = "password" , nullable = false)
	private String password;
	
	@Column(name = "first_name" , nullable = false)
	private String firstName;
	
	@Column(name = "last_name" , nullable = false)
	private String lastName;
	
	@Column(name = "mobile_number" , nullable = false)
	private String mobile;
	
	@Column(name = "gender" , nullable = false)
	private String gender;
	
	@Column(name = "location" , nullable = false)
	private String location;
	
	@Column(name = "dob" , nullable = false)
	private Date dob;
	
	//Constructors
	public User() {}
	
	public User(String email , String password , String firstName , String lastName , String mobile , String gender , String location) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobile = mobile;
		this.gender = gender;
		this.location = location;
	}
	
	//Full constructor with all fields
	public User(String email , String password , String firstName , String lastName , String mobile , String gender , String location ,Date dob) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobile = mobile;
		this.gender = gender;
		this.location = location;
		this.dob = dob;
	}

	// Getters and Setters
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	
	public String getMobile() { return mobile; }
	public void setMobile(String mobile) { this.mobile = mobile; }
	
	public String getGender() { return gender; }
	public void setGender(String gender) { this.gender = gender; }
	
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	
	public Date getDob() { return dob; }
	public void setDob(Date dob) {this.dob = dob;} 
	
	public String getFullname() {
	return firstName + " " + lastName;
	}

}
