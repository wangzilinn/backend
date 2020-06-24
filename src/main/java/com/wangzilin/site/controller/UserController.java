package com.wangzilin.site.controller;

import com.wangzilin.site.annotation.WebLog;
import com.wangzilin.site.model.DTO.Response;
import com.wangzilin.site.model.user.User;
import com.wangzilin.site.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/user")
@Tag(name = "UserController", description = "鉴权接口")
@Slf4j
public class UserController {


    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * @return com.wangzilin.site.model.DTO.Response
     * @Author wangzilin
     * @Description 用户登录
     * @Date 11:39 PM 5/10/2020
     * @Param [username, password]
     **/
    @PostMapping("/signIn")
    @WebLog
    public Response<?> SignIn(@RequestParam(value = "username") String username,
                              @RequestParam(value = "password") String password) throws AuthenticationException {

        final User user = userService.auth(username, password);
        if (user == null) {
            return new Response<>(422, "找不到用户");
        }
        return new Response<>(user);
    }

    @PostMapping("/info")
    @WebLog
    public Response<?> getInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return new Response<>(user);
    }

    /**
     * @return com.wangzilin.site.model.DTO.Response
     * @Author wangzilin
     * @Description 用户注册
     * @Date 11:47 PM 5/10/2020
     * @Param [username, password]
     **/
    @PostMapping("/signUp")
    public Response<?> SignUp(@RequestParam(value = "username") String username,
                              @RequestParam(value = "password") String password) throws AuthenticationException {
        userService.add(new User(username, password));
        return new Response<>();
    }
}
