package org.hzero.core.util;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 域名工具
 *
 * @author bojiangzhou 2020/04/27
 */
public class DomainUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainUtils.class);

    public static final String HTTP = "http://";

    public static final String HTTPS = "https://";

    /**
     * Check the url is an valid domain.
     *
     * @param url domain url
     * @return true if vali
     */
    public static boolean isValidDomain(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        boolean hasProtocol = false;
        if (url.startsWith(HTTP)) {
            hasProtocol = true;
            url = url.substring(HTTP.length());
        } else if (url.startsWith(HTTPS)) {
            hasProtocol = true;
            url = url.substring(HTTPS.length());
        }
        if (!hasProtocol) {
            return false;
        }
        String[] urls = url.split("/");
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(urls[0]);
        } catch (UnknownHostException e) {
            //do nothing
        }
        return inetAddress != null;
    }

    /**
     * Extract uri parameters from uri.
     *
     * @param url url
     * @return Map of uri parameters.
     */
    public static Map<String, String> getQueryMap(String url) {
        String query = getQuery(url);
        if (StringUtils.isBlank(query)) {
            return Collections.emptyMap();
        }

        return Stream.of(query.split("&")).map(kv -> kv.split("=")).collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    /**
     * Get the query of an URI.
     *
     * @param uri a string regarded an URI
     * @return the query string; <code>null</code> if empty or undefined
     */
    public static String getQuery(String uri) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
                "/",
                at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
        );
        // the authority part of URI ignored
        int to = uri.length();
        // reuse the at and from variables to consider the query
        at = uri.indexOf("?", from);
        if (at >= 0) {
            from = at + 1;
        } else {
            return null;
        }
        // check the fragment
        if (uri.lastIndexOf("#") > from) {
            to = uri.lastIndexOf("#");
        }
        // get the path and query.
        return from == to ? null : uri.substring(from, to);
    }


    /**
     * Get the path of an URI.
     *
     * @param uri a string regarded an URI
     * @return the path string
     */
    public static String getPath(String uri) {
        if (uri == null) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
                "/",
                at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
        );
        // the authority part of URI ignored
        int to = uri.length();
        // check the query
        if (uri.indexOf('?', from) != -1) {
            to = uri.indexOf('?', from);
        }
        // check the fragment
        if (uri.lastIndexOf("#") > from && uri.lastIndexOf("#") < to) {
            to = uri.lastIndexOf("#");
        }
        // get only the path.
        return (from < 0) ? (at >= 0 ? "/" : uri) : uri.substring(from, to);
    }


    /**
     * Get the path and query of an URI.
     *
     * @param uri a string regarded an URI
     * @return the path and query string
     */
    public static String getPathQuery(String uri) {
        if (uri == null) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
                "/",
                at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
        );
        // the authority part of URI ignored
        int to = uri.length();
        // Ignore the '?' mark so to ignore the query.
        // check the fragment
        if (uri.lastIndexOf("#") > from) {
            to = uri.lastIndexOf("#");
        }
        // get the path and query.
        return (from < 0) ? (at >= 0 ? "/" : uri) : uri.substring(from, to);
    }


    /**
     * Get the path of an URI and its rest part.
     *
     * @param uri a string regarded an URI
     * @return the string from the path part
     */
    public static String getFromPath(String uri) {
        if (uri == null) {
            return null;
        }
        // consider of net_path
        int at = uri.indexOf("//");
        int from = uri.indexOf(
                "/",
                at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0
        );
        // get the path and its rest.
        return (from < 0) ? (at >= 0 ? "/" : uri) : uri.substring(from);
    }

    /**
     * 获取地址的主机地址
     */
    public static String getHost(String uri) {
        if (StringUtils.isBlank(uri)) {
            return null;
        }
        try {
            URL url = new java.net.URL(uri);
            if (url.getPort() > 0) {
                uri = url.getHost() + ":" + url.getPort();
            } else {
                uri = url.getHost();
            }
        } catch (MalformedURLException e) {
            LOGGER.warn("Parse uri error, uri is [{}]", uri);
            return null;
        }
        return uri;
    }

    /**
     * 获取地址的域名信息
     */
    public static String getDomain(String uri) {
        if (StringUtils.isBlank(uri)) {
            return null;
        }
        try {
            URL url = new java.net.URL(uri);
            if (url.getPort() > 0) {
                uri = url.getHost() + ":" + url.getPort();
            } else {
                uri = url.getHost();
            }
            uri = url.getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            LOGGER.warn("Parse uri error, uri is [{}]", uri);
            return null;
        }
        return uri;
    }

}
