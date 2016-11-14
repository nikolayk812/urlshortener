package com.urlshortener.web.rest;

import com.urlshortener.AppConfig;
import com.urlshortener.LocalAppConfig;
import com.urlshortener.service.AccountService;
import com.urlshortener.service.UrlService;
import com.urlshortener.util.TestUtils;
import com.urlshortener.web.SecurityConfig;
import com.urlshortener.web.WebConfig;
import com.urlshortener.web.rest.dto.UrlRegisterRequest;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import static com.urlshortener.util.Constants.PASSWORD_LENGTH;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ContextConfiguration(classes = {AppConfig.class, LocalAppConfig.class, WebConfig.class, SecurityConfig.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:db/h2/initDB.sql")
public abstract class AbstractRestResourceTest {
    static final String ACCOUNT_NAME = "first";
    static final String ACCOUNT_NAME_2 = "second";
    static final String URL = "http://test.com";
    static final UrlRegisterRequest URL_REGISTER_REQUEST = new UrlRegisterRequest(URL);
    static final TestUtils.StringLengthMatcher PASSWORD_LENGTH_MATCHER =
            new TestUtils.StringLengthMatcher(PASSWORD_LENGTH);

    protected MockMvc mockMvc;

    @Autowired
    protected AccountService service;

    @Autowired
    protected UrlService urlService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    void postConstruct() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

}