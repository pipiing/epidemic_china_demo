package com.chen.boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.boot.entity.Admin;
import com.chen.boot.entity.GlobalDataBean;
import com.chen.boot.service.AdminService;
import com.chen.boot.service.GlobalDataBeanService;
import com.chen.boot.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;

/**
 * @author Forget_chen
 * @className toPageContoller
 * @desc
 * @date 2022/6/3 15:51
 */
@Controller
public class toPageController {
    @Autowired
    private GlobalDataBeanService globalDataBeanServiceImpl; // 用于操作nocv_global_data表

    @Autowired
    private AdminService adminServiceImpl; // 用于操作admin表

    private final Jedis jedis = new Jedis();

    /**
     * 跳转到中国新增确诊疫情图页面
     */
    @RequestMapping("/to/china/add")
    public String toAdd() {
        return "china-add";
    }

    /**
     * 跳转到全球现有确诊疫情图页面
     */
    @RequestMapping("/to/global")
    public String toGlobal() {
        return "global";
    }

    /**
     * 跳转到全球新增确诊疫情图页面
     */
    @RequestMapping("/to/global/add")
    public String toGlobalAdd() {
        return "global-add";
    }

    /**
     * 跳转到中国疫情数据管理页面
     */
    @RequestMapping("/to/chinaData/admin")
    public String toChinaDataAdmin() {
        return "admin/chinaData-admin";
    }

    /**
     * 跳转到健康打卡数据管理页面
     */
    @RequestMapping("/to/healthClock/admin")
    public String toHealthClockAdmin() {
        return "admin/health-clock";
    }

    /**
     * 跳转到中国现有确诊疫情图页面
     * 获取中国疫情数据进行展示
     */
    @RequestMapping("/to/china")
    public String showData(Model model) {
        /*
         1、先从Redis中查询缓存，看是否命中？
            -- 命中，有数据【直接返回】
            -- 未命中，没有数据【查询MySQL数据库，更新缓存】
         */
        jedis.auth("202428");

        // 判断jedis客户端是否启动
        if (jedis != null) {
            String nowConfirm = jedis.get("nowConfirm");
            String confirm = jedis.get("confirm");
            String heal = jedis.get("heal");
            String dead = jedis.get("dead");
            String date = jedis.get("date");

            // 缓存命中【有数据】
            if (CommonUtil.isAllNotBlank(nowConfirm, confirm, heal, dead)) {
                // 创建一个GlobalDataBean类，用于存放Redis中取出的值
                GlobalDataBean chinaDataRedis = new GlobalDataBean();
                chinaDataRedis.setNowConfirm(Integer.parseInt(nowConfirm));
                chinaDataRedis.setConfirm(Integer.parseInt(confirm));
                chinaDataRedis.setHeal(Integer.parseInt(heal));
                chinaDataRedis.setDead(Integer.parseInt(dead));
                chinaDataRedis.setDate(date);

                model.addAttribute("chinaData", chinaDataRedis);
            } else {
                // 缓存未命中【没有数据】，需要从MySQL中读取，更新Redis缓存
                LambdaQueryWrapper<GlobalDataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(GlobalDataBean::getName, "中国");

                GlobalDataBean chinaData = globalDataBeanServiceImpl.list().get(0);

                // 更新Redis中的缓存 【1、删除原来数据的缓存（在更新数据库时，已经flushDB缓存库啦）】 【2、插入新的数据缓存】
                jedis.set("nowConfirm", String.valueOf(chinaData.getNowConfirm()));
                jedis.set("confirm", String.valueOf(chinaData.getConfirm()));
                jedis.set("heal", String.valueOf(chinaData.getHeal()));
                jedis.set("dead", String.valueOf(chinaData.getDead()));
                jedis.set("date", String.valueOf(chinaData.getDate()));

                // 向request域中共享中国疫情数据
                model.addAttribute("chinaData", chinaData);
            }
            // 跳转到中国
            return "china";
        }
        // Jedis客户端未连接，将从数据库中查询数据，并携带返回
        LambdaQueryWrapper<GlobalDataBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GlobalDataBean::getName, "中国");

        GlobalDataBean chinaData = globalDataBeanServiceImpl.list().get(0);
        // 向request域中共享中国疫情数据
        model.addAttribute("chinaData", chinaData);

        // 跳转到中国
        return "china";
    }

    /**
     * 跳转到后台首页
     * 获取用户数据，通过session共享到Request域中
     */
    @RequestMapping("/")
    public String index(HttpSession session) {
        // 从session域中获取当前登录的用户信息
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
        Authentication authentication = securityContextImpl.getAuthentication();
        // 将主体转为用户详情对象
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        LambdaQueryWrapper<Admin> adminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminLambdaQueryWrapper.eq(Admin::getUserName, username);
        Admin admin = adminServiceImpl.getOne(adminLambdaQueryWrapper);
        // 向 request 域中共享当前登录的用户信息（当前会话）
        session.setAttribute("admin", admin);
        return "index";
    }

    /**
     * 跳转到登录页面
     */
    @RequestMapping("/to/login")
    public String toLogin() {
        return "login";
    }

    /**
     * 跳转到注册页面
     */
    @RequestMapping("/to/register")
    public String toRegister() {
        return "register";
    }


    /**
     * 跳转到个人中心页面
     * 将用户信息存储到Session中
     */
    @RequestMapping("/to/person")
    public String toPerson(HttpSession session, @RequestParam("id") String id) {
        Admin admin = adminServiceImpl.getById(id);
        // 将当前登录的admin信息，共享到request域中
        session.setAttribute("admin", admin);
        return "person";
    }

    /**
     * 跳转到菜单管理页面
     */
    @PreAuthorize("hasRole('管理员') or hasRole('用户') or hasAuthority('menu:get')")
    @RequestMapping("/to/menu")
    public String toMenu() {
        return "menu/menu";
    }

    /**
     * 跳转到角色管理页面
     */
    @PreAuthorize("hasRole('管理员') or hasRole('用户') or hasAuthority('menu:get')")
    @RequestMapping("/to/role")
    public String toRole() {
        return "role/role";
    }

    /**
     * 跳转到用户管理页面
     */
    @PreAuthorize("hasRole('管理员') or hasRole('用户') or hasAuthority('menu:get')")
    @RequestMapping("/to/user")
    public String toUser() {
        return "user/user";
    }

    /**
     * 跳转到修改密码页面
     */
    @RequestMapping("/to/modify/pwd")
    public String toModifyPwd() {
        return "modify-pwd";
    }

}
