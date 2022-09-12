package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:
 */
@Repository
public interface GoodsDao extends ElasticsearchCrudRepository<Goods, Long> {
}
