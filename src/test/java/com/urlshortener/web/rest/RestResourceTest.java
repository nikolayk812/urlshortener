package com.urlshortener.web.rest;

import com.urlshortener.AppConfig;
import com.urlshortener.LocalAppConfig;
import com.urlshortener.model.Account;
import com.urlshortener.service.AccountService;
import com.urlshortener.util.TestUtils;
import com.urlshortener.web.SecurityConfig;
import com.urlshortener.web.WebConfig;
import com.urlshortener.web.rest.dto.AccountRequest;
import com.urlshortener.web.rest.dto.AccountResponse;
import com.urlshortener.web.rest.dto.UrlRegisterRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import static com.urlshortener.model.RedirectType.MOVED_PERMANENTLY;
import static com.urlshortener.util.Constants.ACCOUNT_CREATE_FAILURE_DESCRIPTION;
import static com.urlshortener.util.Constants.PASSWORD_LENGTH;
import static com.urlshortener.web.rest.RestResource.REGISTER_URL_PATH;
import static com.urlshortener.web.rest.RestResource.STATISTICS_PATH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {AppConfig.class, LocalAppConfig.class, WebConfig.class, SecurityConfig.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:db/h2/initDB.sql")
public class RestResourceTest {
    private static final String ACCOUNT_NAME = "first";
    private static final String ACCOUNT_NAME_2 = "second";
    private static final String URL = "http://test.com";
    private static final UrlRegisterRequest URL_REGISTER_REQUEST = new UrlRegisterRequest(URL);
    private static final TestUtils.StringLengthMatcher PASSWORD_LENGTH_MATCHER =
            new TestUtils.StringLengthMatcher(PASSWORD_LENGTH);

    private MockMvc mockMvc;

    @Autowired
    private AccountService service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    void postConstruct() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testCreateAccount() throws Exception {
        TestUtils.print(mockMvc.perform(post(RestResource.ACCOUNT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(new AccountRequest(ACCOUNT_NAME)))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.description", equalTo(AccountResponse.SUCCESS_DESCRIPTION)))
                .andExpect(jsonPath("$.password", PASSWORD_LENGTH_MATCHER));
    }

    @Test
    public void testCreateAccountDuplicate() throws Exception {
        service.createAccount(ACCOUNT_NAME);

        TestUtils.print(mockMvc.perform(post(RestResource.ACCOUNT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(new AccountRequest(ACCOUNT_NAME)))))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.description", equalTo(ACCOUNT_CREATE_FAILURE_DESCRIPTION)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    //TODO: invalid name
    //TODO: invalid JSON

    @Test
    public void testRegisterUrl() throws Exception {
        Account account = service.createAccount(ACCOUNT_NAME);

        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.shortUrl").exists());
        //TODO: validate more of payload
    }

    @Test
    public void testRegisterUrlNoAuthenticationHeader() throws Exception {
        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(isEmptyString()));
    }

    @Test
    public void testRegisterUrlWrongAuthenticationHeader() throws Exception {
        Account account = service.createAccount(ACCOUNT_NAME);
        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .with(TestUtils.userHttpBasic(account.getName(), "somepass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST)))
                .andExpect(status().isUnauthorized()))
                .andExpect(content().string(isEmptyString()));
    }

    @Test
    public void testRegisterUrlDuplicate() throws Exception {
        Account account = service.createAccount(ACCOUNT_NAME);
        service.registerUrl(URL, MOVED_PERMANENTLY, account.getName());

        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isConflict())
                .andExpect(content().string(isEmptyString()));
    }

    //TODO: same URL for other account okay

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

    @Test
    public void testGetStatisticsNoAuthenticationHeader() throws Exception {
        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(isEmptyString()));
    }

    @Test
    public void testGetStatistics() throws Exception {
        Account account = service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, MOVED_PERMANENTLY, account.getName());
        service.hitShortUrl(shortUrl, ACCOUNT_NAME);
        service.hitShortUrl(shortUrl, ACCOUNT_NAME);

        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.['" + URL + "']", equalTo(2))));
    }

}