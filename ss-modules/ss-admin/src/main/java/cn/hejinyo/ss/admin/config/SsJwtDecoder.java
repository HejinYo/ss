package cn.hejinyo.ss.admin.config;

import cn.hejinyo.ss.admin.feign.AuthService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义解析验证JWT
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/24 19:05
 */
@Slf4j
public class SsJwtDecoder implements JwtDecoder {

    private final JWTProcessor<SecurityContext> jwtProcessor;

    private final Converter<Map<String, Object>, Map<String, Object>> claimSetConverter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    private final OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefault();

    private JWKSetCache jwkSetCache;

    private static final String DECODING_ERROR_MESSAGE_TEMPLATE = "An error occurred while attempting to decode the Jwt: %s";

    public SsJwtDecoder(AuthService authService) {
        JWKSource<SecurityContext> jwkSource = (jwkSelector, securityContext) -> jwkSelector.select(this.updateJWKSetFromURL(authService));
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource));
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
        });
        this.jwtProcessor = jwtProcessor;
    }

    private JWKSet updateJWKSetFromURL(AuthService authService) throws RemoteKeySourceException {
        String content = authService.jwks();
        JWKSet jwkSet;
        try {
            jwkSet = JWKSet.parse(content);
        } catch (java.text.ParseException e) {
            throw new RemoteKeySourceException("Couldn't parse remote JWK set: " + e.getMessage(), e);
        }
        // jwkSetCache.put(jwkSet);
        return jwkSet;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        JWT jwt = parse(token);
        if (jwt instanceof PlainJWT) {
            log.trace("Failed to decode unsigned token");
            throw new BadJwtException("Unsupported algorithm of " + jwt.getHeader().getAlgorithm());
        }
        Jwt createdJwt = createJwt(token, jwt);
        return validateJwt(createdJwt);
    }

    private Jwt validateJwt(Jwt jwt) {
        OAuth2TokenValidatorResult result = this.jwtValidator.validate(jwt);
        if (result.hasErrors()) {
            Collection<OAuth2Error> errors = result.getErrors();
            String validationErrorString = getJwtValidationExceptionMessage(errors);
            throw new JwtValidationException(validationErrorString, errors);
        }
        return jwt;
    }

    private String getJwtValidationExceptionMessage(Collection<OAuth2Error> errors) {
        for (OAuth2Error oAuth2Error : errors) {
            if (StringUtils.hasText(oAuth2Error.getDescription())) {
                return String.format(DECODING_ERROR_MESSAGE_TEMPLATE, oAuth2Error.getDescription());
            }
        }
        return "Unable to validate Jwt";
    }

    private Jwt createJwt(String token, JWT parsedJwt) {
        try {
            JWTClaimsSet jwtClaimsSet = this.jwtProcessor.process(parsedJwt, null);
            Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
            Map<String, Object> claims = this.claimSetConverter.convert(jwtClaimsSet.getClaims());
            return Jwt.withTokenValue(token)
                    .headers((h) -> h.putAll(headers))
                    .claims((c) -> c.putAll(claims))
                    .build();
            // @formatter:on
        } catch (Exception ex) {
            log.trace("Failed to process JWT", ex);
            throw new BadJwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
        }
    }

    private JWT parse(String token) {
        try {
            return JWTParser.parse(token);
        } catch (Exception ex) {
            log.trace("Failed to parse token", ex);
            throw new BadJwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
        }
    }
}
