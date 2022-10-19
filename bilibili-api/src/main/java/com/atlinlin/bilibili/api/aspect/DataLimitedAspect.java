package com.atlinlin.bilibili.api.aspect;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.UserMoment;
import com.atlinlin.bilibili.domain.annotation.ApiLimitedRole;
import com.atlinlin.bilibili.domain.auth.UserRole;
import com.atlinlin.bilibili.domain.constant.AuthRoleConstant;
import com.atlinlin.bilibili.domain.exception.ConditionException;
import com.atlinlin.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 22:36
 */
@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    //切点（point cut）：通过正则或指示器的规则来适配连接点
    @Pointcut("@annotation(com.atlinlin.bilibili.domain.annotation.DataLimited)")
    public void check() {
    }

    /**
     * 方法之前
     * @param joinPoint      连接点（join point）：对应的被拦截的对象
     * 这里对参数进行判断，切点拿到相应的参数就可
     */
    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
        //用户关联的角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //用户角色关联列表抽出并转换
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //获取到当前切入方法里类的参数 这里可以使用joinPoint自带的方法
        Object[] args = joinPoint.getArgs();
        //逻辑判断,是否调用需要切入的方法
        for (Object arg : args) {
            if (arg instanceof UserMoment){
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
                //用户角色权限加传参判断 没传参就是不等于0默认
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV0) && !"0".equals(type)){
                    throw new ConditionException("参数异常！");
                }
            }
        }
    }
}
