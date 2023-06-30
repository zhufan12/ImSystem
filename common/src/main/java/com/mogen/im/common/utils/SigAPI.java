package com.mogen.im.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


public class SigAPI {
    final private long appId;
    final private String key;

    private static ObjectMapper objectMapper = JsonUtils.getInstance();

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public SigAPI(long appId, String key) {
        this.appId = appId;
        this.key = key;
    }


    public static void main(String[] args) throws InterruptedException {
        SigAPI asd = new SigAPI(10000, "123456");
        String sign = asd.genUserSig("lld", 100000000);
//        Thread.sleep(2000L);
        JsonNode jsonObject = decodeUserSig(sign);
        System.out.println("sign:" + sign);
        System.out.println("decoder:" + jsonObject.toString());
    }

    public static JsonNode decodeUserSig(String userSig) {
        JsonNode sigDoc = null;
        try {
            byte[] decodeUrlByte = Base64URL.base64DecodeUrlNotReplace(userSig.getBytes());
            byte[] decompressByte = decompress(decodeUrlByte);
            String decodeText = new String(decompressByte, "UTF-8");

            if (!StringUtils.isEmpty(decodeText)) {
                sigDoc = objectMapper.readTree(decodeText);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return sigDoc;
    }

    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }

    public String genUserSig(String userid, long expire) {
        return genUserSig(userid, expire, null);
    }


    private String hmacsha256(String identifier, long currTime, long expire, String base64Userbuf) {
        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
                + "TLS.appId:" + appId + "\n"
                + "TLS.expireTime:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        if (null != base64Userbuf) {
            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
        }
        try {
            byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            return (Base64.getEncoder().encodeToString(byteSig)).replaceAll("\\s*", "");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    private String genUserSig(String userid, long expire, byte[] userbuf) {

        long currTime = System.currentTimeMillis() / 1000;

        Map<String,Object> sigDoc = new HashMap<>();
        sigDoc.put("TLS.identifier", userid);
        sigDoc.put("TLS.appId", appId);
        sigDoc.put("TLS.expire", expire);
        sigDoc.put("TLS.expireTime", currTime);

        String base64UserBuf = null;
        if (null != userbuf) {
            base64UserBuf = Base64.getEncoder().encodeToString(userbuf).replaceAll("\\s*", "");
            sigDoc.put("TLS.userbuf", base64UserBuf);
        }
        String sig = hmacsha256(userid, currTime, expire, base64UserBuf);
        if (sig.length() == 0) {
            return "";
        }
        sigDoc.put("TLS.sig", sig);
        Deflater compressor = new Deflater();

        try {
            compressor.setInput( objectMapper.writeValueAsString(sigDoc).getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }

    public String genUserSig(String userid, long expire, long time,byte [] userbuf) {

        Map<String,Object> sigDoc = new HashMap<>();
        sigDoc.put("TLS.identifier", userid);
        sigDoc.put("TLS.appId", appId);
        sigDoc.put("TLS.expire", expire);
        sigDoc.put("TLS.expireTime", time);

        String base64UserBuf = null;
        if (null != userbuf) {
            base64UserBuf = Base64.getEncoder().encodeToString(userbuf).replaceAll("\\s*", "");
            sigDoc.put("TLS.userbuf", base64UserBuf);
        }
        String sig = hmacsha256(userid, time, expire, base64UserBuf);
        if (sig.length() == 0) {
            return "";
        }
        sigDoc.put("TLS.sig", sig);
        Deflater compressor = new Deflater();
        try {
            compressor.setInput(objectMapper.writeValueAsString(sigDoc).getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }

}