package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date:2022/8/6
 * Author:cjc
 * Description:搜索相关接口实现类
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 关键字搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> Search(Map<String, String> searchData) {
        //参数校验
//        if (searchData == null || searchData.size() == 0){
//            return null;
//        }

        try {
            //拼接条件
            SearchRequest searchRequest = buildSearchParams(searchData);
            //执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //结果解析
            //返回
            return getSearchResult(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回结果
        return null;
    }

    /**
     * 拼接条件
     * @param searchData
     * @return
     */
    private SearchRequest buildSearchParams(Map<String, String> searchData) {
//        //参数校验
//        if (searchData == null || searchData.size() == 0){
//            return null;
//        }
        //拼接条件
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        //条件构造初始化
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //组合条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //关键字条件查询
        String keywords = searchData.get("keywords");
        if (!StringUtils.isEmpty(keywords)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keywords));//匹配查询
        }
        //品牌不为空,作为查询条件
        String tradeMark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(tradeMark)){
            String[] split = tradeMark.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId",split[0]));
        }
        //平台属性
        searchData.entrySet().stream().forEach(entry->{
            //参数名字
            String key = entry.getKey();
            if (key.startsWith("attr_")){
                String value = entry.getValue();
                String[] split = value.split(":");
                //nested组合条件构造
                BoolQueryBuilder nestedBool = QueryBuilders.boolQuery();
                //平台属性id等于用户传递的值
                nestedBool.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                //平台属性的值也要等于用户选择的值
                nestedBool.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));
                //构建查询条件
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs",nestedBool, ScoreMode.None));
            }
        });
        //价格查询条件
        String price = searchData.get("price");
        if (!StringUtils.isEmpty(price)){
            //0-500 5000
            price = price.replace("元","").replace("以上","");
            //切分
            String[] split = price.split("-");
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            //判断是否有第二个值
            if (split.length >1){
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //设置查询条件
        sourceBuilder.query(boolQueryBuilder);
        //设置品牌的聚合条件  进行分组
        sourceBuilder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                        .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                        .size(100)

        );
        //设置平台属性聚合条件
        sourceBuilder.aggregation(
                AggregationBuilders.nested("aggAttrs","attrs")
                        .subAggregation(
                                AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                        .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                        .size(100)
                        )
        );
        String sortField = searchData.get("sortField");
        String sortRule = searchData.get("sortRule");
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            //指定排序
            sourceBuilder.sort(sortField, SortOrder.valueOf(sortRule));
        }else{
            //默认排序
            sourceBuilder.sort("id",SortOrder.DESC);
        }
        //分页实现
        String pageNum = searchData.get("pageNum");
        int page = getPage(pageNum);
        sourceBuilder.from((page-1)*50);
        sourceBuilder.size(50);
        //设置高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮域
        highlightBuilder.field("title");
        //前缀标签
        highlightBuilder.preTags("<font style=color:red");
        //后缀标签
        highlightBuilder.postTags("</font>");
        //设置高亮
        sourceBuilder.highlighter(highlightBuilder);
        //指定条件
        searchRequest.source(sourceBuilder);
        //返回条件
        return searchRequest;
    }

    /**
     * 计算页码
     * @param pageNum
     * @return
     */
    private int getPage(String pageNum) {
        try{
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            return 1;
        }
    }

    /**
     * 结果解析
     * @param searchResponse
     * @return
     */
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        //返回结果初始哈
        Map<String, Object> result = new HashMap<>();
        //获取命中数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //获取总数居
        long totalHits = hits.getTotalHits();
        result.put("totalHits",totalHits);
        //商品列表初始化
        List<Goods> goodsList = new ArrayList<>();
        //遍历每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit next = iterator.next();
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //获取高亮
            HighlightField highlightField = next.getHighlightFields().get("title");
            if (highlightField != null){
                Text[] fragments = highlightField.getFragments();
                if (fragments != null && fragments.length>0){
                    String title="";
                    for (Text fragment : fragments) {
                        title += fragment;
                    }
                    //使用高亮数据替换原数据
                    goods.setTitle(title);
                }
            }
            //保存数据
            goodsList.add(goods);
        }
        //保存商品列表
        result.put("goodsList", goodsList);
        //获取全部聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        //解析品牌聚合结果
        List<SearchResponseTmVo> searchResponseTmVoList = getTmAggResult(aggregations);
        result.put("searchResponseTmVoList",searchResponseTmVoList);
        //解析平台属性的聚合结果
        List<SearchResponseAttrVo> searchResponseAttrVoList = getAttrInfoResult(aggregations);
        result.put("searchResponseAttrVoList",searchResponseAttrVoList);
        //返回结果
        return result;
    }

    /**
     * 解析平台属性聚合结果
     * @param aggregations
     * @return
     */
    private List<SearchResponseAttrVo> getAttrInfoResult(Aggregations aggregations) {
        //平台属性nested聚合结果获取
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        //获取子聚合的结果-平台属性id聚合结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        //遍历
        return aggAttrId.getBuckets().stream().map(attrIdBuck->{
            //返回初始化
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取平台属性id
            long attrId = attrIdBuck.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取子聚合 平台属性名字
            ParsedStringTerms aggAttrName = attrIdBuck.getAggregations().get("aggAttrName");
            //获取一个组的一个名字即可
            if (!aggAttrName.getBuckets().isEmpty()){
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取平台属性值
            ParsedStringTerms aggAttrValue = attrIdBuck.getAggregations().get("aggAttrValue");
            //获取所有值
            List<String> attrValueList = aggAttrValue.getBuckets().stream().map(attrValueBuck -> {
                return attrValueBuck.getKeyAsString();
            }).collect(Collectors.toList());
            searchResponseAttrVo.setAttrValueList(attrValueList);
            //返回
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取品牌聚合结果
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getTmAggResult(Aggregations aggregations) {
        //获取品牌id聚合结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //遍历
        return aggTmId.getBuckets().stream().map(tmIdBuck ->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //每个品牌id
            long tmId = tmIdBuck.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取子聚合的结果
            //品牌名字
            ParsedStringTerms aggTmName = tmIdBuck.getAggregations().get("aggTmName");
            //获取一个品牌的名字
            if (!aggTmName.getBuckets().isEmpty()) {
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //品牌图片
            ParsedStringTerms aggTmLogoUrl = tmIdBuck.getAggregations().get("aggTmLogoUrl");
            if (!aggTmLogoUrl.getBuckets().isEmpty()){
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            return searchResponseTmVo;
        }).collect(Collectors.toList());
    }
}
