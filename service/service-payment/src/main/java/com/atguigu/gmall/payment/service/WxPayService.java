package com.atguigu.gmall.payment.service;

import java.util.Map;

/**
 * Date:2022/8/13
 * Author:cjc
 * Description:微信支付接口
 */
public interface WxPayService {

    /**
     * 获取支付二维码地址
     * @return
     */
    public String getPayUrl(Map<String, String> paramMap);


    /**
     * 根据订单号查询支付结果
     * @param orderId
     * @return
     */
    public String getPayResult(String orderId);
}
