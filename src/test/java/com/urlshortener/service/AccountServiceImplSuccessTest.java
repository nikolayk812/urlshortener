package com.urlshortener.service;

import com.urlshortener.model.Account;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static com.urlshortener.model.RedirectType.MOVED_PERMANENTLY;
import static com.urlshortener.util.Constants.PASSWORD_LENGTH;
import static com.urlshortener.util.Constants.SHORT_URL_LENGTH;
import static junit.framework.TestCase.assertEquals;

public class AccountServiceImplSuccessTest extends AbstractAccountServiceImplTest {

    @Test
    public void testCreateAccount() throws Exception {
        Account created = service.createAccount(ACCOUNT_NAME);
        assertEquals("Wrong account name", ACCOUNT_NAME, created.getName());
        assertEquals("Wrong account id", ACCOUNT_ID, created.getId().intValue());
        assertEquals("Wrong password length", PASSWORD_LENGTH, created.getPassword().length());
    }

    @Test
    public void testRegisterUrl() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        assertEquals("Wrong short url length", SHORT_URL_LENGTH, shortUrl.length());
    }

    @Test
    public void testStatistics() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);

        Map<String, Integer> stats = service.getUrlRedirectStats(ACCOUNT_NAME);
        assertEquals("Wrong URL stats size", 1, stats.size());
        assertEquals("Wrong redirect counter", 0, stats.get(URL).intValue());
    }

    @DirtiesContext
    @Test
    public void testHitShortUrl() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        service.registerUrl("http://test2.com", MOVED_PERMANENTLY, ACCOUNT_NAME);

        urlService.hitShortUrl(shortUrl);
        Map<String, Integer> stats = service.getUrlRedirectStats(ACCOUNT_NAME);
        assertEquals("Wrong URL stats size", 2, stats.size());
        assertEquals("Wrong redirect counter", 1, stats.get(URL).intValue());
    }

}