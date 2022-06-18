package com.chen.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.boot.entity.Admin;
import com.chen.boot.entity.Auth;
import com.chen.boot.entity.Role;
import com.chen.boot.exception.RegisterFailedException;
import com.chen.boot.mapper.AuthMapper;
import com.chen.boot.mapper.RoleMapper;
import com.chen.boot.service.AdminService;
import com.chen.boot.mapper.AdminMapper;
import com.chen.boot.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chen
 * @description 针对表【admin】的数据库操作Service实现
 * @createDate 2022-06-08 17:09:50
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 注册用户
     *
     * @param admin         表单提交的数据
     * @param checkPassword 需要重复验证的密码
     * @return
     */
    @Override
    public boolean saveAdmin(Admin admin, String checkPassword) {
        // 1、判断表单提交的数据是否有效（为空、为null则无效）
        if (StringUtils.isEmpty(admin.getUserName()) || StringUtils.isEmpty(admin.getPassword()) || StringUtils.isEmpty(admin.getNickName()) || StringUtils.isEmpty(admin.getPhone())) {
            throw new RegisterFailedException("提交注册用户数据有误！");
        }

        // 2、判断数据库中是否存在相同的userName和phone（要保证userName和phone的唯一性）
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUserName, admin.getUserName())
                .or()
                .eq(Admin::getPhone, admin.getPhone())
        ;

        List<Admin> adminList = adminMapper.selectList(lambdaQueryWrapper);

        if (adminList != null && adminList.size() > 0) {
            for (Admin dbAdmin : adminList) {
                if (StringUtils.isNotBlank(dbAdmin.getUserName()) && Objects.equals(dbAdmin.getUserName(),
                        admin.getUserName())) {
                    // 则说明数据库中存在相同的userName，抛出用户名不唯一异常
                    throw new RegisterFailedException("该用户名已经被注册！");
                }
                if (StringUtils.isNotBlank(dbAdmin.getPhone()) && Objects.equals(dbAdmin.getPhone(),
                        admin.getPhone())) {
                    // 则说明数据库中存在相同的phone，抛出电话号码不唯一异常
                    throw new RegisterFailedException("该电话号码已经被注册！");
                }
            }
        }

        // 3、两次密码是否一致
        if (!Objects.equals(admin.getPassword(), checkPassword)) {
            // 不一致，则抛出两次密码不一致异常
            throw new RegisterFailedException("两次密码输入不一致！");
        }

        // 4、封装创建时间
        admin.setCreateTime(sdf.format(new Date()));

        // 5、使用bCryptPasswordEncoder实现密码加密
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));

        // 6、执行添加用户操作
        int insertCount = adminMapper.insert(admin);
        return insertCount != 0;
    }

    /**
     * 忘记密码 - 修改密码
     * 根据用户名和电话号码进行校验
     *
     * @param admin 表单提交的数据
     * @return
     */
    @Override
    public boolean resetPwd(Admin admin) {
        // 1、判断表单提交的数据是否有效（为空、为null则无效）
        if (StringUtils.isEmpty(admin.getUserName()) || StringUtils.isEmpty(admin.getPassword()) || StringUtils.isEmpty(admin.getPhone())) {
            throw new RegisterFailedException("提交重置密码数据有误！");
        }

        // 2、判断是否满足修改密码条件（用户名是否对应电话号码）
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUserName, admin.getUserName()).eq(Admin::getPhone, admin.getPhone());
        Admin dbAdmin = adminMapper.selectOne(lambdaQueryWrapper);
        if (dbAdmin == null) {
            throw new RegisterFailedException("用户名或者电话号码不正确，请重试！");
        }

        // 3、执行修改密码操作
        LambdaUpdateWrapper<Admin> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Admin::getUserName, admin.getUserName()).eq(Admin::getPhone, admin.getPhone());
        // 需要使用bCryptPasswordEncoder对重置的密码进行加密
        lambdaUpdateWrapper.set(Admin::getPassword, bCryptPasswordEncoder.encode(admin.getPassword()));
        int updateCount = adminMapper.update(null, lambdaUpdateWrapper);

        return updateCount != 0;
    }

    /**
     * 针对新用户注册成功，自动赋予访客角色
     */
    @Override
    public void addVisitorRole(Integer adminId) {
        // 查询访客角色的ID
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Role::getName,"访客");
        Role role = roleMapper.selectOne(lambdaQueryWrapper);
        // 保存 用户和角色 关联关系【inner_admin_role】
        adminMapper.saveAssignedRoleByAdminId(adminId,role.getId());
    }

    /**
     * 根据用户ID【admin_id】查询已经被分配的角色ID【role_id】
     *
     * @param adminId 用户ID
     */
    @Override
    public List<Integer> queryAssignedRoleIdByAdminId(Integer adminId) {
        return adminMapper.queryAssignedRoleIdByAdminId(adminId);
    }

    /**
     * 根据用户ID删除已经分配的所有角色
     *
     * @param adminId 用户ID
     */
    @Override
    public void deleteAssignedRoleByAdminId(Integer adminId) {
        adminMapper.deleteAssignedRoleByAdminId(adminId);
    }

    /**
     * 保存用户与角色之间的关联关系【inner_admin_role】
     *
     * @param adminId 用户ID
     * @param roleId  角色ID
     */
    @Override
    public void saveAssignedRoleByAdminId(Integer adminId, Integer roleId) {
        adminMapper.saveAssignedRoleByAdminId(adminId, roleId);
    }

    /**
     * 根据用户ID获取已经分配的角色名称
     *
     * @param adminId 用户ID
     */
    @Override
    public List<String> getAssignedRoleNameByAdminId(Integer adminId) {
        // 1、根据用户ID获取已经分配的角色ID
        List<Integer> assignedRoleIds = adminMapper.queryAssignedRoleIdByAdminId(adminId);

        // 如果已经分配的角色ID集合不为空（null），则通过角色ID获取对应的角色名称，否则返回null;
        if (assignedRoleIds == null || assignedRoleIds.size() == 0) {
            return null;
        }
        // 用于存储已经分配的角色名称
        List<String> roleNameList = new ArrayList<>();
        // 2、通过角色ID获取对应的角色名称
        List<Role> roleList = roleMapper.selectBatchIds(assignedRoleIds);
        for (Role role : roleList) {
            roleNameList.add(role.getName());
        }

        return roleNameList;
    }

    /**
     * 根据用户ID获取已经分配的权限名称
     *
     * @param adminId 用户ID
     */
    @Override
    public List<String> getAssignedAuthNameByAdminId(Integer adminId) {
        // 1、根据用户ID获取已经分配的角色ID
        List<Integer> assignedRoleIds = adminMapper.queryAssignedRoleIdByAdminId(adminId);

        // 如果所分配的角色ID集合不为空（null），则通过角色ID获取对应的角色名称，否则返回null;
        if (assignedRoleIds == null || assignedRoleIds.size() == 0) {
            return null;
        }

        // 使用Set集合存储已经分配的权限ID
        Set<Integer> assignAuthIdsSet = new HashSet<>();
        // 2、根据已分配角色ID获取已经分配的权限ID
        for (Integer assignedRoleId : assignedRoleIds) {
            List<Integer> authIdList = roleMapper.queryAssignedAuthIdByRoleId(assignedRoleId);
            assignAuthIdsSet.addAll(authIdList);
        }

        // 如果所分配权限的ID集合不为空（null），则通过权限ID获取对应的权限名称，否则返回null;
        if (assignAuthIdsSet.size() == 0) {
            return null;
        }
        // 用于存储已经分配的权限名称
        List<String> authNameList = new ArrayList<>();
        // 3、通过权限ID获取对应的权限名称
        List<Auth> authList = authMapper.selectBatchIds(assignAuthIdsSet);
        for (Auth auth : authList) {
            String authName = auth.getName();
            // 用于判断是否为模块（不需要将模块加入），模块为null
            if (authName != null) {
                authNameList.add(authName);
            }
        }

        return authNameList;
    }




}




