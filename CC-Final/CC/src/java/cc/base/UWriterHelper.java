package cc.base;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.util.zip.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public final class UWriterHelper {

   static Logger logger = Logger.getLogger(UWriterHelper.class);
   static final int BUFFER_SIZE = 2048;
   public static final int OK = 200;
   public static final int NO_CONTENT = 204;
   public static final int BAD_REQUEST = 400;
   public static final int INTERNAL_ERROR = 500;

   public static void writeSelect(URequestContext ctxt, ResultSet rs, String valueAttr, String nameAttr)
           throws UDBAccessException, UBaseException {

      boolean isEmptyValueRequried = true;
      writeSelect(ctxt, rs, valueAttr, nameAttr, isEmptyValueRequried);
   }

   public static void writeSelect(URequestContext ctxt, ResultSet rs, String valueAttr, String nameAttr, boolean isEmptyValueRequried)
           throws UDBAccessException, UBaseException {

      try {

         HttpServletResponse res = ctxt.getHttpResponse();

         res.setContentType("text/html");

         PrintWriter out = res.getWriter();

         out.println("<select>");
         if (isEmptyValueRequried) {
            out.println("<option value=\"0\">&nbsp;</option>");
         }

         if (rs != null && rs.first()) {
            rs.beforeFirst();
            while (rs.next()) {
               out.println("<option value =\"" + rs.getString(valueAttr) + "\">" + rs.getString(nameAttr) + "</option>");
            }
         }

         out.println("</select>");
      } catch (IOException e) {
         throw new UBaseException("Failed to write SELECT to output stream", e);
      } catch (SQLException e) {
         throw new UDBAccessException(e.getMessage(), e);
      }
   }
   
   public static void writeSelect(URequestContext ctxt, ResultSet rs, String valueAttr, String nameAttr,String selName, boolean isEmptyValueRequried)
           throws UDBAccessException, UBaseException {

      try {

         HttpServletResponse res = ctxt.getHttpResponse();

         res.setContentType("text/html");

         PrintWriter out = res.getWriter();

         out.println("<select>");
         if (isEmptyValueRequried) {
            out.println("<option value=\"0\">All</option>");
         }

         if (rs != null && rs.first()) {
            rs.beforeFirst();
            while (rs.next()) {
               out.println("<option value =\"" + rs.getString(valueAttr) + "\">" + rs.getString(nameAttr) + "</option>");
            }
         }

         out.println("</select>");
      } catch (IOException e) {
         throw new UBaseException("Failed to write SELECT to output stream", e);
      } catch (SQLException e) {
         throw new UDBAccessException(e.getMessage(), e);
      }
   }

   public static void writeSelect(URequestContext ctxt, ResultSet rs, String valueAttr, String nameAttr, String idAttr)
           throws UDBAccessException, UBaseException {

      try {

         HttpServletResponse res = ctxt.getHttpResponse();

         res.setContentType("text/html");

         PrintWriter out = res.getWriter();

         out.println("<select>");
         out.println("<option value=\"0\">&nbsp;</option>");

         if (rs != null) {

            while (rs.next()) {
               out.println("<option value =\"" + rs.getString(valueAttr) + "\" id =\"" + rs.getString(idAttr) + "\" > " + rs.getString(nameAttr) + "</option>");
            }
         }

         out.println("</select>");
      } catch (IOException e) {
         throw new UBaseException("Failed to write SELECT to output stream", e);
      } catch (SQLException e) {
         throw new UDBAccessException(e.getMessage(), e);
      }
   }

   public static void writeSelect(URequestContext ctxt, Vector<String[]> valueArray, boolean blankOption)
           throws UDBAccessException, UBaseException {
      try {
         HttpServletResponse res = ctxt.getHttpResponse();
         res.setContentType("text/html");

         PrintWriter out = res.getWriter();
         out.println("<select>");
         if (blankOption) {
            out.println("<option value=\"0\">&nbsp;</option>");
         }
         for (int i = 0; i < valueArray.size(); i++) {
            String[] optionDetails = valueArray.get(i);
            out.print("<option value=\"" + optionDetails[0] + "\">" + optionDetails[1] + "</option>");
         }
         out.println("</select>");

      } catch (Exception e) {
         throw new UBaseException("Failed to write SELECT to output stream", e);
      }
   }

   public static void writeSelect(URequestContext ctxt, String[] valArray)
           throws UDBAccessException, UBaseException {

      try {

         HttpServletResponse res = ctxt.getHttpResponse();
         res.setContentType("text/html");
         PrintWriter out = res.getWriter();

         out.println("<select>");
         out.println("<option value=\"0\">&nbsp;</option>");
         if (valArray != null) {
            for (int i = 0; i < valArray.length; i++) {
               out.println("<option value =\"" + valArray[i] + "\">" + valArray[i] + "</option>");
            }
         }

         out.println("</select>");
      } catch (IOException e) {
         throw new UBaseException("Failed to write SELECT to output stream", e);
      }
   }

   public static void writeSelect(URequestContext ctxt, String selectName, String onChangeFun, int startingValue, int endingValue)
           throws UDBAccessException, UBaseException {

      try {
         HttpServletResponse res = ctxt.getHttpResponse();
         res.setContentType("text/html");
         PrintWriter out = res.getWriter();
         if (null != onChangeFun) {
            out.println("<select id='" + selectName + "' name = '" + selectName
                    + "' onchange='" + onChangeFun + "'>");
         } else {
            out.println("<select id='" + selectName + "' name = '" + selectName + "'>");
         }
         if(startingValue == 0){
            out.println("<option value='" + startingValue + "'>All</option>");
            startingValue = 1;
         }
         for (int i = startingValue; i <= endingValue; i++) {
            out.println("<option value='" + i + "'>" + i + "</option>");
         }
         out.println("</select>");
      } catch (IOException e) {
         throw new UBaseException("Failed to write SELECT to output stream", e);
      }
   }

   public static void writeString(HttpServletResponse response, String str)
           throws UBaseException {

      try {

         response.setContentType("text/html");

         PrintWriter out = response.getWriter();

         out.println(str);
      } catch (IOException e) {
         throw new UBaseException("Failed to write STRING to output stream", e);
      }
   }

   public static void writeXML(HttpServletResponse response, String str)
           throws UBaseException {
      try {
         response.setContentType("text/xml");
         PrintWriter out = response.getWriter();
         out.print(str);
      } catch (IOException e) {
         throw new UBaseException("Failed to write XML to output", e);
      }
   }

   public static void writeXML(URequestContext ctxt, String str)
           throws UBaseException {
      writeXML(ctxt.getHttpResponse(), str);
   }

   public static void writeString(URequestContext ctxt, String str)
           throws UServletException {
       
       try{
      writeString(ctxt.getHttpResponse(), str);
       }catch(Exception ex){
           throw new UServletException(ex);
       }
   }

   public static void writeFile(URequestContext ctxt, String contextType, String filePath)
           throws UBaseException {

      try {
         File file = new File(filePath);

         if (!file.exists()) {
            throw new UBaseException("File does not exist");
         }

         //declared here only to make visible to finally clause
         BufferedReader input = null;
         FileReader fileReader = null;

         try {
            //use buffering, reading one line at a time
            // FileReader always assumes default encoding is OK!
            fileReader = new FileReader(file);
            input = new BufferedReader(fileReader);
            String line = null; //not declared within while loop

            HttpServletResponse response = ctxt.getHttpResponse();

            response.setContentType(contextType);

            PrintWriter out = response.getWriter();

            while ((line = input.readLine()) != null) {
               out.println(line);
            }
         } catch (Exception e) {
            throw new UBaseException("Failed to write file : " + e.getMessage(), e);
         } finally {
            try {
               if (fileReader != null) {
                  fileReader.close();
               }

               if (input != null) {
                  input.close();
               }
            } catch (IOException ex) {
               throw new UBaseException("Failed to close input readers : " + ex.getMessage(), ex);
            }
         }
      } catch (Exception e) {
         throw new UBaseException(e.getMessage(), e);
      }
   }

   public static void writeFile(URequestContext ctxt, String contentType, String filePath, boolean compress)
           throws UBaseException {

      File file = new File(filePath);

      if (!file.exists()) {
         throw new UBaseException("File does not exist");
      }

      String fileName = file.getName();

      BufferedInputStream input = null;
      FileInputStream fileInput = null;

      try {
         fileInput = new FileInputStream(filePath);
         input = new BufferedInputStream(fileInput, BUFFER_SIZE);

         HttpServletResponse response = ctxt.getHttpResponse();

         response.setContentType(contentType);

         OutputStream out = null;

         if (compress) {
            out = (OutputStream) new GZIPOutputStream(response.getOutputStream());
         } else {
            out = response.getOutputStream();
         }

         int count;
         byte data[] = new byte[BUFFER_SIZE];

         response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

         while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
            out.write(data, 0, count);
         }

         out.flush();
         out.close();

      } catch (Exception e) {
         throw new UBaseException("Failed to write compressed file : " + e.getMessage(), e);
      } finally {
         try {
            if (fileInput != null) {
               fileInput.close();
            }

            if (input != null) {
               input.close();
            }
         } catch (IOException ex) {
            throw new UBaseException("Failed to close input streams : " + ex.getMessage(), ex);
         }
      }
   }

   public static void sendResponse(URequestContext ctxt, String txt)
           throws UServletException {
      try {
         HttpServletResponse res = ctxt.getHttpResponse();
         PrintWriter out = res.getWriter();

         logger.debug("Response = " + txt);
         out.print(txt);
      } catch (Exception e) {
         throw new UServletException("Failed to write to output stream", e);
      }
   }

   public static void sendResponse(URequestContext ctxt, int statusCode, String message)
           throws UBaseException {

      try {
         HttpServletResponse response = ctxt.getHttpResponse();

         response.sendError(statusCode, message);
      } catch (IOException e) {
         throw new UBaseException("Failed to send error : " + e.getMessage(), e);
      }
   }
}
