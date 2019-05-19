package cn.hejinyo.ss.mysql.mock;

import cn.hejinyo.ss.mysql.entity.FormDataEntity;
import cn.hejinyo.ss.mysql.mapper.FormDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/19 17:22
 */
@Service
@Slf4j
public class FormDataMysqlMock {

    @Autowired
    private FormDataMapper formDataMapper;

    public int mock(int count) {
        FormDataEntity formDataEntity = new FormDataEntity();
        formDataEntity.setFormsysno(2000);
        formDataEntity.setOrganizationcode("20000");
        formDataEntity.setInusername("hejinyo1");
        formDataEntity.setInusersysno(2000);

        int random = RandomUtils.nextInt();
        count += random;
        for (int i = random; i < count; i++) {
            String data = "{\"se\": \"" + i + "\", \"pic\": {\"url\": \"http://qiniu.hejinyo.cn/" + i + "\"}, \"title\": \"图片测试" + i + "\", \"id\": " + i + "}";
            formDataEntity.setData(data);
            formDataEntity.setIndate(new Date());
            int id = formDataMapper.save(formDataEntity);
            log.error("index:{},id:{}", i, id);
        }
        return count;
    }
}
