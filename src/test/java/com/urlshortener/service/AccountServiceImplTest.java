package com.urlshortener.service;

import com.urlshortener.AppConfig;
import com.urlshortener.LocalAppConfig;
import com.urlshortener.model.Account;
import com.urlshortener.service.exceptions.AccountDuplicateException;
import com.urlshortener.service.exceptions.NotFoundException;
import com.urlshortener.service.exceptions.UrlDuplicateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionSystemException;

import java.util.Map;

import static com.urlshortener.model.RedirectType.MOVED_PERMANENTLY;
import static com.urlshortener.util.Constants.PASSWORD_LENGTH;
import static com.urlshortener.util.Constants.SHORT_URL_LENGTH;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

@ContextConfiguration(classes = {AppConfig.class, LocalAppConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:db/h2/initDB.sql")
public class AccountServiceImplTest {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImplTest.class);

    private static final int ACCOUNT_ID = 100;
    private static final String ACCOUNT_NAME = "first";
    private static final String URL = "http://test.com";

    @Autowired
    private AccountService service;

    @Autowired
    private UrlService urlService;

    @Test
    public void testCreateAccount() throws Exception {
        Account created = service.createAccount(ACCOUNT_NAME);
        assertEquals("Wrong account name", ACCOUNT_NAME, created.getName());
        assertEquals("Wrong account id", ACCOUNT_ID, created.getId().intValue());
        assertEquals("Wrong password length", PASSWORD_LENGTH, created.getPassword().length());
    }

    @Test(expected = AccountDuplicateException.class)
    public void testCreateAccountDuplicate() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        service.createAccount(ACCOUNT_NAME);
    }

    //TODO: Hibernate Validator throws javax.validation.ConstraintViolationException which is not org.hibernate.HibernateException
    //TODO: which results in wrong translation and weird TransactionSystemException being thrown
    @Test(expected = TransactionSystemException.class)
    public void testCreateAccountEmptyName() throws Exception {
        service.createAccount("");
    }

    @Test(expected = TransactionSystemException.class)
    public void testCreateAccountInvalidName() throws Exception {
        service.createAccount("?%");
    }

    @Test
    public void testRegisterUrl() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        String shortUrl = service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        assertEquals("Wrong short url length", SHORT_URL_LENGTH, shortUrl.length());
    }

    @Test(expected = NotFoundException.class)
    public void testRegisterUrlForAbsentAccount() throws Exception {
        service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
    }

    //TODO: Hibernate Validator throws javax.validation.ConstraintViolationException which is not org.hibernate.HibernateException
    //TODO: which results in wrong translation and weird TransactionSystemException being thrown
    @Test(expected = TransactionSystemException.class)
    public void testRegisterInvalidUrl() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        service.registerUrl("some bad url", MOVED_PERMANENTLY, ACCOUNT_NAME);
    }

    @Test(expected = UrlDuplicateException.class)
    public void testRegisterDuplicateUrl() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
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

    @Test(expected = NotFoundException.class)
    public void testInvalidShortUrl() throws Exception {
        urlService.hitShortUrl("wrong"); //must be 6 symbols
    }

    @Test(expected = NotFoundException.class)
    public void testAbsentShortUrl() throws Exception {
        urlService.hitShortUrl("wrong1");
    }

}