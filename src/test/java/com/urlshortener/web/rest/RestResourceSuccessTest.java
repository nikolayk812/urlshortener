package com.urlshortener.web.rest;

import com.urlshortener.model.Account;
import com.urlshortener.util.TestUtils;
import com.urlshortener.web.rest.dto.AccountCreateRequest;
import com.urlshortener.web.rest.dto.AccountCreateResponse;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultMatcher;

import static com.urlshortener.model.RedirectType.FOUND;
import static com.urlshortener.model.RedirectType.MOVED_PERMANENTLY;
import static com.urlshortener.web.rest.RestResource.REGISTER_URL_PATH;
import static com.urlshortener.web.rest.RestResource.STATISTICS_PATH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RestResourceSuccessTest extends AbstractRestResourceTest {

    @Test
    public void testCreateAccount() throws Exception {
        TestUtils.print(mockMvc.perform(post(RestResource.ACCOUNT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(new AccountCreateRequest(ACCOUNT_NAME)))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.description", equalTo(AccountCreateResponse.SUCCESS_DESCRIPTION)))
                .andExpect(jsonPath("$.password", PASSWORD_LENGTH_MATCHER));
    }

    @Test
    public void testRegisterUrl() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);

        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.shortUrl", SHORT_URL_LENGTH_MATCHER));
    }

    @Test
    public void testRegisterSameUrlDifferentCode() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        accountService.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);

        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.shortUrl", SHORT_URL_LENGTH_MATCHER));
    }

    @Test
    public void testRegisterSameUrlDifferentAccount() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        accountService.registerUrl(URL, FOUND, ACCOUNT_NAME);

        Account account2 = accountService.createAccount(ACCOUNT_NAME_2);
        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .with(TestUtils.userHttpBasic(account2))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.shortUrl", SHORT_URL_LENGTH_MATCHER));
    }


    @Test
    public void testGetStatisticsZero() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        String shortUrl = accountService.registerUrl(URL, MOVED_PERMANENTLY, account.getName());

        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.['" + URL + "']", equalTo(0))));
    }

    @DirtiesContext
    @Test
    public void testGetStatistics() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        String shortUrl = accountService.registerUrl(URL, FOUND, account.getName());
        urlMappingService.hitShortUrl(shortUrl);
        urlMappingService.hitShortUrl(shortUrl);

        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.['" + URL + "']", equalTo(2))));
    }

    @DirtiesContext
    @Test
    public void testGetStatisticsAggregatedForSameUrl() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        String shortUrl = accountService.registerUrl(URL, FOUND, ACCOUNT_NAME);
        String shortUrl2 = accountService.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        urlMappingService.hitShortUrl(shortUrl);
        urlMappingService.hitShortUrl(shortUrl2);

        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.['" + URL + "']", equalTo(2))));
    }

    @DirtiesContext
    @Test
    public void testRedirectFoundStatus() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        String shortUrl = accountService.registerUrl(URL, FOUND, account.getName());

        hitShortUrl(shortUrl, status().isFound(), URL);
    }

    @DirtiesContext
    @Test
    public void testRedirectMovedPermanentlyStatus() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        String shortUrl = accountService.registerUrl(URL, MOVED_PERMANENTLY, account.getName());

        hitShortUrl(shortUrl, status().isMovedPermanently(), URL);
    }



    @DirtiesContext
    @Test
    public void testTwoAccountsTwoUrls() throws Exception {
        Account account = accountService.createAccount(ACCOUNT_NAME);
        Account account2 = accountService.createAccount(ACCOUNT_NAME_2);

        String shortUrl = accountService.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        String shortUrl2 = accountService.registerUrl(URL, FOUND, ACCOUNT_NAME_2);
        String shortUrl3 = accountService.registerUrl(URL_2, MOVED_PERMANENTLY, ACCOUNT_NAME);
        String shortUrl4 = accountService.registerUrl(URL_2, FOUND, ACCOUNT_NAME_2);

        hitShortUrl(shortUrl, status().isMovedPermanently(), URL);
        hitShortUrl(shortUrl2, status().isFound(), URL);
        hitShortUrl(shortUrl3, status().isMovedPermanently(), URL_2);
        hitShortUrl(shortUrl4, status().isFound(), URL_2);

        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.['" + URL + "']", equalTo(2))))
                .andExpect(jsonPath("$.['" + URL_2 + "']", equalTo(2)));

        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME_2)
                .with(TestUtils.userHttpBasic(account2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.['" + URL + "']", equalTo(2))))
                .andExpect(jsonPath("$.['" + URL_2 + "']", equalTo(2)));
    }

    private void hitShortUrl(String shortUrl, ResultMatcher statusMatcher, String redirectUrl) throws Exception {
        TestUtils.print(mockMvc.perform(get("/" + shortUrl))
                .andExpect(statusMatcher)
                .andExpect(redirectedUrl(redirectUrl)));
    }

}