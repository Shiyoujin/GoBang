一、这次考核写的websocket使用前端页面来展示的。
       http://94.191.3.170:8081/login
       此接口就是前端页面，使用消息的方式来实现下棋（图形展示没有实现成功）

二、这次考核实现双人五子棋游戏全部是使用的 集合 来处理逻辑，因此没有涉及到  数据库的操作
       全部用集合这次确实不妥，除了逻辑容易混乱，bug也不好改，但是方便测试。
       所以没有上传 sql文件。
       所以基础功能的bug改了许多，也算是比较完善。

三、代码运行环境：springboot 

四、加分项
       游戏内和房间内的聊天
       思路：聊天的内容（content）通过前端组装成 Rusult这个类的json发送给后台，后台解析转化成Rusult这个
       Bean，判断Rusult的 content是否为空，不为空才给房间内的人发送聊天消息。
       
五、使用说明
     必须将 三个属性填完才能 连接websocket并进入房间。
     默认第一个进入房间的为黑棋手，且每次黑棋手先下棋。
     房间满了或者u_id重复都不可以进入房间。
     下棋中  格式必须为   x-x  例如： 1-5  或者 5-15
     超出棋盘范围不能下棋
     未到用户下棋不可以下
     被落子的地方也不可以下棋
     下棋的规范即 不按 x-x下棋也不可以
     对方玩家没有下棋  你也不可以进行下棋
     
            
