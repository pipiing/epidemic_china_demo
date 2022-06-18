package com.chen.boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.boot.entity.Role;
import com.chen.boot.service.RoleService;
import com.chen.boot.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author chen
* @description 针对表【role】的数据库操作Service实现
* @createDate 2022-06-14 22:09:32
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 根据角色ID【role_id】查询已经被分配的权限ID【auth_id】
     * @param roleId 角色ID
     */
    @Override
    public List<Integer> queryAssignedAuthIdByRoleId(Integer roleId) {

        return roleMapper.queryAssignedAuthIdByRoleId(roleId);
    }

    /**
     * 根据角色ID删除已经分配的所有权限
     * @param roleId 角色ID
     */
    @Override
    public void deleteAssignedAuthByRoleId(Integer roleId) {
        roleMapper.deleteAssignedAuthByRoleId(roleId);
    }

    /**
     * 保存角色与权限之间的关联关系【inner_role_auth】
     * @param roleId 角色ID
     * @param authId 权限ID
     */
    @Override
    public void saveRoleAuthRelationship(Integer roleId, Integer authId) {
        roleMapper.saveRoleAuthRelationship(roleId, authId);
    }

}




