package com.chen.boot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.chen.boot.entity.Admin;
import com.chen.boot.entity.Role;
import com.chen.boot.entity.SecurityAdmin;
import com.chen.boot.exception.UserNameNotFoundException;
import com.chen.boot.mapper.AdminMapper;
import com.chen.boot.service.AdminService;
import com.chen.boot.service.AuthService;
import com.chen.boot.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Forget_chen
 * @className UserDetailsService
 * @desc
 * @date 2022/6/8 20:23
 */
@Component
public class AdminUserDetailsService implements UserDetailsService {
    @Autowired
    private AdminService adminServiceImpl;

    @Autowired
    private RoleService roleServiceImpl;

    @Autowired
    private AuthService authServiceImpl;


    /**
     * 根据表单提交的用户名查询User对象，并装配角色、权限等信息
     *
     * @param username 表单提交的用户名
     * @return 返回封装好的User对象
     * @throws UsernameNotFoundException 用户名找不到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1、根据username查询Admin对象
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(username), Admin::getUserName, username);
        Admin admin = adminServiceImpl.getOne(lambdaQueryWrapper);

        if (admin == null) {
            throw new UserNameNotFoundException("用户名不存在！");
        }

        // 2、根据adminId查询对应的角色、权限信息（只需要获取到对应的name即可）
        Integer adminId = admin.getId();
        List<String> assignedRoleNameList = adminServiceImpl.getAssignedRoleNameByAdminId(adminId);
        List<String> assignedAuthNameList = adminServiceImpl.getAssignedAuthNameByAdminId(adminId);

        // 3、创建集合对象存储GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (assignedRoleNameList != null && assignedRoleNameList.size() > 0) {
            // 4、遍历assignedRoleList集合将角色信息（添加"ROLE_"前缀）添加到authorities集合中
            for (String roleName : assignedRoleNameList) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            }
        }

        if (assignedAuthNameList != null && assignedAuthNameList.size() > 0) {
            // 5、遍历assignedAuthNameList集合将权限信息添加到authorities集合中
            for (String authName : assignedAuthNameList) {
                authorities.add(new SimpleGrantedAuthority(authName));
            }
        }

        // 6、封装成UserDetails对象并且返回，此时这里不需要再使用BCryptPasswordEncoder进行加密，因为数据库中的数据已经被加密过啦
        return new SecurityAdmin(admin, authorities);
    }


}
