package com.shenchen.odds.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: bigodds
 * @description:
 * @author: 沈陈
 * @create: 2019-02-26 21:37
 **/
@Getter
@Setter
public class CompanyOdd {

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

    //初始让球
    private String  FIRST_HANDICAP;

    //初始主队
    private String  FIRST_HOST;

    //初始客队
    private String  FIRST_GUEST;

    //即时让球
    private String  HANDICAP;

    //即时主队
    private String  HOST;

    //即时客队
    private String GUEST;





}
