package org.ocelot.tunes4j.utils;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class SingleInstanceVerifier {
	
	
	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(RecurseFilesProvider.class);
	
	private static final int PORT = 9876;
	private static ServerSocket socket;    

	public static void checkIfRunning() {
	  try {
		  socket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));
		  
		 // consider to use JDK 9 to use ProcessHandle.current().pid();
		  
	  }
	  catch (BindException e) {
		logger.error("Already running.", e);
	    System.exit(1);
	  }
	  catch (IOException e) {
		  logger.error("Unexpected error.", e);
	    System.exit(2);
	  }
	}

}
