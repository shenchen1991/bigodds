package com.shenchen.odds.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExcelHttpClientTest {


//public static void main(String[] args) throws ParseException {
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = f.parse("2011-01-01");
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        while ( c.getTime().getTime() < new Date().getTime()){
//            String dateStr = f.format(c.getTime());
////            String json =  HttpClient4.doGet("http://odds.zgzcw.com/odds/oyzs_ajax.action?type=jc&issue="+dateStr+"&date=&companys=14,8");
//            String json =  HttpClient4.doGet(" http://fenxi.zgzcw.com/export/2402899/bjop");
//
//            JSONArray array = (JSONArray) JSONArray.parse(json);
//            for (int i = 0; i < array.size() ; i++) {
//                JSONObject object = (JSONObject) array.get(i);
//                JSONArray listOdds = object.getJSONArray("listOdds");
//                for (int j = 0; j < listOdds.size() ; j++) {
//                    JSONObject odd = (JSONObject) listOdds.get(j);
//                    System.out.println(object.getString("LEAGUE_NAME_SIMPLY") + "  " +
//                            object.getString("HOST_NAME") + "  " +
//                            object.getString("GUEST_NAME") + "  " +
//                            odd.getString("FIRST_WIN") + "  " +
//                            odd.getString("FIRST_SAME") + "  " +
//                            odd.getString("FIRST_LOST") + "  ");
//                }
//
//
//            }
//
//            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
//        }





//    }


    public static void main(String[] args) {
        downLoadFromUrl("http://fenxi.zgzcw.com/export/2402899/bjop");
    }


    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @throws IOException
     */
    public static String downLoadFromUrl(String urlStr) {
        try {

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 得到输入流
            InputStream inputStream = conn.getInputStream();
            Sheet sheet;
            Workbook book = Workbook.getWorkbook(inputStream);
            sheet = book.getSheet(0);
            Cell cell1, cell2, cell3, cell4, cell5;
            JSONArray array = new JSONArray();

            for (int i = 1; i < sheet.getRows(); i++) {
                //获取每一行的单元格
                cell1 = sheet.getCell(0, i);//（列，行）
                cell2 = sheet.getCell(1, i);
                cell3 = sheet.getCell(2, i);
                cell4 = sheet.getCell(3, i);
                cell5 = sheet.getCell(4, i);
                if ("".equals(cell1.getContents())) {//如果读取的数据为空
                    break;
                }
                JSONObject object = new JSONObject();
                object.put("ID",cell1.getContents());
                object.put("编号",cell2.getContents());
                object.put("姓名",cell3.getContents());
                object.put("数量",cell4.getContents());
                object.put("住址",cell5.getContents());
                array.add(object);
            }

//            fos.write(getData);
//            if (fos != null) {
//                fos.close();
//            }
//            if (inputStream != null) {
//                inputStream.close();
//            }
            // System.out.println("info:"+url+" download success");
            return "嘿嘿";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

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

}