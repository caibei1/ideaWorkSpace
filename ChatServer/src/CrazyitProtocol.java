public interface CrazyitProtocol {
    int PROTOCOL_LEN = 2;
    //协议字符串，服务器端和客户端交换的信息都应该在前后添加这种特殊字符串
    String MSG_ROUND = "群聊";
    String USER_ROUND = "22";
    String LOGIN_SUCCESS = "成功";
    String NAME_REP = "重复";
    String PRIVATE_ROUND = "私聊";
    String SPLIT_SIGN = "55";
}
