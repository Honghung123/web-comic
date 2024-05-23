package com.group17.comic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TemptTest {
    @Test
    public void TwoPlusTwoShouldBeEqual() {
        Assertions.assertEquals(2+2, 5);
    }

    @Test
    void name() {
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    public void MultiplieZeroShouldBeEqual() {
        Assertions.assertEquals(0, 0);


    }

}
