/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learn.test.learning;

import com.hankcs.hanlp.HanLP;
import com.learn.test.tools.Connections;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author X.H.Yang
 */
public class NewsKey {

    public static void main(String[] args) {
        try {
            Connections cons = new Connections();
            String sql = "SELECT id, title, info FROM stang_tbid WHERE pubtime = '2017-10-16' and label = 0 and author != '中国西藏新闻网'GROUP BY title";
//            String sql = "SELECT id, title, info FROM stang_tbid WHERE pubtime = '2017-10-10' and id = 13241";
            Connection con = cons.createCon_252();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = null;
            rs = ps.executeQuery();
            int total = 0;
            String[] keyWord = {"工程", "隧道","车道", "安装", "高铁", "施工", "盾构机","水利枢纽", "水利", "地铁", "PPP", "项目",  "改造", "基础设施", "总投资", "中标", "铁路", "铁路局", "列车", "水电", "建筑", "机场", "炼油", "石化", "招标代理机构", "招标", "建设工程", "设计", "勘察", "线路", "建设", "整治", "中铁", "交通", "高速公路", "道路", "省道", "公路", "垮塌", "山体", "桥隧", "动车组", "轨道", "高架", "跑道", "空管", "海绵", "城市", "路段", "运营", "京沪", "客运", "建成", "开工", "公里", "国企", "通车", "开建", "投资", "基建", "出资", "里程", "修建", "大桥", "桥梁", "轨道交通", "水运", "批复", "规划",  "政策", "中国", "航空", "一带","一路", "丝绸之路", "航天", "政府采购", "政府", "采购"};
            while (rs.next()) {
                String title = rs.getNString("title");
                String info = rs.getNString("info");
                String content = title + "。" + info;
                Document doc = Jsoup.parse(content);
                content = doc.text();
                //去除文中的所有空格。
                Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(content);
                matcher = pattern.matcher(content);
                content = matcher.replaceAll("");
                //去除&nbsp;
                content = content.replaceAll("&nbsp", "");
                Pattern p1 = Pattern.compile("\\s*|t|r|n");
                Matcher m1 = p1.matcher(content);
                content = m1.replaceAll("");
                content = content.replaceAll("\\s*", "");
                content = content.trim();
                List<String> keywordList = HanLP.extractKeyword(content, 5);

                int index = 0;
                out:
                for (String word : keywordList) {
                    for (String key : keyWord) {
                        if (word.contains(key)) {
                            index++;
                        }
                    }
                }
                if (index >= 2) {
                    System.out.println(title);
                    String update = "UPDATE stang_tbid SET label=1 WHERE id =?";
                    PreparedStatement psupdate = con.prepareStatement(update);
                    psupdate.setInt(1, rs.getInt("id"));
                    int result = psupdate.executeUpdate();
                    total++;
                }
            }
            System.out.println(total);
            con.close();

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}
