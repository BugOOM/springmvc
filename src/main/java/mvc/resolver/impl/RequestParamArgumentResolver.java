package mvc.resolver.impl;

import mvc.annotation.MyRequestParam;
import mvc.annotation.MyService;
import mvc.resolver.ArgumentResolver;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@MyService("requestParamArgumentResolver")
public class RequestParamArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type, int index, Method method) {
        Annotation[][] anno = method.getParameterAnnotations();
        Annotation[] paramAnnos=anno[index];
        for(Annotation an:paramAnnos){
            if(MyRequestParam.class.isAssignableFrom(an.getClass()))
                return true;
        }
        return false;
    }

    @Override
    public Object argumentResolve(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index, Method method) {
        Annotation[][] anno = method.getParameterAnnotations();
        Annotation[] paramAnnos=anno[index];
        for(Annotation an:paramAnnos){
            if(MyRequestParam.class.isAssignableFrom(an.getClass())){
                MyRequestParam er=(MyRequestParam)an;
                String value=er.value();
                return request.getParameter(value);
            }
        }
        return null;
    }
}
