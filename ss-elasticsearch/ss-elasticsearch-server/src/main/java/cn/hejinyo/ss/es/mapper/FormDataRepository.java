package cn.hejinyo.ss.es.mapper;

import cn.hejinyo.ss.es.entity.FormDataEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * form_data 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/05/13 14:30
 */
@Component
public interface FormDataRepository extends ElasticsearchRepository<FormDataEntity, Integer> {
}