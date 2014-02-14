/**
 * Copyright 2014 the staff of 52°North Initiative for Geospatial Open
 * Source Software GmbH in their free time
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spare.n52.yadarts.sound;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sound implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(Sound.class);

    private AudioFormat format;
    private byte[] samples;
    private InputStream source;
	private static final String WAVE_SUFFIX_STRING = "wav";
	private LineListener lineListener;
	
    public Sound(String resourcename, SoundId soundId) {
        try {
        	String resourcePath = "/sounds/"+ resourcename + "/" + soundId.name().toLowerCase() + "." + WAVE_SUFFIX_STRING;
            logger.debug("Try to load resource '{}'", resourcePath);

            URL resource = getClass().getResource(resourcePath);
            
            if (resource == null) {
            	return;
            }
            
            // open the audio input stream
            AudioInputStream stream =
                AudioSystem.getAudioInputStream(resource.openStream());

            format = stream.getFormat();

            // get the audio samples
            samples = getSamples(stream);
            
            source = new ByteArrayInputStream(samples);
        }
        catch (UnsupportedAudioFileException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        catch (IOException ex) {
        	logger.warn(ex.getMessage(), ex);
        }
    }


    public byte[] getSamples() {
        return samples;
    }

    public void addLineListener(LineListener listener){
    	this.lineListener = listener;
    }
    
    private byte[] getSamples(AudioInputStream audioStream) {
        // get the number of bytes to read
        int length = (int)(audioStream.getFrameLength() *
            format.getFrameSize());

        // read the entire stream
        byte[] samples = new byte[length];
        DataInputStream is = new DataInputStream(audioStream);
        try {
            is.readFully(samples);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        // return the samples
        return samples;
    }

	@Override
	public void run() {
		if (source == null) {
			return;
		}
	      // use a short, 100ms (1/10th sec) buffer for real-time
        // change to the sound stream
        int bufferSize = format.getFrameSize() *
            Math.round(format.getSampleRate() / 10);
        byte[] buffer = new byte[bufferSize];

        // create a line to play to
        SourceDataLine line;
        try {
            DataLine.Info info =
                new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine)AudioSystem.getLine(info);
            if (lineListener != null) {
            	line.addLineListener(lineListener);
            }
            line.open(format, bufferSize);
        }
        catch (LineUnavailableException ex) {
        	logger.warn(ex.getMessage(), ex);
            return;
        }

        // start the line
        line.start();

        // copy data to the line
        try {
            int numBytesRead = 0;
            while (numBytesRead != -1) {
                numBytesRead =
                    source.read(buffer, 0, buffer.length);
                if (numBytesRead != -1) {
                   line.write(buffer, 0, numBytesRead);
                }
            }
        }
        catch (IOException ex) {
        	logger.warn(ex.getMessage(), ex);
        }

        // wait until all data is played, then close the line
        line.drain();
        line.close();
		
	}

}