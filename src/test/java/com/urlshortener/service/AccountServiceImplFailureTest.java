package com.urlshortener.service;

import com.urlshortener.service.exceptions.AccountDuplicateException;
import com.urlshortener.service.exceptions.AccountNotFoundException;
import com.urlshortener.service.exceptions.ShortUrlNotFoundException;
import com.urlshortener.service.exceptions.TargetUrlDuplicateException;
import org.junit.Test;
import org.springframework.transaction.TransactionSystemException;

import static com.urlshortener.model.RedirectType.MOVED_PERMANENTLY;

public class AccountServiceImplFailureTest extends AbstractAccountServiceImplTest {

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

    @Test(expected = AccountNotFoundException.class)
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

    @Test(expected = TargetUrlDuplicateException.class)
    public void testRegisterDuplicateUrl() throws Exception {
        service.createAccount(ACCOUNT_NAME);
        service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
        service.registerUrl(URL, MOVED_PERMANENTLY, ACCOUNT_NAME);
    }

    @Test(expected = ShortUrlNotFoundException.class)
    public void testInvalidShortUrl() throws Exception {
        urlService.hitShortUrl("wrong"); //must be 6 symbols
    }

    @Test(expected = ShortUrlNotFoundException.class)
    public void testAbsentShortUrl() throws Exception {
        urlService.hitShortUrl("wrong1");
    }

}
