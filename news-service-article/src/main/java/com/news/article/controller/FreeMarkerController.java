package com.news.article.controller;

import com.news.model.user.pojo.Stu;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description FreeMarker测试
 * @create 2022-06-26-22:45
 */
@Controller
@RequestMapping("/free")
public class FreeMarkerController {

    @Value("${freemarker.html.target}")
    private String htmlTarget;

    @GetMapping("/hello")
    public String hello(Model model){

        // 输出字符串
        String world = "world...";
        model.addAttribute("there", world);

        makeModel(model);

        return "stu";
    }

    @GetMapping("/createHTML")
    @ResponseBody
    public String createHTML(Model model) throws Exception{

        //0.配置freemarker基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        // 声明freemarker模板所需要加载的目录的位置
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        //1.获得现有的模板ftl文件
        Template template = cfg.getTemplate("stu.ftl", "utf-8");

        //2.获得动态数据
        String world = "world...";
        model.addAttribute("there", world);
        model = makeModel(model);

        //3.融合动态数据和ftl，生成html
        File tempDic = new File(htmlTarget);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }
        Writer out = null;
        try {
            out = new FileWriter(htmlTarget + File.separator + "10010" + ".html");
            template.process(model, out);
        } finally {
            out.close();
        }

        return "ok";
    }

    private Model makeModel(Model model) {

        Stu stu = new Stu();
        stu.setUid("10010");
        stu.setUsername("Bob");
        stu.setAmount(88.86f);
        stu.setAge(18);
        stu.setHaveChild(false);
        stu.setBirthday(new Date());

        model.addAttribute("stu", stu);

        return model;
    }

}
