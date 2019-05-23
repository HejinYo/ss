package cn.hejinyo.ss.mysql.controller;

import cn.hejinyo.ss.common.utils.JsonUtil;
import cn.hejinyo.ss.common.utils.PageQuery;
import cn.hejinyo.ss.es.entity.FormDataEntity;
import cn.hejinyo.ss.mysql.mock.FormDataMysqlMock;
import cn.hejinyo.ss.mysql.service.FormDataService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/19 15:23
 */
@RestController
@RequestMapping("/data")
@Api(tags = "DataController", description = "Data查询测试")
public class DataController {

    @Autowired
    private FormDataService formDataService;
    @Autowired
    private FormDataMysqlMock mysqlMock;


    @GetMapping("/findAll")
    public Object findAll() {
        return formDataService.findAll();
    }


    @PostMapping("/findByParam")
    public Object findByParam(@RequestBody List<HashMap<String, String>> params) {
        return formDataService.findByParam(params);
    }

    @PostMapping("/mysqlMock/{count}")
    public Object mysqlMock(@PathVariable("count") Integer count) {
        return mysqlMock.mock(count);
    }

    @PostMapping("/test")
    public Object testPageQuery(@RequestBody PageQuery<FormDataEntity> pageQuery) {
        return JsonUtil.toJson(pageQuery);
    }
}
