/*
 * UFileCompress.java
 *
 * Created on 05 January 2007, 17:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cc.util;

import java.io.*;
import java.util.zip.*;
import org.apache.log4j.Logger;

/**
 *
 * @author suhas
 */
public class UFileCompress {

    /** Creates a new instance of UFileCompress */
    static Logger logger = Logger.getLogger(UFileCompress.class);

    public static void compressFile(String sourceFileName, String targetFileName, String filePath, String outputFileExtn)
            throws IOException {
        try {

            String outFilename = filePath + "/" + targetFileName + outputFileExtn;
            // Create the output stream
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFilename));
            // Open the input file
            String inputFile = filePath + "/" + sourceFileName;
            FileInputStream in = new FileInputStream(inputFile);
            // Transfer bytes from the input file to the GZIP output stream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            // Complete the GZIP file
            out.finish();
            out.close();
        } catch (IOException e) {
            logger.error("Failed to compress the file. " + e.toString());
            throw new IOException("Failed to compress the file. " + e.toString());
        }

    }

    public static void compressFiletoZip(String sourceFileName, String targetFileName, String filePath)
            throws IOException {
        try {
            int BUFFER = 2048;
            BufferedInputStream origin = null;
            String inputFile = filePath + "/" + sourceFileName;
            String outFilename = filePath + "/" + targetFileName + ".zip";

            FileOutputStream dest = new FileOutputStream(outFilename);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            // get a list of files from current directory
            FileInputStream fi = new FileInputStream(inputFile);

            origin = new BufferedInputStream(fi, BUFFER);

            ZipEntry entry = new ZipEntry(sourceFileName);

            out.putNextEntry(entry);
            int count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            out.close();

        } catch (IOException e) {
            logger.error("Failed to compress the file to zip. " + e.toString());
            throw new IOException("Failed to compress the file to zip. " + e.toString());
        }
    }

    public static String compressFiles(String zipFile,String sourceFiles[])
    throws Exception{
        try
                {
                      
                       
                        //create byte buffer
                        byte[] buffer = new byte[1024];
                       
                        /*
                         * To create a zip file, use
                         *
                         * ZipOutputStream(OutputStream out)
                         * constructor of ZipOutputStream class.
                        */
                         
                         //create object of FileOutputStream
                         FileOutputStream fout = new FileOutputStream(zipFile);
                         
                         //create object of ZipOutputStream from FileOutputStream
                         ZipOutputStream zout = new ZipOutputStream(fout);
                         
                         for(int i=0; i < sourceFiles.length; i++)
                         {
                               if(null == sourceFiles[i] || "".equals(sourceFiles[i]))
                                   continue;
                               
                                logger.debug("Adding " + sourceFiles[i]);
                                //create object of FileInputStream for source file
                                FileInputStream fin = new FileInputStream(sourceFiles[i]);
 
                                /*
                                 * To begin writing ZipEntry in the zip file, use
                                 *
                                 * void putNextEntry(ZipEntry entry)
                                 * method of ZipOutputStream class.
                                 *
                                 * This method begins writing a new Zip entry to
                                 * the zip file and positions the stream to the start
                                 * of the entry data.
                                 */
 
                                zout.putNextEntry(new ZipEntry(sourceFiles[i]));
 
                                /*
                                 * After creating entry in the zip file, actually
                                 * write the file.
                                 */
                                int length;
 
                                while((length = fin.read(buffer)) > 0)
                                {
                                   zout.write(buffer, 0, length);
                                }
 
                                /*
                                 * After writing the file to ZipOutputStream, use
                                 *
                                 * void closeEntry() method of ZipOutputStream class to
                                 * close the current entry and position the stream to
                                 * write the next entry.
                                 */
 
                                 zout.closeEntry();
 
                                 //close the InputStream
                                 fin.close();
                               
                         }
                       
                         
                          //close the ZipOutputStream
                          zout.close();
                         
                          logger.debug("Zip file has been created! Path:"+zipFile);
               
                }
                catch(IOException ioe){
                    logger.debug("IOException :" + ioe.getMessage());
                    throw new Exception(ioe.getMessage());
                }
                 return zipFile;
        }

    
}