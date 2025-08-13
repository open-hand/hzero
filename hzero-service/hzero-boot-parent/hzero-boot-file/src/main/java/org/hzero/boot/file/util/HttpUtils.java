package org.hzero.boot.file.util;

import feign.Response;
import org.apache.commons.io.IOUtils;
import org.hzero.boot.file.constant.BootFileConstant;
import org.hzero.boot.file.dto.RequestParamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.choerodon.core.exception.CommonException;

/**
 * @author : yinbo.shi@hand-china.com 2019/1/29 17:11
 */
public class HttpUtils {

    private HttpUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 设置请求报头 HTTP Headers
     *
     * @param documentServerToken documentServerToken
     * @param httpURLConnection   HttpURLConnection
     */
    private static void initPostHeaders(HttpURLConnection httpURLConnection, String documentServerToken) {
        // 设置Headers参数
        try {
            // 需要输出
            httpURLConnection.setDoOutput(true);
            // 需要输入
            httpURLConnection.setDoInput(true);
            // 不允许缓存
            httpURLConnection.setUseCaches(false);
            // 请求方式
            httpURLConnection.setRequestMethod(RequestMethod.POST.name());
            // 请求体
            httpURLConnection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            // token
            httpURLConnection.setRequestProperty("token", documentServerToken);
            // 接受参数为JSON，不设置的话默认返回的是XML格式
            httpURLConnection.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
        } catch (IOException e) {
            logger.error("设置请求报头 HTTP Headers异常：{}", e.getMessage());
            throw new CommonException(BootFileConstant.ErrorCode.BUILD_HEADER);
        }
    }

    /**
     * 向documentServer 发送请求
     *
     * @param requestParam 请求参数
     * @return html
     */
    public static StringBuilder sendRequestToDocumentServer(RequestParamDTO requestParam) {
        try {
            // 建立连接
            URL url = new URL(requestParam.getDocServerUrl());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置请求头信息
            initPostHeaders(httpURLConnection, requestParam.getAccessToken());
            // 连接会话
            httpURLConnection.connect();
            // 建立输入流，向指向的URL传入参数
            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            // 设置请求参数
            dos.write(requestParam.getBodyJson().getBytes(StandardCharsets.UTF_8));
            dos.flush();
            dos.close();
            // 获得响应状态
            int resultCode = httpURLConnection.getResponseCode();
            StringBuilder stringBuilder;
            // 得到返回数据
            if (HttpURLConnection.HTTP_OK == resultCode) {
                stringBuilder = new StringBuilder();
                String readLine;
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
                while ((readLine = responseReader.readLine()) != null) {
                    stringBuilder.append(readLine);
                }
                responseReader.close();
            } else {
                logger.error("current onlyOffice resultCode was:{}", resultCode);
                throw new CommonException(BootFileConstant.ErrorCode.REQUEST_DOCUMENT_SERVER);
            }
            return stringBuilder;
        } catch (Exception e) {
            throw new CommonException(BootFileConstant.ErrorCode.REQUEST_DOCUMENT_SERVER);
        }
    }

    /**
     * 下载
     *
     * @param url 下载地址
     * @return 下载输入流
     */
    public static InputStream download(String url) {
        Assert.notNull(url, "Url is not null");
        URL downUrl;
        try {
            downUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) downUrl.openConnection();
            // 设置超时间为10秒
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            return conn.getInputStream();
        } catch (MalformedURLException e) {
            throw new CommonException("Download url is error:{0}", url);
        } catch (IOException e) {
            throw new CommonException("Download file error:{0}", url);
        }
    }

    /**
     * 下载
     *
     * @param url 下载地址
     * @return 二进制数据
     */
    public static byte[] downloadData(String url) {
        Assert.notNull(url, "Url is not null");
        try (InputStream inputStream = HttpUtils.download(url)) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new CommonException("Download file error:{0}", url);
        }
    }

    /**
     * 文件下载补偿重定向， 参考 feign.Client#convertResponse
     *
     * @param url 下载地址
     * @return response
     */
    public static Response redirect(String url) {
        Assert.notNull(url, "Url is not null");
        URL downUrl;
        try {
            downUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) downUrl.openConnection();
            // 设置超时间为10秒
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            Map<String, Collection<String>> headers = new LinkedHashMap<>();
            for (Map.Entry<String, List<String>> field : conn.getHeaderFields().entrySet()) {
                // response message
                if (field.getKey() != null) {
                    headers.put(field.getKey(), field.getValue());
                }
            }
            Integer length = conn.getContentLength();
            if (length == -1) {
                length = null;
            }
            InputStream stream;
            int status = conn.getResponseCode();
            if (status >= 400) {
                stream = conn.getErrorStream();
            } else {
                stream = conn.getInputStream();
            }
            return Response.builder()
                    .status(status)
                    .reason(conn.getResponseMessage())
                    .headers(headers)
                    .body(stream, length)
                    .build();
        } catch (MalformedURLException e) {
            throw new CommonException("Download url is error:{0}", url);
        } catch (IOException e) {
            throw new CommonException("Download file error:{0}", url);
        }
    }
}
