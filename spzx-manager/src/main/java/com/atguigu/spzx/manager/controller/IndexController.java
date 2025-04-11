package com.atguigu.spzx.manager.controller;
import com.atguigu.spzx.manager.service.ValidateCodeService;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.UserInfoVo;
import com.atguigu.spzx.model.vo.system.LoginVo;
import com.atguigu.spzx.manager.service.SysUserService;
import com.atguigu.spzx.model.vo.system.ValidateCodeVo;
import com.atguigu.spzx.utils.AuthContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Swagger 里面的注解
@Tag(name = "用户接口")
@RestController
@RequestMapping(value = "/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService ;

    @Operation(summary = "登录接口")
    @PostMapping(value = "/login")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
        LoginVo loginVo = sysUserService.login(loginDto) ;
        return Result.build(loginVo , ResultCodeEnum.SUCCESS) ;
    }

    @Autowired
    private ValidateCodeService validateCodeService;

    @Operation(summary = "生成图片验证码")
    @GetMapping(value = "/generateValidateCode")
    public Result<ValidateCodeVo> generateValidateCode() {
        ValidateCodeVo validateCodeVo = validateCodeService.generateValidateCode();
        return Result.build(validateCodeVo , ResultCodeEnum.SUCCESS) ;
    }

//    @Operation(summary = "获取用户信息")
//    @GetMapping(value = "/getUserInfo")
//    public Result<UserInfoVo> getUserInfo(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        UserInfoVo userInfoVo = sysUserService.getUserInfo(token);
//        return Result.build(userInfoVo , ResultCodeEnum.SUCCESS) ;
//    }

    @Operation(summary = "获取用户信息")
    @GetMapping(value = "/getUserInfo")
    public Result<UserInfoVo> getUserInfo(HttpServletRequest request) {
        return Result.build(AuthContextUtil.get(), ResultCodeEnum.SUCCESS) ;
    }

    @Operation(summary = "退出登录")
    @GetMapping(value = "/logout")
    public Result logout(@RequestHeader(name = "token") String token) {
        sysUserService.logout(token);
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }


}