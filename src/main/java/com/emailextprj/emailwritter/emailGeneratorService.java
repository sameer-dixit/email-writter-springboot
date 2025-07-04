package com.emailextprj.emailwritter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class emailGeneratorService {
    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String gemeiniapiurl;
    @Value("${gemini.api.key}")
    private String gemeiniapikey;

    public emailGeneratorService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String generateemailreply(emailRequest emailrequest){
//        build the prompt
        String prompt=buildprompt(emailrequest);
//        craft a request
        Map<String,Object> requestbody= Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );
//        do request and get response
String response=webClient.post()
                .uri(gemeiniapiurl + "?key=" + gemeiniapikey)
        .header("Content-Type","application/json")
        .bodyValue(requestbody)
        .retrieve()
        .bodyToMono(String.class)
        .block();
//        return response
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            JsonNode rootnode=objectMapper.readTree(response);
            return rootnode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text").asText();
        } catch (Exception e) {
            return "Error processing request "+e.getMessage();
        }
    }

    private String buildprompt(emailRequest emailrequest) {
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. please dont generate a subject line");
        if(emailrequest.getTone()!=null && !emailrequest.getTone().isEmpty()){
            prompt.append("use a ").append(emailrequest.getTone()).append(" tone ");
        }
        prompt.append("\n original email: \n").append(emailrequest.getEmailcontent());
        return prompt.toString();
    }

}
