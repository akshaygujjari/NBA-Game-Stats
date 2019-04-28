package com.example.demo;


import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Selectfaveteams {
    @Id
    private Long id;
    private String teamslist;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getTeamslist() {
		return teamslist;
	}
	public void setTeamslist(String teamslist) {
		this.teamslist = teamslist;
	}
 	
}