package com.mojang.authlib;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Map;

public abstract class HttpAuthenticationService extends BaseAuthenticationService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Proxy proxy;

    protected HttpAuthenticationService(Proxy proxy) {
        Validate.notNull(proxy);
        this.proxy = proxy;
    }

    /**
     * Gets the proxy to be used with every HTTP(S) request.
     *
     * @return Proxy to be used.
     */
    public Proxy getProxy() {
        return proxy;
    }

    protected HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        LOGGER.debug("Opening connection to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    /**
     * Performs a POST request to the specified URL and returns the result.
     * <p/>
     * The POST data will be encoded in UTF-8 as the specified contentType. The response will be parsed as UTF-8.
     * If the server returns an error but still provides a body, the body will be returned as normal.
     * If the server returns an error without any body, a relevant {@link java.io.IOException} will be thrown.
     *
     * @param url         URL to submit the POST request to
     * @param post        POST data in the correct format to be submitted
     * @param contentType Content type of the POST data
     * @return Raw text response from the server
     * @throws IOException The request was not successful
     */
    public String performPostRequest(URL url, String post, String contentType) throws IOException {
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
        HttpURLConnection connection = createUrlConnection(url);
        byte[] postAsBytes = post.getBytes(Charsets.UTF_8);

        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);

        LOGGER.debug("Writing POST data to " + url + ": " + post);

        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            IOUtils.write(postAsBytes, outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        LOGGER.debug("Reading data from " + url);

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
            LOGGER.debug("Response: " + result);
            return result;
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                LOGGER.debug("Reading error page from " + url);
                String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
                LOGGER.debug("Response: " + result);
                return result;
            } else {
                LOGGER.debug("Request failed", e);
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Performs a GET request to the specified URL and returns the result.
     * <p/>
     * The response will be parsed as UTF-8.
     * If the server returns an error but still provides a body, the body will be returned as normal.
     * If the server returns an error without any body, a relevant {@link java.io.IOException} will be thrown.
     *
     * @param url URL to submit the GET request to
     * @return Raw text response from the server
     * @throws IOException The request was not successful
     */
    public String performGetRequest(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = createUrlConnection(url);

        LOGGER.debug("Reading data from " + url);

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
            LOGGER.debug("Response: " + result);
            return result;
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                LOGGER.debug("Reading error page from " + url);
                String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
                LOGGER.debug("Response: " + result);
                return result;
            } else {
                LOGGER.debug("Request failed", e);
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Creates a {@link URL} with the specified string, throwing an {@link java.lang.Error} if the URL was malformed.
     * <p/>
     * This is just a wrapper to allow URLs to be created in constants, where you know the URL is valid.
     *
     * @param url URL to construct
     * @return URL constructed
     */
    public static URL constantURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            throw new Error("Couldn't create constant for " + url, ex);
        }
    }

    /**
     * Turns the specified Map into an encoded & escaped query
     *
     * @param query Map to convert into a text based query
     * @return Resulting query.
     */
    public static String buildQuery(Map<String, Object> query) {
        if (query == null) return "";
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Object> entry : query.entrySet()) {
            if (builder.length() > 0) {
                builder.append('&');
            }

            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Unexpected exception building query", e);
            }

            if (entry.getValue() != null) {
                builder.append('=');
                try {
                    builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("Unexpected exception building query", e);
                }
            }
        }

        return builder.toString();
    }

    /**
     * Concatenates the given {@link java.net.URL} and query.
     *
     * @param url   URL to base off
     * @param query Query to append to URL
     * @return URL constructed
     */
    public static URL concatenateURL(URL url, String query) {
        try {
            if (url.getQuery() != null && url.getQuery().length() > 0) {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "&" + query);
            } else {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + query);
            }
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Could not concatenate given URL with GET arguments!", ex);
        }
    }
}
