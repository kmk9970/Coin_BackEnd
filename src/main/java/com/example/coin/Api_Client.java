package com.example.coin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
//import org.codehaus.jackson.map.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.squareup.okhttp.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;



@SuppressWarnings("unused")
public class Api_Client {
    protected String api_url = "https://api.bithumb.com";
    protected String api_key;
    protected String api_secret;

    public Api_Client(String api_key, String api_secret) {
        this.api_key = api_key;
        this.api_secret = api_secret;
    }

    /**
     * ������ �ð��� ns�� �����Ѵ�.(1/1,000,000,000 ��)
     *
     * @return int
     */
    private String usecTime() {
    	/*
		long start = System.nanoTime();
		// do stuff
		long nanoseconds = System.nanoTime();
		long microseconds = TimeUnit.NANOSECONDS.toMicros(nanoseconds);
		long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoseconds);

		int elapsedTime = (int) (microseconds + seconds);

		System.out.println("elapsedTime ==> " + microseconds + " : " + seconds);
		*/

        return String.valueOf(System.currentTimeMillis());
    }

    private String request(String strHost, String strMemod, HashMap<String, String> rgParams,  HashMap<String, String> httpHeaders)  {
        String response = "";
//        String answer = "";
//        OkHttpClient client = new OkHttpClient();
//        try {
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody body = RequestBody.create(mediaType, "currency=BTC");
//        Request request = new Request.Builder()
//                .url(strHost)
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("content-type", "application/x-www-form-urlencoded")
//                .addHeader("Api-Key", httpHeaders.get("Api-Key"))
//                .addHeader("Api-Nonce", httpHeaders.get("Api-Nonce"))
//                .addHeader("Api-Sign", httpHeaders.get("Api-Sign"))
//                .build();
//
//                Response response = client.newCall(request).execute();
//
//            answer= response.body().toString();
//            System.out.println(answer);
//            }catch (Exception e){
//            System.out.println("오류 :" + e);
//        }

        // SSL ����
        if (strHost.startsWith("https://")) {
            HttpRequest request = HttpRequest.get(strHost);
            // Accept all certificates
            request.trustAllCerts();
            // Accept all hostnames
            request.trustAllHosts();
        }

        if (strMemod.toUpperCase().equals("HEAD")) {
        } else {
            HttpRequest request = null;

            // POST/GET ����
            if (strMemod.toUpperCase().equals("POST")) {

                request = new HttpRequest(strHost, "POST");
                request.readTimeout(10000);

                System.out.println("POST ==> " + request.url());

                if (httpHeaders != null && !httpHeaders.isEmpty()) {
                    httpHeaders.put("api-client-type", "2");
                    request.headers(httpHeaders);
                    //System.out.println(httpHeaders.toString());
                }
                if (rgParams != null && !rgParams.isEmpty()) {
                    request.form(rgParams);
                    //System.out.println(rgParams.toString());
                }
            } else {
                request = HttpRequest.get(strHost
                        + Util.mapToQueryString(rgParams));
                request.readTimeout(10000);

                //System.out.println("Response was: " + response);
            }

            if (request.ok()) {
                response = request.body();
            } else {
                response = "error : " + request.code() + ", message : "
                        + request.body();
            }
            request.disconnect();
        }

        return response;
        //return  answer;
    }

    public static String encodeURIComponent(String s)
    {
        String result = null;

        try
        {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%26", "&")
                    .replaceAll("\\%3D", "=")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
        }

        return result;
    }

    private HashMap<String, String> getHttpHeaders(String endpoint, HashMap<String, String> rgData, String apiKey, String apiSecret) {

        String strData = Util.mapToQueryString(rgData).replace("?", "");
        String nNonce = usecTime();

        strData = strData.substring(0, strData.length()-1);


        //System.out.println("1 : " + strData);

        strData = encodeURIComponent(strData);

        HashMap<String, String> array = new HashMap<String, String>();


        String str = endpoint + ";"	+ strData + ";" + nNonce;
        //String str = "/info/balance;order_currency=BTC&payment_currency=KRW&endpoint=%2Finfo%2Fbalance;272184496";

        String encoded = asHex(hmacSha512(str, apiSecret));

        //System.out.println("strData was: " + str);
        //System.out.println("apiSecret was: " + apiSecret);
        array.put("Api-Key", apiKey);
        array.put("Api-Sign", encoded);
        array.put("Api-Nonce", String.valueOf(nNonce));

        return array;

    }

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static byte[] hmacSha512(String value, String key){
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    key.getBytes(DEFAULT_ENCODING),
                    HMAC_SHA512);

            Mac mac = Mac.getInstance(HMAC_SHA512);
            mac.init(keySpec);

            final byte[] macData = mac.doFinal( value.getBytes( ) );
            byte[] hex = new Hex().encode( macData );

            //return mac.doFinal(value.getBytes(DEFAULT_ENCODING));
            return hex;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String asHex(byte[] bytes){
        return new String(Base64.encodeBase64(bytes));
    }

    @SuppressWarnings("unchecked")
    public String callApi(String endpoint, HashMap<String, String> params)  {
        String rgResultDecode = "";
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("endpoint", endpoint);

        if (params != null) {
            rgParams.putAll(params);
        }

        String api_host = api_url + endpoint;
        HashMap<String, String> httpHeaders = getHttpHeaders(endpoint, rgParams, api_key, api_secret);
        try{
            System.out.println(api_host);
            rgResultDecode = request(api_host, "POST", rgParams, httpHeaders);
        }catch ( Exception e){
            System.out.println("여기"+e);
        }

        if (!rgResultDecode.startsWith("error")) {
            System.out.println("에러 아님");
            // json �Ľ�
            HashMap<String, String> result;
            try {
                result = new ObjectMapper().readValue(rgResultDecode,
                        HashMap.class);

                System.out.println("==== ��� ��� ====");
                System.out.println(result.get("status").toString());
            } catch (IOException e) {
               System.out.println("오류 "+e);
            }
        }else{
            System.out.println("에러");
        }
        return rgResultDecode;
    }
}
