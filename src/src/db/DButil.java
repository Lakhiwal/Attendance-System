/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bhupender Dagar
 */
public class DButil {
    private static Connection con=null;
    public static Connection getConnection() {
        try {
            if(con==null) {
                BufferedReader br=new BufferedReader(new FileReader("lib//DBCredential.txt"));
                String user=br.readLine();
                String pwd=br.readLine();
                String url=br.readLine();
                con=DriverManager.getConnection(url, user, pwd);
                System.out.println("Connection States="+con);
            }
            return con;
        }
        catch(Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
    public static boolean checkLogin(String user, String pwd) {
        try {
            PreparedStatement ps=getPreparedStatement("select username from login where username=? and password=?");
            ps.setString(1,user);
            ps.setString(2,pwd);
            return ps.executeQuery().next();
        }
        catch(Exception e) {
            System.out.println(e);
            return false;
        }
    }
    
    public static DefaultTableModel getStudentData(String sclass, String sem) {
        try {
            PreparedStatement ps=getPreparedStatement("select rollno, name from student where class=? and sem=?");
            ps.setString(1,sclass);
            ps.setString(2,sem);
            ResultSet rs= ps.executeQuery();
            ResultSetMetaData rsmd=rs.getMetaData();
            DefaultTableModel model= new DefaultTableModel(){
                public Class<?> getColumnClass(int column) {
                    switch(column) {
                        case 0: return String.class;
                        case 1: return String.class;
                        case 2: return Boolean.class;
                        default:return String.class;
                    }
                }   
            };
            model.addColumn("RollNo");
            model.addColumn("Name");
            model.addColumn("Attendance");
            int i=0;
            while(rs.next()) {
               model.addRow(new Object[i]);
               model.setValueAt(rs.getString("rollno"), i, 0);
               model.setValueAt(rs.getString("name"), i, 1);
               model.setValueAt(false, i, 2);
               i++;
            }
            return model;
        }
        catch(Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
    public static void selectAll(boolean status, DefaultTableModel model) {
        try {
            for(int i=0;i<model.getRowCount();i++) {
                model.setValueAt(status, i, 2);
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    public static int generateId(String sqlquery) {
       try {
            Connection con=getConnection();
            PreparedStatement ps=getPreparedStatement(sqlquery);
            ResultSet rs=ps.executeQuery();
            rs.next();
            return rs.getInt(1)+1;
        }
        catch(Exception e) {
            System.out.println(e);
            return 0;
        } 
    }
    public static Map<String, String> addNewStaff(Map<String, Object> staffMap) {
        Map<String, String> resMap=resMap=new HashMap<String, String>();
        try {
            Connection con=getConnection();
            String sqlquery="select id from staff where email=?";
            PreparedStatement ps=getPreparedStatement(sqlquery);
            ps.setString(1, (String)staffMap.get("email"));
            ResultSet rs=ps.executeQuery();
            if(rs.next()) {
                resMap.put("exists", "yes");
                return resMap;
            }
            sqlquery="insert into staff(id,name,doj,sal,desig,address,phone,email) values(?,?,?,?,?,?,?,?)";
            ps=getPreparedStatement(sqlquery);
            ps.setInt(1, Integer.valueOf(staffMap.get("id").toString()));
            ps.setString(2, (String)staffMap.get("name"));
            ps.setString(3, (String)staffMap.get("doj"));
            ps.setFloat(4, Float.valueOf(staffMap.get("sal").toString()));
            ps.setString(5, (String)staffMap.get("desig"));
            ps.setString(6, (String)staffMap.get("add"));
            ps.setString(7, (String)staffMap.get("phone"));
            ps.setString(8, (String)staffMap.get("email"));
            ps.executeUpdate();
            String res=insertLoginDetail((String)staffMap.get("email"), "Staff");
            resMap.put("pwd", res);
        }
        catch(Exception e) {
            System.out.println(e);
            resMap.put("error", e.toString());
        }
        return resMap;
    }
    private static String insertLoginDetail(String user, String type)throws Exception {
        try {
            String sqlquery="insert into login(username, password, type) values(?,?,?)";
            PreparedStatement ps=getPreparedStatement(sqlquery);
            String pwd=getPassword();
            ps.setString(1, user);
            ps.setString(2,pwd);
            ps.setString(3, type);
            ps.executeUpdate();
            return pwd;
        }
        catch(Exception e) {
            System.out.println(e);
            throw new Exception();
        } 
    }
    public static void markAttendance(Set<String> presentRollnos, String sub, String date) {
        try {
            Connection con=getConnection();
            String sqlquery="insert into sattendance values(?,?,?,?)";
            PreparedStatement ps=getPreparedStatement(sqlquery);
            for(String roll:presentRollnos) {
                ps.setString(1, roll);
                ps.setString(2, date);
                ps.setString(3, sub);
                ps.setString(4, "P");
                ps.addBatch();
            }
            ps.executeBatch();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    public static int insertCourse(String cid, String cname, String ts, String duration) {
        try {
            Connection con=getConnection();
            String sqlquery="insert into course values(?,?,?,?)";
            PreparedStatement ps=getPreparedStatement(sqlquery);
            ps.setString(1, cid);
            ps.setString(2, cname);
            ps.setString(3, ts);
            ps.setString(4, duration);
            return ps.executeUpdate();
        }
        catch(Exception e) {
            System.out.println(e);
            return 0;
        }
    }
    public static int insertSubject(String sid, String cid, String sname, String sem) {
        try {
            Connection con=getConnection();
            String sqlquery="insert into subjects values(?,?,?,?)";
            PreparedStatement ps=getPreparedStatement(sqlquery);
            ps.setString(1, sid);
            ps.setString(2, cid);
            ps.setString(3, sname);
            ps.setString(4, sem);
            return ps.executeUpdate();
        }
        catch(Exception e) {
            System.out.println(e);
            return 0;
        }
    }
    private static PreparedStatement getPreparedStatement(String sqlQuery) {
       try {
            Connection con=getConnection();
            return con.prepareStatement(sqlQuery);
        }
        catch(Exception e) {
            System.out.println(e);
            return null;
        } 
    }
    public static Map<String, String> getCourseNames() {
        Map<String, String> resMap=new HashMap<String, String>();
       try {
            PreparedStatement ps=getPreparedStatement("select cname, id from course");
            ResultSet rs=ps.executeQuery();
            while(rs.next()) {
                System.out.println(rs.getString("cname"));
                resMap.put(rs.getString("cname"), rs.getString("id"));
            }
        }
        catch(Exception e) {
            System.out.println(e);
        } 
        return resMap;
    }
    private static String getPassword() {
        final String STR="012345678901abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb=new StringBuilder("");
        final int LEN=6;
        int i=0;
        while(i<LEN) {
            int randomNum=(int)(Math.random()*((STR.length()-1)-0)+1);
            sb.append(STR.charAt(randomNum));
            i++;
        }
        return sb.toString();
    }
    /*Testing*/
    public static void main(String gh[]) {
        //System.out.println(generateId());
    }
}
