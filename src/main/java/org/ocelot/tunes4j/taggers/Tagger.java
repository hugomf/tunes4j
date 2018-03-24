package org.ocelot.tunes4j.taggers;

import java.io.File;

import org.ocelot.tunes4j.dto.Song;

public interface Tagger {

	public Song parse(File sourceFile);

	public void save(File sourceFile, Song bean);

}
