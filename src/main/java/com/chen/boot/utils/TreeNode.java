package com.chen.boot.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:
 * @Date: 2019/11/22 15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeNode {

    private Integer id;
    @JsonProperty("parentId") //返回的json的名称 parentId ,为了确定层级关系
    private Integer pid;
    private String title;
    private String icon;
    private String href;
    private Boolean spread;
    private List<TreeNode> children = new ArrayList<>();

    /**
     * 0为不选中  1为选中
     */
    private String checkArr="0";

    /**
     * 首页左边导航菜单的构造器
     */
    public TreeNode(Integer id, Integer pid, String title, String icon, String href, Boolean spread) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.icon = icon;
        this.href = href;
        this.spread = spread;
    }

    /**
     * 部门 dtree的构造器
     * @param id id
     * @param pid 父亲parentId
     * @param title 名称
     * @param spread 是否展开
     */
    public TreeNode(Integer id, Integer pid, String title, Boolean spread) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.spread = spread;
    }

    /**
     * 给角色分配权限的构造器
     */
    public TreeNode(Integer id, Integer pid, String title, Boolean spread, String checkArr) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.spread = spread;
        this.checkArr = checkArr;
    }
}
