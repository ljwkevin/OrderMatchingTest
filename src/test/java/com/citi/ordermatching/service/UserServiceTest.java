package com.citi.ordermatching.service;

import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.User;
import com.citi.ordermatching.util.TokenProccessor;
import com.sun.prism.impl.Disposer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    private User user = new User();

    private DealRecord dealRecord = new DealRecord();

    @Test
    public void findAllUsers() {

        List<User> all = userService.findAllUsers();
        for (int i = 0; i < all.size(); i++) {
            System.out.println("the users are " + all.get(i).getUsername());
        }
    }

    @Test
    public void userLogin() {
        List<User> all = userService.findAllUsers();
        ArrayList<String> strName = new ArrayList<>();
        ArrayList<String> strPassword = new ArrayList<>();
        user.setUsername("admin");
        user.setPassword("admin");
        for (int i = 0; i < all.size(); i++) {
            //  System.out.println("the users are " + all.get(i).getUsername());
            strName.add(all.get(i).getUsername());
            strPassword.add(all.get(i).getPassword());
            if (strName.get(i).equals(user.getUsername()) & strName.get(i).equals(user.getPassword())) {
                User userLogin = userService.userLogin(user);
                System.out.println("welcome!Your accont is " + userLogin.getUsername());

            } else {
                System.out.println("sorry the account is not exist");
            }
        }
    }



    @Test
    public void isExist() {
        String username = "trader2";
        List<User> allusers=userService.findAllUsers();
        ArrayList<String> existName = new ArrayList<>();
        ArrayList<Integer> existID = new ArrayList<>();
        for(int i=0;i<allusers.size();i++) {
            existName.add(allusers.get(i).getUsername());
            existID.add(Integer.valueOf(allusers.get(i).getId()));
              if (existName.get(i).equals(username)){
                User ExistUser = userService.isExist(username);
//                assertTrue(ExistUser.getUsername(), true);
                System.out.println("the user is exist! And the ID is "+existID.get(i));
            } else{
                System.out.println("the user is not exist!");
            }
        }
    }
    @Test
    public void registerUser() {

        user.setUsername("trader21");
        user.setPassword("1212121");
        user.setMoney(Double.valueOf("300"));

          userService.registerUser(user);

        System.out.println("money is " + user.getMoney());
        System.out.println("userId is " + user.getUsername());
        System.out.println("password is " + user.getPassword());
    }

    @Test
    public void updateUser() {
        user.setUsername("testUser3");
        userService.updateUser(user);
        //  System.out.println(userService.updateUser(user));
    }
      /*
    ???
     */
    @Test
    public void getUserByToken() {
        String token = TokenProccessor.getInstance().makeToken();
        User userTaken = userService.getUserByToken(token);
        Map map = new HashMap();
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("user", userTaken);
        map.put("role", "admin");

    }
}