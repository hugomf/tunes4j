package org.ocelot.tunes4j.gui;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ocelot.tunes4j.taggers.ListID3Tagger;



public class MediaImporterTest {

	private String path ="";
	private String[] severalPaths = new String[10];

	@Before
	public  void init() {

		severalPaths[0] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS";
		severalPaths[1] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Rock Espa�ol/Caifanes";
		severalPaths[2] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Rock Espa�ol/Mana";
		severalPaths[3] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Rock 60s Espa�ol/Enrique Guzman";
		severalPaths[4] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Pop Ingles/Cristina Aguilera";
		severalPaths[5] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Pop Ingles/Cranberries/1993  EVERYBODY ELSE IS DOING IT, SO WHY CAN'T WHY";
		severalPaths[6] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Pop Ingles/SmallVille Soundtrack";
		severalPaths[7] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Pop Ingles/BackStret Boys";
		severalPaths[8] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Navide�as";
		severalPaths[9] = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Norte�as/INTOCABLE";
		
	}
	
//	@Test
	public void compareWithDifferentStrategies() throws Exception {
		
		
//		shoudlTagFolderWithSeveralRecords("taggers.JAudioTaggerImpl");
//		shoudlTagFolderWithSeveralRecords("taggers.JID3TaggerImpl");
//		shoudlTagFolderWithSeveralRecords("taggers.YajilTaggerImpl");
		shoudlTagFolderWithSeveralRecords("taggers.MyId3TaggerImpl");
//		shoudlTagFolderWithSeveralRecords("taggers.EntaggedTaggerImpl");
//		shoudlTagFolderWithSeveralRecords("taggers.JavaID3TaggerImpl");
//		shoudlTagFolderWithSeveralRecords("taggers.JID3LibTaggerImpl");	
		
	}
	
	
	@Test
	@Ignore
	public void testPerformance() throws Exception {

		//path = "H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Rancheras";
		//path = "H:/ORGANIZADO/MEDIA/AUDIO";
		path="H:/ORGANIZADO/MEDIA/AUDIO/TEST";
		List files = new ArrayList();
		testTagFolder("taggers.JID3TaggerImpl", files);
		//printImportedFiles(files);
	}
	
	public void shoudlTagFolderWithSeveralRecords(String strategy) throws Exception {
		for(int i=0;i<1;i++){
			path=severalPaths[i];
			List files = new ArrayList();
			testTagFolder(strategy,files);
			
		}
	}

	private void testTagFolder(String strategy, List files) throws Exception {
		long start = System.currentTimeMillis();
			new ListID3Tagger(strategy).parseFolder(new File(path), files);
		long end = System.currentTimeMillis();
		long elapsedTime = end - start;
		System.out.println("Import process using: '" + strategy + "', Finished In: " + elapsedTime/ 1000f + " seconds, Parsing " + files.size() + " files, In " + path);
		assertTrue(files.size() > 0);
	}

	public void printImportedFiles(List files){
		if (files != null) {
			for (Iterator iterator = files.iterator(); iterator.hasNext();) {
				Mp3FileBean mp3 = (Mp3FileBean) iterator.next();
				System.out.println(mp3);
			}
		}
	}
	
	
	
}
