/*	AudioImg class to link audio and image files
 * Author: Rambo
 * 21/10/2012
 * 
 */

package dev.rambo.image.story;

import java.io.File;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AudioImg implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File audio;
	
	private String bitmapPath;
    transient Bitmap bitmap;
	
	/**
	 * Blank constructor
	 */
	public AudioImg(){
		
		
		
	}
	
	/**
	 * fetches the image file
	 */
	public Bitmap fetchImg(){

		
		Bitmap bitm = BitmapFactory.decodeFile(bitmapPath);
		return bitm;
	}
	
	public File getAudio() {
		return audio;
	}
	public void setAudio(File audio) {
		this.audio = audio;
	}



	public String getBitmapPath() {
		return bitmapPath;
	}



	public void setBitmapPath(String bitmapPath) {
		this.bitmapPath = bitmapPath;
	} 
	

}
