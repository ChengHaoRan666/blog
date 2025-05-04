package com.chr.blog.controller.admin;

import cn.hutool.captcha.ShearCaptcha;
import com.chr.blog.domain.entity.AdminUser;
import com.chr.blog.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理控制层
 *
 * @author 程浩然
 * @since 2025-01-04
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;


    /**
     * 页面展示 Get请求login，展示后台管理登录页
     *
     * @return 页面
     */
    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }

    /**
     * 页面展示 Get请求index，展示后台管理首页
     *
     * @param request req
     * @return 页面
     */
    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("path", "index");
        // 设置分类数量用于页面展示
        request.setAttribute("categoryCount", categoryService.getTotalCategories());
        // 设置总文章数量用于页面展示
        request.setAttribute("blogCount", blogService.getTotalBlogs());
        // 设置友情链接数量用于页面展示
        request.setAttribute("linkCount", linkService.getTotalLinks());
        // 设置标签数量用于页面展示
        request.setAttribute("tagCount", tagService.getTotalTags());
        // 设置收到评论数用于页面展示
        request.setAttribute("commentCount", commentService.getTotalComments());
        return "admin/index";
    }


    /**
     * 用户登录进入后台管理页面
     *
     * @param userName   用户名
     * @param password   密码
     * @param verifyCode 验证码
     * @param session    Session
     * @return 跳转页面
     */
    @PostMapping(value = "/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session) {
        // 如果验证码为空，重新到登录页
        if (!StringUtils.hasText(verifyCode)) {
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }

        // 如果用户名或密码为空，重新到登录页
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
            return "admin/login";
        }

        // 获取验证码
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
        // 进行验证是否正确
        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }

        // 验证全部正确进行登录，获取登录user
        AdminUser adminUser = adminUserService.login(userName, password);

        // 将user存入Session里
        if (adminUser != null) {
            session.setAttribute("loginUser", adminUser.getNickName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            //session过期时间设置为7200秒 即两小时
            //session.setMaxInactiveInterval(60 * 60 * 2);
            return "redirect:/admin/index";
        } else {
            session.setAttribute("errorMsg", "登陆失败");
            return "admin/login";
        }
    }


    /**
     * 修改密码
     *
     * @param request req
     * @return 跳转到修改密码页
     */
    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {
        // 获取登录用户id
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        // 通过id查找用户
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);

        if (adminUser == null) {
            return "admin/login";
        }
        // 将用户的登录名称和昵称放在req里，用于展示
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());
        return "admin/profile";
    }

    /**
     * 修改密码
     *
     * @param request          req
     * @param originalPassword 旧密码
     * @param newPassword      新密码
     * @return 是否成功
     */
    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword) {
        if (!StringUtils.hasText(originalPassword) || !StringUtils.hasText(newPassword)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)) {
            //修改成功后清空session中的数据，前端控制跳转至登录页
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return "success";
        } else {
            return "修改失败";
        }
    }

    /**
     * 修改用户昵称
     *
     * @param request       req
     * @param loginUserName 用户昵称
     * @param nickName      登录名称
     * @return 是否成功
     */
    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request,
                             @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName) {
        if (!StringUtils.hasText(loginUserName) || !StringUtils.hasText(nickName)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
            return "success";
        } else {
            return "修改失败";
        }
    }

    /**
     * 退出登录，清除Session中的信息
     *
     * @param request req
     * @return 跳转页面
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }
}
