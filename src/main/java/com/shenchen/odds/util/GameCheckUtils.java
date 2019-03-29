package com.shenchen.odds.util;

import java.io.Serializable;
import java.util.Arrays;

public class GameCheckUtils implements Serializable {

    public static boolean gameCheck(String league_name_simply){
        String[] strings = {
                "欧冠","欧罗巴杯",
                "英超","英冠","英甲","英乙",
                "意甲","意乙","意丙1A",
                "西甲","西乙",
                "德甲", "德乙",
                "法甲", "法乙",
                "葡超","葡甲",
                "苏超","苏冠","苏甲",
                "荷甲", "荷乙",
                "瑞典超","瑞典甲",
                "日职", "日乙",
                "阿甲","阿乙",
                "美职",
                "中超",
                "挪超",
                "阿甲",
                "韩K联",
                "澳超",
                "俄超",
                "法国杯"
        };
        return Arrays.asList(strings).contains(league_name_simply);
    }


}
