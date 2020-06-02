package com.wani.playground.mantap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.apache.jmeter.protocol.tcp.sampler.AbstractTCPClient;
import org.apache.jmeter.protocol.tcp.sampler.ReadException;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class AHSampler extends AbstractTCPClient {
	
	private static final Logger logger = LoggingManager.getLoggerForClass();
	
	@Override
	public String read(InputStream inputStream) throws ReadException {
		
		logger.info("reading..");
		
		BufferedInputStream dis = new BufferedInputStream(inputStream);
	    
//        byte[] buffer = new byte[4096];
//        ByteArrayOutputStream w = new ByteArrayOutputStream();
//        int x;
//        
//        try {
//            while ((x = inputStream.read(buffer)) > -1) {
//                w.write(buffer, 0, x);
//                if (useEolByte && (buffer[x - 1] == eolByte)) {
//                    break;
//                }
//            }
//        } catch (InterruptedIOException e) {
//            // drop out to handle buffer
//        } catch (IOException e) {
//            logger.error("Read error:", e);
//            return "";
//        }

		try {
			
			// byte[] buffer = new byte[4096];
			
			// while ((inputStream.read()) > -1) {
				
				byte[] b = new byte[ 4 ];
				int ctr = dis.read( b );
				
				logger.info("ctr: " + ctr);
				if( ctr > 0 ) {
				
					int l = Integer.parseInt( new String( b ) );
					byte[] d = new byte[ l ];
					
					int totalRead = 0;
					int readCount = dis.read( d );

					if ( readCount < 0 )
						throw new Exception( "connection was disconnected." );
					
					totalRead += readCount;
					StringBuffer sb = new StringBuffer( new String( d, 0, readCount ) );
					
					while( totalRead < l ) {
						
						Thread.currentThread();
						Thread.sleep( 100 );
						
						byte[] temp = new byte[ l - totalRead ];
						
						readCount = dis.read( temp );
						totalRead += readCount;

						if ( readCount < 0 )
							throw new Exception( "connection was disconnected." );
						
						sb.append( new String( temp, 0, readCount ) );
					}

					// logger.info( "[read.len= " + ctr + "], [msg.len= " + l + "], [msg.read.tot= " + sb.length() + "]" );
					
					logger.info("receive: " + sb.toString());
					return sb.toString();
				}
				else {
				
					throw new Exception( "connection was disconnected." );
				}
//			}
			
		} catch (Exception e) {
			
			logger.error("error: ", e);
		}
		
		return null;
	}

	@Override
	public void write(OutputStream arg0, InputStream arg1) throws IOException {

		throw new UnsupportedOperationException("Not used in JMeter");
	}

	@Override
	public void write(OutputStream outputStream, String s) throws IOException {
		
		s = s + ",BS.TRACE.NUMBER=" + getRandomStan();
		// s = s.replace("\n", "\r");
		
        String aMsg = "0000" + s.length();
     	aMsg = aMsg.substring( aMsg.length() - 4 ) + s;
     		       
        logger.info("sending: " + aMsg);
        try {
        	
        	outputStream.write(aMsg.getBytes());
            outputStream.flush();
        }
        catch (IOException e) {
            logger.error("Write error:", e);
        }
        
    }

	private String getRandomStan() {
	
		return String.valueOf(Calendar.getInstance().getTimeInMillis());
	}	

}
