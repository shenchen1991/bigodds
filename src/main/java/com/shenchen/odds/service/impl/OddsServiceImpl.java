package com.shenchen.odds.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.shenchen.odds.dao.IOddsDao;
import com.shenchen.odds.model.*;
import com.shenchen.odds.service.IOddsService;
import com.shenchen.odds.util.DateUtils;
import com.shenchen.odds.util.GameCheckUtils;
import com.shenchen.odds.util.HttpClient4;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service("oddsService")
@Slf4j
public class OddsServiceImpl implements IOddsService {
    protected final static Logger logger              = Logger.getLogger(OddsServiceImpl.class);

    @Autowired
    private IOddsDao oddsDao;

    //连接本地的 Redis 服务
    Jedis jedis = new Jedis("localhost");

    private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public void InsertOdds() {
        singleThreadExecutor.execute(new Runnable() {
            public void run() {
                try {
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    date = f.parse("2011-01-01");
//                    date = f.parse("2019-01-01");
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    while ( c.getTime().getTime() < new Date().getTime()){
                        String dateStr = f.format(c.getTime());
                        String json =  HttpClient4.doGet("http://odds.zgzcw.com/odds/oyzs_ajax.action?type=jc&issue="+dateStr+"&date=&companys=14,8");
                        //转对象
                        List<GameBean> list = JSONObject.parseArray(json, GameBean.class);
                        for ( GameBean gameBean : list ) {
                            System.out.println(gameBean.toString());
                            BigOdds bigOdds = new BigOdds();
                            bigOdds.setLeague_name_simply(gameBean.getLEAGUE_NAME_SIMPLY());
                            bigOdds.setHost_name(gameBean.getHOST_NAME());
                            bigOdds.setGuest_name(gameBean.getGUEST_NAME());
                            bigOdds.setHost_goal(gameBean.getHOST_GOAL());
                            bigOdds.setGuest_goal(gameBean.getGUEST_GOAL());
                            if(gameBean.getHOST_GOAL() > gameBean.getGUEST_GOAL()){
                                bigOdds.setGame_result(1);
                            }else if (gameBean.getGUEST_GOAL() == gameBean.getHOST_GOAL()){
                                bigOdds.setGame_result(2);
                            }else{
                                bigOdds.setGame_result(3);
                            }
                            bigOdds.setMatch_time(DateUtils.str2Date("yyyy-MM-dd HH:mm:ss",gameBean.getMATCH_TIME()));
                            if(!CollectionUtils.isEmpty(gameBean.getListOdds())){
                                for(CompanyOdd companyOdd : gameBean.getListOdds()){
                                    if("Bet365".equals(companyOdd.getCOMPANY_NAME())){
                                        bigOdds.setBet365_first_win(StringUtils.isBlank(companyOdd.getFIRST_WIN())? null : new BigDecimal(companyOdd.getFIRST_WIN()));
                                        bigOdds.setBet365_first_same(StringUtils.isBlank(companyOdd.getFIRST_SAME())? null : new BigDecimal(companyOdd.getFIRST_SAME()));
                                        bigOdds.setBet365_first_lost(StringUtils.isBlank(companyOdd.getFIRST_LOST())? null : new BigDecimal(companyOdd.getFIRST_LOST()));
                                        bigOdds.setBet365_win(StringUtils.isBlank(companyOdd.getWIN())? null : new BigDecimal(companyOdd.getWIN()));
                                        bigOdds.setBet365_same(StringUtils.isBlank(companyOdd.getSAME())? null : new BigDecimal(companyOdd.getSAME()));
                                        bigOdds.setBet365_lost(StringUtils.isBlank(companyOdd.getLOST())? null : new BigDecimal(companyOdd.getLOST()));
                                        if(StringUtils.isNotBlank(companyOdd.getFIRST_WIN())){
                                            bigOdds.setBet365_win_change(bigOdds.getBet365_win().subtract(bigOdds.getBet365_first_win()));
                                            bigOdds.setBet365_same_change(bigOdds.getBet365_same().subtract(bigOdds.getBet365_first_same()));
                                            bigOdds.setBet365_lost_change(bigOdds.getBet365_lost().subtract(bigOdds.getBet365_first_lost()));

                                            boolean flag = false ;
                                            if(bigOdds.getBet365_win().doubleValue() > 2
                                                    && bigOdds.getBet365_same().doubleValue() > 2
                                                    && bigOdds.getBet365_lost().doubleValue() > 2){
                                                flag = true;
                                            }

                                            int index = 0;
                                            if(bigOdds.getBet365_win_change().doubleValue()  < 0 && flag){
                                                index ++;
                                                bigOdds.setBuy_win_money(new BigDecimal(1));
                                            }else{
                                                bigOdds.setBuy_win_money(new BigDecimal(0));
                                            }

                                            if(bigOdds.getBet365_same_change().doubleValue() < 0 && flag){
                                                index ++;
                                                bigOdds.setBuy_same_money(new BigDecimal(1));
                                            }else{
                                                bigOdds.setBuy_same_money(new BigDecimal(0));
                                            }
                                            if(bigOdds.getBet365_lost_change().doubleValue() < 0 && flag){
                                                index ++;
                                                bigOdds.setBuy_lost_money(new BigDecimal(1));
                                            }else{
                                                bigOdds.setBuy_lost_money(new BigDecimal(0));
                                            }
                                            if(index > 0){
                                                bigOdds.setBuy_win_money(bigOdds.getBuy_win_money().divide(new BigDecimal(index)));
                                                bigOdds.setBuy_same_money(bigOdds.getBuy_same_money().divide(new BigDecimal(index)));
                                                bigOdds.setBuy_lost_money(bigOdds.getBuy_lost_money().divide(new BigDecimal(index)));
                                            }
                                            switch (bigOdds.getGame_result()){
                                                case 1:
                                                    bigOdds.setBuy_result(bigOdds.getBuy_win_money().multiply(bigOdds.getBet365_win()));
                                                    break;
                                                case 2:
                                                    bigOdds.setBuy_result(bigOdds.getBuy_same_money().multiply(bigOdds.getBet365_same()));
                                                    break;
                                                case 3:
                                                    bigOdds.setBuy_result(bigOdds.getBuy_lost_money().multiply(bigOdds.getBet365_lost()));
                                                    break;

                                            }



                                        }

                                    }
                                    if("韦德".equals(companyOdd.getCOMPANY_NAME())){
                                        bigOdds.setWd_first_win(StringUtils.isBlank(companyOdd.getFIRST_WIN())? null : new BigDecimal(companyOdd.getFIRST_WIN()));
                                        bigOdds.setWd_first_same(StringUtils.isBlank(companyOdd.getFIRST_SAME())? null : new BigDecimal(companyOdd.getFIRST_SAME()));
                                        bigOdds.setWd_first_lost(StringUtils.isBlank(companyOdd.getFIRST_LOST())? null : new BigDecimal(companyOdd.getFIRST_LOST()));
                                        bigOdds.setWd_win(StringUtils.isBlank(companyOdd.getWIN())? null : new BigDecimal(companyOdd.getWIN()));
                                        bigOdds.setWd_same(StringUtils.isBlank(companyOdd.getSAME())? null : new BigDecimal(companyOdd.getSAME()));
                                        bigOdds.setWd_lost(StringUtils.isBlank(companyOdd.getLOST())? null : new BigDecimal(companyOdd.getLOST()));
                                        if(StringUtils.isNotBlank(companyOdd.getFIRST_WIN())){
                                            bigOdds.setWd_win_change(bigOdds.getWd_win().subtract(bigOdds.getWd_first_win()));
                                            bigOdds.setWd_same_change(bigOdds.getWd_same().subtract(bigOdds.getWd_first_same()));
                                            bigOdds.setWd_lost_change(bigOdds.getWd_lost().subtract(bigOdds.getWd_first_lost()));
                                        }

                                    }
                                }
                            }

                            oddsDao.insertOdds(bigOdds);

                        }
                        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
                    }

                } catch (ParseException e) {
                    logger.error("error",e);
                }
            }
        });


    }

    public void insertKellyOdds(final String inputDateStr) {
        singleThreadExecutor.execute(new Runnable() {
            public void run() {
                try {
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//                    Date date = f.parse("2014-05-13");

                    Date date = f.parse(inputDateStr);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    while ( c.getTime().getTime() < new Date().getTime()){
                        String dateStr = f.format(c.getTime());
                        try {
                            logger.info("当前日期："+ dateStr);
                            Thread.sleep(10000);
                            String json =  HttpClient4.doGet("http://odds.zgzcw.com/odds/oyzs_ajax.action?type=jc&issue="+dateStr+"&date=&companys=14,8");
                            //转对象
                            logger.info("获取信息："+json);
                            List<GameBean> list = JSONObject.parseArray(json, GameBean.class);
                            for ( GameBean gameBean : list ) {
                                System.out.println(gameBean.toString());
                                BigOddsKelly bigOddsKelly = new BigOddsKelly();
                                bigOddsKelly.setMatch_id(gameBean.getID());
                                bigOddsKelly.setLeague_name_simply(gameBean.getLEAGUE_NAME_SIMPLY());
                                bigOddsKelly.setHost_name(gameBean.getHOST_NAME());
                                bigOddsKelly.setGuest_name(gameBean.getGUEST_NAME());
                                bigOddsKelly.setHost_goal(gameBean.getHOST_GOAL());
                                bigOddsKelly.setGuest_goal(gameBean.getGUEST_GOAL());
                                if(gameBean.getHOST_GOAL() > gameBean.getGUEST_GOAL()){
                                    bigOddsKelly.setGame_result(1);
                                }else if (gameBean.getGUEST_GOAL() == gameBean.getHOST_GOAL()){
                                    bigOddsKelly.setGame_result(2);
                                }else{
                                    bigOddsKelly.setGame_result(3);
                                }
                                bigOddsKelly.setMatch_time(DateUtils.str2Date("yyyy-MM-dd HH:mm:ss",gameBean.getMATCH_TIME()));
                                //下载文件
                                for(;;){
                                    boolean flag = getKellyData(bigOddsKelly.getMatch_id(),bigOddsKelly);
                                    if(flag){
                                        break;
                                    }
                                    logger.info("当前日期："+ dateStr);
                                    String idStr = jedis.get("out");
                                    if(StringUtils.isNotBlank(idStr)){
                                        List<String> ids = Arrays.asList(idStr.split(","));
                                        if(ids.contains(bigOddsKelly.getMatch_id())){
                                            oddsDao.insertOddsKelly(bigOddsKelly);
                                            break;
                                        }
                                    }
                                    Thread.sleep(10000);
                                }
//
                            }
                            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
                        }catch (Exception e){
                            logger.error("拉取数据异常",e);
                        }
//                        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
                    }
                } catch (ParseException e) {
                    logger.error("解析异常",e);
                }
            }
        });
    }

    public void listAll() {
        log.error("111");
        List<BigOddsKelly> bigOddsKellyList = oddsDao.listAll();
        for(BigOddsKelly bigOddsKelly : bigOddsKellyList){
            if("1595".equals(bigOddsKelly.getBigodds_id()) ){
                System.out.println();
            }

            int index = 0;

            bigOddsKelly.setBuy_win_money(new BigDecimal(0));
            bigOddsKelly.setBuy_same_money(new BigDecimal(0));
            bigOddsKelly.setBuy_lost_money(new BigDecimal(0));
            bigOddsKelly.setBuy_result(new BigDecimal(0));
            if(bigOddsKelly.getWin_kelly().doubleValue() < bigOddsKelly.getPay_back_percent().doubleValue()
                && (bigOddsKelly.getPay_back_percent().doubleValue() - bigOddsKelly.getWin_kelly().doubleValue()) > 0.1
            ){
                bigOddsKelly.setBuy_win_money(new BigDecimal(30));
                index++;
            }
            if(bigOddsKelly.getSame_kelly().doubleValue() < bigOddsKelly.getPay_back_percent().doubleValue()
                    && (bigOddsKelly.getPay_back_percent().doubleValue() - bigOddsKelly.getSame_kelly().doubleValue()) > 0.05
            ){
                bigOddsKelly.setBuy_same_money(new BigDecimal(30));
                index++;
            }
            if(bigOddsKelly.getLost_kelly().doubleValue() < bigOddsKelly.getPay_back_percent().doubleValue()
                    && (bigOddsKelly.getPay_back_percent().doubleValue() - bigOddsKelly.getLost_kelly().doubleValue()) > 0.05
            ){
                bigOddsKelly.setBuy_lost_money(new BigDecimal(30));
                index++;
            }


            if(index > 1){
                bigOddsKelly.setBuy_win_money(bigOddsKelly.getBuy_win_money().divide(new BigDecimal(index)));
                bigOddsKelly.setBuy_same_money(bigOddsKelly.getBuy_same_money().divide(new BigDecimal(index)));
                bigOddsKelly.setBuy_lost_money(bigOddsKelly.getBuy_lost_money().divide(new BigDecimal(index)));
            }
            bigOddsKelly.setBuy_result(new BigDecimal(0));
            switch (bigOddsKelly.getGame_result()){
                case 1 :
                    bigOddsKelly.setBuy_result(bigOddsKelly.getBuy_result().add(bigOddsKelly.getWin().multiply(bigOddsKelly.getBuy_win_money())));
                    break;
                case 2 :
                    bigOddsKelly.setBuy_result(bigOddsKelly.getBuy_result().add(bigOddsKelly.getSame().multiply(bigOddsKelly.getBuy_same_money())));
                    break;
                case 3 :
                    bigOddsKelly.setBuy_result(bigOddsKelly.getBuy_result().add(bigOddsKelly.getLost().multiply(bigOddsKelly.getBuy_lost_money())));
                    break;
                    default:break;
            }

            oddsDao.update(bigOddsKelly);

        }

    }


    /**
     * 从网络Url中下载文件
     *
     * @param id
     * @throws IOException
     */
    public  boolean getKellyData(String id,BigOddsKelly bigOddsKelly) {
        try {
            URL url = new URL("http://fenxi.zgzcw.com/export/"+id+"/bjop");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");

            // 得到输入流
            InputStream inputStream = conn.getInputStream();

            // 获取自己数组
            byte[] getData = readInputStream(inputStream);

            // 文件保存位置
            File saveDir = new File("D:\\bigData");
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + id + ".xls");
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            //读取文件
            File file1 = new File(file.getAbsolutePath());
            InputStream in = null;
            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
            // 一次读一个字节
            in = new FileInputStream(file);
            Sheet sheet;
            Workbook book = Workbook.getWorkbook(in);
            sheet = book.getSheet(0);
            for (int i = 0; i < sheet.getRows(); i++) {
                String companyName = sheet.getCell(1, i).getContents();
                if(StringUtils.isNotBlank(companyName) && (companyName.equals("Bet365") || companyName.contains("伟德"))){
                    bigOddsKelly.setCompany_name(companyName);
                    for (int j = 0; j < 15; j++){
                        String cell1 = sheet.getCell(j, i).getContents();//（列，行）
                        System.out.print(cell1  + "\t");
                        switch (j){
                            case 2 : bigOddsKelly.setFirst_win(new BigDecimal(cell1));
                            break;
                            case 3 : bigOddsKelly.setFirst_same(new BigDecimal(cell1));
                                break;
                            case 4 : bigOddsKelly.setFirst_lost(new BigDecimal(cell1));
                                break;
                            case 5 : bigOddsKelly.setWin(new BigDecimal(cell1));
                                break;
                            case 6 : bigOddsKelly.setSame(new BigDecimal(cell1));
                                break;
                            case 7 : bigOddsKelly.setLost(new BigDecimal(cell1));
                                break;
                            case 8 : bigOddsKelly.setWin_bet_rate(new BigDecimal(cell1));
                                break;
                            case 9 : bigOddsKelly.setSame_bet_rate(new BigDecimal(cell1));
                                break;
                            case 10 : bigOddsKelly.setLost_bet_rate(new BigDecimal(cell1));
                                break;
                            case 11 : bigOddsKelly.setWin_kelly(new BigDecimal(cell1));
                                break;
                            case 12 : bigOddsKelly.setSame_kelly(new BigDecimal(cell1));
                                break;
                            case 13 : bigOddsKelly.setLost_kelly(new BigDecimal(cell1));
                                break;
                            case 14 : bigOddsKelly.setPay_back_percent(new BigDecimal(cell1));
                                break;
                        }
                    }
                    System.out.println(StringUtils.EMPTY);
                    oddsDao.insertOddsKelly(bigOddsKelly);
                }
            }
            if (in != null) {
                in.close();
            }
            return true;
        } catch (IOException e) {
            logger.error("download error" +  id,e);
            return false;
        }catch(BiffException biffException){
            logger.error("download error" +  id,biffException);
            return true;

        }

    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }




    public void InsertOddsyz(String inputDateStr) {
        singleThreadExecutor.execute(new Runnable() {
            public void run() {
                try {
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();

//                    date = f.parse("2019-03-27");
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    Date endDate = f.parse("2013-01-01");
                    while ( c.getTime().getTime() > endDate.getTime()){
                        Thread.sleep(1000);
                        String dateStr = f.format(c.getTime());
                        logger.info("当前日期："+ dateStr);
                        List<GameBean> list = new ArrayList<GameBean>();
                        try{
                            String json =  HttpClient4.doGet("http://odds.zgzcw.com/odds/oyzs_ajax.action?type=qb&issue="+dateStr+"&date=&companys=14");
                            //转对象
                            logger.info("获取信息："+json);
                            list = JSONObject.parseArray(json, GameBean.class);
                        for ( GameBean gameBean : list ) {
                            System.out.println(gameBean.toString());
                            if(!GameCheckUtils.gameCheck(gameBean.getLEAGUE_NAME_SIMPLY())){
                                continue;
                            }
                            BigOddsYz bigOdds = new BigOddsYz();
                            bigOdds.setMatch_id(gameBean.getID());
                            bigOdds.setLeague_name_simply(gameBean.getLEAGUE_NAME_SIMPLY());
                            bigOdds.setHost_name(gameBean.getHOST_NAME());
                            bigOdds.setGuest_name(gameBean.getGUEST_NAME());
                            bigOdds.setHost_goal(gameBean.getHOST_GOAL());
                            bigOdds.setGuest_goal(gameBean.getGUEST_GOAL());
                            if(gameBean.getHOST_GOAL() > gameBean.getGUEST_GOAL()){
                                bigOdds.setGame_result(1);
                            }else if (gameBean.getGUEST_GOAL() == gameBean.getHOST_GOAL()){
                                bigOdds.setGame_result(2);
                            }else{
                                bigOdds.setGame_result(3);
                            }
                            bigOdds.setMatch_time(DateUtils.str2Date("yyyy-MM-dd HH:mm:ss",gameBean.getMATCH_TIME()));
                            if(!CollectionUtils.isEmpty(gameBean.getListOdds())){
                                for(CompanyOdd companyOdd : gameBean.getListOdds()){
                                    bigOdds.setCompany_name(companyOdd.getCOMPANY_NAME());
                                    bigOdds.setFirst_let_ball(StringUtils.isBlank(companyOdd.getFIRST_HANDICAP())? null : new BigDecimal(companyOdd.getFIRST_HANDICAP()));
                                    bigOdds.setFirst_host_bet(StringUtils.isBlank(companyOdd.getFIRST_HOST())? null : new BigDecimal(companyOdd.getFIRST_HOST()));
                                    bigOdds.setFirst_guest_bet(StringUtils.isBlank(companyOdd.getFIRST_GUEST())? null : new BigDecimal(companyOdd.getFIRST_GUEST()));
                                    bigOdds.setLet_ball(StringUtils.isBlank(companyOdd.getHANDICAP())? null : new BigDecimal(companyOdd.getHANDICAP()));
                                    bigOdds.setHost_bet(StringUtils.isBlank(companyOdd.getHOST())? null : new BigDecimal(companyOdd.getHOST()));
                                    bigOdds.setGuest_bet(StringUtils.isBlank(companyOdd.getGUEST())? null : new BigDecimal(companyOdd.getGUEST()));
                                    oddsDao.insertOddsyz(bigOdds);
                                }
                            }



                        }
                        c.add(Calendar.DAY_OF_MONTH, -1);// 今天+1天
                        }catch (Exception e){
                            log.error("error",e);
                        }
                    }

                } catch (ParseException e) {
                    logger.error("error",e);
                } catch (InterruptedException e) {
                    logger.error("error",e);
                }
                sumYaZhou();
                guilv();


            }
        });


    }


    /**
     * 从日期获取最新的亚洲赔率
     * @param inputDateStr
     */
    @Override
    public void insertLastYz(String inputDateStr) {
        singleThreadExecutor.execute(new Runnable() {
            public void run() {
                try {
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    Date startDate = f.parse(inputDateStr);
                    c.setTime(startDate);
                    while ( c.getTime().getTime() < new Date().getTime() - 86400000){
                        Thread.sleep(1000);
                        String dateStr = f.format(c.getTime());
                        logger.info("当前日期："+ dateStr);
                        List<GameBean> list = new ArrayList<GameBean>();
                        try{
                            String json =  HttpClient4.doGet("http://odds.zgzcw.com/odds/oyzs_ajax.action?type=qb&issue="+dateStr+"&date=&companys=14");
                            //转对象
                            logger.info("获取信息："+json);
                            list = JSONObject.parseArray(json, GameBean.class);
                            for ( GameBean gameBean : list ) {
                                System.out.println(gameBean.toString());
                                if(!GameCheckUtils.gameCheck(gameBean.getLEAGUE_NAME_SIMPLY())){
                                    continue;
                                }
                                BigOddsYz bigOdds = new BigOddsYz();
                                bigOdds.setMatch_id(gameBean.getID());
                                bigOdds.setLeague_name_simply(gameBean.getLEAGUE_NAME_SIMPLY());
                                bigOdds.setHost_name(gameBean.getHOST_NAME());
                                bigOdds.setGuest_name(gameBean.getGUEST_NAME());
                                bigOdds.setHost_goal(gameBean.getHOST_GOAL());
                                bigOdds.setGuest_goal(gameBean.getGUEST_GOAL());
                                if(gameBean.getHOST_GOAL() > gameBean.getGUEST_GOAL()){
                                    bigOdds.setGame_result(1);
                                }else if (gameBean.getGUEST_GOAL() == gameBean.getHOST_GOAL()){
                                    bigOdds.setGame_result(2);
                                }else{
                                    bigOdds.setGame_result(3);
                                }
                                bigOdds.setMatch_time(DateUtils.str2Date("yyyy-MM-dd HH:mm:ss",gameBean.getMATCH_TIME()));
                                if(!CollectionUtils.isEmpty(gameBean.getListOdds())){
                                    for(CompanyOdd companyOdd : gameBean.getListOdds()){
                                        bigOdds.setCompany_name(companyOdd.getCOMPANY_NAME());
                                        bigOdds.setFirst_let_ball(StringUtils.isBlank(companyOdd.getFIRST_HANDICAP())? null : new BigDecimal(companyOdd.getFIRST_HANDICAP()));
                                        bigOdds.setFirst_host_bet(StringUtils.isBlank(companyOdd.getFIRST_HOST())? null : new BigDecimal(companyOdd.getFIRST_HOST()));
                                        bigOdds.setFirst_guest_bet(StringUtils.isBlank(companyOdd.getFIRST_GUEST())? null : new BigDecimal(companyOdd.getFIRST_GUEST()));
                                        bigOdds.setLet_ball(StringUtils.isBlank(companyOdd.getHANDICAP())? null : new BigDecimal(companyOdd.getHANDICAP()));
                                        bigOdds.setHost_bet(StringUtils.isBlank(companyOdd.getHOST())? null : new BigDecimal(companyOdd.getHOST()));
                                        bigOdds.setGuest_bet(StringUtils.isBlank(companyOdd.getGUEST())? null : new BigDecimal(companyOdd.getGUEST()));



                                        if(bigOdds.getHost_goal() == null
                                                || bigOdds.getGuest_goal() == null
                                                || bigOdds.getLet_ball() == null){
                                            continue;

                                        }
                                        BigDecimal yzResult = new BigDecimal(bigOdds.getHost_goal())
                                                .subtract(new BigDecimal(bigOdds.getGuest_goal())).add(bigOdds.getLet_ball());

                                        if(yzResult.doubleValue() > 0.3){
                                            bigOdds.setBuy_host(new BigDecimal(2));
                                            bigOdds.setBuy_guest(new BigDecimal(0));
                                        }else if(yzResult.doubleValue() < 0.3 && yzResult.doubleValue() > 0){
                                            bigOdds.setBuy_host(new BigDecimal(1.5));
                                            bigOdds.setBuy_guest(new BigDecimal(0.5));
                                        }else if(yzResult.doubleValue() == 0){
                                            bigOdds.setBuy_host(new BigDecimal(1));
                                            bigOdds.setBuy_guest(new BigDecimal(1));
                                        }else if(yzResult.doubleValue() < 0&& yzResult.doubleValue() > -0.3){
                                            bigOdds.setBuy_host(new BigDecimal(0.5));
                                            bigOdds.setBuy_guest(new BigDecimal(1.5));
                                        }else if(yzResult.doubleValue() < -0.3 ){
                                            bigOdds.setBuy_host(new BigDecimal(0));
                                            bigOdds.setBuy_guest(new BigDecimal(2));
                                        }

                                        oddsDao.deleteOddsyz(bigOdds.getMatch_id());

                                        oddsDao.insertOddsyz(bigOdds);
                                    }
                                }



                            }
                            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
                        }catch (Exception e){
                            log.error("error",e);
                        }
                    }

                } catch (ParseException e) {
                    logger.error("error",e);
                } catch (InterruptedException e) {
                    logger.error("error",e);
                }
//                sumYaZhou();
//                guilv();


            }
        });


    }




    @Override
    public void sumYaZhou() {
        log.error("111");
        List<BigOddsYz> bigOddsYzList = oddsDao.listYZAll();
        for(BigOddsYz bigOddsYz : bigOddsYzList){

            if(bigOddsYz.getHost_goal() == null
                    || bigOddsYz.getGuest_goal() == null
                    || bigOddsYz.getLet_ball() == null){
                continue;

            }
            BigDecimal yzResult = new BigDecimal(bigOddsYz.getHost_goal())
                    .subtract(new BigDecimal(bigOddsYz.getGuest_goal())).add(bigOddsYz.getLet_ball());

            if(yzResult.doubleValue() > 0.3){
                bigOddsYz.setBuy_host(new BigDecimal(2));
                bigOddsYz.setBuy_guest(new BigDecimal(0));
            }else if(yzResult.doubleValue() < 0.3 && yzResult.doubleValue() > 0){
                bigOddsYz.setBuy_host(new BigDecimal(1.5));
                bigOddsYz.setBuy_guest(new BigDecimal(0.5));
            }else if(yzResult.doubleValue() == 0){
                bigOddsYz.setBuy_host(new BigDecimal(1));
                bigOddsYz.setBuy_guest(new BigDecimal(1));
            }else if(yzResult.doubleValue() < 0&& yzResult.doubleValue() > -0.3){
                bigOddsYz.setBuy_host(new BigDecimal(0.5));
                bigOddsYz.setBuy_guest(new BigDecimal(1.5));
            }else if(yzResult.doubleValue() < -0.3 ){
                bigOddsYz.setBuy_host(new BigDecimal(0));
                bigOddsYz.setBuy_guest(new BigDecimal(2));
            }



            oddsDao.updateYz(bigOddsYz);

        }

    }

    @Override
    public  void  guilv(){
        try{
            String[] strings = {
                    "欧冠","欧罗巴杯",
                    "英超","英冠","英甲","英乙",
                    "意甲","意乙","意丙1A",
                    "西甲","西乙",
                    "德甲", "德乙",
                    "法甲", "法乙",
                    "葡超","葡甲",
                    "苏超","苏冠","苏甲",
                    "荷甲", "荷乙",
                    "瑞典超","瑞典甲",
                    "日职", "日乙",
                    "阿甲","阿乙",
                    "中超",
                    "挪超",
                    "阿甲",
                    "韩K联",
                    "澳超",
                    "俄超",
                    "美职",
                    "法国杯"
            };

            double beginD = -0.50D;
            double addStep = 0.05D;
            int time = 20;

            for(String str : strings){
                // 让盘 升盘 主胜
                rang_sheng_zhu(str,beginD,addStep,time);

                // 让盘 降盘 主胜
                rang_jiang_zhu(str,beginD,addStep,time);

                // 让盘 不变盘 主胜
                rang_bubian_zhu(str,beginD,addStep,time);

                // 让盘 升盘 客队
                rang_sheng_ke(str,beginD,addStep,time);

                // 让盘 降盘 客队
                rang_jiang_ke(str,beginD,addStep,time);

                // 让盘 不变盘 客队
                rang_bubian_ke(str,beginD,addStep,time);

                // 受让盘 升盘 主胜
                shou_sheng_zhu(str,beginD,addStep,time);

                //受让盘 降盘 主胜
                shou_jiang_zhu(str,beginD,addStep,time);

                // 受让盘 不变盘 主胜
                shou_bubian_zhu(str,beginD,addStep,time);
                // 受让盘 升盘 客队
                shou_sheng_ke(str,beginD,addStep,time);
                // 受让盘 降盘 客队
                shou_jiang_ke(str,beginD,addStep,time);
                // 受让盘 不变盘 客队
                shou_bubian_ke(str,beginD,addStep,time);
            }


        }catch (Exception e){
            log.error("error!",e);
        }
    }


    // 让盘 升盘 主胜
    private void rang_sheng_zhu(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.rang_sheng_zhu(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.rang_sheng_zhu(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.rang_sheng_zhu(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("让盘");
            bigOddsYzModel.setLet_change_type("升盘");
            bigOddsYzModel.setBet_change_type("主队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|让盘|升盘|主队|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 让盘 降盘 主胜
    private void rang_jiang_zhu(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.rang_jiang_zhu(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.rang_jiang_zhu(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.rang_jiang_zhu(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("让盘");
            bigOddsYzModel.setLet_change_type("降盘");
            bigOddsYzModel.setBet_change_type("主队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|让盘|降盘|主胜|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 让盘 不变盘 主胜
    private void rang_bubian_zhu(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.rang_bubian_zhu(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.rang_bubian_zhu(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.rang_bubian_zhu(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("让盘");
            bigOddsYzModel.setLet_change_type("不变盘");
            bigOddsYzModel.setBet_change_type("主队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|让盘|不变盘|主胜|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 让盘 升盘 客队
    private void rang_sheng_ke(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.rang_sheng_ke(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.rang_sheng_ke(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.rang_sheng_ke(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("让盘");
            bigOddsYzModel.setLet_change_type("升盘");
            bigOddsYzModel.setBet_change_type("客队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|让盘|升盘|客队|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 让盘 降盘 客队
    private void rang_jiang_ke(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.rang_jiang_ke(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.rang_jiang_ke(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.rang_jiang_ke(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("让盘");
            bigOddsYzModel.setLet_change_type("降盘");
            bigOddsYzModel.setBet_change_type("客队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|让盘|降盘|客队|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 让盘 不变盘 客队
    private void rang_bubian_ke(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.rang_bubian_ke(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.rang_bubian_ke(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.rang_bubian_ke(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("让盘");
            bigOddsYzModel.setLet_change_type("不变盘");
            bigOddsYzModel.setBet_change_type("客队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|让盘|不变盘|客队|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }


    // 受让盘 升盘 主胜
    private void shou_sheng_zhu(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.shou_sheng_zhu(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.shou_sheng_zhu(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.shou_sheng_zhu(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("受让盘");
            bigOddsYzModel.setLet_change_type("升盘");
            bigOddsYzModel.setBet_change_type("主队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|受让盘|升盘|主胜|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 受让盘 降盘 主胜
    private void shou_jiang_zhu(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.shou_jiang_zhu(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.shou_jiang_zhu(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.shou_jiang_zhu(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("受让盘");
            bigOddsYzModel.setLet_change_type("降盘");
            bigOddsYzModel.setBet_change_type("主队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|受让盘|降盘|主胜|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 受让盘 不变盘 主胜
    private void shou_bubian_zhu(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.shou_bubian_zhu(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.shou_bubian_zhu(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.shou_bubian_zhu(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("受让盘");
            bigOddsYzModel.setLet_change_type("不变盘");
            bigOddsYzModel.setBet_change_type("主队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|受让盘|不变盘|主胜|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 受让盘 升盘 客队
    private void shou_sheng_ke(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.shou_sheng_ke(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.shou_sheng_ke(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.rang_sheng_ke(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("受让盘");
            bigOddsYzModel.setLet_change_type("升盘");
            bigOddsYzModel.setBet_change_type("客队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|受让盘|升盘|客队|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 受让盘 降盘 客队
    private void shou_jiang_ke(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.shou_jiang_ke(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.shou_jiang_ke(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.shou_jiang_ke(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("受让盘");
            bigOddsYzModel.setLet_change_type("降盘");
            bigOddsYzModel.setBet_change_type("客队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|受让盘|降盘|客队|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }

    // 受让盘 不变盘 客队
    private void shou_bubian_ke(String name,double beginD,double addStep,int time){
        BigDecimal changeBet = new BigDecimal(beginD);
        for (int index  = 0; index < time ;index ++){
            YzJiSuan yzJiSuan = new YzJiSuan();
            yzJiSuan.setLeague_name_simply(name);
            yzJiSuan.setStart_change_bet(changeBet);
            yzJiSuan.setEnd_change_bet(yzJiSuan.getStart_change_bet().add(new BigDecimal(addStep)));
            Integer totalCount = oddsDao.shou_bubian_ke(yzJiSuan);
            yzJiSuan.setResultType(1);
            Integer hostCount = oddsDao.shou_bubian_ke(yzJiSuan);
            yzJiSuan.setResultType(2);
            Integer guestCount = oddsDao.shou_bubian_ke(yzJiSuan);
            if(hostCount == null){
                hostCount = 0;
            }
            if(guestCount == null){
                guestCount = 0;
            }
            Integer sum  = (hostCount == null ? 0 :hostCount) + (guestCount == null ? 0 : guestCount);
            Double rate = 0D;
            Double rate2 = 0D;
            if(sum != 0){
                rate = hostCount.doubleValue()/sum.doubleValue();
                rate2 = 1- rate;
            }
            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(name);
            bigOddsYzModel.setLet_type("受让盘");
            bigOddsYzModel.setLet_change_type("不变盘");
            bigOddsYzModel.setBet_change_type("客队");
            bigOddsYzModel.setBet_change_start(changeBet);
            bigOddsYzModel.setBet_change_end(yzJiSuan.getEnd_change_bet());
            bigOddsYzModel.setHost_win(hostCount);
            bigOddsYzModel.setGuest_win(guestCount);
            bigOddsYzModel.setHost_win_rate(new BigDecimal(rate));
            bigOddsYzModel.setGuest_win_rate(new BigDecimal(rate2));
            oddsDao.insertOddsyzModel(bigOddsYzModel);
            System.out.println(name+"|受让盘|不变盘|客队|开始变化:"+changeBet.doubleValue()+ "|"+totalCount + "|"+hostCount+ "|"+guestCount+ "|"+rate+ "|"+rate2);
            changeBet = changeBet.add(new BigDecimal(addStep));
        }
    }



    @Override
    public List<BigOddsYzResult> getLastYz() {
//        singleThreadExecutor.execute(new Runnable() {
////            public void run() {
                try {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DAY_OF_MONTH, -2);// 今天+1天

                Thread.sleep(1000);
                while ( c.getTime().getTime() < new Date().getTime() + 100000){
                String dateStr = f.format(c.getTime());
                logger.info("当前日期："+ dateStr);
                boolean flag = false;
                    List<GameBean> list = new ArrayList<GameBean>();
                    while(CollectionUtils.isEmpty(list)){
                        try{
                            String json =  HttpClient4.doGet("http://odds.zgzcw.com/odds/oyzs_ajax.action?type=qb&issue="+dateStr+"&date=&companys=14");
                            //转对象
                            logger.info("获取信息："+json);
                            list = JSONObject.parseArray(json, GameBean.class);
                        }catch (Exception e){
                         log.error("error ",e);
                        }
                }
                for ( GameBean gameBean : list ) {
                    if(!GameCheckUtils.gameCheck(gameBean.getLEAGUE_NAME_SIMPLY())){
                        continue;
                    }
                    System.out.println(gameBean.toString());
                    BigOddsYz bigOdds = new BigOddsYz();
                    bigOdds.setMatch_id(gameBean.getID());
                    bigOdds.setLeague_name_simply(gameBean.getLEAGUE_NAME_SIMPLY());
                    bigOdds.setHost_name(gameBean.getHOST_NAME());
                    bigOdds.setGuest_name(gameBean.getGUEST_NAME());
                    bigOdds.setHost_goal(gameBean.getHOST_GOAL());
                    bigOdds.setGuest_goal(gameBean.getGUEST_GOAL());
                    if (gameBean.getHOST_GOAL() > gameBean.getGUEST_GOAL()) {
                        bigOdds.setGame_result(1);
                    } else if (gameBean.getGUEST_GOAL() == gameBean.getHOST_GOAL()) {
                        bigOdds.setGame_result(2);
                    } else {
                        bigOdds.setGame_result(3);
                    }
                    bigOdds.setMatch_time(DateUtils.str2Date("yyyy-MM-dd HH:mm:ss", gameBean.getMATCH_TIME()));
//                    if(bigOdds.getMatch_time().getTime() < new Date().getTime()){
//                        continue;
//                    }

                    if (!CollectionUtils.isEmpty(gameBean.getListOdds())) {
                        for (CompanyOdd companyOdd : gameBean.getListOdds()) {
                            bigOdds.setCompany_name(companyOdd.getCOMPANY_NAME());
                            bigOdds.setFirst_let_ball(StringUtils.isBlank(companyOdd.getFIRST_HANDICAP()) ? null : new BigDecimal(companyOdd.getFIRST_HANDICAP()));
                            bigOdds.setFirst_host_bet(StringUtils.isBlank(companyOdd.getFIRST_HOST()) ? null : new BigDecimal(companyOdd.getFIRST_HOST()));
                            bigOdds.setFirst_guest_bet(StringUtils.isBlank(companyOdd.getFIRST_GUEST()) ? null : new BigDecimal(companyOdd.getFIRST_GUEST()));
                            bigOdds.setLet_ball(StringUtils.isBlank(companyOdd.getHANDICAP()) ? null : new BigDecimal(companyOdd.getHANDICAP()));
                            bigOdds.setHost_bet(StringUtils.isBlank(companyOdd.getHOST()) ? null : new BigDecimal(companyOdd.getHOST()));
                            bigOdds.setGuest_bet(StringUtils.isBlank(companyOdd.getGUEST()) ? null : new BigDecimal(companyOdd.getGUEST()));

                            if (bigOdds.getHost_goal() == null
                                    || bigOdds.getGuest_goal() == null
                                    || bigOdds.getLet_ball() == null) {
                                continue;

                            }
                            BigDecimal yzResult = new BigDecimal(bigOdds.getHost_goal())
                                    .subtract(new BigDecimal(bigOdds.getGuest_goal())).add(bigOdds.getLet_ball());

                            if (yzResult.doubleValue() > 0.3) {
                                bigOdds.setBuy_host(new BigDecimal(2));
                                bigOdds.setBuy_guest(new BigDecimal(0));
                            } else if (yzResult.doubleValue() < 0.3 && yzResult.doubleValue() > 0) {
                                bigOdds.setBuy_host(new BigDecimal(1.5));
                                bigOdds.setBuy_guest(new BigDecimal(0.5));
                            } else if (yzResult.doubleValue() == 0) {
                                bigOdds.setBuy_host(new BigDecimal(1));
                                bigOdds.setBuy_guest(new BigDecimal(1));
                            } else if (yzResult.doubleValue() < 0 && yzResult.doubleValue() > -0.3) {
                                bigOdds.setBuy_host(new BigDecimal(0.5));
                                bigOdds.setBuy_guest(new BigDecimal(1.5));
                            } else if (yzResult.doubleValue() < -0.3) {
                                bigOdds.setBuy_host(new BigDecimal(0));
                                bigOdds.setBuy_guest(new BigDecimal(2));
                            }

                            oddsDao.deleteOddsyzNewById(bigOdds.getMatch_id());
                            oddsDao.insertOddsyzNew(bigOdds);
                        }
                    }

                }

                    c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
                }

                } catch (ParseException e) {
                    logger.error("error",e);
                } catch (InterruptedException e) {
                    logger.error("error",e);
                }
//            }
//        });
        return this.lastYzModel();


    }



    @Override
    public List<BigOddsYzResult> lastYzModel() {

        List<BigOddsYzResult> results = new ArrayList<BigOddsYzResult>();
        String[] strings = {
                "欧冠","欧罗巴杯",
                "英超","英冠","英甲","英乙",
                "意甲","意乙","意丙1A",
                "西甲","西乙",
                "德甲", "德乙",
                "法甲", "法乙",
                "葡超","葡甲",
                "苏超","苏冠","苏甲",
                "荷甲", "荷乙",
                "瑞典超","瑞典甲",
                "日职", "日乙",
                "阿甲","阿乙",
                "美职",
                "中超",
                "挪超",
                "阿甲",
                "韩K联",
                "澳超",
                "俄超",
                "法国杯"
        };
        List<BigOddsYz> bigOddsYzList = oddsDao.listYzNewAll();
        for(BigOddsYz bigOddsYz : bigOddsYzList){
            if(!Arrays.asList(strings).contains(bigOddsYz.getLeague_name_simply())){
                continue;
            }
//            if(bigOddsYz.getMatch_time().getTime() <  (new Date().getTime() - 900000)){
//                continue;
//            }

            BigOddsYzModel bigOddsYzModel = new BigOddsYzModel();
            bigOddsYzModel.setLeague_name_simply(bigOddsYz.getLeague_name_simply());
            if(bigOddsYz.getFirst_let_ball().doubleValue() <= 0){
                bigOddsYzModel.setLet_type("让盘");
            }else if(bigOddsYz.getFirst_let_ball().doubleValue() > 0){
                bigOddsYzModel.setLet_type("受让盘");
            }
            if(bigOddsYz.getFirst_let_ball().doubleValue() > bigOddsYz.getLet_ball().doubleValue()){
                bigOddsYzModel.setLet_change_type("降盘");
            }else if(bigOddsYz.getFirst_let_ball().doubleValue() < bigOddsYz.getLet_ball().doubleValue()){
                bigOddsYzModel.setLet_change_type("升盘");
            }else if(bigOddsYz.getFirst_let_ball().doubleValue() == bigOddsYz.getLet_ball().doubleValue()){
                bigOddsYzModel.setLet_change_type("不变盘");
            }
            bigOddsYzModel.setBet_change_type("主队");
            bigOddsYzModel.setBet_change_end(bigOddsYz.getHost_bet().subtract(bigOddsYz.getFirst_host_bet()) );

            List<BigOddsYzModel> bigOddsYzModels =  oddsDao.query_model(bigOddsYzModel);


            bigOddsYzModel.setBet_change_type("客队");
            bigOddsYzModel.setBet_change_end(bigOddsYz.getGuest_bet().subtract(bigOddsYz.getFirst_guest_bet()) );

            List<BigOddsYzModel> bigOddsYzModels_2 =  oddsDao.query_model(bigOddsYzModel);


            bigOddsYz.setMatch_time_str(DateUtils.date2Str("yyyy-MM-dd HH:mm:ss", bigOddsYz.getMatch_time()));

            BigOddsYzResult result = new BigOddsYzResult();
            result.setBigOddsYz(bigOddsYz);
            System.out.println("赛事分析："+ bigOddsYz.toString());
            if(bigOddsYzModels != null && bigOddsYzModels.size() > 0){
                System.out.println( bigOddsYzModels.get(0).toString());
                result.setHostModel(bigOddsYzModels.get(0));
            }

            if(bigOddsYzModels_2 != null && bigOddsYzModels_2.size() > 0){
                System.out.println(bigOddsYzModels_2.get(0).toString());
                result.setGuestModel(bigOddsYzModels_2.get(0));
            }
            results.add(result);
        }
        return results;

    }



}
