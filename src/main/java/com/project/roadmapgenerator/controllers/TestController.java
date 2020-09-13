package com.project.roadmapgenerator.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sample")
public class TestController {
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public String sampleControllerMethod() {
		return "Controller initiliazed";
	}
	

}
