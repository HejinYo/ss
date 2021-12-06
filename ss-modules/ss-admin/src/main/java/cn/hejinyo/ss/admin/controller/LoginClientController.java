package cn.hejinyo.ss.admin.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/20 21:50
 */
@RestController
@RequestMapping("/login")
@AllArgsConstructor
@Slf4j
public class LoginClientController {

    private final RestTemplate restTemplate;

    private final String CODE_CHALLENGE = "hejinyo";


    @GetMapping("/toLogin")
    public void toLogin(HttpServletResponse response) throws IOException {
        String url = "http://hejinyo.com:9001/oauth2/authorize";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(CODE_CHALLENGE.getBytes(StandardCharsets.US_ASCII));
            String encodedVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(url)
                    .queryParam("response_type", "code")
                    .queryParam("scope", "user.userInfo")
                    .queryParam("client_id", "csdn")
                    .queryParam("code_challenge", encodedVerifier)
                    .queryParam("code_challenge_method", "S256")
                    .queryParam("redirect_uri", "http://hejinyo.com:9010/login/code")
                    .build();
            response.sendRedirect(uriComponents.toUri().toString());
        } catch (NoSuchAlgorithmException ex) {
            // It is unlikely that SHA-256 is not available on the server. If it is not available,
            // there will likely be bigger issues as well. We default to SERVER_ERROR.
        }
    }

    @GetMapping("/code")
    public ResponseEntity<String> codeLogin(@RequestParam("code") String code) {
        //header
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        //请求参数 param 设置header之后等同于 http://xxx/xxx?password=xxxxx
        String url = "http://hejinyo.com:9001/oauth2/token";
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(url)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", "csdn")
                // .queryParam("client_secret", "csdn123")
                .queryParam("code_verifier", CODE_CHALLENGE)
                .queryParam("redirect_uri", "http://hejinyo.com:9010/login/code")
                .queryParam("code", code)
                .build();
        log.error("url=>{}", uriComponents.toUri());
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(null, header);
        ResponseEntity<String> result = restTemplate.postForEntity(uriComponents.toUri(), httpEntity, String.class);
        log.error("response=>{}", result);
        return result;
    }

}
