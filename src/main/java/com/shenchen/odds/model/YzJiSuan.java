package com.shenchen.odds.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class YzJiSuan implements Serializable {
    private static final long serialVersionUID = 1188436762022698740L;


    //联赛
    private String league_name_simply;

    //水位变化
    private BigDecimal start_change_bet;

    private BigDecimal end_change_bet;

    //结果类型   0-全部 1-主队 2-客队
    private Integer resultType = 0;

}