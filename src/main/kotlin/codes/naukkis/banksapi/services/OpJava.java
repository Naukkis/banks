package codes.naukkis.banksapi.services;

import codes.naukkis.banksapi.config.Config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Map;

public class OpJava {
    private Config config;
    private HttpClient httpClient;

    public OpJava(Config config) {
        this.config = config;
        this.httpClient = buildAndGetHttpClient();
    }

    private static final String TPP_AUTHENTICATION_URL = "https://mtls-apis.psd2-sandbox.op.fi/oauth/token";

    public String getAccessToken() {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(buildAndGetAccessRequest(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, String> stringStringMap = getResponseBodyAsMap(response);
        return stringStringMap.get("access_token");
    }

    @Nullable
    private Map<String, String> getResponseBodyAsMap(HttpResponse<String> response) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseBody = null;
        try {
            responseBody = objectMapper.readValue(response.body(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    public String registerTppIntent(String accessToken) {
        try {
            HttpRequest registerRequest = getRegisterRequest(accessToken);
            HttpResponse<String> response = httpClient.send(registerRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            Map<String, String> responseBodyAsMap = getResponseBodyAsMap(response);
            return responseBodyAsMap.get("authorizationId");

            // todo other fields: created, status, expires
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "jes";
    }

    private HttpRequest getRegisterRequest(String accessToken) {
        Map<String, String> params = Map.of(
                "expires", LocalDateTime.now().plusDays(1L).toString() + "Z",
                "transactionFrom", "2020-11-22",
                "transactionTo", "2020-11-24"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";
        try {
            requestBody = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(params);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("https://mtls-apis.psd2-sandbox.op.fi/accounts-psd2/v1/authorizations"))
                .setHeader("x-api-key", config.opApiKey)
                .setHeader("Authorization", "Bearer " + accessToken)
                .setHeader("x-fapi-financial-id", "test")
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
    }


    private HttpClient buildAndGetHttpClient() {
        try {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .sslContext(getSslContext())
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException | CertificateException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpRequest buildAndGetAccessRequest() {
        var params = Map.of(
                "grant_type", "client_credentials",
                "scope", "accounts",
                "client_id", config.opClientId,
                "client_secret", config.opClientSecret);

        return HttpRequest.newBuilder()
                .POST(createFormData(params))
                .uri(URI.create(TPP_AUTHENTICATION_URL))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
    }


    private HttpRequest.BodyPublisher createFormData(Map<String, String> data) {
        var builder = new StringBuilder();

        for (String key : data.keySet()) {
            if (builder.toString().length() != 0) {
                builder.append("&");
            }
            builder.append(key);
            builder.append("=");
            builder.append(data.get(key));
        }

        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private SSLContext getSslContext() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(config.opTppCert);
        clientStore.load(resource, config.opTppCertPassword.toCharArray());

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.setProtocol("TLSv1.2");
        sslContextBuilder.loadKeyMaterial(clientStore, config.opTppCertPassword.toCharArray());
        sslContextBuilder.loadTrustMaterial(TrustAllStrategy.INSTANCE);
        return sslContextBuilder.build();
    }

}
