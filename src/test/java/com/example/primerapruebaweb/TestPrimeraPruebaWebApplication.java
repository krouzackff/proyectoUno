package com.example.primerapruebaweb;

import org.springframework.boot.SpringApplication;

public class TestPrimeraPruebaWebApplication {

    public static void main(String[] args) {
        SpringApplication.from(PrimeraPruebaWebApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
