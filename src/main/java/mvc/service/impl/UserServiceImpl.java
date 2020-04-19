package mvc.service.impl;

import mvc.annotation.MyService;
import mvc.service.UserService;
@MyService("userServiceImpl")
public class UserServiceImpl implements UserService {
    @Override
    public String query(String name, String age) {
        return "name:"+name+"----"+"age:"+age;
    }
}
