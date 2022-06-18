package com.chen.boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.boot.constant.MyConstant;
import com.chen.boot.entity.Menu;
import com.chen.boot.service.MenuService;
import com.chen.boot.utils.TreeNode;
import com.chen.boot.vo.MenuVo;
import com.chen.boot.vo.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Forget_chen
 * @className MenuController
 * @desc
 * @date 2022/6/13 23:03
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuServiceImpl;

    /**
     * 获取分页数据
     *
     * @param menuVo 继承了Menu，多了page和limit属性
     * @return
     */
    @RequestMapping("/load/right")
    public ResultEntity<Object> loadRightMenu(MenuVo menuVo) {
        // 1、创建 Page 分页对象
        Page<Menu> page = new Page<>(menuVo.getPage(), menuVo.getLimit());

        // 2、创建模糊查询 LambdaQueryWrapper 对象
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 模糊查询：根据菜单标题进行查询
        lambdaQueryWrapper.like(StringUtils.isNotBlank(menuVo.getTitle()), Menu::getTitle, menuVo.getTitle());
        // 按照排序数值的升序排序
        lambdaQueryWrapper.orderByAsc(Menu::getOrderNum);

        // 分页查询
        menuServiceImpl.page(page, lambdaQueryWrapper);

        // 6、请求成功，携带参数：总共数据条数、数据 返回类型：Object类型
        return ResultEntity.successWithData(MyConstant.LAYUI_SUCCESS_CODE, page.getTotal(), page.getRecords());
    }

    /**
     * 加载 下拉菜单数据和初始化下拉树
     */
    @RequestMapping("/load/left/tree/json")
    public ResultEntity<Object> LoadLeftTreeJsonMenu() {
        List<Menu> menuList = menuServiceImpl.list();
        // 创建一个List集合对象用于存放treeNode的层级关系
        ArrayList<TreeNode> treeNodes = new ArrayList<>();

        for (Menu menu : menuList) {
            Boolean open = menu.getOpen() == 1;
            treeNodes.add(new TreeNode(menu.getId(), menu.getPid(), menu.getTitle(), open));
        }
        return ResultEntity.successWithData(0, treeNodes);
    }

    /**
     * 赋值最大排序码 + 1
     * 条件查询：根据排序码倒序排序，取第一条数据
     */
    @RequestMapping("/load/max/orderNum")
    public Map<String, Integer> loadMaxOrderNum() {
        HashMap<String, Integer> map = new HashMap<>();
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.orderByDesc(Menu::getOrderNum);
        // 获取当前最大的排序码
        List<Menu> menuList = menuServiceImpl.list(lambdaQueryWrapper);
        Menu menu = menuList.get(0);
        // 判断menu是否有效，无效则说明不存在任何菜单，排序码初始化为1
        if (menu == null) {
            map.put("value", 1);
            return map;
        }
        // 有效，则将最大的排序码+1赋值
        map.put("value", menu.getOrderNum() + 1);

        return map;
    }

    /**
     * 新增菜单信息
     *
     * @param menu 表单提交的新增菜单信息
     */
    @PreAuthorize("hasAuthority('menu:add')")
    @RequestMapping("/add")
    public ResultEntity<Integer> addMenu(Menu menu) {
        boolean flag = menuServiceImpl.save(menu);
        if (flag) {
            return ResultEntity.successWithOutData("新增菜单信息成功～～");
        }
        return ResultEntity.failed("新增菜单信息失败！");
    }

    /**
     * 修改菜单信息
     *
     * @param menu 表单提交的修改菜单信息
     */
    @PreAuthorize("hasAuthority('menu:update')")
    @RequestMapping("/update")
    public ResultEntity<Integer> updateMenu(Menu menu) {
        boolean flag = menuServiceImpl.updateById(menu);
        if (flag) {
            return ResultEntity.successWithOutData("修改菜单信息成功～～");
        }
        return ResultEntity.failed("修改菜单信息失败！");
    }

    /**
     * 根据ID检测该节点是否存在子节点
     * 1、有子节点：id=数据库中的pid
     * 2、无子节点：id!=数据库中的pid
     *
     * @param id 根据ID查询该节点是否存在子节点
     * @return
     */
    @RequestMapping("/check/has/childrenNode")
    public Map<String, Boolean> checkHasChildrenNode(@RequestParam("id") Integer id) {
        HashMap<String, Boolean> map = new HashMap<>();
        // 根据ID查询该menu是否在数据库中存在子节点（有子节点：id=数据库中的pid，无子节点：id!=数据库中的pid）
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getPid, id);

        List<Menu> menuList = menuServiceImpl.list(lambdaQueryWrapper);
        if (menuList != null && menuList.size() > 0) {
            // 说明该节点有子节点，不能删除
            map.put("value", true);
        } else {
            // 说明该节点无子节点，可以删除
            map.put("value", false);
        }
        return map;
    }

    /**
     * 根据ID删除菜单信息
     *
     * @param id 根据ID删除
     * @return
     */
    @PreAuthorize("hasAuthority('menu:delete')")
    @RequestMapping("/delete")
    public ResultEntity<Integer> deleteMenu(@RequestParam("id") Integer id) {
        boolean flag = menuServiceImpl.removeById(id);
        if (flag) {
            return ResultEntity.successWithOutData();
        }
        return ResultEntity.failed("删除菜单信息失败！");
    }


}
