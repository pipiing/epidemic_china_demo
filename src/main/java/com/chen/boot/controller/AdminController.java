package com.chen.boot.controller;

import com.chen.boot.entity.Admin;
import com.chen.boot.service.AdminService;
import com.chen.boot.vo.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


/**
 * @author Forget_chen
 * @className AdminController
 * @desc
 * @date 2022/6/10 20:57
 */
@RestController
public class AdminController {
    @Autowired
    private AdminService adminServiceImpl; // 用于操作admin表

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; // 用于密码加密

    /**
     * 实现注册功能
     *
     * @param admin 提单提交的用户数据
     * @return
     */
    @RequestMapping("/do/register")
    public ResultEntity<Integer> doRegister(@RequestParam("checkPassword") String checkPassword, Admin admin) {

        boolean flag = adminServiceImpl.saveAdmin(admin, checkPassword);
        if (flag) {
            // 注册成功后，需要给予访客角色
            adminServiceImpl.addVisitorRole(admin.getId());
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("注册失败！");
    }

    /**
     * 实现忘记密码（重置密码）功能
     *
     * @param admin 提单提交的用户数据
     * @return
     */
    @RequestMapping("/do/resetPwd")
    public ResultEntity<Integer> doResetPwd(Admin admin) {
        boolean flag = adminServiceImpl.resetPwd(admin);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("重置密码失败！");
    }

    /**
     * 更新个人中心信息
     *
     * @param admin 提单提交的用户数据
     * @return
     */
    @RequestMapping("/do/update/personal")
    public ResultEntity<Integer> doUpdatePersonal(Admin admin) {
        boolean flag = adminServiceImpl.saveOrUpdate(admin);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("更新个人信息失败！");
    }

    /**
     * 修改用户密码
     *
     * @param id          要修改密码的用户ID
     * @param oldPassword 旧密码
     * @param newPwdOne   新密码
     * @param newPwdTwo   确认密码
     * @return
     */
    @RequestMapping("/do/modify/pwd")
    public ResultEntity<Integer> doModifyPwd(
            @RequestParam("id") Long id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPwdOne") String newPwdOne,
            @RequestParam("newPwdTwo") String newPwdTwo
    ) {
        // 1、根据用户ID查询数据库，看旧密码是否一致
        Admin admin = adminServiceImpl.getById(id);

        if (!bCryptPasswordEncoder.matches(oldPassword, admin.getPassword())) {
            return ResultEntity.failed("旧密码输入有误，请重新输入！");
        }
        // 2、判断两次新密码是否相同
        if (!Objects.equals(newPwdOne, newPwdTwo)) {
            return ResultEntity.failed("两次新密码输入不一致，请重新输入！");
        }

        // 3、执行更新密码操作，然后进行更新
        admin.setPassword(bCryptPasswordEncoder.encode(newPwdOne));
        boolean flag = adminServiceImpl.updateById(admin);
        if (flag) {
            return ResultEntity.successWithOutData("修改密码成功～～");
        }
        return ResultEntity.failed("修改密码失败！");
    }


}
