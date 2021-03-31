package com.asyncworking.utility;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtils {
    public String GenerateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }
}
