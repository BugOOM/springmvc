package mvc.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public interface ArgumentResolver {
    boolean support(Class<?> type, int index, Method method);
    Object argumentResolve(HttpServletRequest request, HttpServletResponse response,
                                  Class<?> type, int index, Method method);
}
