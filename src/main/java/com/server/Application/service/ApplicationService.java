package com.server.application.service;
import org.apache.commons.codec.binary.Base64;
import org.javatuples.Pair;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;


@Service
public class ApplicationService {

    public String generateKey(String exp, String mod) throws NoSuchAlgorithmException, InvalidKeySpecException {

        Base64 decoder = new Base64(true);

        byte[] modulusByte = decoder.decodeBase64(mod);
        BigInteger modulus = new BigInteger(1, modulusByte);
        byte[] exponentByte = decoder.decodeBase64(exp);
        BigInteger exponent = new BigInteger(1, exponentByte);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey pk = factory.generatePublic(spec);
        return Base64.encodeBase64String(pk.getEncoded());
    }

    public String validateBearerToken(String publicKeyPEM, String token) throws NoSuchAlgorithmException, InvalidKeySpecException {

        Base64 decoder = new Base64();
        byte[] encodedPublicKey = decoder.decodeBase64(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = factory.generatePublic(spec);

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer("Test system") // whom the JWT needs to have been issued by
                .setExpectedAudience("Test audience") // to whom the JWT is intended for
                .setVerificationKey(publicKey) // verify the signature with the public key
                .build(); // create the JwtConsumer instance
        try
        {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            System.out.println("JWT validation succeeded! " + jwtClaims);
            return "JWT validation succeeded! " + jwtClaims;
        }
        catch (InvalidJwtException e) {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            System.out.println("Invalid JWT! " + e);
            return "Invalid JWT! " + e;
        }
    }


    public Pair<String, String> generateSignedToken(String user) throws NoSuchAlgorithmException, JoseException {
        // Generate a key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Generate token claims
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("Test system");
        claims.setAudience("Test audience");
        claims.setExpirationTimeMinutesInTheFuture(10);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject("Test subject");
        claims.setClaim("customUser", user);

        // Generate token signature
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(keyPair.getPrivate());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setKeyIdHeaderValue("0xVidiTest");
        String token = jws.getCompactSerialization();
        String publicKey = java.util.Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        return new Pair<>(token, publicKey);
    }
}

