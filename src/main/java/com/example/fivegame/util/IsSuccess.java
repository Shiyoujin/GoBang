package com.example.fivegame.util;

import org.springframework.stereotype.Component;

/**
 * @author white matter
 */
@Component
public class IsSuccess {

    public int isSuccess(int x,int y,int color,int[][] oriData){
        //x的范围在0-14之间，y的范围在0-19
        //棋盘的行数
        int row = 15;
        //棋盘的列数
        int colunm = 20;
        //判断是否达到 5连条件
        int result = 3;

        //先横向判断是否有连续五枚相同颜色的棋子
        int count = 0;
        for (int j = y-1;j>-1;j--){
            if (oriData[x][j] !=color){
                break;
            }else {
                count++;
            }
        }

        for (int j = y+1;j<colunm;j++ ){
            if (oriData[x][j] !=color){
                break;
            }else {
                count++;
            }
        }

        if (count > result){
            return color;
        }

        //纵向
        count =0;
        for (int i = x-1;i>-1;i--){
            if (oriData[i][y] !=color){
                break;
            }else {
                count++;
            }
        }

        for (int i = x+1;i<row;i++){
            if (oriData[i][y] !=color){
                break;
            }else {
                count++;
            }
        }

        if (count > result){
            return color;
        }

        //左上右下
        count = 0;
        for (int i = x-1,j = y-1;i>-1;i--,j--){
            if (j>-1){
                if (oriData[i][j] != color){
                    break;
                }else {
                    count++;
                }
            }else {
                break;
            }
        }
        for (int i = x+1,j = y+1;i<row;i++,j++){
            if (j<colunm){
                if (oriData[i][j] != color){
                    break;
                }else {
                    count++;
                }
            }else {
                break;
            }
        }
        if (count > result){
            return color;
        }

        //右上左下
        count = 0;
        for (int i = x-1,j = y+1;i>-1;i--,j++){
            if (j<colunm){
                if (oriData[i][j] != color){
                    break;
                }else {
                    count++;
                }
            }else {
                break;
            }
        }

        for (int i = x+1,j=y-1;i<row;i++,j--){
            if (j>-1){
                if (oriData[i][j] !=color){
                    break;
                }else {
                    count++;
                }
            }else {
                break;
            }
        }

        if (count > result){
            return color;
        }

        //表示这一次下棋，没有分出胜负
        return -1;
    }

}
