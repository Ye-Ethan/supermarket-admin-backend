package org.yaojiu.supermarket.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaojiu.supermarket.entity.Result;
import org.yaojiu.supermarket.entity.UserDTO;
import org.yaojiu.supermarket.entity.UserEntity;
import org.yaojiu.supermarket.service.AuthService;
import org.yaojiu.supermarket.service.UserService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Resource
    private UserService userService;
    @Resource
    private AuthService authService;

    @PostMapping(value = "/login")
    public Result login(@NotNull @Valid @RequestBody UserEntity userEntity){
        UserDTO loginUser = userService.login(userEntity);
        Map<String, String> login = authService.login(loginUser);
        return Result.success().resetData(login);
    }
    @PostMapping(value = "/reg")
    public Result reg(@NotNull @Valid @RequestBody UserEntity userEntity){
        if (userService.register(userEntity)) return Result.success().resetMsg("注册成功,即将跳转至登陆页面");
        return Result.fail().resetMsg("注册失败");
    }
    @PostMapping(value = "/logout")
    public Result logout(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        String token = authorization.substring(7);
        authService.logout(token);
        return Result.success().resetMsg("注销成功");
    }
    @PostMapping(value = "/refresh")
    public Result refresh(@NotNull @RequestBody @Valid Map<String, String> requestMap){
        String refreshToken = requestMap.get("refreshToken");
        Map<String, String> stringStringMap = authService.refreshToken(refreshToken);
        return Result.success().resetData(stringStringMap);
    }
}
