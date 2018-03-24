package org.ocelot.tunes4j.notification;

import org.apache.commons.lang.SystemUtils;

public class NotifierFactory {
	
	public static Notifier instance() {
		
		 if(SystemUtils.IS_OS_WINDOWS)
			 return new ConsoleNotifier();
		 if(SystemUtils.IS_OS_LINUX)
			 return new UbuntuNotifier();
	      if(SystemUtils.IS_OS_MAC)
	    	  	return new MacNotifier();
		
	      return new ConsoleNotifier();
	}

}
