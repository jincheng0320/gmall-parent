package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:es在商品相关接口实现类
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemFeign itemFeign;
    /**
     * 上架商品写入ES
     *
     * @param skuId
     */
    @Override
    public void addGoodsToEs(Long skuId) {
        //数据初始化
        Goods goods = new Goods();
        //查询商品信息
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        //校验
        if (skuInfo == null || skuInfo.getId() == null){
            return;
        }
        //补全信息
        //补全Id
        goods.setId(skuInfo.getId());
        //补全默认图片信息
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        //补全价格
        BigDecimal prices = itemFeign.getPrices(skuId);
        goods.setPrice(prices.doubleValue());
        //补全创建时间
        goods.setCreateTime(new Date());
        //补全品牌信息
        BaseTrademark trademark = itemFeign.getTrademarkById(skuInfo.getTmId());
        goods.setTmId(trademark.getId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());
        //补全商品分类信息
        BaseCategoryView categoryView = itemFeign.getCategoryView(skuInfo.getCategory3Id());
        goods.setCategory1Id(categoryView.getCategory1Id());
        goods.setCategory1Name(categoryView.getCategory1Name());
        goods.setCategory2Id(categoryView.getCategory2Id());
        goods.setCategory2Name(categoryView.getCategory2Name());
        goods.setCategory3Id(categoryView.getCategory3Id());
        goods.setCategory3Name(categoryView.getCategory3Name());
        //补全平台属性键值对
        List<BaseAttrInfo> attrInfoList = itemFeign.getBaseAttrInfo(skuInfo.getId());
        List<SearchAttr> attrs = attrInfoList.stream().map(baseAttrInfo -> {
            //结果初始化
            SearchAttr searchAttr = new SearchAttr();
            //补全平台属性id
            searchAttr.setAttrId(baseAttrInfo.getId());
            //补全平台属性值
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //补全平台属性名
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(attrs);
        //保存商品信息
        goodsDao.save(goods);
    }

    /**
     * 从ES中删除下架的商品
     *
     * @param goodsId
     */
    @Override
    public void removeFromEs(Long goodsId) {
        goodsDao.deleteById(goodsId);
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 为商品加热度
     *
     * @param goodsId
     */
    @Override
    public void addHotScore(Long goodsId) {
        //参数判断
        if (goodsId == null){
            return;
        }
        //商品热度存储到redis
        Double hotScore = redisTemplate.opsForZSet().incrementScore("goods_hotScore", goodsId, 1);
        //热度值可以对10整除进行更新
        if (hotScore.intValue() % 10 == 0){
            //查询商品在es是否存在
            Optional<Goods> optionalGoods = goodsDao.findById(goodsId);
            if (optionalGoods.isPresent()){
                //获取商品信息
                Goods goods = new Goods();
                //热度值更新
                goods.setHotScore(hotScore.longValue());
                //更新覆盖旧数据
                goodsDao.save(goods);
            }
        }

    }
}
