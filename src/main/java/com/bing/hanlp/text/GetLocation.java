/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bing.hanlp.text;

import com.learn.test.tools.AreaConstant;
import com.learn.test.tools.AreaContant;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author X.H.Yang
 */
public class GetLocation {

    private Connection conn;

    private static String[][][] lists = {AreaContant.list, AreaConstant.list2};
    private static final List<String[]> all = new ArrayList<>();

    public GetLocation() {
        for (String[] lis : AreaContant.list) {
            all.add(lis);
        }
        for (String[] lis : AreaConstant.list2) {
            all.add(lis);
        }
        //连数据库
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.1.252:3306/suidaobig?zeroDateTimeBehavior=convertToNull&characterEncoding=utf8&allowMultiQueries=true", "root", "123asd123asd");
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GetLocation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public HashMap<String, Object> getRealAreaId(String str) throws SQLException {
        HashMap<String, Object> map = new HashMap<>();
        String[] prov = null;
        String[] city = null;
        String[] country = null;
        String pp = "", cc = "", co = "";
        int city_id = 0;
        int area_id = 1;
        String location = str;
        int id = 0;
        double longitude = 0, latitude = 0;
        for (String[] lis : all) {
            if ("1".equals(lis[3])) {
                String address = lis[2];
                address = address.replace("省", "").replace("自治区", "");
                str = str.replace("省", "").replace("自治区", "");
                Pattern s = Pattern.compile(address);
                Matcher e = s.matcher(str);
                int l = address.length();
                if (e.find() && l > 1 && !address.equals("中国")) {
                    area_id = Integer.valueOf(lis[0]);
                    prov = lis;
                    location = str.replace(address, "");
                    id = Integer.valueOf(lis[0]);
                }
            }

            if ("2".equals(lis[3])) {
                String address = lis[2];
                address = address.replace("市", "").replace("区", "").replace("自治区", "");
                location = location.replace("市", "").replace("区", "").replace("自治区", "");
                Pattern s = Pattern.compile(address);
                Matcher e = s.matcher(location);
                int l = address.length();
                if ((e.find()) && l > 1) {
                    city = lis;
                    city_id = Integer.valueOf(lis[0]);
                    location = location.replace(address, "");
                    id = Integer.valueOf(lis[0]);
                }
            }

            if ("3".equals(lis[3])) {
                String address = lis[2];
                address = address.replace("县", "").replace("自治县", "").replace("区", "").replace("市", "");
                location = location.replace("县", "").replace("自治县", "").replace("区", "").replace("市", "");
                Pattern s = Pattern.compile(address);
                Matcher e = s.matcher(location);
                int l = address.length();
                if ((e.find()) && l > 1) {
                    country = lis;
                    id = Integer.valueOf(lis[0]);
                    break;
                }
            }
        }

        if (country != null) {
            co = country[2];
            city = all.get(Integer.valueOf(country[1]) - 1);
            city_id = Integer.valueOf(country[1]);
            cc = city[2];
            prov = all.get(Integer.valueOf(city[1]) - 1);
            pp = prov[2];
            area_id = Integer.valueOf(prov[0]);

        }
        if (city != null) {
            cc = city[2];
            prov = all.get(Integer.valueOf(city[1]) - 1);
            pp = prov[2];
            area_id = Integer.valueOf(prov[0]);
        }
        if (prov != null) {
            pp = prov[2];
        }
        
        if(id != 0){
             String sql = "select longitude,  latitude from stang_area where id=?";
            PreparedStatement pstmt = null;
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
            } catch (SQLException ex) {
                Logger.getLogger(GetLocation.class.getName()).log(Level.SEVERE, null, ex);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                longitude = rs.getDouble("longitude");
                latitude = rs.getDouble("latitude");
            }
        }
        map.put("area_id", area_id);
        map.put("city_id", city_id);
        map.put("address", pp + cc + co);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        return map;
    }

    public static void main(String[] args) throws SQLException {
        GetLocation get = new GetLocation();
        get.getRealAreaId("长乐要驾驶员两名13859961041");

    }
}
