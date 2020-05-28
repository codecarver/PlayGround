package com.wani.playground.jambi;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

/**
 * 
 * Simple client to upload file using HTTP POST method
 * 
 * @author codecarver
 *
 */
public class ClientUploadFile {

	public static void main(String[] args) throws Exception{

		// server URL
		URL url = new URL("http://localhost:4567/api/upload");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");

		// file location
		FileBody fileBody = new FileBody(new File("C:\\Users\\codecarver\\workspace\\workspace_gitlab\\PlayGround\\src\\main\\java\\com\\wani\\playground\\jambi\\test.txt"));
		MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
		multipartEntity.addPart("file", fileBody);

		connection.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
		OutputStream out = connection.getOutputStream();
		
		try {
			
		    multipartEntity.writeTo(out);
		} finally {
		
			out.close();
		}
		
		int status = connection.getResponseCode();
		
		// the result
		System.out.println("status: " + status);
	}
}
