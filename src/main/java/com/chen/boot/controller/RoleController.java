package com.chen.boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.boot.constant.MyConstant;
import com.chen.boot.entity.Auth;
import com.chen.boot.entity.Role;
import com.chen.boot.service.AuthService;
import com.chen.boot.service.RoleService;
import com.chen.boot.utils.TreeNode;
import com.chen.boot.vo.ResultEntity;
import com.chen.boot.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Forget_chen
 * @className RoleController
 * @desc
 * @date 2022/6/14 21:38
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleServiceImpl;

    @Autowired
    private AuthService authServiceImpl;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取分页数据
     *
     * @param roleVo 继承了Role，多了page和limit属性
     */
    @RequestMapping("/load/all")
    public ResultEntity<Object> roleLoadAll(RoleVo roleVo) {
        // 1、创建一个 Page 分页对象
        Page<Role> page = new Page<>(roleVo.getPage(), roleVo.getLimit());

        // 2、创建模糊查询 LambdaQueryWrapper 对象
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 模糊查询：根据 角色名称 进行查询
        lambdaQueryWrapper.like(StringUtils.isNotBlank(roleVo.getName()), Role::getName, roleVo.getName());

        roleServiceImpl.page(page, lambdaQueryWrapper);

        return ResultEntity.successWithData(MyConstant.LAYUI_SUCCESS_CODE, page.getTotal(), page.getRecords());
    }

    /**
     * 新增角色信息
     *
     * @param role 表单提交的新增角色信息
     */
    @PreAuthorize("hasAuthority('role:add')")
    @RequestMapping("/add")
    public ResultEntity<Integer> addRole(Role role) {
        // 添加更新时间
        role.setUpdateTime(sdf.format(new Date()));
        boolean flag = roleServiceImpl.save(role);
        if (flag) {
            return ResultEntity.successWithOutData("新增角色信息成功～～");
        }
        return ResultEntity.failed("新增角色信息失败！");
    }

    /**
     * 修改角色信息
     *
     * @param role 表单提交的修改角色信息,包含该角色的ID数据
     */
    @PreAuthorize("hasAuthority('role:update')")
    @RequestMapping("/update")
    public ResultEntity<Integer> updateRole(Role role) {
        // 添加更新时间
        role.setUpdateTime(sdf.format(new Date()));
        boolean flag = roleServiceImpl.updateById(role);
        if (flag) {
            return ResultEntity.successWithOutData("修改角色信息成功～～");
        }
        return ResultEntity.failed("修改角色信息失败！");
    }

    /**
     * 根据ID删除角色信息
     *
     * @param id 要删除角色的ID
     */
    @PreAuthorize("hasAuthority('role:delete')")
    @RequestMapping("/delete")
    public ResultEntity<Integer> deleteRole(@RequestParam("id") String id) {
        boolean flag = roleServiceImpl.removeById(id);
        if (flag) {
            return ResultEntity.successWithOutData("删除角色信息成功～～");
        }
        return ResultEntity.failed("删除角色信息失败！");
    }

    /**
     * 初始化下拉列表的权限分配
     * 根据角色ID查询所对应的权限
     *
     * @param roleId 角色ID
     */
    @RequestMapping("/get/assign/auth")
    public ResultEntity<Object> getAssignAuth(Integer roleId) {
        LambdaQueryWrapper<Auth> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 1、查询出所有的权限
        List<Auth> authList = authServiceImpl.list();

        // 2、根据角色ID【role_id】查询出已分配的权限ID【auth_id】通过inner_role_auth中间表
        List<Integer> assignedAuthIds = roleServiceImpl.queryAssignedAuthIdByRoleId(roleId);

        List<Auth> assignedAuthList = null;

        // 3、根据 assignedAuthIds 查询出所对应的权限
        if (assignedAuthIds != null && assignedAuthIds.size() > 0) {
            // 该角色有一个或一个以上的权限
            lambdaQueryWrapper.in(Auth::getId, assignedAuthIds);
            assignedAuthList = authServiceImpl.list(lambdaQueryWrapper);
        } else {
            // 该角色没有任何权限
            assignedAuthList = new ArrayList<>();
        }

        // 4、返回前台的格式，带有层级关系 使用treeNode工具类帮助我们判断层级关系
        List<TreeNode> treeNodes = new ArrayList<>();

        for (Auth auth : authList) {
            String checkArr = "0"; // 用于给已经被分配的权限的 复选框被选中标识
            for (Auth assignAuth : assignedAuthList) {
                if (Objects.equals(assignAuth.getId(), auth.getId())) {
                    checkArr = "1"; // 是被分配的权限，复选框为选中
                    break;
                }
            }
            // 是否展开，pid=null 展开
            Boolean spread = auth.getPid() == 0;
            treeNodes.add(new TreeNode(auth.getId(), auth.getPid(), auth.getTitle(), spread, checkArr));
        }

        return ResultEntity.successWithData(0, treeNodes);
    }

    /**
     * 保存角色分配的权限
     * 执行插入数据表【inner_role_auth】
     *
     * @param roleId  角色ID
     * @param authIds 需要分配的权限ID数组
     */
    @PreAuthorize("hasAuthority('role:update')")
    @RequestMapping("/save/assign/auth")
    public ResultEntity<Integer> saveAssignAuth(
            @RequestParam("rid") Integer roleId,
            @RequestParam(value = "ids", required = false) Integer[] authIds // ids可以为空，不分配任何权限
    ) {
        // 1、根据角色ID【role_id】删除该角色的所有权限
        roleServiceImpl.deleteAssignedAuthByRoleId(roleId);

        // 2、判断传入的authIds是否存在值，为空则直接返回 成功 给前台
        if (authIds != null && authIds.length > 0) {
            // 执行保存角色和权限的关系存入数据表【inner_role_auth】中
            for (Integer authId : authIds) {
                roleServiceImpl.saveRoleAuthRelationship(roleId, authId);
            }
        }
        return ResultEntity.successWithOutData("分配权限成功～～");
    }


}
