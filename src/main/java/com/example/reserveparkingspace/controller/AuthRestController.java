package com.example.reserveparkingspace.controller;

import com.example.reserveparkingspace.entity.UserEntity;
import com.example.reserveparkingspace.other.LoginResponse;
import com.example.reserveparkingspace.repository.UserRepo;
import com.example.reserveparkingspace.util.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * auth restController
 *
 * @author dafengzhen
 */
@RequestMapping("/api/public")
@RestController
public class AuthRestController {

    @Value("${appId}")
    private String appId;

    @Value("${appSecret}")
    private String appSecret;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String jsCode) throws JsonProcessingException {
        // 参考文档
        // https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
        // https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html

        // required parameter
        /*
            appid	    string		    是	小程序 appId
            secret	    string		    是	小程序 appSecret
            js_code	    string		    是	登录时获取的 code
            grant_type	string		    是	授权类型，此处只需填写 authorization_code
        */

        // errcode 的合法值
        /*
            -1	    系统繁忙，此时请开发者稍候再试
            0	    请求成功
            40029	code 无效
            45011	频率限制，每个用户每分钟100次
        */

        // response
        /*
            openid	    string	用户唯一标识
            session_key	string	会话密钥
            unionid	    string	用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回，详见 UnionID 机制说明。
            errcode	    number	错误码
            errmsg	    string	错误信息
        */

        String reqUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + jsCode + "&grant_type=authorization_code";
        ResponseEntity<String> response = restTemplate.getForEntity(reqUrl, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode errMsg = root.path("errmsg");
        JsonNode unionId = root.path("unionid");
        System.out.println(response);

        if (!errMsg.isMissingNode()) {
            throw new RuntimeException("登录失败");
        }

        UserEntity user = new UserEntity();
        user.setWxOpenId(root.get("openid").asText());
        user.setWxSessionKey(root.get("session_key").asText());

        if (!unionId.isMissingNode()) {
            user.setUnionId(root.get("unionid").asText());
        }

        UserEntity newUser = userRepo.findByWxOpenId(user.getWxOpenId()).orElseGet(() -> userRepo.save(user));
        newUser.setWxSessionKey(user.getWxSessionKey());
        newUser = userRepo.save(newUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtTokenUtil.generateAccessToken(newUser));
        loginResponse.setUserId(newUser.getId());
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/getUserInfo")
    public ResponseEntity<String> getUserInfo(@RequestBody String data, Authentication authentication) throws JsonProcessingException {
        // 参考文档
        /*
            https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
            https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html
        */

        // 注意事项：需要提前获取用户的授权（1.前端请求授权 2.将拿到的数据在后端解密，或者在前端解密）

        UserEntity user = (UserEntity) authentication.getPrincipal();
        System.out.println(user);

        JsonNode root = objectMapper.readTree(data);
        String encryptedData = root.get("encryptedData").asText();
        String iv = root.get("iv").asText();
        String decryptData = decryptData(user.getWxSessionKey(), encryptedData, iv);

        JsonNode decrypt = objectMapper.readTree(decryptData);
        if (!Objects.equals(decrypt.get("watermark").get("appid").asText(), appId)) {
            throw new RuntimeException("获取用户信息失败");
        }

        System.out.println(decryptData);
        return ResponseEntity.ok().body(decrypt.get("nickName").asText());
    }

    /**
     * 解密微信小程序加密数据
     *
     * @param sessionKey    sessionKey
     * @param encryptedData encryptedData
     * @param iv            iv
     * @return String
     */
    private String decryptData(String sessionKey, String encryptedData, String iv) {
        // 参考文档
        // https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html
        // https://www.cnblogs.com/rogersma/p/13664374.html

        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] encryptedDataByte = base64Decoder.decodeBuffer(encryptedData);
            byte[] sessionKeyByte = base64Decoder.decodeBuffer(sessionKey);
            byte[] ivByte = base64Decoder.decodeBuffer(iv);

            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeyByte, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivByte);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] original = cipher.doFinal(encryptedDataByte);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Illegal Buffer");
        }
    }

}
