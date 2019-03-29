package com.shenchen.odds.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BigOddsKelly implements Serializable {
    private static final long serialVersionUID = 1188436762022698740L;


    private String bigodds_id;

    //比赛id
    private String match_id;

    //联赛
    private String league_name_simply;

    //主队名称
    private String host_name;

    //客队名称
    private String guest_name;

    //比赛时间
    private Date match_time;

    //主队得分
    private Integer host_goal;

    //客队得分
    private Integer guest_goal;

    //比赛结果
    private Integer game_result;

    //公司名称
    private String company_name;

    //初始主胜赔率
    private BigDecimal first_win;

    //初始平局赔率
    private BigDecimal first_same;

    //初始客胜赔率
    private BigDecimal first_lost;

    //即时主胜赔率
    private BigDecimal win;

    //即时平局赔率
    private BigDecimal same;

    //即时客胜赔率
    private BigDecimal lost;


    //即时主胜投注比例
    private BigDecimal win_bet_rate;

    //即时平局投注比例
    private BigDecimal same_bet_rate;

    //即时客胜投注比例
    private BigDecimal lost_bet_rate;

    //即时主胜凯利指数
    private BigDecimal win_kelly;

    //即时平局投凯利指数
    private BigDecimal same_kelly;

    //即时客胜凯利指数
    private BigDecimal lost_kelly;

    //返还率
    private BigDecimal pay_back_percent;

    private BigDecimal buy_win_money;

    private BigDecimal buy_same_money;

    private BigDecimal buy_lost_money;

    private BigDecimal buy_result;


    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getLeague_name_simply() {
        return league_name_simply;
    }

    public void setLeague_name_simply(String league_name_simply) {
        this.league_name_simply = league_name_simply;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getGuest_name() {
        return guest_name;
    }

    public void setGuest_name(String guest_name) {
        this.guest_name = guest_name;
    }

    public Date getMatch_time() {
        return match_time;
    }

    public void setMatch_time(Date match_time) {
        this.match_time = match_time;
    }

    public Integer getHost_goal() {
        return host_goal;
    }

    public void setHost_goal(Integer host_goal) {
        this.host_goal = host_goal;
    }

    public Integer getGuest_goal() {
        return guest_goal;
    }

    public void setGuest_goal(Integer guest_goal) {
        this.guest_goal = guest_goal;
    }

    public Integer getGame_result() {
        return game_result;
    }

    public void setGame_result(Integer game_result) {
        this.game_result = game_result;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public BigDecimal getFirst_win() {
        return first_win;
    }

    public void setFirst_win(BigDecimal first_win) {
        this.first_win = first_win;
    }

    public BigDecimal getFirst_same() {
        return first_same;
    }

    public void setFirst_same(BigDecimal first_same) {
        this.first_same = first_same;
    }

    public BigDecimal getFirst_lost() {
        return first_lost;
    }

    public void setFirst_lost(BigDecimal first_lost) {
        this.first_lost = first_lost;
    }

    public BigDecimal getWin() {
        return win;
    }

    public void setWin(BigDecimal win) {
        this.win = win;
    }

    public BigDecimal getSame() {
        return same;
    }

    public void setSame(BigDecimal same) {
        this.same = same;
    }

    public BigDecimal getLost() {
        return lost;
    }

    public void setLost(BigDecimal lost) {
        this.lost = lost;
    }

    public BigDecimal getWin_bet_rate() {
        return win_bet_rate;
    }

    public void setWin_bet_rate(BigDecimal win_bet_rate) {
        this.win_bet_rate = win_bet_rate;
    }

    public BigDecimal getSame_bet_rate() {
        return same_bet_rate;
    }

    public void setSame_bet_rate(BigDecimal same_bet_rate) {
        this.same_bet_rate = same_bet_rate;
    }

    public BigDecimal getLost_bet_rate() {
        return lost_bet_rate;
    }

    public void setLost_bet_rate(BigDecimal lost_bet_rate) {
        this.lost_bet_rate = lost_bet_rate;
    }

    public BigDecimal getWin_kelly() {
        return win_kelly;
    }

    public void setWin_kelly(BigDecimal win_kelly) {
        this.win_kelly = win_kelly;
    }

    public BigDecimal getSame_kelly() {
        return same_kelly;
    }

    public void setSame_kelly(BigDecimal same_kelly) {
        this.same_kelly = same_kelly;
    }

    public BigDecimal getLost_kelly() {
        return lost_kelly;
    }

    public void setLost_kelly(BigDecimal lost_kelly) {
        this.lost_kelly = lost_kelly;
    }

    public BigDecimal getPay_back_percent() {
        return pay_back_percent;
    }

    public void setPay_back_percent(BigDecimal pay_back_percent) {
        this.pay_back_percent = pay_back_percent;
    }

    public BigDecimal getBuy_win_money() {
        return buy_win_money;
    }

    public void setBuy_win_money(BigDecimal buy_win_money) {
        this.buy_win_money = buy_win_money;
    }

    public BigDecimal getBuy_same_money() {
        return buy_same_money;
    }

    public void setBuy_same_money(BigDecimal buy_same_money) {
        this.buy_same_money = buy_same_money;
    }

    public BigDecimal getBuy_lost_money() {
        return buy_lost_money;
    }

    public void setBuy_lost_money(BigDecimal buy_lost_money) {
        this.buy_lost_money = buy_lost_money;
    }

    public BigDecimal getBuy_result() {
        return buy_result;
    }

    public void setBuy_result(BigDecimal buy_result) {
        this.buy_result = buy_result;
    }

    @Override
    public String toString() {
        return "BigOddsKelly{" +
                "match_id='" + match_id + '\'' +
                ", league_name_simply='" + league_name_simply + '\'' +
                ", host_name='" + host_name + '\'' +
                ", guest_name='" + guest_name + '\'' +
                ", match_time=" + match_time +
                ", host_goal=" + host_goal +
                ", guest_goal=" + guest_goal +
                ", game_result=" + game_result +
                ", company_name='" + company_name + '\'' +
                ", first_win=" + first_win +
                ", first_same=" + first_same +
                ", first_lost=" + first_lost +
                ", win=" + win +
                ", same=" + same +
                ", lost=" + lost +
                ", win_bet_rate=" + win_bet_rate +
                ", same_bet_rate=" + same_bet_rate +
                ", lost_bet_rate=" + lost_bet_rate +
                ", win_kelly=" + win_kelly +
                ", same_kelly=" + same_kelly +
                ", lost_kelly=" + lost_kelly +
                ", pay_back_percent=" + pay_back_percent +
                ", buy_win_money=" + buy_win_money +
                ", buy_same_money=" + buy_same_money +
                ", buy_lost_money=" + buy_lost_money +
                ", buy_result=" + buy_result +
                '}';
    }

    public String getBigodds_id() {
        return bigodds_id;
    }

    public void setBigodds_id(String bigodds_id) {
        this.bigodds_id = bigodds_id;
    }
}