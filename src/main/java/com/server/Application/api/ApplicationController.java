package com.server.Application.api;

import org.javatuples.Pair;
import org.jose4j.lang.JoseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class ApplicationController {

    private ApplicationService service;

    @Inject
    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @RequestMapping("/")
    public String index() {
        return "";
    }

    @PostMapping(path = "/generate-key", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateKey(@RequestBody PublicKeyModel key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String publicKey = service.generateKey(key.getKeyExponent(), key.getKeyModulus());
        return "-----BEGIN PUBLIC KEY-----\n" + publicKey + "\n-----END PUBLIC KEY-----";
    }

    @PostMapping(path = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String validateBearerToken(@RequestBody Map<String, String> payload) throws InvalidKeySpecException, NoSuchAlgorithmException {
        //payload.forEach((k,v) -> System.out.println("Key = " + k + ", Value = " + v));
        String response = service.validateBearerToken(payload.get("public_key"), payload.get("token"));
        return response;
    }

    @PostMapping(path = "/generate-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateSignedToken(@RequestBody Map<String, String> payload) throws JoseException, NoSuchAlgorithmException {
        Pair pair = service.generateSignedToken(payload.get("user"));
        String response = "Token:\n" + pair.getValue0() +
                "\n\nPublic Key:\n-----BEGIN PUBLIC KEY-----\n" + pair.getValue1() + "\n-----END PUBLIC KEY-----" ;
        return response;
    }
}
