package com.atguigu.spzx.manager.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysUserMapper;
import com.atguigu.spzx.manager.mapper.SysUserRoleMapper;
import com.atguigu.spzx.manager.service.SysUserService;
import com.atguigu.spzx.model.dto.system.AssginRoleDto;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.dto.system.SysUserDto;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.UserInfoVo;
import com.atguigu.spzx.model.vo.system.LoginVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.minio.Digest;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public LoginVo login(LoginDto loginDto) {
        // 核对验证码
        String codeKey = loginDto.getCodeKey();
        String captcha = loginDto.getCaptcha();
        String redisCode = redisTemplate.opsForValue().get("user:validate" + codeKey);
        if(StrUtil.isEmpty(redisCode) || !StrUtil.equalsIgnoreCase(redisCode, captcha)) {
            throw new GuiguException(ResultCodeEnum.VALIDATECODE_ERROR);
        }
        // 验证通过 删除验证码
        redisTemplate.delete("user:validate" + codeKey);

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
//        System.out.println(input_password);
//        System.out.println(database_pass);
//        System.out.println(loginDto.getPassword());
//        System.out.println(DigestUtils.md5DigestAsHex(loginDto.getPassword().getBytes()));
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

    @Override
    public UserInfoVo getUserInfo(String token) {
        String userJson = redisTemplate.opsForValue().get("user:login" + token);
        return JSON.parseObject(userJson, UserInfoVo.class);
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete("user:login" + token);
    }

    @Override
    public PageInfo<SysUser> findByPage(SysUserDto sysUserDto, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<SysUser> pageInfo = new PageInfo<>(sysUserMapper.findByPage(sysUserDto));
        return pageInfo;
    }

    @Override
    public void saveSysUser(SysUser sysUser) {
        // 用户名重复判断
        String username = sysUser.getUserName();
        SysUser database_user = sysUserMapper.selectUserInfoByUserName(username);
        if(database_user != null) {
            throw new GuiguException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        // 明文密码加密
        String md5_password = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
        sysUser.setPassword(md5_password);

        sysUser.setStatus(1);
        sysUserMapper.saveSysUser(sysUser);
    }

    @Override
    public void updateSysUser(SysUser sysUser) {
        // 用户名重复判断
        String username = sysUser.getUserName();
        SysUser database_user = sysUserMapper.selectUserInfoByUserName(username);
        if(database_user != null && !database_user.getId().equals(sysUser.getId())) {
            throw new GuiguException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        // 明文密码加密
//        String md5_password = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
//        sysUser.setPassword(md5_password);
        sysUserMapper.update(sysUser);
    }

    @Override
    public void deleteById(Long userId) {
        sysUserMapper.delete(userId);
    }

    @Override
    public void doAssign(AssginRoleDto assginRoleDto) {
        // 删除用户之前分配的角色
        sysUserRoleMapper.deleteByUserId(assginRoleDto.getUserId());
        List<Long> roleIds = assginRoleDto.getRoleIdList();
        // 2. 插入新角色（当角色列表非空时）
        if (roleIds != null && !roleIds.isEmpty()) {
            sysUserRoleMapper.batchInsert(assginRoleDto.getUserId(), roleIds);
        }
    }
}
