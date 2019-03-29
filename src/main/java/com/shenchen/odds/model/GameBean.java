package com.shenchen.odds.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class GameBean {
    //比赛唯一ID
    private String ID;

    //联赛
    private String LEAGUE_NAME_SIMPLY;

    //主队名称
    private String HOST_NAME;

    //客队名称
    private String GUEST_NAME;

    //比赛时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private String MATCH_TIME;

    //主队得分
    private Integer HOST_GOAL;

    //客队得分
    private Integer GUEST_GOAL;

    private List<CompanyOdd> listOdds;

    public String getLEAGUE_NAME_SIMPLY() {
        return LEAGUE_NAME_SIMPLY;
    }

    public void setLEAGUE_NAME_SIMPLY(String LEAGUE_NAME_SIMPLY) {
        this.LEAGUE_NAME_SIMPLY = LEAGUE_NAME_SIMPLY;
    }

    public String getHOST_NAME() {
        return HOST_NAME;
    }

    public void setHOST_NAME(String HOST_NAME) {
        this.HOST_NAME = HOST_NAME;
    }

    public String getGUEST_NAME() {
        return GUEST_NAME;
    }

    public void setGUEST_NAME(String GUEST_NAME) {
        this.GUEST_NAME = GUEST_NAME;
    }

    public String getMATCH_TIME() {
        return MATCH_TIME;
    }

    public void setMATCH_TIME(String MATCH_TIME) {
        this.MATCH_TIME = MATCH_TIME;
    }

    public List<CompanyOdd> getListOdds() {
        return listOdds;
    }

    public void setListOdds(List<CompanyOdd> listOdds) {
        this.listOdds = listOdds;
    }

    public Integer getHOST_GOAL() {
        return HOST_GOAL;
    }

    public void setHOST_GOAL(Integer HOST_GOAL) {
        this.HOST_GOAL = HOST_GOAL;
    }

    public Integer getGUEST_GOAL() {
        return GUEST_GOAL;
    }

    public void setGUEST_GOAL(Integer GUEST_GOAL) {
        this.GUEST_GOAL = GUEST_GOAL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "GameBean{" +
                "ID='" + ID + '\'' +
                ", LEAGUE_NAME_SIMPLY='" + LEAGUE_NAME_SIMPLY + '\'' +
                ", HOST_NAME='" + HOST_NAME + '\'' +
                ", GUEST_NAME='" + GUEST_NAME + '\'' +
                ", MATCH_TIME='" + MATCH_TIME + '\'' +
                ", HOST_GOAL=" + HOST_GOAL +
                ", GUEST_GOAL=" + GUEST_GOAL +
                ", listOdds=" + listOdds +
                '}';
    }
}