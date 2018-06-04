package com.myprojects.grideye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GridEyeApplication {
    public static void main(String[] args) {
        try {
//            (new ReadDataFromUSB()).connect("/dev/ttyACM0");
            (new ReadDataFromUSB()).connect("COM4");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(GridEyeApplication.class, args);
    }
}
