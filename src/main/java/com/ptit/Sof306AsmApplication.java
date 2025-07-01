package com.ptit;

import com.ptit.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Sof306AsmApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Sof306AsmApplication.class);
        app.addInitializers(new DotenvConfig());
        app.run(args);
    }

}
