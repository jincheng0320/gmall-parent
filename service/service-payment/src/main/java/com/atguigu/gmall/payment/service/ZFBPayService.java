package com.atguigu.gmall.payment.service;

/**
 * Date:2022/8/14
 * Author:cjc
 * Description:支付宝支付接口
 */
public interface ZFBPayService {

    /**
     * 获取支付宝支付页面
     * @param desc
     * @param orderId
     * @param money
     * @return
     */
    public String getPayPage(String desc, String orderId, String money);

    /**
     * 查询支付结果
     * @param orderId
     * @return
     */
    public String getPayResult(String orderId);
}
