package mvc.handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

public interface HandToolsService {
     Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method,
                          Map<String,Object> beans);
}
