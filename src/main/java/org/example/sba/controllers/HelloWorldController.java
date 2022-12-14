package org.example.sba.controllers;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/")
public class HelloWorldController {

    @GetMapping()
    public String hello() {
        return "Hello, World: /";
    }
}
