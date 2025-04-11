package com.atguigu.spzx.manager.mapper;

import com.atguigu.spzx.model.entity.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper {
    List<SysMenu> findAll();

    void save(SysMenu sysMenu);

    void updateById(SysMenu sysMenu);

    void deleteById(Long id);

    int countByParentId(Long id);
}
