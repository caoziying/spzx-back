package com.atguigu.spzx.manager.service;

import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.vo.system.LoginVo;
import org.springframework.web.bind.annotation.RequestBody;

public interface SysUserService {
    LoginVo login(LoginDto loginDto);
}
