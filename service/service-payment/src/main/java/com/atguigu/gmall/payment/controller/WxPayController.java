package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Date:2022/8/13
 * Author:cjc
 * Description:微信支付控制层
 */
@RestController
@RequestMapping("/wx/pay")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 获取支付二维码
     * @return
     */
    @GetMapping("/getPayUrl")
    public String getPayUrl(@RequestParam Map<String, String> paramMap){
        return wxPayService.getPayUrl(paramMap);
    }

    /**
     * 获取订单结果
     * @param orderId
     * @return
     */
    @GetMapping("/getPayResult")
    public String getPayResult(String orderId){
        return wxPayService.getPayResult(orderId);
    }
    /**
     * 给微信调用的获取支付结果的异步通知接口
     */
    @RequestMapping("/callback/notify")
    public String wxNotify(HttpServletRequest request) throws Exception{
//        //获取微信支付通知的数据流
//        ServletInputStream inputStream = request.getInputStream();
//        //读取数据流
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        //调用缓存区
//        byte[] buffer = new byte[1024];
//        //读取长度
//        int len = 0;
//        while ((len = inputStream.read(buffer)) != -1){
//            outputStream.write(buffer,0,len);
//        }
//        //获取输出流的字节码
//        byte[] bytes = outputStream.toByteArray();
//        //转换
//        String sxML = new String(bytes);
//        //将xml转换成map
//        Map<String, String> stringMap = WXPayUtil.xmlToMap(sxML);
        String result = "{\"transaction_id\":\"4200001568202208169158755012\",\"nonce_str\":\"dd3293984914463aa6b436305301e8a8\",\"bank_type\":\"CMB_CREDIT\",\"openid\":\"oHwsHuIE5pkWXo64GtUN7drzqvHs\",\"sign\":\"A76FA38C0F17A7F53244B340EEB56D8D\",\"payway\":\"1\",\"fee_type\":\"CNY\",\"mch_id\":\"1558950191\",\"cash_fee\":\"1\",\"out_trade_no\":\"java0217000004\",\"appid\":\"wx74862e0dfcf69954\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"attach\":\"{\\\"exchange\\\":\\\"pay_exchange\\\",\\\"routingKey\\\":\\\"pay.seckill.order\\\",\\\"username\\\":\\\"huqiang\\\"}\",\"time_end\":\"20220816153320\",\"is_subscribe\":\"N\",\"return_code\":\"SUCCESS\"}";
        Map<String, String> map = JSONObject.parseObject(result, Map.class);
        String attachString = map.get("attach");
        Map<String, String> attach = JSONObject.parseObject(attachString, Map.class);
        //将支付结果发送mq订单微服务 -
        rabbitTemplate.convertAndSend(attach.get("exchange"),
                attach.get("routingKey"),
                result);
        //返回微信 防止一直调用
        HashMap<String, String> wx = new HashMap<>();
        wx.put("return_code","SUCCESS");
        wx.put("return_msg","OK");
        return WXPayUtil.mapToXml(map);
    }
}
