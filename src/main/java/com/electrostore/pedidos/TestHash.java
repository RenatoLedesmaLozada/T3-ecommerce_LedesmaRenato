package com.electrostore.pedidos;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashAdmin = encoder.encode("adminpassword");
        System.out.println("NUEVO HASH ADMIN: " + hashAdmin);
    }
}