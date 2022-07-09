package com.news.api.controller.elasticsearch;

import com.news.api.config.MyServiceLists;
import com.news.common.jsonres.R;
import com.news.model.user.eo.FansEo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description es粉丝远程调用接口
 * @create 2022-07-04-13:03
 */
@Api(value = "es粉丝业务", tags = {"es粉丝业务controller"})
@RequestMapping("/esFans")
@FeignClient(value = MyServiceLists.SERVICE_ELASTICSEARCH, contextId = "fans") //开启feign
public interface EsFansControllerApi {

    @ApiOperation(value = "创建索引库", notes = "创建索引库请求", httpMethod = "GET")
    @GetMapping("/createFansIndex")
    public R createIndex();


    @ApiOperation(value = "创建文档", notes = "创建文档请求", httpMethod = "POST")
    @PostMapping("/addFansDoc")
    public R addDocument(@RequestBody FansEo fansEo);

    @ApiOperation(value = "删除文档", notes = "删除文档请求", httpMethod = "GET")
    @GetMapping("/delFansDoc")
    public R deleteDocument(@RequestParam String writerId, @RequestParam String fansId);


    @ApiOperation(value = "修改文档", notes = "修改文档请求", httpMethod = "POST")
    @PostMapping("/updateDoc")
    public R updateDocument(@RequestBody FansEo fansEo);


    @ApiOperation(value = "用户分页查询所有粉丝", notes = "用户分页查询所有粉丝请求", httpMethod = "GET")
    @GetMapping("/list")
    public R search(@RequestParam String writerId,
                    @RequestParam Integer page,
                    @RequestParam Integer pageSize);


    @ApiOperation(value = "用户查询自己粉丝男女分布", notes = "用户查询自己粉丝男女分布请求", httpMethod = "GET")
    @GetMapping("/sexCounts")
    public R queryFansSex(@RequestParam String writerId);

    @ApiOperation(value = "用户查询自己粉丝地域分布", notes = "用户查询自己粉丝地域分布请求", httpMethod = "GET")
    @GetMapping("/regionCounts")
    public R queryFansRegion(@RequestParam String writerId);
}
