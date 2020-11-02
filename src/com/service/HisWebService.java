package com.service;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface HisWebService {
	
	public String hisWebFunction(@WebParam(name="function")String function,@WebParam(name="xml")String xml);
}
