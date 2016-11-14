package com.urlshortener.model;

/**
 * Redirect type
 * @see org.springframework.http.HttpStatus
 */
public enum RedirectType {

    /**
     * {@code 301 Moved Permanently}.
     * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.2">HTTP/1.1: Semantics and Content, section 6.4.2</a>
     */
    MOVED_PERMANENTLY(301),

    /**
     * {@code 302 Found}.
     * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.3">HTTP/1.1: Semantics and Content, section 6.4.3</a>
     */
    FOUND(302);

    private final int code;

    RedirectType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
