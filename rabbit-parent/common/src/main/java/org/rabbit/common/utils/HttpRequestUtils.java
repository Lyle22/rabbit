package org.rabbit.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * The type Http Request Utils.
 */
@Slf4j
public class HttpRequestUtils {

    private static final String UTF8 = "UTF8";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    /**
     * Send request of post
     *
     * @param requestUrl the url of request
     * @param params     data
     * @return String the string of json format
     * @throws IOException
     */
    public static String postByForm(String requestUrl, Map<String, String> params) throws IOException {
        HttpURLConnection conn = buildConnection(requestUrl, POST, CONTENT_TYPE_FORM, null);

        byte[] data = getRequestData(params, UTF8).toString().getBytes();
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(data);

        return response(conn);
    }

    public static String postByForm(String requestUrl, Map<String, String> params, String accessToken) throws IOException {
        HttpURLConnection conn = buildConnection(requestUrl, POST, CONTENT_TYPE_FORM, accessToken);

        byte[] data = getRequestData(params, UTF8).toString().getBytes();
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(data);

        return response(conn);
    }

    public static String postByJson(String requestUrl, String params, String accessToken) throws IOException {
        HttpURLConnection conn = buildConnection(requestUrl, POST, APPLICATION_JSON, accessToken);

        byte[] data = params.getBytes();
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));

        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(data);

        return response(conn);
    }

    public static String postByJson(String requestUrl, String params, Map<String, String> headMap) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(POST);
            conn.setRequestProperty("Content-Type", APPLICATION_JSON);
            conn.setRequestProperty("Accept", APPLICATION_JSON);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (!headMap.isEmpty()) {
            HttpURLConnection finalConn = conn;
            for (String key : headMap.keySet()) {
                conn.setRequestProperty(key, headMap.get(key));
            }
        }
        byte[] data = params.getBytes();
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(data);

        return response(conn);
    }

    public static String getByJson(String requestUrl, String accessToken) {
        HttpURLConnection conn = buildConnection(requestUrl, GET, APPLICATION_JSON, accessToken);
        return response(conn);
    }

    public static String getByJson(String requestUrl) {
        HttpURLConnection conn = buildConnection(requestUrl, GET, APPLICATION_JSON, null);
        return response(conn);
    }

    private static String response(HttpURLConnection conn) {
        try {
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                StringBuilder response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                return response.toString();
            } else {
                log.warn(String.format("Connection returned HTTP code: %s with message: %s", responseCode,
                        conn.getResponseMessage()));
                return null;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            log.error("Convert data to string has error, the message is: {}", e.getMessage());
            e.printStackTrace();
        }
        return stringBuffer;
    }

    private static HttpURLConnection buildConnection(String requestUrl, String method, String contentType, String accessToken) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(method);
            if (StringUtils.isNotBlank(accessToken)) {
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            }
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Accept", APPLICATION_JSON);
            return conn;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }


    public static String requestByJson(String url, HttpMethod Methods, Map<String, String> UrlParams,
                           Map<String, String> HeaderParams) throws IOException {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = Methods;
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!HeaderParams.isEmpty()) {
            HeaderParams.forEach((k, v) -> {
                headers.set(k, v);
            });
        }
        // 将请求头部和参数合成一个请求
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (Methods == HttpMethod.GET) {
            url = url + "?" + getRequestData(UrlParams, UTF8).toString();
        } else {
            if (!UrlParams.isEmpty()) {
                UrlParams.forEach((k, v) -> {
                    params.put(k, Collections.singletonList(v));
                });
            }
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        // 执行HTTP请求，将返回的结构使用spring ResponseEntity处理http响应
        ResponseEntity<byte[]> responseEntity = client.exchange(url, method, requestEntity, byte[].class);
        String contentEncoding = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
        // gzip编码
        if ("gzip".equals(contentEncoding)) {
            byte[] data = unGZip(new ByteArrayInputStream(responseEntity.getBody()));
            return new String(data);
        } else {
            return new String(responseEntity.getBody());
        }
    }

    /**
     * Gzip解压缩
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] unGZip(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            byte[] buf = new byte[4096];
            int len = -1;
            while ((len = gzipInputStream.read(buf, 0, buf.length)) != -1) {
                byteArrayOutputStream.write(buf, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        } finally {
            byteArrayOutputStream.close();
        }
    }

}
