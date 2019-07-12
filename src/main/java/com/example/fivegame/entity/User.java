package com.example.fivegame.entity;

import lombok.Data;

/**
 * @author white matter
 */
@Data
public class User {
    public String u_id;

    public String u_name;

    public String u_pass;

    public User(String u_id,String u_name,String u_pass){
        this.u_id = u_id;
        this.u_name = u_name;
        this.u_pass = u_pass;
    }
    public User(){

    }
}
