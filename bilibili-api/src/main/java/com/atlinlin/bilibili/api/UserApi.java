package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.User;
import com.atlinlin.bilibili.domain.UserInfo;
import com.atlinlin.bilibili.service.UserService;
import com.atlinlin.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ author : LiLin
 * @ create : 2022-10-15 12:34
 */
@RestController
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 获取用户信息
     *
     * @return token请求头中 统一方法获取userSupport
     */
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId(); //userId从token中取出为了安全就算就被拦截也有有效期
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    //获取RSA公钥
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String publicKeyStr = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(publicKeyStr);
    }

    //用户注册 post使用
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();

    }

    //用户登录获取token
    @PostMapping("/user-tokens") //登录成功会获得用户凭证,相当于请求这个资源
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }

    /**
     * 修改个人表
     *
     * @param user
     * @return
     * @throws Exception
     */
    @PutMapping("/users")
    public JsonResponse<String> updateUser(@RequestBody User user) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    /**
     * 修改个人详细表
     * @param userInfo
     * @return
     * @throws Exception
     */
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfo(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId); //这里id是用户的不是那个主键id
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }


}
