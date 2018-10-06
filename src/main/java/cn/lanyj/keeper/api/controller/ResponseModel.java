package cn.lanyj.keeper.api.controller;

import java.util.HashMap;
import java.util.Map;

public class ResponseModel {

	private static final String SUCCESS = "success";
	private static final String MSG = "msg";
	
	private Map<String, Object> response = new HashMap<String, Object>();
	
	public ResponseModel success() {
		return put(SUCCESS, true);
	}
		
	public ResponseModel error() {
		return put(SUCCESS, false);
	}
	
	public ResponseModel success(String msg) {
		return success().put(MSG, msg);
	}
		
	public ResponseModel error(String msg) {
		return error().put(MSG, msg);
	}
	
	public ResponseModel put(String key, Object value) {
		response.put(key, value);
		return this;
	}
	
	public Map<String, Object> getResponse() {
		return response;
	}
	
	public void setResponse(Map<String, Object> response) {
		this.response = response;
	}
	
}
