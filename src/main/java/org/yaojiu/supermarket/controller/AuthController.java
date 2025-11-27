package org.yaojiu.supermarket.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaojiu.supermarket.entity.Result;
import org.yaojiu.supermarket.entity.UserDTO;
import org.yaojiu.supermarket.entity.UserEntity;
import org.yaojiu.supermarket.service.AuthService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/auth")
public class UserController {

    @Resource
    private AuthService authService;

    @PostMapping(value = "/login")
    public Result login(@NotNull @Valid @RequestBody UserEntity userEntity, HttpSession session){
        UserDTO loginUser = authService.login(userEntity);
        session.setAttribute("user", loginUser);
        return Result.success().resetData(loginUser);
    }
    @PostMapping(value = "/reg")
    public Result reg(@NotNull @Valid @RequestBody UserEntity userEntity){
        if (authService.register(userEntity)) return Result.success().resetMsg("注册成功,即将跳转至登陆页面");
        return Result.fail().resetMsg("注册失败");
    }
    @PostMapping(value = "/logout")
    public Result logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return Result.success().resetMsg("注销成功");
    }
    @PostMapping(value = "/refresh")
    public Result refresh(HttpServletRequest request){

    }
}
