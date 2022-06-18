package com.chen.boot.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * 考虑到User对象中仅仅包含账号和密码，为了能够获取到原始的Admin对象
 * 专门创建这个类对SpringSecurity中的User类进行扩展（继承SpringSecurity中的 User 类）
 * @author Forget_chen
 * @className SecurityAdmin
 * @desc
 * @date 2022/6/9 16:45
 */
public class SecurityAdmin extends User {
    private static final long serialVersionUID = 1L;
    private Admin originalAdmin; // 原始用户对象，包含Admin对象的全部属性

    /**
     * 有参构造器
     * @param originalAdmin 传入原始的Admin对象
     * @param authorities 传入角色、权限信息的集合
     */
    public SecurityAdmin(Admin originalAdmin, List<GrantedAuthority> authorities) {
        // 调用父类的有参构造器
        super(originalAdmin.getUserName(), originalAdmin.getPassword(), authorities);

        // 将originalAdmin进行初始化
        this.originalAdmin = originalAdmin;

        // 因为我们是继承的User类，eraseCredentials()只擦除User对象中的password
        // 因此我们需要自己手动将原始Admin对象进行密码擦除
        this.originalAdmin.setPassword(null);
    }

    /**
     * 对外提供的获取原始Admin对象的方法
     * @return 原始的Admin对象
     */
    public Admin getOriginalAdmin() {
        return originalAdmin;
    }
}

