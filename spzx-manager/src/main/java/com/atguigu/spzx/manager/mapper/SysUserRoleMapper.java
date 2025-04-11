package com.atguigu.spzx.manager.mapper;

import com.atguigu.spzx.model.entity.system.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserRoleMapper {
    List<Long> findUserRoles(Long userId);
    void deleteByUserId(Long userId);
    void batchInsert(Long userId, List<Long> roleIds);
}
