package mvc.controller;

import mvc.annotation.MyController;
import mvc.annotation.MyQualifier;
import mvc.annotation.MyRequestMapping;
import mvc.annotation.MyRequestParam;
import mvc.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MyController
@MyRequestMapping("/test")
public class TestController {
    @MyQualifier("userServiceImpl")
    private UserService userService;
    @MyRequestMapping("/query")
    public void test(HttpServletRequest request, HttpServletResponse response,
                     @MyRequestParam("name") String userName,@MyRequestParam("age") String userAge)throws Exception{
        String result=userService.query(userName,userAge);
        System.out.println(result);
        response.getWriter().write(result);
    }
}
