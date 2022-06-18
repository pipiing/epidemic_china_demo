package com.chen.boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.boot.constant.MyConstant;
import com.chen.boot.entity.Admin;
import com.chen.boot.entity.Role;
import com.chen.boot.exception.RegisterFailedException;
import com.chen.boot.service.AdminService;
import com.chen.boot.service.RoleService;
import com.chen.boot.vo.AdminVo;
import com.chen.boot.vo.ResultEntity;
import com.chen.boot.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Forget_chen
 * @className UserController
 * @desc
 * @date 2022/6/15 15:22
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AdminService adminServiceImpl;

    @Autowired
    private RoleService roleServiceImpl;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 获取分页数据
     *
     * @param adminVo 继承了Admin，多了page和limit属性
     */
    @RequestMapping("/load/all")
    public ResultEntity<Object> userLoadAll(AdminVo adminVo) {
        // 1、创建一个 Page 分页对象
        Page<Admin> page = new Page<>(adminVo.getPage(), adminVo.getLimit());

        // 2、创建模糊查询 LambdaQueryWrapper 对象
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 模糊查询：根据 昵称、电话号码、家庭住址 进行查询
        lambdaQueryWrapper.like(StringUtils.isNotBlank(adminVo.getNickName()), Admin::getNickName,
                adminVo.getNickName());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(adminVo.getPhone()), Admin::getPhone, adminVo.getPhone());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(adminVo.getAddress()), Admin::getAddress, adminVo.getAddress());

        adminServiceImpl.page(page, lambdaQueryWrapper);

        return ResultEntity.successWithData(MyConstant.LAYUI_SUCCESS_CODE, page.getTotal(), page.getRecords());
    }

    /**
     * 添加用户信息
     *
     * @param admin 表单提交的新增用户信息
     */
    @PreAuthorize("hasAuthority('user:add')")
    @RequestMapping("/add")
    public ResultEntity<Integer> addUser(Admin admin) {
        // 如果result为空（null）则说明用户名和电话号码都是唯一的，直接执行新增操作
        ResultEntity<Integer> result = checkUserNameOrPhoneIsExists(admin.getUserName(), admin.getPhone());
        if (result != null) {
            // 否则直接返回前台错误原因
            return result;
        }

        // 为新增的用户生成创建时间
        admin.setCreateTime(sdf.format(new Date()));
        // 将明文密码进行加密操作，再存入数据库
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));

        // 执行新增用户信息操作
        boolean flag = adminServiceImpl.save(admin);
        if (flag) {
            return ResultEntity.successWithOutData("新增用户信息成功～～");
        }
        return ResultEntity.failed("新增用户信息失败！");
    }

    /**
     * 修改用户信息
     *
     * @param admin 表单提交的修改用户信息
     */
    @PreAuthorize("hasAuthority('user:update')")
    @RequestMapping("/update")
    public ResultEntity<Integer> updateUser(Admin admin) {
        String userName = admin.getUserName();
        String phone = admin.getPhone();

        // 更新时，先判断是否修改了用户名和电话，没有修改则不需要判断唯一性
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUserName, userName).eq(Admin::getPhone, phone);
        Admin dbAdmin = adminServiceImpl.getOne(lambdaQueryWrapper);

        if (dbAdmin == null) {
            // 如果result为空（null）则说明用户名和电话号码都是唯一的，直接执行修改操作
            ResultEntity<Integer> result = checkUserNameOrPhoneIsExists(userName, phone);
            if (result != null) {
                // 否则直接返回前台错误原因
                return result;
            }
        }

        // 执行修改用户信息操作
        boolean flag = adminServiceImpl.updateById(admin);
        if (flag) {
            return ResultEntity.successWithOutData("修改用户信息成功～～");
        }
        return ResultEntity.failed("修改用户信息失败！");
    }

    /**
     * 根据用户ID删除用户信息
     *
     * @param id 要删除的用户ID
     */
    @PreAuthorize("hasAuthority('user:delete')")
    @RequestMapping("/delete")
    public ResultEntity<Integer> deleteUser(@RequestParam("id") Integer id) {
        boolean flag = adminServiceImpl.removeById(id);
        if (flag) {
            return ResultEntity.successWithOutData("删除用户信息成功～～");
        }
        return ResultEntity.failed("删除用户信息失败！");
    }

    /**
     * 根据用户ID重置密码
     * 重置后的默认密码为：123456
     *
     * @param id 要重置密码的用户ID
     */
    @PreAuthorize("hasAuthority('user:update')")
    @RequestMapping("/resetPwd")
    public ResultEntity<Integer> resetPwdUser(@RequestParam("id") Integer id) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setPassword(bCryptPasswordEncoder.encode("123456"));

        // 执行重置密码操作
        boolean flag = adminServiceImpl.updateById(admin);
        if (flag) {
            return ResultEntity.successWithOutData("重置用户密码成功，默认密码：123456");
        }
        return ResultEntity.failed("重置用户密码失败！");
    }

    /**
     * 打开分配角色模态窗时，初始化用户角色
     *
     * @param adminId 要分配角色的用户ID
     */
    @RequestMapping("/init/role/by/id")
    public ResultEntity<Object> userInitRoleById(@RequestParam("id") Integer adminId) {
        // 1、查询所有的角色
        List<Map<String, Object>> listMaps = roleServiceImpl.listMaps();

        // 2、根据用户ID【admin_id】查询已分配的的角色ID【role_id】通过inner_admin_role中间表
        List<Integer> assignedRoleIds = adminServiceImpl.queryAssignedRoleIdByAdminId(adminId);

        // 3、将被分配的角色，标志为勾选状态，返回给前端
        for (Map<String, Object> map : listMaps) {
            Boolean LAY_CHECKED = false; // 前端被勾选状态的标志，固定使用"LAY_CHECKED"作为key
            for (Integer assignedRoleId : assignedRoleIds) {
                // 判断 已经分配的角色ID == 和map中的角色ID相等时，就标识为勾选
                Integer roleId = (Integer) map.get("id");
                if (Objects.equals(roleId, assignedRoleId)) {
                    // 标志为勾选状态
                    LAY_CHECKED = true;
                    break;
                }
            }
            map.put("LAY_CHECKED", LAY_CHECKED);
        }
        // 将数据的数量和数据，传递给前台
        return ResultEntity.successWithData(0, (long) listMaps.size(), listMaps);
    }

    /**
     * 保存用户分配的角色
     * 执行插入数据表【inner_admin_role】
     *
     * @param adminId  用户ID
     * @param roleIds 需要分配的角色ID数组
     */
    @PreAuthorize("hasAuthority('user:update')")
    @RequestMapping("/save/assign/role")
    public ResultEntity<Integer> saveAssignRole(
            @RequestParam("uid") Integer adminId,
            @RequestParam(value = "ids", required = false) Integer[] roleIds // ids可以为空，不分配任何权限
    ) {
        // 1、根据用户ID【admin_id】删除该用户的所有角色
        adminServiceImpl.deleteAssignedRoleByAdminId(adminId);

        // 2、判断传入的roleIds是否存在值，为空则直接返回 成功 给前台
        if (roleIds != null && roleIds.length > 0) {
            // 执行保存用户和角色的关系存入数据表【inner_admin_role】中
            for (Integer roleId : roleIds) {
                adminServiceImpl.saveAssignedRoleByAdminId(adminId, roleId);
            }
        }
        return ResultEntity.successWithOutData("分配角色成功～～");
    }


    /**
     * 验证数据库中是否存在相同的用户名、电话号码，要保证用户名、电话号码的唯一性
     *
     * @param userName 用户名
     * @param phone    电话号码
     */
    public ResultEntity<Integer> checkUserNameOrPhoneIsExists(String userName, String phone) {
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUserName, userName)
                .or()
                .eq(Admin::getPhone, phone);
        List<Admin> adminList = adminServiceImpl.list();

        if (adminList != null && adminList.size() > 0) {
            for (Admin dbAdmin : adminList) {
                if (StringUtils.isNotBlank(dbAdmin.getUserName()) && Objects.equals(dbAdmin.getUserName(),
                        userName)) {
                    // 则说明数据库中存在相同的userName，抛出用户名不唯一异常
                    return ResultEntity.failed("该用户名已经被注册！");
                }
                if (StringUtils.isNotBlank(dbAdmin.getPhone()) && Objects.equals(dbAdmin.getPhone(),
                        phone)) {
                    // 则说明数据库中存在相同的phone，抛出电话号码不唯一异常
                    return ResultEntity.failed("该电话号码已经被注册！");
                }
            }
        }
        return null;
    }


}
