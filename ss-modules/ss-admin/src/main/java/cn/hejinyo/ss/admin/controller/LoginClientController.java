package cn.hejinyo.ss.admin.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
                .queryParam("client_secret", "csdn123")
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
