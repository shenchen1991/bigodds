package com.shenchen.odds.service;

import com.shenchen.odds.model.BigOddsYzResult;

import java.util.List;

public interface IOddsService {
    void InsertOdds();

    void insertKellyOdds(final String inputDateStr);

    void listAll();

    void InsertOddsyz(String inputDateStr);

    void insertLastYz(String inputDateStr);

    void sumYaZhou();

    void  guilv();

    List<BigOddsYzResult> getLastYz(boolean isFinish);

    List<BigOddsYzResult> lastYzModel(boolean isFinish);
}