package cn.hejinyo.ss.auth.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/3 21:34
 */
@UtilityClass
public class HttpUtils {

    public void writeResult(HttpServletResponse response, String msg) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getWriter().write(JsonUtils.toJson(Result.error(msg)));
    }
}
