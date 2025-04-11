package com.atguigu.spzx.manager.service.impl;

import com.atguigu.spzx.manager.mapper.SysMenuMapper;
import com.atguigu.spzx.manager.mapper.SysRoleMapper;
import com.atguigu.spzx.manager.mapper.SysRoleMenuMapper;
import com.atguigu.spzx.manager.service.SysMenuService;
import com.atguigu.spzx.manager.service.SysRoleMenuService;
import com.atguigu.spzx.model.dto.system.AssginMenuDto;
import com.atguigu.spzx.model.entity.system.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public Map<String, Object> findSysRoleMenuByRoleId(Long roleId) {
        List<SysMenu> allMenus = sysMenuService.findNodes();
        List<Long> roleMenuIds = sysRoleMenuMapper.findSysRoleMenuByRoleId(roleId);
        Map<String, Object> map = new HashMap<>();
        map.put("allMenus", allMenus);
        map.put("roleMenuIds", roleMenuIds);
        return map;
    }

    @Override
    public void doAssign(AssginMenuDto assginMenuDto) {
        // 根据角色的id删除其所对应的菜单数据
        sysRoleMenuMapper.deleteByRoleId(assginMenuDto.getRoleId());

        // 获取菜单的id
        List<Map<String, Number>> menuInfo = assginMenuDto.getMenuIdList();
        if(menuInfo != null && menuInfo.size() > 0) {
            sysRoleMenuMapper.doAssign(assginMenuDto) ;
        }
    }


}
