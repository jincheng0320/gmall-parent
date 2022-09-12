package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Date:2022/7/27
 * Author:cjc
 * Description:平台属性与分类的接口实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class ManageServiceImpl implements ManageService {
    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    /**
     * 查询所有一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1List() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 根据一级分类id查询二级分类
     *
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2List(Long category1Id) {
        //id非空校验
        if (category1Id == null || category1Id == 0){
            throw new RuntimeException("参数错误,一级分类id不能为空");
        }
        //条件构造
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory2::getCategory1Id, category1Id);
        return baseCategory2Mapper.selectList(wrapper);
    }

    /**
     * 根据二级分类id查询三级分类
     *
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3List(Long category2Id) {
        //id非空校验
        if (category2Id == null || category2Id == 0){
            throw new RuntimeException("参数错误,二级分类id不能为空");
        }
        //条件构造
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory3::getCategory2Id, category2Id);
        return baseCategory3Mapper.selectList(wrapper);
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //参数非空校验
        if (baseAttrInfo ==null || StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("数据不能为空");
        }
        //判断用户是修改还是新增
        if (baseAttrInfo.getId() != null){
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update < 0){
                throw new RuntimeException("平台属性修改失败,请重试");
            }
            //清理旧的属性值的信息
            LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseAttrValue::getAttrId, baseAttrInfo.getId());
            int delete = baseAttrValueMapper.delete(wrapper);
            if (delete < 0){
                throw new RuntimeException("平台属性修改失败,请重试");
            }
        }else{
            //保存到平台属性表
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            //判断是否保存成功
            if (insert <= 0){
                throw new RuntimeException("平台属性保存失败,请重试");
            }
        }

        //保存属性成功,就能获取到属性id
        Long attrId = baseAttrInfo.getId();
        //补全到每个属性值中
        baseAttrInfo.getAttrValueList().stream().forEach(baseAttrValue -> {
            //补入attId
            baseAttrValue.setAttrId(attrId);
            //保存数据
            int insert1 = baseAttrValueMapper.insert(baseAttrValue);
            if (insert1 <= 0){
                throw new RuntimeException("平台属性保存失败,请重试");
            }
        });
    }

    /**
     * 根据分类id获取平台属性
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoByCategoryId(category1Id, category2Id, category3Id);
    }

    /**
     * 根据平台id获取属性值
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getBaseAttrValue(Long attrId) {
        //条件构造
        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseAttrValue::getAttrId, attrId);
        return baseAttrValueMapper.selectList(wrapper);
    }

    /**
     * 品牌分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseTrademark> getBaseTrademark(Integer page, Integer size) {
        return baseTrademarkMapper.selectPage(new Page<BaseTrademark>(page, size),null);
    }

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademarkById(Long id) {
        return baseTrademarkMapper.selectById(id);
    }

    /**
     * 添加品牌
     *
     * @param baseTrademark
     */
    @Override
    public void saveBaseTrademark(BaseTrademark baseTrademark) {
        //非空校验
        if (StringUtils.isEmpty(baseTrademark.getTmName())){
            throw new RuntimeException("添加失败,品牌名不能为空");
        }
        int insert = baseTrademarkMapper.insert(baseTrademark);
        if (insert <= 0){
            throw new RuntimeException("添加失败,请重试");
        }
    }

    /**
     * 修改品牌
     *
     * @param baseTrademark
     */
    @Override
    public void updateBaseTrademark(BaseTrademark baseTrademark) {
        //非空校验
        if (StringUtils.isEmpty(baseTrademark.getTmName())){
            throw new RuntimeException("修改失败,品牌名不能为空");
        }
        int update = baseTrademarkMapper.updateById(baseTrademark);
        if (update < 0){
            throw new RuntimeException("修改失败,请重试");
        }
    }

    /**
     * 删除品牌
     *
     * @param id
     */
    @Override
    public void deleteBaseTrademark(Long id) {
        //非空校验
        if (id == 0){
            throw new RuntimeException("删除失败,id不能为0");
        }
        int i = baseTrademarkMapper.deleteById(id);
        if (i < 0){
            throw new RuntimeException("删除失败,请重试");
        }

    }

    /**
     * 根据三级分类id分页查询spuInfo信息
     *
     * @param categories3Id
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoList(Long categories3Id, Integer page, Integer size) {
        //非空校验
        if (categories3Id == 0 || categories3Id == null){
            throw new RuntimeException("参数错误,分类id不能为空");
        }
        //条件构造
        LambdaQueryWrapper<SpuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuInfo::getCategory3Id, categories3Id);
        return spuInfoMapper.selectPage(new Page<SpuInfo>(page, size), wrapper);
    }

    /**
     * 添加或修改spu
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //参数校验
        if (spuInfo == null){
            throw new RuntimeException("参数错误");
        }
        //判断是添加还是修改
        if (spuInfo.getId() != null){
            //有id 修改
            int update = spuInfoMapper.updateById(spuInfo);
            if (update < 0){
                throw new RuntimeException("修改失败");
            }
            //删除spu图片表
            int image = spuImageMapper.deleteById(new LambdaQueryWrapper<SpuImage>().
                    eq(SpuImage::getSpuId, spuInfo.getId()));
            //判断
            if (image < 0){
                throw new RuntimeException("修改spu图片表失败");
            }
            //删除spu销售属性表
            int saleAttr = spuSaleAttrMapper.deleteById(new LambdaQueryWrapper<SpuSaleAttr>().
                    eq(SpuSaleAttr::getSpuId, spuInfo.getId()));
            if (saleAttr < 0){
                throw new RuntimeException("修改spu销售属性表失败");
            }
            //删除spu销售属性值表
            int saleAttrValue = spuSaleAttrValueMapper.deleteById(new LambdaQueryWrapper<SpuSaleAttrValue>().
                    eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
            if (saleAttrValue < 0){
                throw new RuntimeException("修改spu销售属性值表失败");
            }
        }else{
            //添加
            int insert = spuInfoMapper.insert(spuInfo);
            if (insert <= 0){
                throw new RuntimeException("添加失败");
            }
        }
        //获取spuId
        Long spuId = spuInfo.getId();
        //添加spu图片表
        saveSpuImageInfo(spuInfo.getSpuImageList(), spuId);
        //添加spu销售属性表
        saveSpuSaleAttr(spuInfo.getSpuSaleAttrList(), spuId);
    }

    /**
     * 获取销售属性
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttr() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 根据spuId获取图片列表
     *
     * @param supId
     * @return
     */
    @Override
    public List<SpuImage> spuImageList(Long supId) {
        return spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>()
                .eq(SpuImage::getSpuId, supId));
    }

    /**
     * 根据spu的id获取spu的销售属性各信息
     *
     * @param spuId
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.spuSaleAttrList(spuId);
    }

    /**
     * 获取品牌属性
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    /**
     * 添加或修改 sku
     *
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //非空校验
        if (skuInfo == null){
            throw new RuntimeException("参数错误");
        }
        //id判断
        if (skuInfo.getId() == null){
            //id为空 新增
            int insert = skuInfoMapper.insert(skuInfo);
            if (insert <= 0){
                throw new RuntimeException("新增失败,请重试");
            }

        }else{
            //id有值,修改
            int update = skuInfoMapper.updateById(skuInfo);
            if (update < 0){
                throw new RuntimeException("修改失败,请重试");
            }
            //获取skuId
            Long skuId = skuInfo.getId();
            //删除sku图片表对应信息
            int delete1 = skuImageMapper.deleteById(new LambdaQueryWrapper<SkuImage>().
                    eq(SkuImage::getSkuId, skuId));
            //删除sku平台属性值表对应信息
            int delete2 = skuAttrValueMapper.deleteById(new LambdaQueryWrapper<SkuAttrValue>().
                    eq(SkuAttrValue::getSkuId, skuId));
            //删除sku销售属性值表对应信息
            int delete3 = skuSaleAttrValueMapper.deleteById(new LambdaQueryWrapper<SkuSaleAttrValue>().
                    eq(SkuSaleAttrValue::getSkuId, skuId));
            if (delete1 < 0 || delete2 < 0 || delete3< 0){
                throw new RuntimeException("修改失败");
            }
        }
        //skuId
        Long skuId = skuInfo.getId();
        //新增sku图片表
        saveSkuImageBySkuId(skuInfo.getSkuImageList(), skuId);
        //新增sku平台属性值
        saveSkuAttrValueById(skuInfo.getSkuAttrValueList(), skuId);
        //新增sku销售属性值
        saveSkuSaleAttrValue(skuInfo.getSkuSaleAttrValueList(), skuId, skuInfo.getSpuId());
    }

    /**
     * sku分页查询
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> list(Integer page, Integer size) {
        return skuInfoMapper.selectPage(new Page<>(page, size),null);
    }


    /**
     * 新增sku销售属性值
     * @param skuSaleAttrValueList
     * @param skuId
     * @param spuId
     */
    private void saveSkuSaleAttrValue(List<SkuSaleAttrValue> skuSaleAttrValueList, Long skuId, Long spuId) {
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {
            //补全skuId
            skuSaleAttrValue.setSkuId(skuId);
            //补全spuId
            skuSaleAttrValue.setSpuId(spuId);
            //保存
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if (insert <= 0){
                throw new RuntimeException("新增销售属性值失败");
            }
        });
    }

    @Autowired
    private ListFeign listFeign;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 上架或者下架
     *
     * @param skuId
     * @param status
     */
    @Override
    public void upOrDown(Long skuId, Short status) {
        //非空校验
        if (skuId == null) {
            return;
        }
        //根据skuId查询商品是否纯在
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo == null || skuInfo.getId() == null){
            return;
        }
        //存在修改
        skuInfo.setIsSale(status);
        int update = skuInfoMapper.updateById(skuInfo);
        if (update < 0){
            throw new RuntimeException("操作失败");
        }
        //保证消息投递可靠性
        rabbitTemplate.setReturnCallback((a,b,c,d,e)->{
            log.error("上下架发生错误,发送的消息内容为 : "+new String(a.getBody()));
            log.error("上下架发生错误,发送的消息状态码为 : " + b);
            log.error("上下架发生错误,发生错误的原因是 : "+ c);
            log.error("上下架发生错误,消息发送时指定的交换机是 : "+ d);
            log.error("上下架发生错误,消息发送时的路由是 : "+ e);
        });
        //上传或下加数据到es ---待优化
        if (ProductConst.SKU_ON_SALE.equals(status)){
            //listFeign.add(skuId);
            rabbitTemplate.convertAndSend("sku_exchange", "sku.upper", skuId+"");
        }else{
            //listFeign.remove(skuId);
            rabbitTemplate.convertAndSend("sku_exchange", "sku.down", skuId+"");
        }
    }

    /**
     * 新增sku平台属性值
     * @param skuAttrValueList
     * @param skuId
     */
    private void saveSkuAttrValueById(List<SkuAttrValue> skuAttrValueList, Long skuId) {
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            //补全skuId
            skuAttrValue.setSkuId(skuId);
            //保存
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if (insert <= 0){
                throw new RuntimeException("新增平台属性值失败");
            }
        });
    }

    /**
     * 新增sku图片
     * @param skuImageList
     * @param skuId
     */
    private void saveSkuImageBySkuId(List<SkuImage> skuImageList, Long skuId) {
        skuImageList.stream().forEach(skuImage -> {
            //补全skuId
            skuImage.setSkuId(skuId);
            //保存
            int insert = skuImageMapper.insert(skuImage);
            if (insert <= 0){
                throw new RuntimeException("新增sku图片失败");
            }
        });
    }

    /**
     * 新增spu图片
     * @param spuImageList
     * @param spuId
     */
    private void saveSpuImageInfo(List<SpuImage> spuImageList, Long spuId) {
        spuImageList.stream().forEach(spuImage -> {
            //补全spuId
            spuImage.setSpuId(spuId);
            int insert = spuImageMapper.insert(spuImage);
            if (insert <= 0){
                throw new RuntimeException("新增spu图片失败");
            }
        });
    }

    /**
     * 新增销售属性
     * @param spuSaleAttrList
     * @param spuId
     */
    private void saveSpuSaleAttr(List<SpuSaleAttr> spuSaleAttrList, Long spuId) {
        //遍历
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            //补全spuId
            spuSaleAttr.setSpuId(spuId);
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if (insert < 0){
                throw new RuntimeException("新增销售属性失败");
            }
            //添加spu销售属性值表
            saveSpuSaleAttrValue(spuSaleAttr.getSpuSaleAttrValueList(), spuId, spuSaleAttr.getSaleAttrName());
        });
    }

    /**
     * 新增spu销售属性值表
     * @param spuSaleAttrValueList
     * @param spuId
     * @param saleAttrName
     */
    private void saveSpuSaleAttrValue(List<SpuSaleAttrValue> spuSaleAttrValueList, Long spuId, String saleAttrName) {
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            //补全spuId
            spuSaleAttrValue.setSpuId(spuId);
            //补全销售属性名称
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            //新增
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if (insert < 0){
                throw new RuntimeException("新增销售属性值失败");
            }
        });
    }


}
