package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.common.core.util.Utils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/12 22:38
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @Value("${useLocalCache:false}")
    private boolean useLocalCache;

    @RequestMapping("/get")
    public String get() {
        return useLocalCache + "--->" + configurableApplicationContext.getEnvironment().getProperty("useLocalCache");
    }

    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicK = Base64.encodeBase64String(publicKey.getEncoded());
        String privateK = Base64.encodeBase64String(privateKey.getEncoded());
        System.out.println(publicK);
        System.out.println("--------");
        System.out.println(privateK);
        PublicKey publicKeyp = Utils.getPublicKey(publicK);
        PrivateKey privateKeyp = Utils.getPrivateKey(privateK);
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);

        // jks
        char[] pwdArray = "123456".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("/Users/hejinyo/java/marvel/ss/ss-auth/src/main/resources/jwt.jks"), pwdArray);
        RSAPrivateCrtKey privateKey1 = (RSAPrivateCrtKey) ks.getKey("jwt", pwdArray);
        System.out.println(privateKey1);
    }

    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                // 客户端id 需要唯一
                .clientId("SS-WEB")
                // 客户端密码，这里不需要加密，保存的时候自动加密了
                .clientSecret(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456"))
                // 自定义模式的方式需要 NONE 和授权服务器进行认证
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                // 密码模式
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                // 客户端申请的作用域
                .scope("ss:auth")
                .scope("ss:admin")
                .tokenSettings(TokenSettings.builder()
                        // accessToken 的有效期
                        .accessTokenTimeToLive(Duration.ofDays(1))
                        // 是否可重用刷新令牌
                        .reuseRefreshTokens(false).build())
                .build();
        JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        if (null == jdbcRegisteredClientRepository.findByClientId("SS-WEB")) {
            jdbcRegisteredClientRepository.save(registeredClient);
        }
        return jdbcRegisteredClientRepository;
    }
}
