package mvc.resolver.impl;

import mvc.annotation.MyService;
import mvc.resolver.ArgumentResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
@MyService("httpServletReqeustArgumentResolver")
public class HttpServletReqeustArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type, int index, Method method) {
        return ServletRequest.class.isAssignableFrom(type);
    }

    @Override
    public Object argumentResolve(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index, Method method) {
        return request;
    }
}
