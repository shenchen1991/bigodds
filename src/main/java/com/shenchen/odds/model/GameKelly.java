package com.shenchen.odds.model;

public class GameKelly {
    //比赛唯一ID
    private String ID;

    //联赛
    private String LEAGUE_NAME_SIMPLY;

    //主队名称
    private String HOST_NAME;

    //客队名称
    private String GUEST_NAME;

    //比赛时间
    private String MATCH_TIME;

    //主队得分
    private Integer HOST_GOAL;

    //客队得分
    private Integer GUEST_GOAL;

    //公司名称
    private String COMPANY_NAME;

    //主队胜初始赔率
    private String FIRST_WIN;

    //和盘初始赔率
    private String FIRST_SAME;

    //主队负初始赔率
    private String FIRST_LOST;

    //主队胜即时赔率
    private String WIN;

    //和盘即时赔率
    private String SAME;

    //主队负即时赔率
    private String LOST;

}