package com.qexz.controller;

import com.qexz.common.QexzConst;
import com.qexz.dto.AjaxResult;
import com.qexz.exception.QexzWebError;
import com.qexz.model.Account;
import com.qexz.service.AccountService;
import com.qexz.util.MD5;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/account")
public class AccountController {

    private static Log LOG = LogFactory.getLog(DefaultController.class);

    @Autowired
    private AccountService accountService;

    /**
     * 个人信息页面
     */
    @RequestMapping(value="/profile", method= RequestMethod.GET)
    public String profile(HttpServletRequest request, Model model) {
        Account currentAccount = (Account) request.getSession().getAttribute(QexzConst.CURRENT_ACCOUNT);
        //TODO::拦截器过滤处理
        if (currentAccount == null) {
            //用户未登录直接返回首页面
            return "redirect:/";
        }
        model.addAttribute(QexzConst.CURRENT_ACCOUNT, currentAccount);
        return "/user/profile";
    }

    /**
     * 更改密码页面
     */
    @RequestMapping(value="/password", method= RequestMethod.GET)
    public String password(HttpServletRequest request, Model model) {
        Account currentAccount = (Account) request.getSession().getAttribute(QexzConst.CURRENT_ACCOUNT);
        //TODO::拦截器过滤处理
        if (currentAccount == null) {
            //用户未登录直接返回首页面
            return "redirect:/";
        }
        model.addAttribute(QexzConst.CURRENT_ACCOUNT, currentAccount);
        return "/user/password";
    }

    /**
     * 考试记录页面
     */
    @RequestMapping(value="/myExam", method= RequestMethod.GET)
    public String myExam(HttpServletRequest request, Model model) {
        Account currentAccount = (Account) request.getSession().getAttribute(QexzConst.CURRENT_ACCOUNT);
        //TODO::拦截器过滤处理
        if (currentAccount == null) {
            //用户未登录直接返回首页面
            return "redirect:/";
        }
        model.addAttribute(QexzConst.CURRENT_ACCOUNT, currentAccount);
        return "/user/myExam";
    }

    /**
     * 我的发帖页面
     */
    @RequestMapping(value="/myDiscussPost", method= RequestMethod.GET)
    public String myDiscussPost(HttpServletRequest request, Model model) {
        Account currentAccount = (Account) request.getSession().getAttribute(QexzConst.CURRENT_ACCOUNT);
        //TODO::拦截器过滤处理
        if (currentAccount == null) {
            //用户未登录直接返回首页面
            return "redirect:/";
        }
        model.addAttribute(QexzConst.CURRENT_ACCOUNT, currentAccount);
        return "/user/myDiscussPost";
    }

    /**
     * 更新密码
     */
    @RequestMapping(value = "/api/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updatePassword(HttpServletRequest request, HttpServletResponse response) {
        AjaxResult ajaxResult = new AjaxResult();
        try {
            String password = request.getParameter("password");
            Account currentAccount = (Account) request.getSession().getAttribute(QexzConst.CURRENT_ACCOUNT);
            currentAccount.setPassword(password);
            boolean result = accountService.updateAccount(currentAccount);
            ajaxResult.setSuccess(result);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return AjaxResult.fixedError(QexzWebError.COMMON);
        }
        return ajaxResult;
    }

    /**
     * 更新个人信息
     */
    @RequestMapping(value = "/api/updateAccount", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updateAccount(HttpServletRequest request, HttpServletResponse response) {
        AjaxResult ajaxResult = new AjaxResult();
        try {
            String phone = request.getParameter("phone");
            String qq = request.getParameter("qq");
            String email = request.getParameter("email");
            String description = request.getParameter("description");

            Account currentAccount = (Account) request.getSession().getAttribute(QexzConst.CURRENT_ACCOUNT);
            currentAccount.setPhone(phone);
            currentAccount.setQq(qq);
            currentAccount.setEmail(email);
            currentAccount.setDescription(description);
            boolean result = accountService.updateAccount(currentAccount);
            ajaxResult.setSuccess(result);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return AjaxResult.fixedError(QexzWebError.COMMON);
        }
        return ajaxResult;
    }

    /**
     * 验证登录
     */
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult login(HttpServletRequest request, HttpServletResponse response) {
        AjaxResult ajaxResult = new AjaxResult();
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            Account current_account = accountService.getAccountByUsername(username);
            if(current_account != null) {
                String pwd = MD5.md5(password);
                if(pwd.equals(current_account.getPassword())) {
                    request.getSession().setAttribute(QexzConst.CURRENT_ACCOUNT,current_account);
                    ajaxResult.setData(current_account);
                } else {
                    return AjaxResult.fixedError(QexzWebError.WRONG_PASSWORD);
                }
            } else {
                return AjaxResult.fixedError(QexzWebError.WRONG_USERNAME);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return AjaxResult.fixedError(QexzWebError.COMMON);
        }
        return ajaxResult;
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout", method= RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        request.getSession().setAttribute(QexzConst.CURRENT_ACCOUNT,null);
        String url=request.getHeader("Referer");
        LOG.info("url = " + url);
        return "redirect:"+url;
    }


}
