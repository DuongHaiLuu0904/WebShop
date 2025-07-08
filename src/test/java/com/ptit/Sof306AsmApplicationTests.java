package com.ptit;

import com.ptit.config.DotenvConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = DotenvConfig.class)
class Sof306AsmApplicationTests {
    @Test
    void contextLoads() {
    }
}