package com.news.api.controller.admin;

import com.news.common.jsonres.R;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.AdminLoginBO;
import com.news.model.user.bo.NewAdminBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 管理员管理接口
 * @create 2022-06-20-10:41
 */
@Api(value = "管理员维护", tags = {"管理员维护controller"})
@RequestMapping("/adminMng")
public interface AdminMngControllerApi {

    @ApiOperation(value = "管理员账号密码登录", notes = "管理员账号密码登录请求", httpMethod = "POST")
    @PostMapping("/adminLogin")
    public R adminLogin(@RequestBody AdminLoginBO adminLoginBO,
                        HttpServletRequest request,
                        HttpServletResponse response);


    @ApiOperation(value = "查询管理人员信息是否存在", notes = "查询管理人员信息是否存在请求", httpMethod = "POST")
    @PostMapping("/adminIsExist")
    public R adminIsExist(@RequestParam String username);


    @ApiOperation(value = "添加管理员", notes = "添加管理员请求", httpMethod = "POST")
    @PostMapping("/addNewAdmin")
    public R addNewAdmin(@RequestBody NewAdminBo newAdminBo,
                         HttpServletRequest request,
                         HttpServletResponse response);


    @ApiOperation(value = "分页查询管理人员列表", notes = "分页查询管理人员列表请求", httpMethod = "POST")
    @PostMapping("/getAdminList")
    public R getAdminList(@ApiParam(name = "page", value = "当前页码", required = false)
                          @RequestParam(defaultValue = "1") Integer page,
                          @ApiParam(name = "pageSize", value = "一页显示的条数", required = false)
                          @RequestParam(defaultValue = "5") Integer pageSize);


    @ApiOperation(value = "管理员退出登录", notes = "管理员退出登录请求", httpMethod = "POST")
    @PostMapping("/adminLogout")
    public R adminLogout(@RequestParam String adminId,
                         HttpServletRequest request,
                         HttpServletResponse response);


}
