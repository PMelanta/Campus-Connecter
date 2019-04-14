/*
 * AttachmentManager.java
 *
 * Created on October 27, 2009, 10:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cc.user;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import cc.base.UDBAccessException;
import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.util.UDate;
import cc.util.UString;
import org.apache.log4j.Logger;
import cc.base.UConfig;
import cc.base.UFile;
import cc.util.UFileCompress;
import cc.util.UFileUpload;
import cc.util.UFileUploadContext;

/**
 *
 * @author suhas
 */
public class FileManager {
    public static final String FILE_BROWSER_CONTEXT = "FILE_BROWSER";
    public static final String DISCUSSION_FORUM_CONTEXT = "DISCUSSION_FORUM";
    public static final String NEW_PROJECT_CONTEXT = "NEW_PROJECT";
    public static final String SEND_MESSAGE_CONTEXT = "SEND_MESSAGE";

    static Logger logger = Logger.getLogger(FileManager.class);
    
    private static String childDirRids = "-1";

    
    /** Creates a new instance of AttachmentManager */
    public FileManager() {
    }

   

   

    public static UFileUploadContext uploadFile(URequestContext ctxt, String leafLevelFolderName)
            throws UDBAccessException {
        try {

            HashMap ctxtElems = new HashMap();
            //Vector ctxtElems = new Vector();
            String currentDateTemp = UDate.nowDBString();
            long sizeLimit = 10;
            String pathToCreate = null;
            UFileUploadContext myCtxt = new UFileUploadContext(ctxt, ctxtElems);
            //int userRID = profileRID;
            Random generator = new Random();
            int randomNo = generator.nextInt();
            String defaultPath = UConfig.getParameterValue(1, "ATTACHEMENT_PATH", "C:/ATTACHEMENTS");
            pathToCreate = defaultPath + "/" + leafLevelFolderName + "/" + randomNo;
            (new File(pathToCreate)).mkdirs();
            UFileUpload.uploadFiles(pathToCreate + "/", ctxt, ctxtElems, sizeLimit);
            myCtxt.setAttribute("uploadedFilesPath", ctxtElems.get("file"));
            myCtxt.setAttribute("uploadedFilesContentType", ctxtElems.get("fileContentType"));
            myCtxt.setAttribute("uploadedFilesName", ctxtElems.get("fileName"));
            myCtxt.setAttribute("uploadedFilesSize", ctxtElems.get("fileSize"));
            return myCtxt;

        } catch (Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }
    
    public static UFileUploadContext uploadAvatar(URequestContext ctxt)
            throws UDBAccessException {
        try {

            HashMap ctxtElems = new HashMap();
            //Vector ctxtElems = new Vector();
            String currentDateTemp = UDate.nowDBString();
            long sizeLimit = 10;
            String pathToCreate = null;
            UFileUploadContext myCtxt = new UFileUploadContext(ctxt, ctxtElems);
            String defaultPath = UConfig.getParameterValue(0, "PROFILE_PIC_PATH", "c:/users");
            pathToCreate = defaultPath;
            (new File(pathToCreate)).mkdirs();
            UFileUpload.uploadFiles(pathToCreate + "/", ctxt, ctxtElems, sizeLimit);
            myCtxt.setAttribute("uploadedFilesPath", ctxtElems.get("file"));
            myCtxt.setAttribute("uploadedFilesContentType", ctxtElems.get("fileContentType"));
            myCtxt.setAttribute("uploadedFilesName", ctxtElems.get("fileName"));
            myCtxt.setAttribute("uploadedFilesSize", ctxtElems.get("fileSize"));
            return myCtxt;

        } catch (Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }

    public static boolean deleteFile(URequestContext ctxt, String fileName)
            throws UDBAccessException {
        try {
            boolean success = false;
            File f = new File(fileName);
            if (fileName != null && f.exists()) {
                success = (new File(fileName)).delete();
                fileName = fileName.substring(0, fileName.indexOf("//"));
                File files[] = new File(fileName).listFiles();
                if (files.length == 0) {
                    success = (new File(fileName)).delete();
                }
            }
            return success;
        } catch (Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }
    
    public static synchronized void  deleteFolder(URequestContext ctxt, int dirRid) 
                throws UDBAccessException,Exception{
        String strDirRidsToDelete = "0";
        String sql = "select dir_parent_rid from directory_details where dir_rid = "+dirRid;
        ResultSet rsRootCheck = ctxt.getQueryEngine().executeQuery(sql);
        if(null != rsRootCheck && rsRootCheck.next()){
            if(rsRootCheck.getInt("dir_parent_rid") == 0 ){
                throw new Exception("Can not delete root folder");
            }
            strDirRidsToDelete = getDirRidsOfChildDirs(dirRid,ctxt.getQueryEngine());
        }

        childDirRids = "0";//reset global variable
        
        strDirRidsToDelete = String.valueOf(dirRid) + "," + strDirRidsToDelete;
        
        sql = "SELECT file_path FROM files WHERE file_dir_rid IN("+strDirRidsToDelete+")";
        
        ResultSet rsFilesToDelete = ctxt.getQueryEngine().executeQuery(sql);
        
        while(null != rsFilesToDelete && rsFilesToDelete.next()){
            deleteFile(ctxt, rsFilesToDelete.getString("file_path"));
        }
        
        //clear table entries
        
        sql = "delete FROM files where file_dir_rid IN("+strDirRidsToDelete+")";
        ctxt.getQueryEngine().executeUpdate(sql);
        
        sql = "delete FROM directory_details where dir_rid IN("+strDirRidsToDelete+")";
        ctxt.getQueryEngine().executeUpdate(sql);
        
    }
    
    public static String getDirRidsOfChildDirs(int dirRid,UQueryEngine qe) 
                throws UDBAccessException,Exception{
        String sql = "SELECT dir_rid FROM directory_details WHERE dir_parent_rid = "+dirRid;
        
        ResultSet rsChildDirRids = qe.executeQuery(sql);
        if(null == rsChildDirRids || !rsChildDirRids.first())
            return "-1";
        rsChildDirRids.beforeFirst();
        
        while(rsChildDirRids.next()){
            childDirRids = childDirRids+","+rsChildDirRids.getString("dir_rid");
            getDirRidsOfChildDirs(rsChildDirRids.getInt("dir_rid"),qe);
            
        }
        return childDirRids;
        
    }


    public static void recordUploadDetails(URequestContext ctxt)
            throws UDBAccessException, SQLException {
        UQueryEngine qe = ctxt.getQueryEngine();
        String sql = "";
        try {
            qe.beginTransaction();

            String uploadedFilesPath = (String) ctxt.getAttribute("uploadedFilesPath");
            String uploadedFilesContentType = (String) ctxt.getAttribute("uploadedFilesContentType");
            String uploadedFilesName = (String) ctxt.getAttribute("uploadedFilesName");
            String uploadedFilesSize = (String) ctxt.getAttribute("uploadedFilesSize");
            int uploadDirRid = ctxt.getIntParameter("uploadDirRid");

            if (uploadedFilesPath == null || uploadedFilesContentType == null
                    || "".equals(uploadedFilesPath.trim()) || "".equals(uploadedFilesContentType.trim())) {
                //nothing to upload, just return
                return;
            }

            int contextRID = ctxt.getIntParameter("contextRID");

            if (contextRID == 0) {
                contextRID = ctxt.getAttribute("contextRID") != null ? Integer.parseInt(ctxt.getAttribute("contextRID").toString()) : 0;
            }
            String contextType = ctxt.getParameter("contextType");

            if ("".equals(contextType.trim())) {
                throw new Exception("Upload context type can not be 0");
            }



            String[] fileDesc = ctxt.getParameterValues("fileDesc");

            String[] uploadedFilesPathArr = uploadedFilesPath.split("`");
            String[] uploadedFilesContentTypeArr = uploadedFilesContentType.split("`");
            String[] uploadedFilesNameArr = uploadedFilesName.split("`");
            String[] uploadedFilesSizeArr = uploadedFilesSize.split("`");
            String[] fileDescription = ctxt.getParameterValues("fileDescription");

//            int uploadDirRid = ctxt.getIntParameter("uploadDirRid");
//
//            if (uploadDirRid == 0) {
//                String uploadDirStr = ctxt.getAttribute("uploadDirRid") == null ? "0" : ctxt.getAttribute("uploadDirRid").toString();
//                uploadDirRid = Integer.parseInt(uploadDirStr);
//            }
//
//            if (uploadDirRid == 0) {
//                String newDirPath = ctxt.getParameter("dirPath");
//                uploadDirRid = createNewDirectory(ctxt, newDirPath);
//            }

            int projectRid = ctxt.getProjectRID();
            for (int i = 0; i < uploadedFilesNameArr.length; i++) {
                //record upload details
                sql = "INSERT INTO files (file_project_rid, file_context_rid, file_context_type, file_name, file_path,"
                        + "file_size,file_dir_rid,file_content_type, file_user_rid,"
                        + "file_created_datetime,file_last_modified_datetime, file_upload_date,file_desc,file_remarks,file_hidden)"
                        + "VALUES(" + projectRid + ", " + contextRID + ",'" + contextType + "','" + uploadedFilesNameArr[i] + "', '" + uploadedFilesPathArr[i]
                        + "'," + uploadedFilesSizeArr[i] + ", " + uploadDirRid + ",'" + uploadedFilesContentTypeArr[i] + "'," + ctxt.getUserRID()
                        + ", NOW(),NOW(),CURRENT_DATE, '" + fileDescription[i] + "', '', 0)";
                qe.executeInsert(sql);
            }

            qe.commitTransaction();

        } catch (Exception e) {
            qe.rollbackTransaction();
            throw new UDBAccessException("Unable to record upload details", e);
        }
    }

    public static int createNewDirectory(URequestContext ctxt, String newDirPath)
            throws UDBAccessException, SQLException {
        if (null == newDirPath || "".equals(newDirPath.trim())) {
            return 0;
        }
        UQueryEngine qe = ctxt.getQueryEngine();
        int dirRid = 0;
        int dirLevel = 0;
        String sql = "";
        
        newDirPath.replaceAll("//", "/");
        newDirPath.replaceAll("%20", " ");
        String dirList[] = newDirPath.split("/");

        for (int i = 0; i < dirList.length; i++) {
            dirLevel = i + 1; //dir level starts with 1 and will be increamented by 1 for each level of directory hierarchy, this is used to avoid duplicate directory name in the same hierarchy 

            sql = "select dir_rid from directory_details where dir_name = '" + dirList[i].trim()
                    + "' and dir_level = " + dirLevel + " and dir_project_rid = " + ctxt.getProjectRID() + ""
                    + " and dir_parent_rid = "+dirRid;

            ResultSet rsDirRid = qe.executeQuery(sql);

            if (null != rsDirRid && rsDirRid.first()) {
                dirRid = rsDirRid.getInt("dir_rid");
            } else {
                sql = "INSERT INTO directory_details (dir_name, dir_level, dir_project_rid, dir_user_rid, "
                        + "dir_created_datetime, dir_last_modified_datetime,"
                        + "dir_row_invalidated, dir_hidden, dir_is_write_protected, dir_parent_rid)"
                        + "VALUES('" + dirList[i].trim() + "'," + dirLevel + "," + ctxt.getProjectRID() + ","
                        + ctxt.getUserID() + ", NOW(), NOW(),0, 0, 0, " + dirRid + ")";

                dirRid = qe.executeInsert(sql);
            }
        }
        return dirRid;
    }
    /* receives new directory name. creates it under parent direcory*/
    
    public static int createNewDirectory(URequestContext ctxt, int parentDirRid,String newDirName)
            throws UDBAccessException, SQLException,Exception {
        if (null == newDirName || "".equals(newDirName.trim())) {
            return -1;
        }
        UQueryEngine qe = ctxt.getQueryEngine();
        int newDirRid = 0;
        int dirLevel = 0;
        String sql = "";
        
        newDirName.replaceAll("%20", " "); //

            sql = "select dir_level from directory_details where dir_rid = " + parentDirRid
                    + " and dir_project_rid = " + ctxt.getProjectRID();

            ResultSet rsDirRid = qe.executeQuery(sql);

            if (null != rsDirRid && rsDirRid.first()) {
                dirLevel= rsDirRid.getInt("dir_level") + 1;
            } else {
                throw new Exception("Invalid parent directory");
            }
            
            sql = "select 1 from directory_details where dir_name = '" + newDirName.trim()
                    + "' and dir_level = "+ dirLevel +" and dir_parent_rid = "+parentDirRid+" and dir_project_rid = " + ctxt.getProjectRID();  //check for duplicates
            ResultSet rsduplicateDir = qe.executeQuery(sql);

            if (null != rsduplicateDir && rsduplicateDir.first()) {
                throw new Exception("directory already exists with the name "+newDirName);
            }
            
                
            sql = "INSERT INTO directory_details (dir_name, dir_level, dir_project_rid, dir_user_rid, "
                        + "dir_created_datetime, dir_last_modified_datetime,"
                        + "dir_row_invalidated, dir_hidden, dir_is_write_protected, dir_parent_rid)"
                        + "VALUES('" + newDirName + "'," + dirLevel + "," + ctxt.getProjectRID() + ","
                        + ctxt.getUserRID() + ", NOW(), NOW(),0, 0, 0, " + parentDirRid + ")";

            newDirRid = qe.executeInsert(sql);
                
        return newDirRid;
    }
    
    public static int createParentDirectory(URequestContext ctxt,String newDirName,int projectRid)
            throws UDBAccessException, SQLException,Exception {
        if (null == newDirName || "".equals(newDirName.trim())) {
            return -1;
        }
        UQueryEngine qe = ctxt.getQueryEngine();
        int newDirRid = 0;
        String sql = "";
        
        newDirName.replaceAll("%20", " "); //

            sql = "select 1 from directory_details where dir_name = '"+newDirName+"' AND dir_project_rid = " + projectRid;

            ResultSet rsDirRid = qe.executeQuery(sql);

            if (null != rsDirRid && rsDirRid.first()) {
               throw new Exception("Root directory already exists with the name "+newDirName);
            }
            
            sql = "INSERT INTO directory_details (dir_name, dir_level, dir_project_rid, dir_user_rid, "
                        + "dir_created_datetime, dir_last_modified_datetime,"
                        + "dir_row_invalidated, dir_hidden, dir_is_write_protected, dir_parent_rid)"
                        + "VALUES('" + newDirName + "',0," + projectRid + ","
                        + ctxt.getUserRID() + ", NOW(), NOW(),0, 0, 0, 0)";

            newDirRid = qe.executeInsert(sql);
                
        return newDirRid;
    }
    
    

    //@new functions
    public static ResultSet readDirectories(URequestContext ctxt, String rootDir) {

        //String sql = "SELECT FROM attachment_details"
        return null;
    }

    static ResultSet getDirList(URequestContext ctxt, int parentDirRid)
    throws Exception{
        String sql = "";
        try{
        sql = "select dir_rid,dir_name,dir_user_rid from directory_details where dir_parent_rid = "+parentDirRid+
                " and dir_project_rid = "+ctxt.getProjectRID()+" and dir_row_invalidated = 0";
        return ctxt.getQueryEngine().executeQuery(sql);
        }catch(Exception ex){
            throw new Exception("unable read project directories:"+ex.getMessage());
        }
        
    }
    static ResultSet readDirContents(URequestContext ctxt, int dirRid,String contextType)
    throws Exception{
        String sql = "";
        try{
        sql = "select file_rid,file_name,file_path,file_size,file_content_type,file_user_rid,"
                + " DATE_FORMAT(file_created_datetime,'%d/%m/%Y %H:%i') file_created_datetime,"
               + "file_last_modified_datetime,file_desc,file_remarks from files where file_dir_rid = "+dirRid+
                " and file_project_rid = "+ctxt.getProjectRID()+" and file_context_type = '"+contextType+"'";
        return ctxt.getQueryEngine().executeQuery(sql);
        }catch(Exception ex){
            throw new Exception("unable read project files");
        }
        
    }
    
    static ResultSet searchFiles(URequestContext ctxt, String searchStr)
    throws Exception{
        String sql = "";
        try{
        sql = "select file_rid,file_name,dir_name,file_path,file_size,file_content_type,file_user_rid,"
                + " DATE_FORMAT(file_created_datetime,'%d/%m/%Y %H:%i') file_created_datetime,"
               + " file_last_modified_datetime,file_desc,file_remarks "
                + " from files join directory_details ON(file_dir_rid = dir_rid) where file_name like '%"+searchStr+"%'"+
                " and file_project_rid = "+ctxt.getProjectRID()+" and file_context_type = '"+FileManager.FILE_BROWSER_CONTEXT+"'";
        return ctxt.getQueryEngine().executeQuery(sql);
        }catch(Exception ex){
            throw new Exception("unable read project files");
        }
        
    }
    
    public static String downloadFiles(URequestContext ctxt,String fileRids)
            throws Exception{
        String sql = "";
        String sourceFiles[] = null;
        int i = 0;
        try{
        sql = "select file_path from files where file_rid in( "+fileRids+")";
        
        ResultSet rsFilesPath = ctxt.getQueryEngine().executeQuery(sql);
        //compressFiles
        if(null != rsFilesPath && rsFilesPath.first()){
            rsFilesPath.last();
            sourceFiles = new String[rsFilesPath.getRow()];
            rsFilesPath.beforeFirst();
        }
        
        while(null != rsFilesPath && rsFilesPath.next()){
            sourceFiles[i] = rsFilesPath.getString("file_path");
            i = i + 1;
        }
        
        String zippedFilePath = UFileCompress.compressFiles("C:/Users/nawab/Documents/attachments.zip", sourceFiles); //zip path hardcoded may give problem in concurrent operations. need to change
        
        return zippedFilePath;
        
        }catch(Exception ex){
            throw new Exception("unable download files"+ex.getMessage());
        }
    }
    
    public static void deleteFiles(URequestContext ctxt,String fileRids)
            throws Exception{
        String sql = "";
        try{
        sql = "select file_path from files where file_rid in( "+fileRids+")";
        
        ResultSet rsFilesPath = ctxt.getQueryEngine().executeQuery(sql);
        
        while(null != rsFilesPath && rsFilesPath.next()){
            if(null != rsFilesPath.getString("file_path") && !"".equals(rsFilesPath.getString("file_path").trim()));
            //UFile.deleteDirectory(rsFilesPath.getString("file_path"));
            deleteFile(ctxt,rsFilesPath.getString("file_path"));
        }
        
        sql = "delete from files where file_rid in( "+fileRids+")";
        
        ctxt.getQueryEngine().executeUpdate(sql);
        
        }catch(Exception ex){
            throw new Exception("unable delete files"+ex.getMessage());
        }
    }
    
    public static String getAbsolutePath(URequestContext ctxt,int dirRid)
    throws Exception{
        String absolutePath = "";
        String sql = "";
        
        if(dirRid<1)
            return "";
        
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rsDirDetails = null;
        
        while(dirRid != 0){
            sql = "select dir_name,dir_parent_rid from directory_details where dir_rid = "+dirRid;
            rsDirDetails = qe.executeQuery(sql);
            if(null != rsDirDetails && rsDirDetails.first()){
                absolutePath = rsDirDetails.getString("dir_name") + "/" +absolutePath;
                dirRid = rsDirDetails.getInt("dir_parent_rid");
            }else{
                break; //unknown case,  break the loop to avoid infinite looping
            }
                
        }
        
        return absolutePath;
    }
    
    public static ResultSet getAttachments(URequestContext ctxt, String contextType, int contextRID, String contentType) throws UDBAccessException {
        try {
            UQueryEngine qe = ctxt.getQueryEngine();

            String sql = " SELECT file_rid,file_name,file_path,file_size,file_content_type,file_desc FROM files "
                    + " where file_context_type = '" + contextType + "'";

            if (contextRID > 0) {
                sql += " and file_context_rid = " + contextRID;
            }

            if (contentType != null && !"".equals(contentType)) {
                sql += " and file_content_type like '" + contentType + "%'";
            }

            sql += " order by file_rid ";

            return qe.executeQuery(sql);
        } catch (Exception ex) {
            throw new UDBAccessException(ex.getMessage(), ex);
        }
    }
    
    //END
}