package com.edu.tau.alo.tau_survey_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://apisurveys.vercel.app")
public class TestController {
//    @GetMapping("/test")
//    public String test() {
//        return "Backend połączony pomyślnie! Uwierzytalnianie Azure ID działa.";
//}
}