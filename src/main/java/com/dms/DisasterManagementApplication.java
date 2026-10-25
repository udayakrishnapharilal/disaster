package com.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Disaster Management System full-stack application.
 * Demonstrates clean architecture, OOP principles, and Spring Boot framework integration.
 */
@SpringBootApplication
public class DisasterManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisasterManagementApplication.class, args);
        System.out.println("==================================================================");
        System.out.println(" Disaster Management System (DMS) running at: http://localhost:8080");
        System.out.println(" H2 Database Console accessible at: http://localhost:8080/h2-console");
        System.out.println(" Demo Administrator: admin / admin123");
        System.out.println(" Demo Victim User:   victim1 / victim123");
        System.out.println(" Demo Volunteer:     volunteer1 / volunteer123");
        System.out.println("==================================================================");
    }
}
