package cc.textMessage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jsmsengine.COutgoingMessage;
import org.jsmsengine.CService;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author suhas
 */
public class FlushMessageDaemon extends CService implements Runnable {

    public static final int MESSAGE_SENT = 0;

    Connection con = null;

    public static void main(String[] args) {
        new FlushMessageDaemon();
    }

    public FlushMessageDaemon() {
        super("COM1", 9600);
        Thread t = new Thread(this);
        t.start();

    }

    @Override
    public void run() {
        int status;

        try {
            this.initialize();
            //Set the cache directory.
            this.setCacheDir(".\\");
            //Connect to GSM device.
            status = this.connect();
            //	Did we connect ok?
            if (status == CService.ERR_OK) {
                //	Set the operation mode to PDU
                //this.setOperationMode(1);
                // Set the SMSC number (set to default).
                this.setSmscNumber("");
                this.setReceiveMode(CService.RECEIVE_MODE_ASYNC);
                flushQueue(status);
            } else {
                System.out.println("Connection to mobile failed, error: " + status);
                JOptionPane.showMessageDialog(null, "Error while connecting to Com port", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "" + e, "Exception", JOptionPane.ERROR_MESSAGE);
            this.disconnect();
            e.printStackTrace();
        }
    }

    private Connection getDBConnection() throws Exception {

        System.out.println("Database connection");
        Class.forName("com.mysql.jdbc.Driver");
        String db_url = "jdbc:mysql://localhost/cc";
        return DriverManager.getConnection(db_url, "root", "mysql");

    }

    public void flushQueue(int deviceStatus) {
        if (deviceStatus == CService.ERR_OK) {
            System.out.println("Flush message from message_queue table "
                    + "expected table structure [mq_id,mq_message,mq_mob_num,mq_created_datetime,mq_sent_datetime,mq_is_sent]");
            System.out.println("Message Daemon status : message_daemon_status(status)");
            List<Integer> mqIdList = new ArrayList<Integer>();

            try {
                con = getDBConnection();
                PreparedStatement statusStmt = con.prepareStatement("select status from message_daemon_status");
                PreparedStatement queStmt = con.prepareStatement("select * from message_queue where mq_is_sent=0");
                PreparedStatement queUpdateStmt = con.prepareStatement("update message_queue "
                        + "SET mq_is_sent = 1 , mq_sent_datetime = NOW() WHERE mq_id = ?");

                ResultSet rsStatus = statusStmt.executeQuery();
                if (rsStatus.first()) {
                    while (rsStatus.getInt(1) == 1) {
                        ResultSet rsMQ = queStmt.executeQuery();
                        mqIdList.clear();

                        while (rsMQ.next()) {
                            String mes = rsMQ.getString("mq_message");
                            String num = rsMQ.getString("mq_mob_num");
                            if (num == null || num.trim().length() < 10) {
                                continue;
                            }

                            num = num.length() == 10 ? "+91" + num.trim() : num.trim();

                            if (sendMessage(mes, num)) {
                                mqIdList.add(rsMQ.getInt("mq_id"));
                            }
                        }

                        if (mqIdList.size() > 0) {
                            for (Integer mqId : mqIdList) {
                                queUpdateStmt.setInt(1, mqId);
                                queUpdateStmt.executeUpdate();
                            }
                        }

                        Thread.sleep(3000);
                        rsStatus = statusStmt.executeQuery();
                        rsStatus.first();

                    }
                }

                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex1) {
                        Logger.getLogger(FlushMessageDaemon.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }

                stopReading();

            } catch (Exception ex) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex1) {
                        Logger.getLogger(FlushMessageDaemon.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
                stopReading();
            }

        }

        System.exit(0);
    }

    public void stopReading() {
        try {
            this.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean sendMessage(String message, String phoneNumber) {

        System.out.println("......8.....send sms" + message);

        int sentStatus = this.sendMessage(new COutgoingMessage(phoneNumber, message));
        System.out.println("........9..........send sms" + phoneNumber);

        if (sentStatus < 0) {
            for (int l = 0; l < 5 && sentStatus < 0; l++) {
                System.out.println("........10..........send sms" + sentStatus);
                sentStatus = this.sendMessage(new COutgoingMessage(phoneNumber, message));
                System.out.println("........11..........send sms" + sentStatus);
            }
        }

        if (sentStatus < 0) {
            System.out.println("Error : Unable send the message ");
            System.out.println("Message is ::" + message);
            System.out.println("Telephone No ::" + phoneNumber);
            return false;
        } else {
            System.out.println("........12.....Message sent sucssesfully ");
            System.out.println(".........13..........Message is ::" + message);
            System.out.println("........14.........Telephone No ::" + phoneNumber);
            return true;
        }

    }

}
