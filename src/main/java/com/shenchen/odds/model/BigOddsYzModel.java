package com.shenchen.odds.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class BigOddsYzModel implements Serializable {
    private static final long serialVersionUID = 1188436762022698740L;

    private String big_odds_yz_model_id;

    //联赛
    private String league_name_simply;

    //让盘情况 让盘 受让
    private String let_type;

    //盘口变化情况 升盘 降盘 不变
    private String let_change_type;

    //赔率变化类型 主队 客队
    private String bet_change_type;

    //变化开始范围
    private BigDecimal bet_change_start;

    //变化结束范围
    private BigDecimal bet_change_end;

    //主队赢盘数
    private Integer host_win;

    //客队赢盘数
    private Integer guest_win;

    //主队赢盘率
    private BigDecimal host_win_rate;

    //客队赢盘率
    private BigDecimal guest_win_rate;

    @Override
    public String toString() {
        return "BigOddsYzModel{" +
                "big_odds_yz_model_id='" + big_odds_yz_model_id + '\'' +
                ", league_name_simply='" + league_name_simply + '\'' +
                ", let_type='" + let_type + '\'' +
                ", let_change_type='" + let_change_type + '\'' +
                ", bet_change_type='" + bet_change_type + '\'' +
                ", bet_change_start=" + bet_change_start +
                ", bet_change_end=" + bet_change_end +
                ", host_win=" + host_win +
                ", guest_win=" + guest_win +
                ", host_win_rate=" + host_win_rate +
                ", guest_win_rate=" + guest_win_rate +
                '}';
    }
}