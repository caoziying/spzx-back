package com.atguigu.spzx.manager.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysUserMapper;
import com.atguigu.spzx.manager.service.SysUserService;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.LoginVo;
import io.minio.Digest;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public LoginVo login(LoginDto loginDto) {
        // 获取用户名，密码
        String userName = loginDto.getUserName();

        // 查询 sys_user 中对应用户名
        SysUser sysUser = sysUserMapper.selectUserInfoByUserName(userName);
        // 查不到，返回 error
        if(sysUser == null) {
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }
        // 查到，核对密码
        String database_pass = sysUser.getPassword();

        // 加密输入的密码
        String input_password = DigestUtils.md5DigestAsHex(loginDto.getPassword().getBytes());

        // 密码错误
        if(!input_password.equals(database_pass)) {
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }

        // 成功，生成 token，放入 redis
        String token = UUID.randomUUID().toString().replace("-", "");
        // key: token  val: 用户信息
        redisTemplate.opsForValue()
                .set("user:login" + token,
                        JSON.toJSONString(sysUser),
                        7,
                        TimeUnit.DAYS);
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return loginVo;
    }
}
