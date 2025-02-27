package com.example.demo.proxy;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfigurableOkHttpProxy {
    private static OkHttpClient okHttpClient;
    private static Map<String, String> routeMap = new LinkedHashMap<>(); // 保持插入顺序

    public static void main(String[] args) throws Exception {
        // 加载路由配置
        loadRouteConfig("proxy-config.yml");

        // 初始化 OkHttp 客户端（支持 HTTPS）
        okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory(), trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        // 启动代理服务器
        HttpServer server = HttpServer.create(new InetSocketAddress(18080), 0);
        server.createContext("/", exchange -> handleRequest(exchange));
        server.start();
        System.out.println("Configurable Proxy running on port 18080");
    }

    // 路由配置加载（支持 YAML）
    private static void loadRouteConfig(String configPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File configFile = Paths.get(configPath).toFile();
        
        // 解析 YAML 结构
        Map<String, Object> config = mapper.readValue(configFile, Map.class);
        Map<String, String> routes = (Map<String, String>) config.get("routes");
        
        // 按路径长度降序排序（确保最长路径优先匹配）
        routes.entrySet().stream()
              .sorted((e1, e2) -> Integer.compare(e2.getKey().length(), e1.getKey().length()))
              .forEachOrdered(e -> routeMap.put(e.getKey(), e.getValue()));
    }

    // 请求处理逻辑
    private static void handleRequest(HttpExchange exchange) throws IOException {
        try {
            URI originalUri = exchange.getRequestURI();
            String originalPath = originalUri.getPath();
            
            // 1. 查找匹配的路由规则
            Map.Entry<String, String> matchedRoute = findMatchedRoute(originalPath);
            if (matchedRoute == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            // 2. 构建目标 URL
            String targetBase = matchedRoute.getValue();
            String remainingPath = originalPath.replaceFirst(matchedRoute.getKey(), "");
            String targetUrl = buildTargetUrl(targetBase, remainingPath, originalUri.getQuery());

            // 3. 创建转发请求
            byte[] requestBytes = IOUtils.toByteArray(exchange.getRequestBody());
            Request.Builder requestBuilder = new Request.Builder()
                    .url(targetUrl)
                    .method(exchange.getRequestMethod(), 
                           exchange.getRequestMethod().equalsIgnoreCase("GET") ? null :
                           RequestBody.create(requestBytes));

            // 4. 复制请求头
            exchange.getRequestHeaders().forEach((key, values) -> {
                if (!"Host".equalsIgnoreCase(key)) {
                    requestBuilder.header(key, String.join(", ", values));
                }
            });

            // 5. 执行转发
            try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
                // 复制响应头
                response.headers().forEach(pair -> 
                    exchange.getResponseHeaders().add(pair.getFirst(), pair.getSecond()));
                
                // 发送响应状态码
                exchange.sendResponseHeaders(response.code(), 0);
                
                // 复制响应体
                if (response.body() != null) {
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.body().bytes());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(502, -1); // Bad Gateway
        } finally {
            exchange.close();
        }
    }

    // 路由匹配算法（最长前缀匹配）
    private static Map.Entry<String, String> findMatchedRoute(String path) {
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            String routeKey = entry.getKey()
                    .replace("**", ".*")  // 处理通配符
                    .replace("*", "[^/]*");
            
            if (path.matches("^" + routeKey + ".*")) {
                return entry;
            }
        }
        return null;
    }

    // URL 构建工具
    private static String buildTargetUrl(String baseUrl, String remainingPath, String query) throws URISyntaxException {
        URI baseUri = URI.create(baseUrl);
        String path = baseUri.getPath() != null ? baseUri.getPath() : "";
        
        // 处理路径拼接
        String fullPath = path + (path.endsWith("/") ? remainingPath.replaceFirst("/", "") : remainingPath);
        String newPath = fullPath.replaceAll("/+", "/"); // 去除多余斜杠
        
        // 构建完整 URL
        return new URI(
            baseUri.getScheme(),
            baseUri.getUserInfo(),
            baseUri.getHost(),
            baseUri.getPort(),
            newPath,
            query,
            null
        ).toString();
    }

    // HTTPS 支持（生产环境应使用正规证书）
    private static SSLSocketFactory createSSLSocketFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    private static final X509TrustManager[] trustAllCerts = new X509TrustManager[]{
        new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        }
    };
}
