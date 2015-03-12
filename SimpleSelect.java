import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class SimpleSelect {
  public static void main(String[] args) {
    String output = "\n";
     try{
      // load the driver
      //Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      Class.forName("org.sqlite.JDBC");
      // get a connection to the database through the data source
     // Connection con = DriverManager.getConnection("jdbc:odbc:Worker");       
      Connection con = DriverManager.getConnection("jdbc:sqlite:sample.db");
      // create a statement for query
      Statement stmt = con.createStatement();
      //Statement stmt2 = con2.createStatement();
      // execute the query and get resultset
      ResultSet rs = stmt.executeQuery("Select * from Worker");
      // get column information
      
      ResultSetMetaData rsmd = rs.getMetaData();
      int nCols = rsmd.getColumnCount();
      for ( int i=1 ; i<=nCols ; i++ ){
        if (rsmd.getColumnType(i)==java.sql.Types.VARCHAR)
            output += String.format(" %-6s", rsmd.getColumnLabel(i));
        else output += String.format("%4s ",rsmd.getColumnLabel(i)) ;
      }
      // get row information
    
      for (output+="\n" ; rs.next() ; output+='\n'){
        /*
        String cmd = "insert into  Worker values("
                +rs.getInt(1)+","+
                "'"+rs.getString(2)+"',"
                +rs.getInt(3) +","
                +rs.getInt(4) +","
                +rs.getFloat(5)+","
                +rs.getFloat(6)+")";
        stmt2.executeUpdate(cmd);
        System.out.println(cmd);
        */
        for ( int i=1 ; i<=nCols ; i++ ){          
          System.out.println(rsmd.getColumnType(i));
          switch (rsmd.getColumnType(i)){
              
              case java.sql.Types.REAL:
              case java.sql.Types.NUMERIC:
              case java.sql.Types.FLOAT:              
                   float f = rs.getFloat(i);
                   if (f>1000)  output +=String.format("%6.0f",f);
                   else output+=String.format("%6.2f",f);
                   break;
              case java.sql.Types.VARCHAR:
                   output += String.format("%-6s ", rs.getString(i));
                   break;
              case java.sql.Types.INTEGER:
                   output += String.format("%4d ", rs.getInt(i));
                break;
              default:                        
                   output += String.format("%8s", rs.getString(i));
          }
          
        }
      }
      rs.close();
      stmt.close();
      //stmt2.close();
      con.close();
      //con2.close();
      } catch (ClassNotFoundException e){
        System.err.println("driver error");
        System.exit(1);
      } catch (SQLException e){          // display error info, 
        System.err.println("sql error"); // multiple objects can be 
        for (; e!=null; e = e.getNextException()) // linked.
          System.err.println("SQL State "+e.getSQLState()+
           " Message "+e.getMessage()+" Vendor "+ e.getErrorCode());
      }
    System.out.println(output);
    JTextArea jta = new JTextArea(output);
    jta.setFont(new Font("Consolas", Font.BOLD, 24));
    jta.setTabSize(9);
    JOptionPane.showMessageDialog(null,jta);
  }
}