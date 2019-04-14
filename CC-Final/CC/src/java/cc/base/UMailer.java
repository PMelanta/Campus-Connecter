/*
 * UMailer.java
 *
 */
package cc.base;

import cc.util.UDate;
import java.io.*;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;


import javax.mail.*;
import javax.mail.internet.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import java.sql.*;

import java.util.*;
import java.text.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


/**
 *
 * @author suhas
 */
public final class UMailer {

   private static Logger logger = Logger.getLogger(UMailer.class);
   private static String smtpHost = null;
   private static String mailId = null;
   private static String mailPasswd = null;

   public static void sendMail(URequestContext ctxt, String from, String to, String subject, String messageText)
           throws UDBAccessException, SQLException {

      String sql = "insert into u_mail_queue (mq_subject, mq_from, mq_to, mq_body, mq_post_date, "
              + " mq_post_time) values ("
              + "'" + subject + "', '" + from + "', '" + to + "', '" + messageText + "', '" + UDate.nowDBString() + "', "
              + "'" + UDate.currentTime() + "')";

      UQueryEngine qe = ctxt.getQueryEngine();

      qe.executeInsert(sql);
   }

   public static void sendMail(URequestContext ctxt, String from, String to, String subject, String messageText, String contentType)
           throws UDBAccessException, SQLException {

      String sql = "insert into u_mail_queue (mq_subject, mq_from, mq_to, mq_body, mq_post_date, "
              + " mq_post_time,mq_content_type) values ("
              + "'" + subject + "', '" + from + "', '" + to + "', '" + messageText + "', '" + UDate.nowDBString() + "', "
              + "'" + UDate.currentTime() + "','" + contentType + "')";

      UQueryEngine qe = ctxt.getQueryEngine();

      qe.executeInsert(sql);
   }

   public static void doSend(Session session, String from, String to, String subject, String messageText, String conType)
           throws UBaseException {

      try {
         // Create a MimeMessage from the session. 
         MimeMessage message = new MimeMessage(session);

         //: Set the from field of the message. 
         message.setFrom(new InternetAddress(from));


         // Set the to field of the message. 
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set the subject of the message. 
         message.setSubject(subject);

         if (null != conType && !"".equals(conType.trim())) {//html
            message.setContent(messageText, conType);
         } else {
            // Set the content of the message. 
            message.setText(messageText);
         }
         //: Use a Transport to send the message. 
         Transport.send(message);

         logger.info("Mail sent from " + from + " to " + to + " : Subject - " + subject);
      } catch (Exception e) {

         String msg = "Failed to send mail from " + from + " to " + to + " : Subject - "
                 + subject + ": " + e.getMessage();

         logger.error(msg);

         throw new UBaseException(msg, e);
      }
   }

   public static void doSendWithAttachment(Session session, String from, String to, String subject, String messageText,
           String fileName, String fileNamewithPath)
           throws UBaseException {

      try {
         // Create a MimeMessage from the session. 
         MimeMessage message = new MimeMessage(session);

         //: Set the from field of the message. 
         message.setFrom(new InternetAddress(from));


         // Set the to field of the message. 
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set the subject of the message. 
         message.setSubject(subject);

         // create the message part 
         MimeBodyPart messageBodyPart = new MimeBodyPart();

         //fill message
         messageBodyPart.setText(messageText);

         Multipart multipart = new MimeMultipart();

         multipart.addBodyPart(messageBodyPart);

         // Attachment file
         messageBodyPart = new MimeBodyPart();

         DataSource source = new FileDataSource(fileNamewithPath);

         messageBodyPart.setDataHandler(new DataHandler(source));

         messageBodyPart.setFileName(fileName);

         multipart.addBodyPart(messageBodyPart);

         // Put parts in message
         message.setContent(multipart);

         //: Use a Transport to send the message. 
         Transport.send(message);

         logger.info("Mail sent from " + from + " to " + to + " : Subject - " + subject);
      } catch (Exception e) {

         String msg = "Failed to send mail from " + from + " to " + to + " : Subject - "
                 + subject + ": " + e.getMessage();

         logger.error(msg);

         throw new UBaseException(msg, e);
      }
   }

   public static void flushMailQueue()
           throws UBaseException {
      UQueryEngine qe = new UQueryEngine();
      try {
         final String hostName = UConfig.getParameterValue(0, "SMTP_HOST");
         final String username = UConfig.getParameterValue(0, "DFM_APP_EMAIL_ID");
         final String password = UConfig.getParameterValue(0, "DFM_APP_EMAIL_PASSWD");

         Properties props = new Properties();
         props.put("mail.smtp.auth", "true");
         props.put("mail.smtp.starttls.enable", "true");
         props.put("mail.smtp.host", hostName);
         props.put("mail.smtp.port", "587");

         Session session = Session.getInstance(props,
                 new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                       return new PasswordAuthentication(username, password);
                    }
                 });
         // Get unsent mails and try to send them out

         String sql = "select * from u_mail_queue where mq_sent = 0";

         // Use the silent version of executeQuery so that we don't
         // fill up the log file with the default DEBUG traces
         ResultSet rs = qe.silentExecuteQuery(sql);
         int attachFile = 0;
         while (rs.next()) {
            try {
               attachFile = rs.getInt("mq_is_attachment_exist");
               if (attachFile == 0) {
                  doSend(session, rs.getString("mq_from"), rs.getString("mq_to"),
                          rs.getString("mq_subject"), rs.getString("mq_body"), rs.getString("mq_content_type"));
               } else {
                  doSendWithAttachment(session, rs.getString("mq_from"), rs.getString("mq_to"),
                          rs.getString("mq_subject"), rs.getString("mq_body"), rs.getString("mq_file_name"), rs.getString("mq_file_name_with_path"));
               }
            } catch (Exception e) {
               sql = "update u_mail_queue set mq_no_of_iteration= mq_no_of_iteration + 1 "
                       + " where mq_rid = " + rs.getInt("mq_rid");
               qe.executeUpdate(sql);
               // Error has already been reported. Continue with the rest of the mails.
               continue;
            }
            // Mark message as sent
            sql = "update u_mail_queue set "
                    + "mq_sent = 1, " + "mq_sent_date = '" + UDate.nowDBString() + "', "
                    + "mq_sent_time = CURRENT_TIME "
                    + "where mq_rid = " + rs.getInt("mq_rid");

            qe.executeUpdate(sql);
         }
      } catch (Exception e) {

         String msg = "Failed while flushing mail queue: " + e.getMessage();

         logger.error(msg);

         throw new UBaseException(msg, e);
      } finally {
         if (qe != null) {
            qe.close();
         }
      }
   }
   
   public static void flushMailQueue(UQueryEngine qe)
           throws UBaseException {
        if(null == qe){
      qe = new UQueryEngine();}
      try {
         final String hostName = UConfig.getParameterValue(0, "SMTP_HOST");
         final String username = UConfig.getParameterValue(0, "DFM_APP_EMAIL_ID");
         final String password = UConfig.getParameterValue(0, "DFM_APP_EMAIL_PASSWD");

         Properties props = new Properties();
         props.put("mail.smtp.auth", "true");
         props.put("mail.smtp.starttls.enable", "true");
         props.put("mail.smtp.host", hostName);
         props.put("mail.smtp.port", "587");

         Session session = Session.getInstance(props,
                 new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                       return new PasswordAuthentication(username, password);
                    }
                 });
         // Get unsent mails and try to send them out

         String sql = "select * from u_mail_queue where mq_sent = 0";

         // Use the silent version of executeQuery so that we don't
         // fill up the log file with the default DEBUG traces
         ResultSet rs = qe.silentExecuteQuery(sql);
         int attachFile = 0;
         while (rs.next()) {
            try {
               attachFile = rs.getInt("mq_is_attachment_exist");
               if (attachFile == 0) {
                  doSend(session, username, rs.getString("mq_to"),
                          rs.getString("mq_subject"), rs.getString("mq_body"), rs.getString("mq_content_type"));
               } else {
                  doSendWithAttachment(session, rs.getString("mq_from"), rs.getString("mq_to"),
                          rs.getString("mq_subject"), rs.getString("mq_body"), rs.getString("mq_file_name"), rs.getString("mq_file_name_with_path"));
               }
            } catch (Exception e) {
               sql = "update u_mail_queue set mq_no_of_iteration= mq_no_of_iteration + 1 "
                       + " where mq_rid = " + rs.getInt("mq_rid");
               qe.executeUpdate(sql);
               // Error has already been reported. Continue with the rest of the mails.
               continue;
            }
            // Mark message as sent
            sql = "update u_mail_queue set "
                    + "mq_sent = 1, " + "mq_sent_date = '" + UDate.nowDBString() + "', "
                    + "mq_sent_time = CURRENT_TIME "
                    + "where mq_rid = " + rs.getInt("mq_rid");

            qe.executeUpdate(sql);
         }
      } catch (Exception e) {

         String msg = "Failed while flushing mail queue: " + e.getMessage();

         logger.error(msg);

         throw new UBaseException(msg, e);
      } finally {
         if (qe != null) {
            qe.close();
         }
      }
   }

   public static void sendMail(UQueryEngine qe, String from, String to, String subject, String messageText, String attachFileName, String attachFilewithPath)
           throws UDBAccessException, SQLException {
      int attachmentExist = 0;
      if (!"".equals(attachFilewithPath)) {
         attachmentExist = 1;
      }
      String sql = "insert into u_mail_queue (mq_subject, mq_from, mq_to, mq_body, mq_post_date, "
              + " mq_post_time, mq_file_name, mq_file_name_with_path, mq_is_attachment_exist) values ("
              + "'" + subject + "', '" + from + "', '" + to + "', '" + messageText + "', '" + UDate.nowDBString() + "', "
              + "'" + UDate.currentTime() + "','" + attachFileName + "','" + attachFilewithPath + "'," + attachmentExist + ")";

      qe.executeInsert(sql);
   }

   public static void sendMail(String from, String to, String subject, String messageText, String attachFileName, String attachFilewithPath)
           throws UDBAccessException {
      UQueryEngine qe = new UQueryEngine();
      try {
         int attachmentExist = 0;
         if (!"".equals(attachFilewithPath)) {
            attachmentExist = 1;
         }
         String sql = "insert into u_mail_queue (mq_subject, mq_from, mq_to, mq_body, mq_post_date, "
                 + " mq_post_time, mq_file_name, mq_file_name_with_path, mq_is_attachment_exist) values ("
                 + "'" + subject + "', '" + from + "', '" + to + "', '" + messageText + "', '" + UDate.nowDBString() + "', "
                 + "'" + UDate.currentTime() + "','" + attachFileName + "','" + attachFilewithPath + "'," + attachmentExist + ")";

         qe.executeInsert(sql);

      } catch (Exception ex) {
         throw new UDBAccessException(ex.toString(), ex);
      } finally {
         if (qe != null) {
            qe.close();
         }
      }
   }
}
