package com.chen.boot.service;

import com.chen.boot.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author chen
* @description 针对表【admin】的数据库操作Service
* @createDate 2022-06-08 17:09:50
*/
public interface AdminService extends IService<Admin> {
    boolean saveAdmin(Admin admin, String checkPassword);
    boolean resetPwd(Admin admin);

    List<Integer> queryAssignedRoleIdByAdminId(Integer adminId);

    void deleteAssignedRoleByAdminId(Integer adminId);

    void saveAssignedRoleByAdminId(Integer adminId, Integer roleId);

    List<String> getAssignedRoleNameByAdminId(Integer adminId);

    List<String> getAssignedAuthNameByAdminId(Integer adminId);

    void addVisitorRole(Integer adminId);
}
