package com.chen.boot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.boot.entity.Admin;
import com.chen.boot.entity.Auth;
import com.chen.boot.entity.DataBean;
import com.chen.boot.entity.Role;
import com.chen.boot.mapper.AdminMapper;
import com.chen.boot.mapper.AuthMapper;
import com.chen.boot.mapper.DataBeanMapper;
import com.chen.boot.mapper.RoleMapper;
import com.chen.boot.service.RoleService;
import com.chen.boot.service.impl.RoleServiceImpl;
import com.chen.boot.utils.CommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class EpidemicChinaApplicationTests {

    @Autowired
    private DataBeanMapper dataBeanMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private RoleService roleServiceImpl;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void contextLoads() {
    }

    @Test
    public void test() {
        Page<DataBean> page = new Page<DataBean>(1, 5);
        LambdaQueryWrapper<DataBean> queryWrapper = new LambdaQueryWrapper<>();
        // 3、按照确诊病例人数进行倒序排序（从大到小）
        queryWrapper.orderByDesc(DataBean::getNowConfirm);

        dataBeanMapper.selectPage(page, queryWrapper);

        System.out.println(page.getTotal());
    }

    @Test
    public void test1() {
        // 利用UUID生成文件名
        String fileName = "123.123.jpg";
        String suffix = "";
        if (StringUtils.isBlank(fileName)) {
            System.out.println("参数错误！");
        }
        String[] split = fileName.split("\\.");
        suffix = split[split.length - 1];

        fileName = (UUID.randomUUID() + "." + suffix).replace("-", "");

        System.out.println(fileName);
    }

    @Test
    public void test2() {
        Role role = new Role(null, "用户", "拥有所有查看权限", sdf.format(new Date()));
        roleServiceImpl.save(role);
    }

    @Test
    public void test3() {
        System.out.println(bCryptPasswordEncoder.matches("123456", "$2a$10$PgjtIn2tQpA5xyXD0" +
                ".k/geGoX7dR9D2xjzZiT/xpNQTkTbEDftbf6"));
    }

    @Test
    public void test4() {
        List<Map<String, Object>> maps = roleServiceImpl.listMaps();
        System.out.println(maps);
    }

    @Test
    public void test5() {
        // 1、根据用户ID获取已经分配的角色ID
        List<Integer> assignedRoleIds = adminMapper.queryAssignedRoleIdByAdminId(1);

        // 如果所分配的角色ID集合不为空（null），则通过角色ID获取对应的角色名称，否则返回null;
        if (assignedRoleIds == null || assignedRoleIds.size() == 0) {
            System.out.println("未分配角色");
            return;
        }

        Set<Integer> assignAuthIdsSet = new HashSet<>();
        // 2、根据已分配角色ID获取已经分配的权限ID
        for (Integer assignedRoleId : assignedRoleIds) {
            List<Integer> authIdList = roleMapper.queryAssignedAuthIdByRoleId(assignedRoleId);
            assignAuthIdsSet.addAll(authIdList);
        }

        // 用于存储已经分配的权限名称
        List<String> authNameList = new ArrayList<>();
        // 3、通过权限ID获取对应的权限名称
        List<Auth> authList = authMapper.selectBatchIds(assignAuthIdsSet);
        for (Auth auth : authList) {
            String authName = auth.getName();
            if (authName != null) {
                authNameList.add(authName);
            }
        }

        System.out.println(authNameList);

    }

    @Test
    public void test6() {
        String a = "1234";
        String b = "123";
        String c = "null";
        String d = "456";

        System.out.println(CommonUtil.isAllNotBlank(a,b,c,d));
    }

}
