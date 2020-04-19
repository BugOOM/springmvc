package mvc.handle.impl;

import mvc.annotation.MyService;
import mvc.handle.HandToolsService;
import mvc.resolver.ArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
@MyService("handToolsServiceImpl")
public class HandToolsServiceImpl implements HandToolsService {
    @Override
    public Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method, Map<String, Object> beans) {
        Class<?>[] paramClazzs=method.getParameterTypes();
        Object[] args=new Object[paramClazzs.length];
        //拿到所有实现ArgumentResolver接口的实例
        Map<String,Object> argResolvers=getInstanceType(beans, ArgumentResolver.class);
        int index=0;
        int i=0;
        for(Class<?> paramClazz:paramClazzs){
            //哪个参数对应哪个解析类
            for(Map.Entry<String,Object> entry:argResolvers.entrySet()){
                ArgumentResolver ar= (ArgumentResolver) entry.getValue();
                if(ar.support(paramClazz,index,method)){
                    args[i++]=ar.argumentResolve(request,response,paramClazz,index,method);
                }
            }
            index++;
        }
        return args;
    }

    private Map<String, Object> getInstanceType(Map<String, Object> beans, Class<?> type) {
        Map<String, Object> resultBeans = new HashMap<>();
        for(Map.Entry<String,Object> entry:beans.entrySet()){
            Class<?>[] infs=entry.getValue().getClass().getInterfaces();
            if(infs!=null && infs.length>0){
                for(Class<?> inf:infs){
                    if(inf.isAssignableFrom(type)){
                        resultBeans.put(entry.getKey(),entry.getValue());
                    }
                }
            }
        }
        return resultBeans;
    }
}
