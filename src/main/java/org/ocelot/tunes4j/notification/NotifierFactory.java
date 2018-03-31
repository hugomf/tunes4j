package org.ocelot.tunes4j.notification;

import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Component;

@Component
public class NotifierFactory {
	
	public static Notifier instance() {
		
		 if(SystemUtils.IS_OS_WINDOWS)
			 //return new WindowsSystemTrayNotifier();
			 return new SwingNotifier();
		 if(SystemUtils.IS_OS_LINUX)
			 return new UbuntuNotifier();
	     if(SystemUtils.IS_OS_MAC)
	    	  	return new MacNotifier();
		
	      return new ConsoleNotifier();
	}

}
