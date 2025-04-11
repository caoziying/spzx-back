package com.atguigu.spzx.manager.service.impl;

import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysMenuMapper;
import com.atguigu.spzx.manager.service.SysMenuService;
import com.atguigu.spzx.manager.utils.MenuHelper;
import com.atguigu.spzx.model.entity.system.SysMenu;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> findNodes() {
        // 查询所有菜单，返回list集合
        List<SysMenu> allMenu = sysMenuMapper.findAll();
        if(CollectionUtils.isEmpty(allMenu)){
            return null;
        }
        return MenuHelper.buildTree(allMenu);
    }

    @Override
    public void save(SysMenu sysMenu) {
        sysMenuMapper.save(sysMenu);
    }

    @Override
    public void updateById(SysMenu sysMenu) {
        sysMenuMapper.updateById(sysMenu);
    }

    // com.atguigu.spzx.manager.service.impl#SysMenuServiceImpl
    @Override
    public void removeById(Long id) {
        int count = sysMenuMapper.countByParentId(id);  // 先查询是否存在子菜单，如果存在不允许进行删除
        if (count > 0) {
            throw new GuiguException(ResultCodeEnum.NODE_ERROR);
        }
        sysMenuMapper.deleteById(id);		// 不存在子菜单直接删除
    }


}
