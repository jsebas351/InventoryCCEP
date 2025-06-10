package com.api.backendCCEP.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "persons")
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "first_name")
	private String first_name;
	
	@Column(name = "second_name")
	private String second_name;
	
	@Column(name = "first_last_name")
	private String first_last_name;
	
	@Column(name = "second_last_name")
	private String second_last_name;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "phone")
	private long phone;
	
	@Column(name = "identification")
	private long identification;
	
	@Column(name = "type_identification")
	private String type_identification;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getSecond_name() {
		return second_name;
	}

	public void setSecond_name(String second_name) {
		this.second_name = second_name;
	}

	public String getFirst_last_name() {
		return first_last_name;
	}

	public void setFirst_last_name(String first_last_name) {
		this.first_last_name = first_last_name;
	}

	public String getSecond_last_name() {
		return second_last_name;
	}

	public void setSecond_last_name(String second_last_name) {
		this.second_last_name = second_last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public long getIdentification() {
		return identification;
	}

	public void setIdentification(long identification) {
		this.identification = identification;
	}

	public String getType_identification() {
		return type_identification;
	}

	public void setType_identification(String type_identification) {
		this.type_identification = type_identification;
	}

	public Person() {
	}

	public Person(long id, String first_name, String second_name, String first_last_name, String second_last_name,
			String email, long phone, long identification, String type_identification) {
		this.id = id;
		this.first_name = first_name;
		this.second_name = second_name;
		this.first_last_name = first_last_name;
		this.second_last_name = second_last_name;
		this.email = email;
		this.phone = phone;
		this.identification = identification;
		this.type_identification = type_identification;
	}
	
}
