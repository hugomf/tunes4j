package org.ocelot.tunes4j.gui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocelot.tunes4j.config.JpaConfiguration;
import org.ocelot.tunes4j.gui.ApplicationWindow;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes={JpaConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ApplicationWindowTest {

	@Test
	public void shouldRender() throws Exception {
		try {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	        context.register(ApplicationWindow.class);
	        context.refresh();
	        ApplicationWindow window = context.getBean(ApplicationWindow.class);
	        assertNotNull(window);
			
		} catch(Exception e) { 
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}

}
