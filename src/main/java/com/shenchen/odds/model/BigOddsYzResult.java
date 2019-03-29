package com.shenchen.odds.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class BigOddsYzResult implements Serializable {
    private static final long serialVersionUID = 1188436762022698740L;

    BigOddsYz bigOddsYz;
    BigOddsYzModel hostModel;
    BigOddsYzModel guestModel;

}