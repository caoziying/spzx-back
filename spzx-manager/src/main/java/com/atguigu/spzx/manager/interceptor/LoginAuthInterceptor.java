package com.atguigu.spzx.manager.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static com.atguigu.spzx.utils.AuthContextUtil.threadLocal;

// 拦截器
@Component
public class LoginAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1 获取请求方式
        // options  预检请求，放行
        String method = request.getMethod();
        if("OPTIONS".equals(method)){
            return true;
        }
        // 2 从请求头获取 token
        String token = request.getHeader("token");
        // token 为空，ret
        if(StrUtil.isEmpty(token)){
            responseNoLoginInfo(response);
            return false;
        }
        // 非空，与 redis 比对
        String userInfoString = redisTemplate.opsForValue().get("user:login" + token);
        if(StrUtil.isEmpty(userInfoString)){
            responseNoLoginInfo(response);
            return false;
        }

        // 比对成功，把用户信息放入 threadLocal
        threadLocal.set(JSON.parseObject(userInfoString, SysUser.class));

        // 更新 redis 过期时间
        redisTemplate.expire("user:login" + token, 30, TimeUnit.MINUTES);

        // 放行
        return true;
    }

    //响应208状态码给前端
    private void responseNoLoginInfo(HttpServletResponse response) {
        Result<Object> result = Result.build(null, ResultCodeEnum.LOGIN_AUTH);
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JSON.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 删除 threadLocal 中数据
        threadLocal.remove();
    }
}
