package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.ZFBPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Date:2022/8/14
 * Author:cjc
 * Description:支付宝控制层
 */
@RestController
@RequestMapping("/zfb/pay")
public class ZFBPayController {

    @Autowired
    private ZFBPayService zfbPayService;

    /**
     * 获取支付宝支付页面
     * @param desc
     * @param orderId
     * @param money
     * @return
     */
    @GetMapping("/getPayPage")
    public String getPayPage(String desc, String orderId, String money){
        return zfbPayService.getPayPage(desc, orderId, money);
    }

    /**
     * 获取支付结果
     * @param orderId
     * @return
     */
    @GetMapping("/getPayResult")
    public String getPayResult(String orderId){
        return zfbPayService.getPayResult(orderId);
    }

    /**
     * 支付宝同步回调: 用户付款完成以后,从支付宝的页面会跳转到这个地址,无法确定支付成功还是失败!!!
     * @param returnMap
     * @return
     */
    @RequestMapping("/callback/return")
    public String returnJump(@RequestParam Map<String, String> returnMap){
        System.out.println("支付宝同步回调参数是: "+ JSONObject.toJSONString(returnMap));
        return "同步回调成功";
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 支付宝异步通知的地址: 支付结果的依据
     * @param returnMap
     * @return
     */
    @RequestMapping("/callback/notify")
    public String notifyUrl(@RequestParam Map<String, String> returnMap){
        returnMap.put("payway", "2");
        rabbitTemplate.convertAndSend("pay_exchange","pay.order", JSONObject.toJSONString(returnMap));
        return "SUCCESS";
    }
}
