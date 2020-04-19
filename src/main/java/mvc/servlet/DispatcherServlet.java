package mvc.servlet;

import mvc.annotation.MyController;
import mvc.annotation.MyQualifier;
import mvc.annotation.MyRequestMapping;
import mvc.annotation.MyService;
import mvc.controller.TestController;
import mvc.handle.HandToolsService;

import javax.servlet.DispatcherType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {
    List<String> classNames = new ArrayList<>();
    Map<String, Object> beans = new HashMap<>();
    Map<String, Object> handle = new HashMap<>();

    public DispatcherServlet() {

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.扫描哪些类需要被实例化
        doScanPackage("mvc");
        for (String cname : classNames)
            System.out.println(cname);
        //2.classNames已经包含所有类的路径
        doInstance();
        //3.依赖注入
        iocDi();
        //4.简历url和method映射关系
        handlerMapper();
        for(Map.Entry<String,Object> entry:handle.entrySet())
            System.out.println(entry.getKey()+":"+entry.getValue());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求路径
        String uri=req.getRequestURI();
        String context=req.getContextPath();
        String path=uri.replace(context,"");
        Method method=(Method)handle.get(path);
        TestController instance=(TestController) beans.get("/"+path.split("/")[1]);
        HandToolsService hand=(HandToolsService)beans.get("handToolsServiceImpl");
        Object[] args=hand.hand(req,resp,method,beans);
        try {
            method.invoke(instance, args);
        }catch (Exception e){

        }
    }

    //扫描包
    private void doScanPackage(String basePackage) {
        //扫描编译好的项目下的所有类
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        String fileStr = url.getFile();
        File file = new File(fileStr);
        String[] files = file.list();
        for (String path : files) {
            File filePath = new File(fileStr + path);
            if (filePath.isDirectory()) {
                doScanPackage(basePackage + "." + path);
            } else {
                classNames.add(basePackage + "." + filePath.getName());
            }
        }
    }

    private void doInstance() {
        if (classNames.size() <= 0) {
            System.out.println("doScanf filed");
            return;
        }
        //遍历所有路径，创建对象
        for (String className : classNames) {
            String cn = className.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(cn);
                if (clazz.isAnnotationPresent(MyController.class)) {
                    MyController controller = clazz.getAnnotation(MyController.class);
                    Object instance = clazz.newInstance();//拿到实例化bean
                    MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    String key = requestMapping.value();
                    beans.put(key, instance);
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService service = clazz.getAnnotation(MyService.class);
                    Object instance = clazz.newInstance();
                    beans.put(service.value(), instance);
                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void iocDi() {
        if (beans.isEmpty()) {
            System.out.println("doInstance fail");
            return;
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            //获取类中声明了哪些注解
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(MyController.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(MyQualifier.class)) {
                        MyQualifier qualifier = field.getAnnotation(MyQualifier.class);
                        String value = qualifier.value();
                        field.setAccessible(true);
                        try {
                            field.set(instance, beans.get(value));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                continue;
            }
        }
    }

    private void handlerMapper() {
        if (beans.isEmpty()) {
            System.out.println("doInstance fail");
            return;
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            //获取类中声明了哪些注解
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(MyController.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                String classPath = requestMapping.value();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(MyRequestMapping.class)) {
                        MyRequestMapping mapping = method.getAnnotation(MyRequestMapping.class);
                        String methodUrl = mapping.value();
                        handle.put(classPath+methodUrl,method);
                    } else {
                        continue;
                    }
                }
            }
        }
    }

}
