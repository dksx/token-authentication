package com.server.application.controller;

import com.server.application.entity.PublicKeyEntity;
import com.server.application.service.ApplicationService;
import org.javatuples.Pair;
import org.jose4j.lang.JoseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

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
    public Flux<String> index() {
        return Flux.just("");
    }

    @PostMapping(path = "/generate-key", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> generateKey(@RequestBody PublicKeyEntity key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String publicKey = service.generateKey(key.getKeyExponent(), key.getKeyModulus());
        return Flux.just("-----BEGIN PUBLIC KEY-----\n" + publicKey + "\n-----END PUBLIC KEY-----");
    }

    @PostMapping(path = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> validateBearerToken(@RequestBody Map<String, String> payload) throws InvalidKeySpecException, NoSuchAlgorithmException {
        //payload.forEach((k,v) -> System.out.println("Key = " + k + ", Value = " + v));
        String response = service.validateBearerToken(payload.get("public_key"), payload.get("token"));
        return Flux.just(response);
    }

    @PostMapping(path = "/generate-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> generateSignedToken(@RequestBody Map<String, String> payload) throws JoseException, NoSuchAlgorithmException {
        Pair pair = service.generateSignedToken(payload.get("user"));
        String response = "Token:\n" + pair.getValue0() +
                "\n\nPublic Key:\n-----BEGIN PUBLIC KEY-----\n" + pair.getValue1() + "\n-----END PUBLIC KEY-----" ;
        return Flux.just(response);
    }
}
