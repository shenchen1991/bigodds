package com.shenchen.odds.util;

import redis.clients.jedis.Jedis;


public class RedisJava {
    public static void main(String[] args) {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        jedis.set("out","1773205,1867229,1770900,1765954,1765952,1768236,1770752,1840168,1770883,1770910,1770885,1770751,1770837,1770756,1770842,1770790,1773003,1770849,1770877,1770847,1770583,1770583,1834107," +
                "1834099," +
                "1770663," +
                "1766677," +
                "1766693," +
                "1767915," +
                "1770721,1781856,1768222,1842309,1766706,1770720,1770844");
        String values = jedis.get("out");
    }
}