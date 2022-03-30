package cn.mcfun.utils;

import cn.mcfun.request.GetRequest;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BattleStatus {

    public String getBattleStatus(long battleId, long userId) {
        String result = new GetRequest().sendGet("https://service-7d3sbma7-1307225969.bj.apigw.tencentcs.com/release/getBattleStatus?userid="+userId+"&battleid="+battleId);
        return result;
    }
}

