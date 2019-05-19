package cn.hejinyo.ss.mysql.service;

import cn.hejinyo.ss.mysql.dto.FormDataParam;
import cn.hejinyo.ss.mysql.dto.FormDataQuery;
import cn.hejinyo.ss.mysql.entity.FormDataEntity;
import cn.hejinyo.ss.mysql.mapper.FormDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/19 15:24
 */
@Service
public class FormDataService {

    private final static String REGEX = "^[0-9]+.*|.*\\.[0-9]+.*";
    private final static String REGEX_PORINT = "^[0-9]+.*";

    @Autowired
    private FormDataMapper formDataMapper;

    public List<FormDataEntity> findAll() {
        return formDataMapper.findAll();
    }

    public Object findByParam(List<HashMap<String, String>> params) {
        HashMap<String, FormDataParam> dataParams = new HashMap<>();
        params.forEach(param -> {
            String key = param.get("key");
            String value = param.get("value");
            FormDataParam dataParam = new FormDataParam();
            dataParam.setValue(value);
            dataParam.setType(0);
            if (key.matches(REGEX)) {
                // 查询DATA数据
                String[] keys = key.split("\\.");
                dataParam.setQueryKey(buildJsonExtract(keys, 1, keys[0]));
            } else if (key.contains(".")) {
                dataParam.setQueryKey(key.replaceFirst("\\.", "->'\\$.") + "'");
            } else {
                dataParam.setQueryKey(key);
            }
            dataParams.put(key, dataParam);
        });
        FormDataQuery dataQuery = new FormDataQuery();
        dataQuery.setDataParam(dataParams);

        String sort = Optional.ofNullable(dataQuery.getSort()).orElse("data.id");
        if (dataParams.get(sort) != null) {
            sort = dataParams.get(sort).getQueryKey();
        }
        dataQuery.setSort(sort);
        return formDataMapper.findPage(dataQuery);
    }

    private String buildJsonExtract(String[] keys, int index, String data) {
        int length = keys.length;
        if (length == 1) {
            return keys[0];
        }
        if (index >= length) {
            return null;
        }

        String dataStr = "json_extract(" + data + ",concat('$.\"','" + keys[index] + "','\"'))";
        int newIndex = index + 1;

        if (newIndex >= length) {
            return dataStr;
        }
        return buildJsonExtract(keys, newIndex, dataStr);
    }
}
