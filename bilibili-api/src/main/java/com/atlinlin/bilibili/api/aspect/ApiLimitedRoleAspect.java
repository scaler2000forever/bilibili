package com.atlinlin.bilibili.api.aspect;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.annotation.ApiLimitedRole;
import com.atlinlin.bilibili.domain.auth.UserRole;
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
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    //切点（point cut）：通过正则或指示器的规则来适配连接点
    @Pointcut("@annotation(com.atlinlin.bilibili.domain.annotation.ApiLimitedRole)")
    public void check() {
    }

    /**
     * 方法之前
     * @param joinPoint      连接点（join point）：对应的被拦截的对象
     * @param apiLimitedRole 自定义的角色编码列表注解
     */
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {
        Long userId = userSupport.getCurrentUserId();
        //用户关联的角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //用户被限制的角色(通过调用我们自定义的接口)
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        //取出元素 转换为数组（set还具有去重的好处）
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        //用户角色关联列表抽出并转换
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodeSet.retainAll(limitedRoleCodeSet);
        //有交集
        if (roleCodeSet.size() > 0){
            throw new ConditionException("权限不足 ！");
        }
    }
}
