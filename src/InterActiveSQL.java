// Copyright (c) 2005-2006 Ramiro Villarreal  All rights reserved.

import javax.swing.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class InterActiveSQL extends JFrame {

    Connection con;
    JTable tbl;
    JPanel ConnectPanel;
    static String op[] = {"Ok", "Cancel"};
    String query;
    JTextField jtf;

    public void connect(String url, String driver) {

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("SQL error");
            System.exit(1);
        }
    }

    public InterActiveSQL(String url, String driver) {
        super("InteractiveSQL");
        query = "Select * from Worker";
        connect(url, driver);

    //Things toDo
        //1.Insert update queries
        //2.connect buttons to work
        // Connection Panel for Dialog
        ConnectPanel = new JPanel();
    // null for layout means we do the layout manually
        // only do this if you know exactly how u want it to look
        ConnectPanel.setLayout(null);
        JLabel jl;

        ConnectPanel.add(jl = new JLabel("Data Source"));
        jl.setBounds(0, 0, 76, 24);

        final JTextField jtfurl = new JTextField(url);
        ConnectPanel.add(jtfurl);
        jtfurl.setBounds(84, 0, 200, 24);

        final JTextField jtfdriver = new JTextField(driver);
        ConnectPanel.add(jl = new JLabel("Driver"));
        jl.setBounds(0, 24, 80, 24);
        ConnectPanel.add(jtfdriver);
        jtfdriver.setBounds(84, 24, 200, 24);
    // even though JComponent has a setSize method that is 
        // not what is used to set the size;
        ConnectPanel.setPreferredSize(new Dimension(300, 72));

        // Top Panel - a panel at the top to hold the buttons
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createLineBorder(Color.black));
    // gridbagLayout is the toughest, usually avoided, but we 
        // need at least one example

        GridBagLayout gb = new GridBagLayout();
        jp.setLayout(gb);
        GridBagConstraints c = new GridBagConstraints();
    // Connect Button
        // add the component as usual, but then set the constraints
        // so the layout knows what to do with it
        JButton jb = new JButton("Connect");
        c.weightx = 0.0;
        gb.setConstraints(jb, c);
        jp.add(jb);
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showOptionDialog(InterActiveSQL.this,
                        ConnectPanel, "Set connection",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null, op, op[0]) == 0) {
                    connect(jtfurl.getText().trim(),
                            jtfdriver.getText().trim());
                }
            }
        });
        // textfield
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        jtf = new JTextField(query, 28);
        jtf.setFont(jtf.getFont().deriveFont(Font.BOLD, 12));
        gb.setConstraints(jtf, c);
        jp.add(jtf);
        jtf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setTable(jtf.getText().trim());
            }
        });

        //Help Buton
        jb = new JButton("Help");
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        gb.setConstraints(jb, c);
        jp.add(jb);

        jb.addActionListener((ActionEvent e) -> new MetalworksHelp());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    con.close();
                } catch (SQLException sqle) {
                    System.err.println("sql error in shutdown");
                }
                System.exit(0);
            }
        });

    //getContentPane().add(jp);
        //  the WindowListener is needed to make sure we
        //  shut down the connection
        getContentPane().add(jp, BorderLayout.NORTH);
        setBounds(100, 100, 500, 300);
        setVisible(true);
    }

    public void setTable(String q) {
    //query = q;
        // remove the old table. For a lot of records,
        // we would not do this, but i'm keeping it simple
        if (tbl != null) {
            getContentPane().remove(1);
        }
        Vector<Vector> rows = new Vector<Vector>();
        Vector<String> cols = new Vector<String>();
        try {
            Statement stmt = con.createStatement();
      // submit query - not all queries return resultsets
            // but select does

            if (!(q.trim().toLowerCase().startsWith("select")));
            {
                stmt.executeUpdate(q);
                q = query;
            }
            query = q;

            ResultSet rs = stmt.executeQuery(query);

            // metaData gives info about database fields
            ResultSetMetaData rsmd = rs.getMetaData();
            // get Column info
            int nCols = rsmd.getColumnCount();
            for (int i = 1; i <= nCols; i++) {
                cols.add(rsmd.getColumnLabel(i));
            }
            // get Row info - table info
            while (rs.next()) {
                Vector<String> row = new Vector<String>();
                for (int i = 1; i <= nCols; i++) {
                    if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) //              if(i==5)
                    {
                        row.addElement(String.format("%10.2f", rs.getFloat(i)));
                    } //              else row.addElement(String.format("%10.0f",rs.getFloat(i)));
                    else {
                        row.addElement(rs.getString(i));
                    }
                }
                rows.addElement(row);
            }
            // close resultset and statement
            rs.close();
            stmt.close();
        } catch (SQLException sqle) {
            // display error info, multiple objjects can be linked together.
            for (; sqle != null; sqle = sqle.getNextException()) {
                System.err.println("SQL State:" + sqle.getSQLState()
                        + " Vendor:" + sqle.getErrorCode()
                        + "\nMessage:" + sqle.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // create the table and add to scroll pane
        tbl = new JTable(rows, cols);

        tbl.setFont(tbl.getFont().deriveFont(Font.BOLD, 16));
        tbl.setRowHeight(20);
        getContentPane().add(new JScrollPane(tbl));
        validate();
    }

    public static void main(String[] args) {
        String driver = new String("org.sqlite.JDBC");//sun.jdbc.odbc.JdbcOdbcDriver");
        new InterActiveSQL("jdbc:sqlite:sample.db", driver);
        //new MetalworksHelp();
    }
}
