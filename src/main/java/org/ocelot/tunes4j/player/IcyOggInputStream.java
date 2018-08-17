package org.ocelot.tunes4j.player;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;

public class IcyOggInputStream extends BufferedInputStream {

	private int metaInt = -1;
	private int bytesUntilNextMetadata = -1;

	public IcyOggInputStream(InputStream in, int metaInt) {
		super(in);
		this.metaInt = metaInt;
		this.bytesUntilNextMetadata = metaInt;
	}

	protected void readMetadata() throws IOException {
		int blockCount = super.read();
		int byteCount = blockCount * 16; // 16 bytes per block
		if (byteCount < 0)
			return;
		byte[] metadataBlock = new byte[byteCount];
		int index = 0;
		while (byteCount > 0) {
			int bytesRead = super.read(metadataBlock, index, byteCount);
			index += bytesRead;
			byteCount -= bytesRead;
		}
		if (blockCount > 0) {
	        OggVorbisInfo info = new OggVorbisInfo();
	        try {
				info.loadInfo(in);
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int read() throws IOException {
        if (bytesUntilNextMetadata > 0) {
            bytesUntilNextMetadata--;
        }
        else if (bytesUntilNextMetadata == 0) {
            bytesUntilNextMetadata = this.metaInt - 1;
        }
        return super.read();
    }

	@Override
	public int read(byte[] buf, int offset, int length) throws IOException {
		if (bytesUntilNextMetadata > 0) {
			int adjLength = Math.min(length, bytesUntilNextMetadata);
			int got = super.read(buf, offset, adjLength);
			bytesUntilNextMetadata -= got;
			return got;
		} else if (bytesUntilNextMetadata == 0) {
			readMetadata();
			int adjLength = Math.min(length, bytesUntilNextMetadata);
			int got = super.read(buf, offset, adjLength);
			bytesUntilNextMetadata = this.metaInt - got;
			return got;
		} else {
			return super.read(buf, offset, length);
		}
		
	}
}
