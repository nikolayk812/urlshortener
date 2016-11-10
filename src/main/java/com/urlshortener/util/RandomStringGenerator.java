package com.urlshortener.util;

import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.security.SecureRandom;

//TODO: test

/**
 * Inspired by {@code http://stackoverflow.com/a/41156/1360074}
 */
public class RandomStringGenerator {
    private static final SecureRandom random = new SecureRandom();

    /**
     * TODO:
     *
     * @return
     */
    public static String generate(int targetLength) {
        Preconditions.checkArgument(targetLength > 0);
        String first = generateImpl(targetLength);
        if (first.length() < targetLength) {
            String second = generateImpl(targetLength);
            first = (first + second).substring(0, targetLength);
        }
        return first;
    }

    private static String generateImpl(int targetLength) {
        //5 bits per symbol, 32 = 2^5
        return new BigInteger(targetLength * 5, random)
                .toString(32);
    }
}
