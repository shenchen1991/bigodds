package com.shenchen.odds.dao;

import com.shenchen.odds.model.*;

import java.util.List;

public interface IOddsDao {

    void insertOdds(BigOdds bigOdds);
    void insertOddsKelly(BigOddsKelly bigOddsKelly);

    List<BigOddsKelly> listAll();

    void update(BigOddsKelly bigOddsKelly);


    void insertOddsyz(BigOddsYz bigOdds);

    void deleteOddsyz(String matchId);

    List<BigOddsYz> listYZAll();

    void updateYz(BigOddsYz bigOddsYz);

    Integer rang_sheng_zhu(YzJiSuan yzJiSuan);

    Integer rang_jiang_zhu(YzJiSuan yzJiSuan);

    Integer rang_bubian_zhu(YzJiSuan yzJiSuan);

    Integer rang_sheng_ke(YzJiSuan yzJiSuan);

    Integer rang_jiang_ke(YzJiSuan yzJiSuan);

    Integer rang_bubian_ke(YzJiSuan yzJiSuan);

    Integer shou_sheng_zhu(YzJiSuan yzJiSuan);

    Integer shou_jiang_zhu(YzJiSuan yzJiSuan);

    Integer shou_bubian_zhu(YzJiSuan yzJiSuan);

    Integer shou_sheng_ke(YzJiSuan yzJiSuan);

    Integer shou_jiang_ke(YzJiSuan yzJiSuan);

    Integer shou_bubian_ke(YzJiSuan yzJiSuan);

    void deleteOddsyzNew();
    void deleteOddsyzNewById(String matchId);

    void insertOddsyzModel(BigOddsYzModel bigOddsYzModel);

    List<BigOddsYzModel> query_model(BigOddsYzModel bigOddsYzModel);

    void insertOddsyzNew(BigOddsYz bigOdds);


    List<BigOddsYz> listYzNewAll();

    List<BigOddsYz> listYzNewFinished();
}