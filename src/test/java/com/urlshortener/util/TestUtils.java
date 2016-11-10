package com.urlshortener.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.model.Account;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.UnsupportedEncodingException;

public final class TestUtils {
    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ResultActions print(ResultActions action) throws UnsupportedEncodingException {
        log.info("Response content: {}", getContent(action));
        return action;
    }

    private static String getContent(ResultActions action) throws UnsupportedEncodingException {
        return action.andReturn().getResponse().getContentAsString();
    }

    public static byte[] toJson(Object object) {
        try {
            return MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + object + "'", e);
        }
    }

    public static class StringLengthMatcher extends BaseMatcher<String> {
        private final int expectedLength;

        public StringLengthMatcher(int expectedLength) {
            this.expectedLength = expectedLength;
        }

        @Override
        public boolean matches(Object item) {
            return ((String)item).length() == expectedLength;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.valueOf(expectedLength));
        }
    }

    public static RequestPostProcessor userHttpBasic(Account account) {
        return userHttpBasic(account.getName(), account.getPassword());
    }

    public static RequestPostProcessor userHttpBasic(String name, String password) {
        return SecurityMockMvcRequestPostProcessors.httpBasic(name, password);
    }
}
