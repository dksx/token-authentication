package com.server.Application.entity;

import java.util.Objects;

public class PublicKeyEntity {

    private final String exponent;
    private final String modulus;

    public PublicKeyEntity(String exponent, String modulus) {
        this.exponent = exponent;
        this.modulus = modulus;
    }

    public String getKeyExponent() {
        return exponent;
    }
    public String getKeyModulus() {
        return modulus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicKeyEntity that = (PublicKeyEntity) o;
        return Objects.equals(exponent, that.exponent) &&
                Objects.equals(modulus, that.modulus);
    }
}