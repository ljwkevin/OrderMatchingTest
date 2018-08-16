package com.citi.ordermatching.controller;

import com.alibaba.fastjson.JSON;
import com.citi.ordermatching.domain.User;
import com.citi.ordermatching.service.UserService;
import com.citi.ordermatching.util.TokenProccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dell on 2018/8/12.
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @RequestMapping("/getUsers")
    @ResponseBody
    public List<User> findAllUsers(){
        return userService.findAllUsers();
    }

    @RequestMapping("/login")
    @ResponseBody
    public String userLogin(@RequestParam("username")String username,@RequestParam("password")String password){


        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        User result=userService.userLogin(user);
        if(result!=null){

            Map map=new HashMap();
            String token = TokenProccessor.getInstance().makeToken();
            result.setToken(token);
            userService.updateUser(result);

            map.put("token",token);
            map.put("result",result);
            String jsonResult= JSON.toJSONString(map);
            return jsonResult;
        }else{
            Map map=new HashMap();
            map.put("role",null);
            String jsonResult=JSON.toJSONString(map);
            return jsonResult;
        }

    }

    @RequestMapping("info")
    @ResponseBody
    public String info(@RequestParam("token")String token){

        User user=userService.getUserByToken(token);

        Map map=new HashMap();
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("user",user);
        map.put("role","admin");

        String jsonResult=JSON.toJSONString(map);
        return jsonResult;
    }

    @RequestMapping("/register")
    @ResponseBody
    public String userRegister(@RequestParam("username")String username,@RequestParam("password")String password){

        User user=userService.isExist(username);
        if(user!=null){
            return "";
        }else{
            User user1=new User();
            user1.setUsername(username);
            user1.setPassword(password);
            user1.setMoney(500000.00);
            userService.registerUser(user1);
            return "";
        }
    }

    @RequestMapping("/logout")
    @ResponseBody
    public String logOut(@RequestParam("username")String username){

        User user=userService.isExist(username);
        user.setToken("");
        userService.updateUser(user);
        Map map=new HashMap();
        map.put("code",200);

        String json=JSON.toJSONString(map);
        return json;
    }
}