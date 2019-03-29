package com.shenchen.odds.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class BigOddsYz implements Serializable {
    private static final long serialVersionUID = 1188436762022698740L;

    private String big_odds_yz_id;

    //比赛id
    private String match_id;

    //联赛
    private String league_name_simply;

    //主队名称
    private String host_name;

    //客队名称
    private String guest_name;

    //比赛时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date match_time;

    //比赛时间
    private String match_time_str;



    //主队得分
    private Integer host_goal;

    //客队得分
    private Integer guest_goal;

    //比赛结果
    private Integer game_result;

    //公司名称
    private String company_name;

    //网站初始让球
    private BigDecimal first_let_ball;

    //主场初始赔率
    private BigDecimal first_host_bet;

    //客场初始赔率
    private BigDecimal first_guest_bet;

    //网站让球
    private BigDecimal let_ball;

    //主场赔率
    private BigDecimal host_bet;

    //客场赔率
    private BigDecimal guest_bet;

    private BigDecimal buy_host;

    private BigDecimal buy_guest;

    @Override
    public String toString() {
        return "BigOddsYz{" +
                "big_odds_yz_id='" + big_odds_yz_id + '\'' +
                ", match_id='" + match_id + '\'' +
                ", league_name_simply='" + league_name_simply + '\'' +
                ", host_name='" + host_name + '\'' +
                ", guest_name='" + guest_name + '\'' +
                ", match_time=" + match_time +
                ", host_goal=" + host_goal +
                ", guest_goal=" + guest_goal +
                ", game_result=" + game_result +
                ", company_name='" + company_name + '\'' +
                ", first_let_ball=" + first_let_ball +
                ", first_host_bet=" + first_host_bet +
                ", first_guest_bet=" + first_guest_bet +
                ", let_ball=" + let_ball +
                ", host_bet=" + host_bet +
                ", guest_bet=" + guest_bet +
                ", buy_host=" + buy_host +
                ", buy_guest=" + buy_guest +
                '}';
    }
}