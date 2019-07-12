package com.example.fivegame.server;

import com.example.fivegame.config.GetHttpSessionConfigurator;
import com.example.fivegame.entity.Result;
import com.example.fivegame.util.IsSuccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author white matter
 */
@ServerEndpoint(value = "/Five/{u_name}/{u_id}/{roomNumber}", configurator = GetHttpSessionConfigurator.class)
@Component
public class FiveGameServer {

    //concurrent包的线程安全Set，
    // 用来存放每个客户端对应的FiveGameServer对象。
//    private static final Map<String,FiveGameServer> FIVE_GAME_SERVER_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Integer, CopyOnWriteArrayList<FiveGameServer>> TwoServerMap = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<FiveGameServer> listTwo = new CopyOnWriteArrayList<>();

    //房间号和u_id 唯一标识
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>> RoomMap = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<String> listR = new CopyOnWriteArrayList<>();

    //房间号和u_name
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>> RoomMapA = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<String> listRA = new CopyOnWriteArrayList<>();

    //房间号和棋盘的二维数组
    private static Map<Integer, int[][]> chessboardMap = new HashMap<>();

    //u_id玩家的准备状态
    private static ConcurrentHashMap<String, String> statusMap = new ConcurrentHashMap<>();

    //房间号和 初始棋子出棋
    private static HashMap<Integer, Integer> colorMap = new HashMap<>();

    //房间号和 begin 是否开局的记录
    private static Map<Integer, Integer> beginMap = new HashMap<>();

    //u_id和棋子颜色绑定 0 白色 1 黑色
    private static ConcurrentHashMap<String, Integer> u_idColorMap = new ConcurrentHashMap<>();

    private Session session;

    public HttpSession httpSession;

    private String u_id;

    private String u_name;

    private int roomNuber;

    @OnOpen
    public void onopen(Session session, @PathParam("roomNumber") int roomNumber, @PathParam("u_id") String u_id, @PathParam("u_name") String u_name, EndpointConfig config) {
        //TODO
        //一个页面一个session？
        //获取HttpSession
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
        httpSession.setAttribute("u_id", u_id);
        httpSession.setAttribute("u_name", u_name);
        this.u_id = (String) httpSession.getAttribute("u_id");
        this.u_name = (String) httpSession.getAttribute("u_name");
        this.roomNuber = roomNumber;
        //这里应该用数据库来操作账户的重复
        listR.add(u_id);

        System.out.println("listR"+" "+listR.size());
        //只有一种情况重复
        if (u_id.equals(listR.get(0)) && listR.size() ==2) {
            session.getAsyncRemote().sendText("用户ID已经重复");
            listR.remove(listR.size() - 1);
        }else if (listR.size()>2){
            session.getAsyncRemote().sendText("请不要重复进入房间");
            listR.remove(listR.size() - 1);
        } else {
            //这里可以通过数据库获取昵称
            this.session = session;
            //初始化棋盘
            int[][] oriData = new int[15][20];
            //初始化第一步下棋棋子的颜色
            int count = 0;

            if (u_idColorMap.size() == 0) {
                //默认第一个加入的为 黑棋手
                u_idColorMap.put(u_id, 1);
            } else {
                //白棋手
                u_idColorMap.put(u_id, 0);
            }

            listRA.add(u_name);
            listTwo.add(this);
            RoomMap.put(roomNumber, listR);
            TwoServerMap.put(roomNumber, listTwo);
            RoomMapA.put(roomNumber, listRA);
            int roomSize = RoomMap.get(roomNumber).size();

            listTwo = TwoServerMap.get(roomNumber);

            if (roomSize == 3 && listTwo.size() == 3) {
                listR.remove(2);
                listRA.remove(2);
                listTwo.remove(2);
                RoomMap.put(roomNumber, listR);
                TwoServerMap.put(roomNumber, listTwo);
                RoomMapA.put(roomNumber, listRA);
                System.out.println("房间人数已满，请选择其他房间");
                session.getAsyncRemote().sendText("房间人数已满，请选择其他房间");
            } else if (roomSize == 2 && listTwo.size() == 2) { //指定房间号加入 这里还有 bug用数据库或许会更好

                //进入房间后，默认为未准备状态
                statusMap.put(u_id, "1");

                String userA = listRA.get(listRA.size()-2);
                String userB = listRA.get(listRA.size()-1);

                colorMap.put(roomNumber, count);

                //之前已经有玩家加入房间了

                sendText(u_name + "已进入房间，请双方准备即开始比赛!" + "当前房间有:" + userA+";"+userB, listTwo);

                //双方进入后，记录为 未开局状态
                beginMap.put(roomNumber,0);

                //初始化棋盘并与 房间号roomNumber一起存进Map中
                oriData = beginchessboard(oriData);

                chessboardMap.put(roomNumber, oriData);

            } else if (roomSize == 1 && listTwo.size() == 1) {
                colorMap.put(roomNumber, count);

                sendText("你已创建房间此房间号为" + roomNumber + " ---等待其他玩家进入房间", listTwo);

                //进入房间后，默认为准备状态
                statusMap.put(u_id, "1");

                //同上
                oriData = beginchessboard(oriData);

                chessboardMap.put(roomNumber, oriData);
            }
        }
        System.out.println("RoomMap"+RoomMap.size());
        System.out.println("RoomMapA"+RoomMapA.size());
        System.out.println("listTwo"+listTwo.size());
    }

    /**
     * 默认黑棋先走
     *
     * @param messageAll
     */
    @OnMessage
    public void action(String messageAll, Session session) throws IOException {
        //TODO
        //从客户端传过来的数据是json数据，所以这里使用jackson进行转换为SocketMsg对象，
        ObjectMapper objectMapper = new ObjectMapper();
        //用来接受客户端传过来的所有信息

        Result result;
        result = objectMapper.readValue(messageAll, Result.class);

        //记录准备状态
        if (result.getStatus() != null && result.getStatus() != "") {
            statusMap.put(u_id, result.getStatus());
        }

        if (listTwo.size() < 2) {
            session.getAsyncRemote().sendText("请等待其他玩家进入房间");
        } else if ((statusMap.size() < 2 || ("1".equals(statusMap.get(listR.get(0))) || "1".equals(statusMap.get(listR.get(1)))))&&beginMap.get(roomNuber)==0) {
            //当前未开局
            sendText("双方玩家请做好准备", listTwo);
        }

        IsSuccess isSuccess = new IsSuccess();

        int[][] oriData = new int[15][20];

        String[] split = new String[2];

        //聊天内容若不为空则发送聊天信息
        if (result.getContent() != null && result.getContent() != "") {
            sendText(u_name + "说：" + result.getContent() + "--", listTwo);
        }

        System.out.println("listTwo"+ "  "+listTwo.size());
        System.out.println("statusMap："+"   "+ statusMap.size()+"statusMap1号："+ statusMap.get(listR.get(0))+"  "+ "statusMap2号:" + statusMap.get(listR.get(1)));
        //这里是 status 判断双方是否都准备，开始比赛。 "1"未准备 "2"已准备
        if (listTwo.size() == 2 && beginMap.get(roomNuber) == 0 && statusMap.size() >= 2 && (result.getXy() == null || result.getXy() == "")) {
            if ("2".equals(statusMap.get(listR.get(0))) && "2".equals(statusMap.get(listR.get(1)))) {
                System.out.println("双方都已经准备，游戏开始！");
                for (FiveGameServer fiveGameServer : listTwo) {
                    fiveGameServer.session.getAsyncRemote().sendText("双方都已经准备，开始比赛!---先黑棋执手");
                }
                //改变成开局状态
                beginMap.put(roomNuber, 1);
            } else {
                for (FiveGameServer fiveGameServer : listTwo) {
                    fiveGameServer.session.getAsyncRemote().sendText("另一处-----等待玩家做好准备");
                }
            }
        } else if (listTwo.size() == 2 && beginMap.get(roomNuber) == 0 && statusMap.size() == 2 && (result.getXy() == null || result.getXy() == "")) {
            sendText("提醒玩家：" + u_name + "  " + "未开局前，不可以下棋!", listTwo);
        } else if (listTwo.size() == 2 && beginMap.get(roomNuber) == 1 && statusMap.size() == 2) {
            if (result.getXy() != null && result.getXy() != "") {
                split = result.getXy().split("-");
                if (split.length == 2) {

                    int color = colorMap.get(roomNuber);
                    //或者 int i = ++color % 2
                    color++;
                    int i = color % 2;
                    colorMap.put(roomNuber,i);

                    int x = Integer.parseInt(split[0]);
                    int y = Integer.parseInt(split[1]);
                    //获取房间号内棋盘布局信息
                    oriData = chessboardMap.get(roomNuber);

                    //记录是否满足胜利条件
                    int success;

                    //进行下棋的动作
                    if (i == u_idColorMap.get(u_id)) {
                        if (x >= 0 && x <= 14 && y >= 0 && y <= 19) {
                            if (oriData[x][y] == -1) {
                                if (i == 1) {
                                    sendText("黑手" + "  " + u_name + "----" + "下棋坐标为" + " " + result.getXy(), listTwo);
                                    oriData[x][y] = i;
                                    success = isSuccess.isSuccess(x, y, color, oriData);
                                    //记录此次下棋后的棋盘
                                    chessboardMap.put(roomNuber, oriData);
                                } else {
                                    sendText("白手" + "  " + u_name + "----" + "下棋坐标为" + " " + result.getXy(), listTwo);
                                    oriData[x][y] = i;
                                    success = isSuccess.isSuccess(x, y, color, oriData);
                                    //记录此次下棋后的棋盘
                                    chessboardMap.put(roomNuber, oriData);
                                }

                                if (success == 1) {
                                    sendText("黑手" + "：" + u_name + "  获胜", listTwo);
                                    //获胜后 初始化棋盘
                                    oriData = beginchessboard(oriData);
                                    chessboardMap.put(roomNuber, oriData);
                                    //初始化 双方准备状态
                                    statusMap.replace(listR.get(0), "1");
                                    statusMap.replace(listR.get(1), "1");
                                    colorMap.put(roomNuber, 0);
                                    //初始化beginMap
                                    beginMap.put(roomNuber, 0);
                                } else if (success == 0) {
                                    sendText("白手" + "：" + u_name + "  获胜", listTwo);
                                    //获胜后 初始化棋盘
                                    oriData = beginchessboard(oriData);
                                    chessboardMap.put(roomNuber, oriData);
                                    //初始化 双方准备状态
                                    statusMap.replace(listR.get(0), "1");
                                    statusMap.replace(listR.get(1), "1");
                                    colorMap.put(roomNuber, 0);
                                    //初始化beginMap
                                    beginMap.put(roomNuber, 0);
                                }
                            } else {
                                synchronized (session) {
                                    session.getAsyncRemote().sendText("这里已经被落子了，请重新选择下棋!");
                                }
                                //还原多余的 下棋操作改变的 记录
                                colorMap.put(roomNuber, (u_idColorMap.get(u_id) + 1) % 2);
                            }
                        } else {
                            session.getAsyncRemote().sendText("棋子坐标超出棋盘范围，请重新选择下棋");
                            //还原多余的 下棋操作改变的 记录
                            colorMap.put(roomNuber, u_idColorMap.get(u_id)+1);
                        }
                    } else {
                        session.getAsyncRemote().sendText("请等待另一位玩家落子后再下棋");
                        //还原多余的 下棋操作改变的 记录
                        colorMap.put(roomNuber, u_idColorMap.get(u_id));
                    }
                } else {
                    //本来前端传值过来肯定是 规范的
                    session.getAsyncRemote().sendText("你输入的棋盘坐标不规范！");
                }
            }
        }
    }

    /**
     * @param msg
     * @param listTow
     * @Description 对指定的房间内所有人发送消息
     */
    private  void sendText(String msg, List<FiveGameServer> listTow) {
        for (FiveGameServer fiveGameServer : listTow) {
            //注意这里 锁session
            synchronized (fiveGameServer.session) {
                fiveGameServer.session.getAsyncRemote().sendText(msg);
            }
        }
        return;
//            catch (Exception e) {
//                listTow.remove(fiveGameServer);
//                try {
//                    fiveGameServer.session.close();
//                } catch (IOException e1) {
//                }
//                sendText(fiveGameServer.u_name + "童鞋已下线", listTow);
//            }
//    }
    }

    /**
     * @param oriData
     * @return int[][]
     * @Decription 初始化棋盘
     */
    private static int[][] beginchessboard(int[][] oriData) {
        for (int i = 0; i < oriData.length; i++) {
            for (int j = 0; j < oriData.length; j++) {
                //xy坐标位置设为默认 -1，0为下了白棋，1为下了黑棋
                oriData[i][j] = -1;
            }
        }
        return oriData;
    }


    /**
     * @param session
     * @Description 关闭连接，相应的删掉 server及其他信息等。
     */
    @OnClose
    public void onClose(Session session) {

        for (FiveGameServer fiveGameServer : listTwo){
            if (fiveGameServer == this){
                listTwo.remove(this);
            }
        }

        //删掉 TwoServerMap对应的房间号里面的 server
//      private static final ConcurrentHashMap<Integer,CopyOnWriteArrayList<FiveGameServer>> TwoServerMap = new ConcurrentHashMap<>();

        CopyOnWriteArrayList<FiveGameServer> newTwoList = new CopyOnWriteArrayList();
        newTwoList = TwoServerMap.get(roomNuber);
        for (FiveGameServer fiveGameServer : newTwoList){
            if (fiveGameServer == this){
                newTwoList.remove(this);
            }
        }
        TwoServerMap.put(roomNuber,newTwoList);


        for (int i = 0;i<listRA.size();i++){
            if (u_name.equals(listRA.get(i))){
                listRA.remove(i);
            }
        }

        for (int i = 0;i<listR.size();i++){
            if (u_id.equals(listR.get(i))){
                listR.remove(i);
            }
        }

        //删掉 RoomMap对应房间号的 u_id
        CopyOnWriteArrayList<String> newListR = new CopyOnWriteArrayList();
        newListR = RoomMap.get(roomNuber);
        for (String s : newListR){
            if (s.equals(this)){
                newListR.remove(this);
            }
        }

        RoomMap.put(roomNuber,newListR);

        //删掉 RoomMapA 对应房间号的 u_name
        CopyOnWriteArrayList<String> newListRA = new CopyOnWriteArrayList();
        newListRA = RoomMapA.get(roomNuber);
        for (String s : newListRA){
            if (s.equals(this)){
                newListRA.remove(this);
            }
        }

        RoomMapA.put(roomNuber,newListRA);

        //关闭连接移除准备状态
        statusMap.remove(u_id);

        //关闭连接移除 棋手颜色状态
        u_idColorMap.remove(u_id);
    }

    /**
     * * 发生错误时调用
     * *   
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

}


