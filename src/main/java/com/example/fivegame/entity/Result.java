package com.example.fivegame.entity;

import lombok.Data;
import org.springframework.context.annotation.Bean;

/**
 * @author white matter
 */
@Data
public class Result {

    /**
     *  棋子坐标
     */
    public String xy;

    /**
     * 发送消息
     */
    public String content;

    /**
     * 是否允许落子
     */
    public boolean about;
    /**
     * 玩家准备状态
     */
    public String  status;

    /**
     * 落子颜色
     */
    public String color;

    /**
     * 最终获胜者
     */
    public String winUser;


}
