package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author wangzhe
 * @date 2019/7/9 9:15
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;


    @RequestMapping(path = {"/reglogin"}, method = RequestMethod.GET)
    public String reg(Model model, @RequestParam(value = "next", required = false) String next) {

        // 将 next 变量埋入登录页面中
        model.addAttribute("next", next);

        return "login";
    }

    @RequestMapping(path = {"/reg/"}, method = RequestMethod.POST)
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam("next") String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {

        try {
            Map<String, String> map = userService.register(username, password);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);

                //返回上次请求页面
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";

            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
    }


    @RequestMapping(path = {"/login/"}, method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {

        try {
            Map<String, String> map = userService.login(username, password);

            if (map.containsKey("ticket")) {
                //将 ticket 放入 cookie 中
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/"); // 设置为在同一服务器内共享

                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }

                response.addCookie(cookie);

                //返回上次请求的页面
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }

                return "redirect:/";

            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }


        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
