package com.wani.playground.jambi;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.google.common.net.MediaType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mastfrog.netty.http.client.HttpClient;
import com.mastfrog.netty.http.client.ResponseFuture;
import com.mastfrog.netty.http.client.ResponseHandler;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class AsyncHTTPClient {

	private static final String URL = "http://172.9.1.219/rest_file_rekon_dev/resttr";
	
	private static final Logger logger = LoggingManager.getLoggerForClass();
	
	public static void main(String[] args) throws Exception{
		
		sendGet("");
	}
	
    public static void sendGet(String message) throws Exception {

    	HttpClient client = HttpClient.builder()
    			.followRedirects()
    			.setTimeout(Duration.ofSeconds(10))
//    			.threadCount(4)		// default
    			.build();	
    	
    	JsonArray jsonArray = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		
    	Map<String, String> requestMap = new HashMap<String, String>();
		
//		curl -H "Content-Type: application/json" -X POST -d 
//		'd=[{"mti":"0200","txap":"A001","txfunc":"F010","txres":"0","txus":"ATM001"
//		,"o1":"0410042007000594","o2":"P.1.00045.06.03.5"
//		,"o3":"PARKIR HOTEL PUTRI PINANG MASAK","o4":"JL. KAPT. PATTIMURA"
//		,"o5":"4.1.1.07.01","o6":"Pajak Parkir","o7":"H. DASRIL GANI","o8":"JL.DR.SUTOMO"
//		,"o9":"04","o10":"2020","o11":"2020-05-15","o12":"3000","o13":"25","o14":"750"
//		,"o15":"4","o16":"30","o17":"780","o18":"20200616","o19":"KOTA JAMBI","o20":"901"
//		,"o21":"12345","o22":"00"}]' http://172.9.1.219/rest_file_rekon_dev/resttr
			
		requestMap.put("mti", "0200");
		requestMap.put("txap", "A001");
		requestMap.put("txfunc", "F010");
		requestMap.put("token", "");
		requestMap.put("txus", "BJ010TL01"); 
		requestMap.put("txres", "0");
		requestMap.put("o1", "0410042007000594");
		requestMap.put("o2", "P.1.00045.06.03.5");
		requestMap.put("o3", "PARKIR HOTEL PUTRI PINANG MASAK");
		requestMap.put("o4", "JL. KAPT. PATTIMURA");
		requestMap.put("o5", "4.1.1.07.01");
		requestMap.put("o6", "Pajak Parkir");
		requestMap.put("o7", "H. DASRIL GANI");
		requestMap.put("o8", "JL.DR.SUTOMO");
		requestMap.put("o9", "04");
		requestMap.put("o10", "2020");
		requestMap.put("o11", "2020-05-15");
		requestMap.put("o12", "3000");
		requestMap.put("o13", "25");
		requestMap.put("o14", "750");
		requestMap.put("o15", "4");
		requestMap.put("o16", "30");
		requestMap.put("o17", "780");
		requestMap.put("o18", "20200616");
		requestMap.put("o19", "KOTA JAMBI");
		requestMap.put("o20", "901");
		requestMap.put("o21", "12345");
		requestMap.put("o22", "00");
		
		for (Map.Entry<String, String> pair : requestMap.entrySet()) {
		    jsonObject.addProperty(pair.getKey(), pair.getValue());
		}
		
		jsonArray.add(jsonObject);
		
    	String requestBody = "data="+jsonArray.toString();
    	
    	@SuppressWarnings("unused")
		ResponseFuture responseFuture = client
    			.post().setURL ( URL )
    			.setBody(requestBody, MediaType.PLAIN_TEXT_UTF_8)
    			.execute ( new ResponseHandler <String> ( String.class ) {

    				@Override
    				protected void onError(Throwable err) {
    				
//    					super.onError(err);
    					logger.error("error ", err);
    				}
    				
    				@Override
    				protected void onErrorResponse(HttpResponseStatus status, HttpHeaders headers, String content) {
    				
    					logger.error( "Resp: '" + content + "'" );
    				}
    				
    	            protected void receive ( HttpResponseStatus status, HttpHeaders headers, String response ) {
    	                
    	            	logger.info( "Resp: '" + response + "'" );
    	            }
    	        });
    	
    	logger.info("Done async");
    }

}
