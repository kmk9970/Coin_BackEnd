package com.example.coin;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class GetMyAccount {
    public static void main(String[] args) {
        String accessKey = "e8d62d019ec15f02480fff030e52db42";
        String secretKey = "eddf5053c29a30136cc954f41d49df65";

        String apiUrl = "https://api.bithumb.com";

        // Generate access token
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("timestamp", System.currentTimeMillis())
                .sign(algorithm);
        String authenticationToken = "Bearer " + jwtToken;

        // Call API
        final HttpGet httpRequest = new HttpGet(apiUrl + "/v1/accounts");
        httpRequest.addHeader("Authorization", authenticationToken);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(httpRequest)) {
            // handle to response
            int httpStatus = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println(httpStatus);
            System.out.println(responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
