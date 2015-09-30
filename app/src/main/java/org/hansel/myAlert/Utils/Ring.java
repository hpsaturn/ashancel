package org.hansel.myAlert.Utils;

public class Ring {
	private String id;
	private String name;
	
	public Ring(){
		id = null;
		name = null;
	}
	
	public Ring(String id, String name){
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
