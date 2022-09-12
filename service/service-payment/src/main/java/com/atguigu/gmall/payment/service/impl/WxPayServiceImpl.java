package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.WxPayService;
import com.atguigu.gmall.payment.util.HttpClient;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Date:2022/8/13
 * Author:cjc
 * Description:微信支付实现类
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Value("${weixin.pay.appid}")
    private String appid;

    @Value("${weixin.pay.partner}")
    private String partner;

    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;

    /**
     * 获取二维码支付地址
     * @param orderMap
     * @return
     */
    @Override
    public String getPayUrl(Map<String, String> orderMap) {
        //获取统一下单地址
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //包装参数
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", appid);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", orderMap.get("desc"));
        paramMap.put("out_trade_no", orderMap.get("orderId"));
        paramMap.put("total_fee", orderMap.get("money"));
        paramMap.put("spbill_create_ip", "192.168.200.1");
        paramMap.put("notify_url", notifyUrl);
        paramMap.put("trade_type", "NATIVE");
        //附加参数
        HashMap<String, String> attachMap = new HashMap<>();
        attachMap.put("exchange",orderMap.get("exchange"));
        attachMap.put("routingKey",orderMap.get("routingKey"));
        //判断用户名是否为空
        if(!StringUtils.isEmpty(orderMap.get("username"))){
            attachMap.put("username", orderMap.get("username"));
        }
        //保存附加数据
        paramMap.put("attach", JSONObject.toJSONString(attachMap));
        try {
            //将map转换为xml,同时生成签名
            String paraXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发生post请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paraXml);
            httpClient.post();
            //获取返回结果
            String context = httpClient.getContent();
            //解析
            Map<String, String> result = WXPayUtil.xmlToMap(context);
            //返回
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据订单号查询支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public String getPayResult(String orderId) {
        //查询订单支付结果的地址
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //包装参数
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no",orderId);
        try {
            //将map转换xml并生成签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发post请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            //返回
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
