package com.emailextprj.emailwritter;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class emailgeneratorController {
private final emailGeneratorService emailgeneratorservice;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody emailRequest emailrequest){
        String response=emailgeneratorservice.generateemailreply(emailrequest);
        return ResponseEntity.ok(response);
    }

}
