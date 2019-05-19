package cn.hejinyo.ss.mysql.mapper;

import cn.hejinyo.ss.mysql.entity.FormDataEntity;
import cn.hejinyo.ss.mysql.dto.FormDataQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * form_data 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/05/19 15:22
 */
@Mapper
public interface FormDataMapper {


    List<FormDataEntity> findAll();

    List<FormDataEntity> findPage(FormDataQuery formDataQuery);

    int save(FormDataEntity formDataEntity);
}