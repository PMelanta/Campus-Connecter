/*
 * UFileUpload.java
 *
 * Created on September 20, 2006, 12:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cc.util;

import cc.base.URequestContext;
import cc.base.UConfig;
import cc.base.UFile;
import cc.base.UBaseException;
import cc.base.UDBAccessException;
import cc.base.UServletException;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.* ;

/**
 *
 * @author Gopinath
 */
public final class UFileUpload {
  private static Logger logger = Logger.getLogger(UFileUpload.class);
  //private static Hashtable ctxtHashElems = null ;
  
  /** Creates a new instance of UFileUpload */
  public UFileUpload() {
  }
  
  public static void uploadFiles(String uploadDirectory, URequestContext ctxt, Vector ctxtHashElems)
  throws UServletException{
    try{
      String uploadedPath = null ;
      // Create a factory for disk-based file items
      FileItemFactory factory = new DiskFileItemFactory();

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);

      // Parse the request
      List items = upload.parseRequest(ctxt.getHttpRequest());
      
      // Process the uploaded items
      Iterator iter = items.iterator();
      
      while (iter.hasNext()) {
          FileItem item = (FileItem) iter.next();
          if (item.isFormField()) {
              //processFormField(item);
            logger.debug(item.getFieldName() + " - " + item.getString()) ;
            UCtxtElement nextElem = new UCtxtElement(item.getFieldName(),item.getString()) ;
            ctxtHashElems.add(nextElem) ;            
          } else {

		    if(item.getName() == null || "".equals(item.getName().trim()))
			continue;

		    File tempFile = new File(item.getName().trim()) ;
              
              String fileName = tempFile.getName() ;
		    if(!"".equals(fileName)) {
              uploadedPath = uploadDirectory + fileName ;
              
              File uploadedFile = new File(uploadedPath) ;
              
              item.write(uploadedFile);
              
              logger.debug(item.getContentType()) ;
              
              UCtxtElement nextElem = new UCtxtElement(item.getFieldName(),uploadedPath) ;
              ctxtHashElems.add(nextElem) ;
              
              UCtxtElement nextElem1 = new UCtxtElement("content_type",item.getContentType()) ;
              ctxtHashElems.add(nextElem1) ;
              
              //logger.debug(String.valueOf(item.getSize())) ;
          }
               
		}
      }
      //return ctxtHashElems ;
    }catch(Exception e){
      e.printStackTrace() ;
      throw new UServletException("Unable to upload files", e) ;
    }
  } 

    public static void uploadFiles(String uploadDirectory, URequestContext ctxt, HashMap ctxtHashElems,long sizeLimit)
	throws UBaseException {
	try{
	    String uploadedPath = null ;
            String filesName = null;
            String filesSize = null;
	    // Create a factory for disk-based file items
	    FileItemFactory factory = new DiskFileItemFactory();

	    // Create a new file upload handler
	    ServletFileUpload upload = new ServletFileUpload(factory);

	    // Parse the request
	    List items = upload.parseRequest(ctxt.getHttpRequest());
      
	    // Process the uploaded items
	    Iterator iter = items.iterator();
            
            String fieldValue = "" ;
      
	    while (iter.hasNext()) {
		FileItem item = (FileItem) iter.next();

		if (item.isFormField()) {
                    fieldValue = item.getString() ;                    
                    if(ctxtHashElems.get(item.getFieldName()) != null) {
                          fieldValue = ctxtHashElems.get(item.getFieldName()) + "`" + item.getString() ;
                        }
                    
		    ctxtHashElems.put(item.getFieldName(),fieldValue);       
		} else {

		    if(item.getName() == null || "".equals(item.getName().trim()))
			continue;

		    File tempFile = new File(item.getName()) ;                  
		    String fileName = tempFile.getName() ;

                    if(!"".equals(fileName)) {
                      
			// Make sure the directory exists
			UFile.createDirectory(uploadDirectory);

			uploadedPath = uploadDirectory + "/" + fileName ;

                        File uploadedFile = new File(uploadedPath) ;
                        item.write(uploadedFile);

			if(sizeLimit > 0) {
			    float fileSize = uploadedFile.length()/1000000;

			    if(fileSize > sizeLimit) {
				// Remove the file
				uploadedFile.delete();
				throw new UBaseException("File size should not be more than " + String.valueOf(sizeLimit) +  " MB");
			    }
			}

			logger.debug("File element name = " + item.getFieldName() + 
				     ", uploaded path = " + uploadedPath);
                        logger.debug("File content type : " + item.getContentType()) ;
                        String uploadedFilePathNames = uploadedPath;
                        String contentTypes = item.getContentType();
                        filesName = fileName;
                        filesSize = String.valueOf(uploadedFile.length());
                        
                        if(ctxtHashElems.get(item.getFieldName()) != null) {
                          uploadedFilePathNames = ctxtHashElems.get(item.getFieldName()) + "`" + uploadedPath;
                          contentTypes = ctxtHashElems.get(item.getFieldName()+ "ContentType") + "`" + contentTypes;
                          filesName = ctxtHashElems.get(item.getFieldName()+ "Name") + "`" + filesName;
                          filesSize = ctxtHashElems.get(item.getFieldName()+ "Size") + "`" + filesSize;
                        }
                        ctxtHashElems.put(item.getFieldName(),uploadedFilePathNames);                       
                        ctxtHashElems.put(item.getFieldName() + "ContentType", contentTypes);                    
                        ctxtHashElems.put(item.getFieldName() + "Name", filesName);                    
                        ctxtHashElems.put(item.getFieldName() + "Size", filesSize);                    
		    }
                }
            }

	} catch(Exception e) {
	    throw new UBaseException(e.getMessage(), e) ;
	}
    } 
    
    
    
     public static Vector getFiles(URequestContext ctxt, HashMap ctxtHashElems,long sizeLimit)
	throws UBaseException {
	try{           
            Vector files = new Vector();
	    String uploadedPath = null ;
	    // Create a factory for disk-based file items
	    FileItemFactory factory = new DiskFileItemFactory();

	    // Create a new file upload handler
	    ServletFileUpload upload = new ServletFileUpload(factory);

	    // Parse the request
	    List items = upload.parseRequest(ctxt.getHttpRequest());
      
	    // Process the uploaded items
	    Iterator iter = items.iterator();
            
            String fieldValue = "" ;
      
	    while (iter.hasNext()) {
		FileItem item = (FileItem) iter.next();

		if (item.isFormField()) {
                    fieldValue = item.getString() ;		    
		    logger.debug(item.getFieldName() + " - " + item.getString());
                    
                    if(ctxtHashElems.get(item.getFieldName()) != null) {
                          fieldValue = ctxtHashElems.get(item.getFieldName()) + "~" + item.getString() ;
                        }
                    
		    ctxtHashElems.put(item.getFieldName(),fieldValue);       
		} else {
                                        
		    if(item.getName() == null || "".equals(item.getName().trim()))
			continue;

		    File tempFile = new File(item.getName()) ;
                   // if(!tempFile.exists()) {
                   //     throw new UBaseException("File not found");
                   // }

		    String fileName = tempFile.getName() ;

                    if(!"".equals(fileName)) {
                      
			// Make sure the directory exists
			//UFile.createDirectory(uploadDirectory);

			//uploadedPath = uploadDirectory + "/" + fileName ;

                        //File uploadedFile = new File(uploadedPath) ;
                        //item.write(uploadedFile);
                        files.add(tempFile);                                                

			if(sizeLimit > 0) {
			    float fileSize = tempFile.length()/1000000;

			    if(fileSize > sizeLimit) {

				// Remove the file
				tempFile.delete();
                                files.removeElement(tempFile);

				throw new UBaseException("File size should not be more than 1 MB");
			    }
			}	
                        //String uploadedFilePathNames = uploadedPath;
                        //String contentTypes = item.getContentType();
                        /*
                        if(ctxtHashElems.get(item.getFieldName()) != null) {
                          uploadedFilePathNames = ctxtHashElems.get(item.getFieldName()) + "~" + uploadedPath;
                          contentTypes = ctxtHashElems.get(item.getFieldName()+ "ContentType") + "~" + contentTypes;                        
                        }
                        ctxtHashElems.put(item.getFieldName(),uploadedFilePathNames);                       
                        ctxtHashElems.put(item.getFieldName() + "ContentType", contentTypes);
                    */
		    }
                }
            }
            return files;

	} catch(Exception e) {
	    throw new UBaseException(e.getMessage(), e) ;
	}
    }
     
    public static UFileUploadContext uploadFile(URequestContext ctxt, String childDirName, long sizeLimit)
    throws UDBAccessException {
        try {            
            HashMap ctxtElems = new HashMap();            
            String currentDateTemp = UDate.nowDBString();            
            String pathToCreate = null;
            UFileUploadContext myCtxt = new UFileUploadContext(ctxt, ctxtElems);
            Random generator = new Random();
            int randomNo = generator.nextInt();
            String defaultPath = UConfig.getParameterValue(1,"ATTACHEMENT_PATH","C:/MedicsDir");
            pathToCreate = defaultPath + "/" + childDirName + "/" + randomNo;
            (new File(pathToCreate)).mkdirs();
            UFileUpload.uploadFiles(pathToCreate + "/", ctxt, ctxtElems, sizeLimit);            

            /*added by suhas. need to check this. i have added this coz there is no other place to get this if we are using BO framework*/
            myCtxt.setAttribute("uploadedFilesPath", ctxtElems.get("file"));
            myCtxt.setAttribute("uploadedFilesContentType", ctxtElems.get("fileContentType"));
            /*end :suhas*/

            return myCtxt;
        } catch(Exception e) {
            throw new UDBAccessException(e.getMessage(),e);
        }
    }


}