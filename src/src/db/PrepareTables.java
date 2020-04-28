/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author Bhupender Dagar
 */
public class PrepareTables {
    private final static String[] delim=new String[]{"<staff>","<course>","<subjects>","<login>","<student>","<sattendance>"};
    public static void craeteAllDBTables() {
        try {
            BufferedReader br=new BufferedReader(new FileReader("lib//tables.txt"));
            if(isCreationRequired(br)) {
                br=new BufferedReader(new FileReader("lib//tables.txt"));
                String query=br.readLine();
                Connection con=src.db.DButil.getConnection();
                
                while(query!=null) {
                    System.out.println(query);
                    PreparedStatement ps=con.prepareStatement(query);
                    ps.executeUpdate();
                    query=br.readLine();
                }
                PreparedStatement ps=con.prepareStatement("insert into login values('admin','admin','admin')");
                ps.executeUpdate();
                PrintStream pss=new PrintStream("lib//tables.txt");
                pss.println("<done>");
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    
    private static boolean isCreationRequired(BufferedReader br) throws IOException {
        String line=br.readLine();
        while(line.indexOf("<done>")>-1) {
            line=br.readLine();
            return false;
        }
        return true;
    }
    public static void main(String gh[]) {
        craeteAllDBTables();
    }
}
