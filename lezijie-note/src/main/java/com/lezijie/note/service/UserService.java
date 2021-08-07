package com.lezijie.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.lezijie.note.dao.UserDao;
import com.lezijie.note.po.User;
import com.lezijie.note.vo.ResultInfo;

public class UserService {

    private UserDao userDao = new UserDao();

    /**
     * 用户登录
         1. 判断参数是否为空
             如果为空
                 设置ResultInfo对象的状态码和提示信息
                 返回resultInfo对象
         2. 如果不为空，通过用户名查询用户对象
         3. 判断用户对象是否为空
             如果为空
                 设置ResultInfo对象的状态码和提示信息
                 返回resultInfo对象
         4. 如果用户对象不为空，将数据库中查询到的用户对象的密码与前台传递的密码作比较 （将密码加密后再比较）
             如果密码不正确
                 设置ResultInfo对象的状态码和提示信息
                 返回resultInfo对象
         5. 如果密码正确
            设置ResultInfo对象的状态码和提示信息
         6. 返回resultInfo对象
     * @param userName
     * @param userPwd
     * @return
     */
    public ResultInfo<User> userLogin(String userName, String userPwd) {
        ResultInfo<User> resultInfo = new ResultInfo<>();

        // 数据回显：当登录实现时，将登录信息返回给页面显示
        User u = new User();
        u.setUname(userName);
        u.setUpwd(userPwd);
        // 设置到resultInfo对象中
        resultInfo.setResult(u);

        //  1. 判断参数是否为空
        if (StrUtil.isBlank(userName) || StrUtil.isBlank(userPwd)) {
            // 如果为空 设置ResultInfo对象的状态码和提示信息
            resultInfo.setCode(0);
            resultInfo.setMsg("用户姓名或密码不能为空！");
            // 返回resultInfo对象
            return resultInfo;
        }

        // 2. 如果不为空，通过用户名查询用户对象
        User user = userDao.queryUserByName(userName);

        // 3. 判断用户对象是否为空
        if (user == null) {
            // 如果为空,设置ResultInfo对象的状态码和提示信息
            resultInfo.setCode(0);
            resultInfo.setMsg("该用户不存在！");
            // 返回resultInfo对象
            return resultInfo;
        }

        //  4. 如果用户对象不为空，将数据库中查询到的用户对象的密码与前台传递的密码作比较 （将密码加密后再比较）
        // 将前台传递的密码按照MD5算法的方式加密
        userPwd = DigestUtil.md5Hex(userPwd);
        // 判断加密后的密码是否与数据库中的一致
        if (!userPwd.equals(user.getUpwd())) {
            // 如果密码不正确
            resultInfo.setCode(0);
            resultInfo.setMsg("用户密码不正确！");
            return resultInfo;
        }

        resultInfo.setCode(1);
        resultInfo.setResult(user);
        return resultInfo;
    }


}
