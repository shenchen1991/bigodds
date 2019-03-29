package com.shenchen.odds.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BigOdds implements Serializable {
    private static final long serialVersionUID = 1188436762022698740L;

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

    //bet365初始主胜赔率
    private BigDecimal bet365_first_win;

    //bet365初始平局赔率
    private BigDecimal bet365_first_same;

    //bet365初始客胜赔率
    private BigDecimal bet365_first_lost;

    //bet365即时主胜赔率
    private BigDecimal bet365_win;

    //bet365即时平局赔率
    private BigDecimal bet365_same;

    //bet365即时客胜赔率
    private BigDecimal bet365_lost;

    //bet365主胜赔率变化
    private BigDecimal bet365_win_change;

    //bet365平局赔率变化
    private BigDecimal bet365_same_change;

    //bet365客胜赔率变化
    private BigDecimal bet365_lost_change;

    //韦德初始主胜赔率
    private BigDecimal wd_first_win;

    //韦德初始平局赔率
    private BigDecimal wd_first_same;

    //韦德初始客胜赔率
    private BigDecimal wd_first_lost;

    //韦德即时主胜赔率
    private BigDecimal wd_win;

    //韦德即时平局赔率
    private BigDecimal wd_same;

    //韦德即时客胜赔率
    private BigDecimal wd_lost;

    //韦德主胜赔率变化
    private BigDecimal wd_win_change;

    //韦德平局赔率变化
    private BigDecimal wd_same_change;

    //韦德客胜赔率变化
    private BigDecimal wd_lost_change;


    private BigDecimal buy_win_money;

    private BigDecimal buy_same_money;

    private BigDecimal buy_lost_money;

    private BigDecimal buy_result;

    public BigDecimal getBuy_win_money() {
        return buy_win_money;
    }

    public void setBuy_win_money(BigDecimal buy_win_money) {
        this.buy_win_money = buy_win_money;
    }

    public BigDecimal getBuy_lost_money() {
        return buy_lost_money;
    }

    public void setBuy_lost_money(BigDecimal buy_lost_money) {
        this.buy_lost_money = buy_lost_money;
    }

    public BigDecimal getBuy_same_money() {
        return buy_same_money;
    }

    public void setBuy_same_money(BigDecimal buy_same_money) {
        this.buy_same_money = buy_same_money;
    }


    public BigDecimal getBuy_result() {
        return buy_result;
    }

    public void setBuy_result(BigDecimal buy_result) {
        this.buy_result = buy_result;
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

    public BigDecimal getBet365_first_win() {
        return bet365_first_win;
    }

    public void setBet365_first_win(BigDecimal bet365_first_win) {
        this.bet365_first_win = bet365_first_win;
    }

    public BigDecimal getBet365_first_same() {
        return bet365_first_same;
    }

    public void setBet365_first_same(BigDecimal bet365_first_same) {
        this.bet365_first_same = bet365_first_same;
    }

    public BigDecimal getBet365_first_lost() {
        return bet365_first_lost;
    }

    public void setBet365_first_lost(BigDecimal bet365_first_lost) {
        this.bet365_first_lost = bet365_first_lost;
    }

    public BigDecimal getBet365_win() {
        return bet365_win;
    }

    public void setBet365_win(BigDecimal bet365_win) {
        this.bet365_win = bet365_win;
    }

    public BigDecimal getBet365_same() {
        return bet365_same;
    }

    public void setBet365_same(BigDecimal bet365_same) {
        this.bet365_same = bet365_same;
    }

    public BigDecimal getBet365_lost() {
        return bet365_lost;
    }

    public void setBet365_lost(BigDecimal bet365_lost) {
        this.bet365_lost = bet365_lost;
    }

    public BigDecimal getWd_first_win() {
        return wd_first_win;
    }

    public void setWd_first_win(BigDecimal wd_first_win) {
        this.wd_first_win = wd_first_win;
    }

    public BigDecimal getWd_first_same() {
        return wd_first_same;
    }

    public void setWd_first_same(BigDecimal wd_first_same) {
        this.wd_first_same = wd_first_same;
    }

    public BigDecimal getWd_first_lost() {
        return wd_first_lost;
    }

    public void setWd_first_lost(BigDecimal wd_first_lost) {
        this.wd_first_lost = wd_first_lost;
    }

    public BigDecimal getWd_win() {
        return wd_win;
    }

    public void setWd_win(BigDecimal wd_win) {
        this.wd_win = wd_win;
    }

    public BigDecimal getWd_same() {
        return wd_same;
    }

    public void setWd_same(BigDecimal wd_same) {
        this.wd_same = wd_same;
    }

    public BigDecimal getWd_lost() {
        return wd_lost;
    }

    public void setWd_lost(BigDecimal wd_lost) {
        this.wd_lost = wd_lost;
    }

    public Integer getGame_result() {
        return game_result;
    }

    public void setGame_result(Integer game_result) {
        this.game_result = game_result;
    }

    public BigDecimal getBet365_win_change() {
        return bet365_win_change;
    }

    public void setBet365_win_change(BigDecimal bet365_win_change) {
        this.bet365_win_change = bet365_win_change;
    }

    public BigDecimal getBet365_same_change() {
        return bet365_same_change;
    }

    public void setBet365_same_change(BigDecimal bet365_same_change) {
        this.bet365_same_change = bet365_same_change;
    }

    public BigDecimal getBet365_lost_change() {
        return bet365_lost_change;
    }

    public void setBet365_lost_change(BigDecimal bet365_lost_change) {
        this.bet365_lost_change = bet365_lost_change;
    }

    public BigDecimal getWd_win_change() {
        return wd_win_change;
    }

    public void setWd_win_change(BigDecimal wd_win_change) {
        this.wd_win_change = wd_win_change;
    }

    public BigDecimal getWd_same_change() {
        return wd_same_change;
    }

    public void setWd_same_change(BigDecimal wd_same_change) {
        this.wd_same_change = wd_same_change;
    }

    public BigDecimal getWd_lost_change() {
        return wd_lost_change;
    }

    public void setWd_lost_change(BigDecimal wd_lost_change) {
        this.wd_lost_change = wd_lost_change;
    }
}