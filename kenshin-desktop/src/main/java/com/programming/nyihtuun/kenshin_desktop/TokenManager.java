package com.programming.nyihtuun.kenshin_desktop;

public class TokenManager {

	private String token;

	public TokenManager(String token) {
		
		this.token = token;
	}
	public TokenManager() {}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
