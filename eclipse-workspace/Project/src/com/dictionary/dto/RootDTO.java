package com.dictionary.dto;

public class RootDTO {
	private int id;
	private String rootLetters;

	public int getId() {
		return id;
	}

	public RootDTO( int id, String root) {
		this.rootLetters = root;
		this.id = id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRootLetters() {
		return rootLetters;
	}

	public void setRootLetters(String rootLetters) {
		this.rootLetters = rootLetters;
	}

}
