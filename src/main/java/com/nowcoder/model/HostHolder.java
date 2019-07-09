package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * @author wangzhe
 * @date 2019/7/9 11:10
 * 使用依赖注入，用来存放查找出来的 user
 */
@Component
public class HostHolder {

    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }

}
