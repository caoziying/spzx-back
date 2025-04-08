package com.atguigu.spzx.manager.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.atguigu.spzx.manager.service.ValidateCodeService;
import com.atguigu.spzx.model.vo.system.ValidateCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public ValidateCodeVo generateValidateCode() {
        // 通过 hutool 生成图片验证码
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 48, 4, 2);
        String code = circleCaptcha.getCode();
        String imageBase64 = circleCaptcha.getImageBase64();

        // 存入 redis, [key, val]  key可以用 uuid 生成
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set("user:validate"+key, code, 5, TimeUnit.MINUTES);
        System.out.println("user:validate"+key + "#" + redisTemplate.opsForValue().get("user:validate"+key) + "#" + code);
        // ret
        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
        validateCodeVo.setCodeKey(key);
        validateCodeVo.setCodeValue("data:image/png;base64," + imageBase64);
        return validateCodeVo;
    }
}
