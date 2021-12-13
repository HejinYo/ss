# 创建索引
```text
 PUT http://hejinyo.cn:9200/<index>/<type>/[<id>]
 索引和类型是必需的，而id部分是可选的。如果不指定ID，ElasticSearch会为我们生成一个ID。如果不指定id，应该使用HTTP的POST而不是PUT请求
 
```

request: PUT /form/0b9c1b80fb0443f1a05b776d0f61869e/28
```json
 {
   "1556009107761": "图片测试", 
   "1556009107762": 
     {
       "url": "http://qiniu.hejinyo.cn/IMG_20190502_145427.jpg", 
       "shootDate": "2019:05:02 14:54:27", 
       "shootLocation": "四川省成都市蒲江县环山路"
     },
   "1555999747000_25711": "00012"
 }
```

response：
```json
 {
   "_index" : "form",
   "_type" : "0b9c1b80fb0443f1a05b776d0f61869e",
   "_id" : "LEURr2oBIhJAleu_0hpl",
   "_version" : 1,
   "result" : "created",
   "_shards" : {
     "total" : 2,
     "successful" : 1,
     "failed" : 0
   },
   "_seq_no" : 0,
   "_primary_term" : 1
 }
```

# 查看数据
http://hejinyo.cn:9200/form/0b9c1b80fb0443f1a05b776d0f61869e/28

# 更新数据
request:PUT /form/0b9c1b80fb0443f1a05b776d0f61869e/28
```json
{
  "1556009107761": "图片测试", 
  "1556009107762": 
    {
      "url": "http://qiniu.hejinyo.cn/IMG_20190502_145427.jpg", 
      "shootDate": "2019:05:02 14:54:26", 
      "shootLocation": "四川省成都市蒲江县环山路"
    },
  "1555999747000_25711": "00011"
}
```
response：
```json
{
  "_index" : "form",
  "_type" : "0b9c1b80fb0443f1a05b776d0f61869e",
  "_id" : "28",
  "_version" : 6,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 6,
  "_primary_term" : 1
}

```

# 删除文档
request:PUT http://hejinyo.cn:9200/form/0b9c1b80fb0443f1a05b776d0f61869e/LEURr2oBIhJAleu_0hpl
response:
```json
{
    "_index": "form",
    "_type": "0b9c1b80fb0443f1a05b776d0f61869e",
    "_id": "LEURr2oBIhJAleu_0hpl",
    "_version": 2,
    "result": "deleted",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 7,
    "_primary_term": 1
}
```

# 查询
## 查询所有
request: GET /form/0b9c1b80fb0443f1a05b776d0f61869e/_search
```json
{
  "query": {
    "match_all": {}
  }
}
```

## 查询字符串
GET /form/0b9c1b80fb0443f1a05b776d0f61869e/_search
```json
{
  "query": {
    "query_string": {
            "query": "00004"
    }
  }
}
```
```json
{
  "took" : 7,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : 1,
    "max_score" : 0.9808292,
    "hits" : [
      {
        "_index" : "form",
        "_type" : "0b9c1b80fb0443f1a05b776d0f61869e",
        "_id" : "4",
        "_score" : 0.9808292,
        "_source" : {
          "1556009107761" : "图片测试4",
          "1556009107762" : {
            "url" : "http://qiniu.hejinyo.cn/IMG_20190502_145427.jpg",
            "shootDate" : "2019:05:02 14:54:26",
            "shootLocation" : "四川省成都市蒲江县环山路"
          },
          "1555999747000_25711" : "00004"
        }
      }
    ]
  }
}

```

## 指定搜索的字段
GET /form/0b9c1b80fb0443f1a05b776d0f61869e/_search
```json
{
  "query": {
    "query_string": {
      "query": "四川省",
      "fields": ["1556009107762.shootLocation"]
    }
  }
}

```

```json
{
    "QUERY_NAME": {
        "FIELD_NAME": {
            "ARGUMENT": "VALUE"
        }
    }
}
```
QUERY_NAME:  match, must_not, range, bool( must, must_not, should(term) )
```json
{
    "query": {
        "match": {
            "tweet": "elasticsearch"
        }
    }
}
```

通配符查询，中文精确查询
```json
{
    "query" : {
        "constant_score" : { 
            "filter" : {
                "wildcard" : { 
                    "1556009107762.url.keyword" : "*hejinyo*"
                }
            }
        }
    }
}
```

## 条件过滤器

通常当查找一个精确值的时候，我们不希望对查询进行评分计算。只希望对文档进行包括或排除的计算，所以我们会使用 constant_score 查询以非评分模式来执行 term 查询并以一作为统一评分。

最终组合的结果是一个 constant_score 查询，它包含一个 term 查询：

GET /my_store/products/_search
{
    "query" : {
        "constant_score" : { 
            "filter" : {
                "term" : { 
                    "price" : 20
                }
            }
        }
    }
}