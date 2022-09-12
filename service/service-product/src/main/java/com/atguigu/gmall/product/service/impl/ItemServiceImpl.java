package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Date:2022/8/1
 * Author:cjc
 * Description:内部接口实现类
 */
@Service
@Log4j2
public class ItemServiceImpl implements ItemService {
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取商品详情
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    /**
     * 获取商品详情优化到缓存
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getItemFromDbOrRedis(Long skuId) {
        //参数判断
        if (skuId == null){
            return null;
        }
        //查询redis是否有健为sku:skuId:info的数据
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get("sku:" + skuId + ":info");
        //有直接返回
        if (skuInfo != null) {
            return skuInfo;
        }
        //若没有加锁 保证只放一个线程进来查询数据
        RLock lock = redissonClient.getLock("sku:" + skuId + ":lock");
        try {
            if (lock.tryLock(100,100, TimeUnit.SECONDS)) {
                //进来说明加锁成功
                try{
                    //查询redis是否有健为sku:skuId:info的数据
                    skuInfo = (SkuInfo) redisTemplate.opsForValue().get("sku:" + skuId + ":info");
                    //有直接返回
                    if (skuInfo != null) {
                        return skuInfo;
                    }
                    //通过id查询
                    skuInfo = skuInfoMapper.selectById(skuId);
                    //判断
                    if (skuInfo == null || skuInfo.getId() == null){
                        //缓存没有,数据库也没有
                        skuInfo = new SkuInfo();
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info",skuInfo,300,TimeUnit.SECONDS);
                    }else{
                        //缓存没有,数据库有
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info",skuInfo,24*3600,TimeUnit.SECONDS);
                    }
                    return skuInfo;
                }catch (Exception e1){
                    log.error("加锁成功,代码执行出现异常,异常为:"+e1.getMessage());
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("加锁出现异常,加锁失败");
        }
        return null;
    }

    /**
     * 获取分类信息
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 获取图片列表
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getImageList(Long skuId) {
        return skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId, skuId));
    }

    /**
     * 获取价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        return skuInfoMapper.selectById(skuId).getPrice();
    }

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId) {
        return spuSaleAttrMapper.spuSaleAttrListBySpuIdAndSkuId(skuId, spuId);
    }

    /**
     * 根据spu查询所有sku销售属性和值 键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuIdAndValues(Long spuId) {
        List<Map> values = skuSaleAttrValueMapper.selectSaleAttrKeyValueBySpuId(spuId);
        //返回结果转化
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        //遍历
        values.stream().forEach(value ->{
            Object skuId = value.get("sku_id");
            Object valuesId = value.get("values_id");
            map.put(valuesId.toString(), skuId);
        });
        return map;
    }

    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    /**
     * 根据品牌id查询品牌信息
     *
     * @param tmId
     * @return
     */
    @Override
    public BaseTrademark getTrademarkById(Long tmId) {
        return baseTrademarkMapper.selectById(tmId);
    }

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 根据skuId查询平台属性键值对
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
    }

    /**
     * 扣减库存
     * @param skuParam
     */
    @Override
    public void reduceStock(Map<String, Object> skuParam) {
        //遍历map
        skuParam.entrySet().stream().forEach(skuEntry->{
            //获取商品id
            String skuId = skuEntry.getKey();
            //获取扣减库存数量
            Object value = skuEntry.getValue();
            int i = skuInfoMapper.reduceStock(Long.parseLong(skuId), Integer.parseInt(value.toString()));
            if (i < 0){
                throw new RuntimeException("扣减库存失败");
            }
//            //查询商品
//            SkuInfo skuInfo = skuInfoMapper.selectById(Long.parseLong(skuId));
//            if (skuInfo == null || skuInfo.getId() == null){
//                throw new RuntimeException("商品不存在, 扣除库存失败");
//            }
//            //扣减库存
//            int stock = skuInfo.getStock() - Integer.parseInt(value.toString());
//            if (stock <=0 ){
//                throw new RuntimeException("库存不足!");
//            }
//            skuInfo.setStock(stock);
//            int update = skuInfoMapper.updateById(skuInfo);
//            if (update < 0){
//                throw new RuntimeException("扣减库存失败");
//            }
        });
    }

    /**
     * 回滚库存
     *
     * @param skuParam
     */
    @Override
    public void rollbackStock(Map<String, Object> skuParam) {
        //遍历
        skuParam.entrySet().stream().forEach(entry->{
            //商品id
            String skuId = entry.getKey();
            //回滚商品数量
            Object num = entry.getValue();
            int i = skuInfoMapper.rollbackStock(Long.parseLong(skuId),
                    Integer.parseInt(num.toString()));
            if (i < 0){
                throw new RuntimeException("回滚库存失败");
            }
        });

    }
}
