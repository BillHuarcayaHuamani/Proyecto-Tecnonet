package com.ProyectoTecnonet.tecnonet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TecnonetApplication {

    public static void main(String[] args) {
        SpringApplication.run(TecnonetApplication.class, args);
    }
}