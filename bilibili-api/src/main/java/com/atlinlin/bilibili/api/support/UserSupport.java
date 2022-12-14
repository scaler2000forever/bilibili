package com.atlinlin.bilibili.api.support;

import com.atlinlin.bilibili.domain.exception.ConditionException;
import com.atlinlin.bilibili.service.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {
    //获取请求头中信息

    /**
     * 对api运行模块的支持，验证token
     * @return
     */
    public Long getCurrentUserId(){
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = requestAttributes.getRequest().getHeader("accessToken");
        Long userId = TokenUtil.verifyToken(token);
        if(userId < 0){
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}
