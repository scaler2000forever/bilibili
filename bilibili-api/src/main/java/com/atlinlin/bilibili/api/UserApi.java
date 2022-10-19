package com.atlinlin.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.PageResult;
import com.atlinlin.bilibili.domain.User;
import com.atlinlin.bilibili.domain.UserInfo;
import com.atlinlin.bilibili.service.UserFollowingService;
import com.atlinlin.bilibili.service.UserService;
import com.atlinlin.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserFollowingService userFollowingService;

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
     * 双token登录
     * @param user
     * @return
     */
    @PostMapping("/user-dts")
    public JsonResponse<Map<String,Object>> loginForDts(@RequestBody User user) throws Exception {
       Map<String,Object> map = userService.loginForDts(user);
        return new JsonResponse<>(map);
    }

    /**
     * 退出登录，删除 refreshToken
     * @param request
     * @return
     */
    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String> logout(HttpServletRequest request){
        String refreshToken = request.getHeader("refreshToken");
        Long userId = userSupport.getCurrentUserId();
        userService.logout(refreshToken,userId);
        return JsonResponse.success();
    }


    /**
     * 更新用户
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
     * 修改个人基本信息
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

    /**
     * 分页查询
     * @param no 必传参数
     * @param size 必传参数
     * @param nick  可选参数
     * @return
     */
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no, @RequestParam Integer size ,String nick){
        Long userId = userSupport.getCurrentUserId();
        //内含map数组
        JSONObject params = new JSONObject();
        params.put("no",no);
        params.put("size",size);
        params.put("nick",nick);
        params.put("userId",userId);
        PageResult<UserInfo> result = userService.pageListUserInfos(params);
        //判断用户关注状态
        if(result.getTotal()>0){
            List<UserInfo> checkUserInfoList =  userFollowingService.checkFollowingStatus(result.getList(),userId);
            result.setList(checkUserInfoList);
        }
        return new JsonResponse<>(result);
    }

    /**
     * 刷新accessToken
     * @param request
     * @return
     */
    @PostMapping("/access-tokens")
    public JsonResponse<String> refreshAccessToken(HttpServletRequest request) throws Exception{
        String refreshToken = request.getHeader("refreshToken");
        String accessToken = userService.refreshAccessToken(refreshToken);
        return new JsonResponse<>(accessToken);
    }


}
