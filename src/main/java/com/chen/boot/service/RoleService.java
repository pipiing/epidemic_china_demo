package com.chen.boot.service;

import com.chen.boot.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author chen
* @description 针对表【role】的数据库操作Service
* @createDate 2022-06-14 22:09:32
*/
public interface RoleService extends IService<Role> {

    List<Integer> queryAssignedAuthIdByRoleId(Integer roleId);

    void deleteAssignedAuthByRoleId(Integer roleId);

    void saveRoleAuthRelationship(Integer roleId, Integer authId);

}
