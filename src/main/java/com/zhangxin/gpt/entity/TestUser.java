package com.zhangxin.gpt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * @author zhangxin
 * @date 2023-03-22 17:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestUser {

    private int level;
    private String name;

    public static void main(String[] args) throws IllegalAccessException {
        TestUser user1 = new TestUser(1,"wzx");
        TestUser user2 = new TestUser(2,"wjg");
        String oldContent = "旧内容：";
        String newContent = "新内容：";
        Field[] fields1 = user1.getClass().getDeclaredFields();
        Field[] fields2 = user2.getClass().getDeclaredFields();

        for (int i = 0; i <fields1.length ; i++) {
            fields1[i].setAccessible(true);
            fields2[i].setAccessible(true);
            if(!fields1[i].get(user1).equals(fields2[i].get(user2))){


                oldContent+=(fields1[i].getName()+":"+fields1[i].get(user1)+";");
                newContent+=(fields2[i].getName()+":"+fields2[i].get(user2)+";");
            }
        }

        System.out.println(oldContent);
        System.out.println(newContent);

    }


}


