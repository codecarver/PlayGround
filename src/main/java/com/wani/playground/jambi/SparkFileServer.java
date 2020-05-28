package com.wani.playground.jambi;

import static spark.Spark.post;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import spark.utils.IOUtils;

/**
 * 
 * Simple server class to accept file using HTTP POST
 * 
 * @author codecarver
 *
 */
public class SparkFileServer {

    public static void main(String [] argv){

        post("/api/upload", (req, res) -> {
        	
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("")); // "C:\\Users\\codecarver\\workspace\\workspace_gitlab\\PlayGround\\tmp\\"));
            Part filePart = req.raw().getPart("file");

            try (InputStream inputStream = filePart.getInputStream()) {
            	
                OutputStream outputStream = new FileOutputStream("C:\\Users\\codecarver\\workspace\\workspace_gitlab\\PlayGround\\tmp\\" + filePart.getSubmittedFileName());
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            }

            return "File uploaded and saved.";
        });
    }
}
