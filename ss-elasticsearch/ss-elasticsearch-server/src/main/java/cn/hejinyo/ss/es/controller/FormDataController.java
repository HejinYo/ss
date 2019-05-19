package cn.hejinyo.ss.es.controller;

import cn.hejinyo.ss.es.entity.FormDataEntity;
import cn.hejinyo.ss.es.mapper.FormDataRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/05/13 14:37
 */
@RestController
@RequestMapping("/form")
@Api(tags = "FormDataController", description = "ES查询测试")
public class FormDataController {
    @Autowired
    private FormDataRepository goodsRepository;

    @PostMapping("/{type}/{id}")
    public String save(@PathVariable("type") String type, @PathVariable("id") Integer id, @RequestBody Object data) {
        FormDataEntity goodsInfo = new FormDataEntity();
        goodsInfo.setSysNo(id);
        goodsInfo.setData(data);
        goodsInfo.setInDate(new Date());
        goodsInfo.setInUserName("elasticsearch");
        goodsRepository.save(goodsInfo);
        return "success";
    }


    @DeleteMapping("/{type}/{id}")
    public String delete(@PathVariable("type") String type, @PathVariable("id") Integer id) {
        goodsRepository.deleteById(null);
        return "success";
    }

    @PutMapping("/{type}/{id}")
    public String update(@PathVariable("type") String type, @PathVariable("id") Integer id, @RequestBody HashMap<String,Object> data) {
        FormDataEntity goodsInfo = new FormDataEntity();
        goodsInfo.setData(data);
        goodsInfo.setInDate(new Date());
        goodsInfo.setInUserName("elasticsearch");
        goodsRepository.save(goodsInfo);
        return "success";
    }

    @GetMapping("/{type}/{id}")
    public FormDataEntity getOne(@PathVariable("type") String type, @PathVariable("id") Integer id) {
        FormDataEntity goodsInfo = goodsRepository.findById(id).orElse(null);
        return goodsInfo;
    }


    //每页数量
/*
    private Integer PAGESIZE = 10;

    //http://localhost:8888/getGoodsList?query=商品
    //http://localhost:8888/getGoodsList?query=商品&pageNumber=1
    //根据关键字"商品"去查询列表，name或者description包含的都查询
    @GetMapping("getGoodsList")
    public List<FormDataEntity> getList(Integer pageNumber, String query) {
        if (pageNumber == null) {
            pageNumber = 0;
        }
        //es搜索默认第一页页码是0
        SearchQuery searchQuery = getEntitySearchQuery(pageNumber, PAGESIZE, query);
        Page<FormDataEntity> goodsPage = goodsRepository.search(searchQuery);
        return goodsPage.getContent();
    }
*/


/*    private SearchQuery getEntitySearchQuery(int pageNumber, int pageSize, String searchContent) {
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name", searchContent),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                .add(QueryBuilders.matchPhraseQuery("description", searchContent),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                //设置权重分 求和模式
                .scoreMode("sum")
                //设置权重分最低分
                .setMinScore(10);

        // 设置分页
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        return new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
    }*/
}
