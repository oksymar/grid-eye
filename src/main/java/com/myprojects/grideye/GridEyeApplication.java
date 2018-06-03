package com.myprojects.grideye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class GridEyeApplication {
    public static void main(String[] args) {
        try
        {
            TimeUnit.SECONDS.sleep(2);
            (new ReadDataFromUSB()).connect("/dev/ttyACM0");
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        SpringApplication.run(GridEyeApplication.class, args);
    }
}
