package com.atguigu.spzx.manager.service;

import com.atguigu.spzx.model.dto.system.AssginMenuDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SysRoleMenuService {
    Map<String, Object> findSysRoleMenuByRoleId(Long roleId);

    void doAssign(AssginMenuDto assginMenuDto);
}
