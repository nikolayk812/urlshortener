package com.urlshortener.web.rest;

import com.urlshortener.model.Account;
import com.urlshortener.util.TestUtils;
import com.urlshortener.web.rest.dto.AccountCreateRequest;
import com.urlshortener.web.rest.dto.AccountCreateResponse;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

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
        Account account = service.createAccount(ACCOUNT_NAME);

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
        Account account = service.createAccount(ACCOUNT_NAME);
        service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);

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
        Account account = service.createAccount(ACCOUNT_NAME);
        service.registerUrl(URL, FOUND, ACCOUNT_NAME);

        Account account2 = service.createAccount(ACCOUNT_NAME_2);
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
        Account account = service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, MOVED_PERMANENTLY, account.getName());

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
        Account account = service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, FOUND, account.getName());
        urlService.hitShortUrl(shortUrl);
        urlService.hitShortUrl(shortUrl);

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
        Account account = service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, FOUND, ACCOUNT_NAME);
        String shortUrl2 = service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        urlService.hitShortUrl(shortUrl);
        urlService.hitShortUrl(shortUrl2);

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
        Account account = service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, FOUND, account.getName());

        TestUtils.print(mockMvc.perform(get("/" + shortUrl))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(URL)));
    }

    @DirtiesContext
    @Test
    public void testRedirectMovedPermanentlyStatus() throws Exception {
        Account account = service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, MOVED_PERMANENTLY, account.getName());

        TestUtils.print(mockMvc.perform(get("/" + shortUrl))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl(URL)));
    }

}