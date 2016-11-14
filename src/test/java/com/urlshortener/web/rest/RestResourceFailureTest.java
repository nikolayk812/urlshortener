package com.urlshortener.web.rest;

import com.urlshortener.model.Account;
import com.urlshortener.util.TestUtils;
import com.urlshortener.web.rest.dto.AccountRequest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static com.urlshortener.model.RedirectType.FOUND;
import static com.urlshortener.util.Constants.ACCOUNT_CREATE_FAILURE_DESCRIPTION;
import static com.urlshortener.web.rest.RestResource.REGISTER_URL_PATH;
import static com.urlshortener.web.rest.RestResource.STATISTICS_PATH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RestResourceFailureTest extends AbstractRestResourceTest {

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
        service.registerUrl(URL, FOUND, account.getName());

        TestUtils.print(mockMvc.perform(post(REGISTER_URL_PATH)
                .with(TestUtils.userHttpBasic(account))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isConflict())
                .andExpect(content().string(isEmptyString()));
    }

    //TODO: same URL for other account okay

    @Test
    public void testGetStatisticsNoAuthenticationHeader() throws Exception {
        TestUtils.print(mockMvc.perform(get(STATISTICS_PATH + "/" + ACCOUNT_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(URL_REGISTER_REQUEST))))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(isEmptyString()));
    }

}