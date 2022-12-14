package org.example.sba.controllers;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class HelloWorldController {
    @RequestMapping("/")
    public String hello() {
        return "Hello, World";
    }
}
