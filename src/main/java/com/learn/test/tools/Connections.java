/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learn.test.tools;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Connections {
     protected  Connection conn;
     
    public  Connection createCon_253() throws ClassNotFoundException, SQLException{
         Class.forName("com.mysql.jdbc.Driver");
         conn= DriverManager.getConnection(Constant.MySql_253.URL,Constant.MySql_253.USERNAME,Constant.MySql_253.PASSWORD);
         conn.setAutoCommit(true);
         System.out.println("253连接建立成功。");
         return conn;
    }
    /**
     * 
     * @return
     * @throws ClassNotFoundException 
     */
    public Connection  createCon_252() throws ClassNotFoundException{
        try{
         Class.forName("com.mysql.jdbc.Driver");
          conn= DriverManager.getConnection(Constant.MySql.URL,Constant.MySql.USERNAME,Constant.MySql.PASSWORD);
          conn.setAutoCommit(true);
          System.out.println("连接建立成功。");
          return conn;
        }catch(ClassNotFoundException cf){
            cf.getMessage();
            return null;
            
        } catch (SQLException ex) {
             Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
               return null;
         }
        
    }
    
     public Connection  createCon_253_1() throws ClassNotFoundException{
        try{
         Class.forName("com.mysql.jdbc.Driver");
          conn= DriverManager.getConnection(Constant.MySql_253.URL1,Constant.MySql_253.USERNAME,Constant.MySql_253.PASSWORD);
          conn.setAutoCommit(true);
          System.out.println("连接建立成功。");
          return conn;
        }catch(ClassNotFoundException cf){
            cf.getMessage();
            return null;
            
        } catch (SQLException ex) {
             Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
               return null;
         }
        
    }
     
      public Connection  createCon_local() throws ClassNotFoundException{
        try{
         Class.forName("com.mysql.jdbc.Driver");
          conn= DriverManager.getConnection(Constant.LocalMySql.URL,Constant.LocalMySql.USERNAME,Constant.LocalMySql.PASSWORD);
          conn.setAutoCommit(true);
          System.out.println("本地连接建立成功。");
          return conn;
        }catch(ClassNotFoundException cf){
            cf.getMessage();
            return null;
            
        } catch (SQLException ex) {
             Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
               return null;
         }
        
    }
}



