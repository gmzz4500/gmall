package com.yyds.gmall.web.controller;

import com.yyds.gmall.item.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @ClassName: WebController
 * @Author: yyd
 * @Date: 2022/8/3/003
 * @Description:商品详情页相关的前端控制层
 */
@Controller
@RequestMapping(value = "/page/item")
public class ItemController {

    @Autowired
    private ItemFeign itemFeign;
    /**
     * 打开商品详情页
     * @param model
     * @return
     */
    @GetMapping(value = "/{skuId}")
    public String item(Model model,
                       @PathVariable(value = "skuId")Long skuId){
        //远程调用商品详情微服务获取商品的全部信息
        Map<String, Object> result = itemFeign.getItemInfo(skuId);
        //将查询到的结果放到model里面去
        model.addAllAttributes(result);
        //打开页面
        return "item1.html";
    }

    @Autowired
    private TemplateEngine templateEngine;
    /**
     * 创建商品的静态页面
     * @param skuId
     * @return
     */
    @GetMapping(value = "/createHtml/{skuId}")
    @ResponseBody
    public String createHtml(@PathVariable(value = "skuId") Long skuId) throws Exception {
        //查询商品详情的数据
        Map<String, Object> result = itemFeign.getItemInfo(skuId);
        //初始化文件对象
        File file = new File("D:\\", skuId + ".html");
        //定义输出流
        PrintWriter printWriter = new PrintWriter(file,"UTF-8");
        //初始化数据容器
        Context context = new Context();
        context.setVariables(result);
        /**
         * 1.使用哪个模板页面
         * 2.数据容器
         * 3.输出流
         */
        templateEngine.process("item2",context,printWriter);
        //关闭流
        printWriter.flush();
        printWriter.close();
        //返回
        return "创建成功";
    }
}
