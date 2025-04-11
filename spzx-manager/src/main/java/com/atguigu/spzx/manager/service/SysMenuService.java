package com.atguigu.spzx.manager.service;

import com.atguigu.spzx.model.entity.system.SysMenu;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SysMenuService {
    List<SysMenu> findNodes();

    void save(SysMenu sysMenu);

    void updateById(SysMenu sysMenu);

    void removeById(Long id);
}
