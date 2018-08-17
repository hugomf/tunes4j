package org.ocelot.tunes4j.utils;

import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

public class FileUtilsTest {
	
	@Test
	public void shouldReturnPathWhenFileIsFound() throws Exception {
		String fromPath = "/tmp/documents/abcdef/a/b/c";
		String fileName = "universe-formula";
		String fullPath = fromPath + System.getProperty("file.separator") + fileName;
		
		FileUtils.deleteFile(fromPath, fileName);
		FileUtils.createEmptyFile(fromPath, fileName);
		
		String filePath[] = FileUtils.findFiles(fromPath, fileName);
		assertThat(filePath, Matchers.arrayContaining(fullPath));
	}
	


}
