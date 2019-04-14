package cc.base;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.zip.*;

import org.apache.log4j.Logger;

public final class UFile {

    static final int BUFFER_SIZE = 2048;

    private static Logger logger = Logger.getLogger(UFile.class);

    public static void createDirectory(String dirName) 
	throws UBaseException {

	File dirFile = new File(dirName);

	if(!dirFile.exists()) {
		
	    // Target directory does not exist. Create it.
	    logger.debug("Creating directory '" + dirName + "'...");

	    if(!dirFile.mkdirs()) {
		throw new UBaseException("Failed to create '" + dirName + "'");
	    }

	    logger.debug("Done");
	}
    }

    public static void deleteDirectory(String dirName) {

	File dirFile = new File(dirName);

	logger.debug("Deleting '" + dirName + "'");

	if(!dirFile.exists()) {
	    dirFile.delete();
	}
    }

    public static void saveFileUncompressed(GZIPInputStream gzipInp, String localFilePath) 
	throws UBaseException {

	try {
	    FileOutputStream fos = new FileOutputStream(localFilePath);

            int count;
            byte data[] = new byte[BUFFER_SIZE];

            BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);

            while ((count = gzipInp.read(data, 0, BUFFER_SIZE)) != -1) {
               dest.write(data, 0, count);
            }

            dest.flush();
            dest.close();

	} catch (IOException e) {
	    throw new UBaseException("Failed to save file to " + localFilePath + " : " + e.getMessage(), e);
	}
    }
    
    public static void writeURLOutputToFile(String filePath, Vector urlVector,String reportHeader,String htmlHeader,String htmlFooter,
                String generatedDatetime) throws UServletException{
        try{
            //Construct URL from 
            // URL urlPage = new URL("http://localhost:8084/medicsEnterprise/DynReportsMasterServlet?command=viewReport&reportRID=8&filterRID=15&autoLogin=true&userName=ubqadmin&password=ubqadmin@nova&customerID=130.47.47.1&locationRID=6&writeToFile=true");
           // BufferedWriter out = new BufferedWriter(new FileWriter("d:\\test123.xls"));            
            URL urlPage;
            HttpURLConnection conn;
            BufferedReader br;
            String tmpLine;
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));  
            if(htmlHeader != null){
                out.write(htmlHeader);	
                out.newLine();
            }  
            
            if(reportHeader != null){
                out.newLine();
                out.write("<H2>"+ reportHeader +"</H2>");	
                out.newLine();
                if(!"".equals(generatedDatetime)){
                    out.write("<b>Generated on: </b>" + generatedDatetime);
                    out.write("<br><br>");
                    out.newLine();
                }
            }  
            for(int i = 0; i < urlVector.size(); i++){
                
                urlPage = new URL((String)urlVector.get(i));
                conn = (HttpURLConnection)urlPage.openConnection();
                conn.connect();
                
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                tmpLine = br.readLine();
                while(tmpLine != null){
                    out.write(tmpLine);	
                    out.newLine();
                    tmpLine = br.readLine();
                }
                out.newLine();
                out.newLine();
            }
            if(htmlFooter != null){
                out.write(htmlFooter);	
                out.newLine();
            } 
            out.close();
            
        } catch(Exception e){
            throw new UServletException("Error in writting the report to file: " + e.getMessage(), e) ;
        }
    }
    
    
}
