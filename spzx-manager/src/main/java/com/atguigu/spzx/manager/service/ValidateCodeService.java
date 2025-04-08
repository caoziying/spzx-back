package com.atguigu.spzx.manager.service;

import com.atguigu.spzx.model.vo.system.ValidateCodeVo;
import org.springframework.stereotype.Service;

@Service
public interface ValidateCodeService {
    public abstract ValidateCodeVo generateValidateCode();
}
