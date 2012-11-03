/*	AudioImg class to link audio and image files
 * Author: Rambo
 * 21/10/2012
 * 
 */

package dev.rambo.image.story;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AudioImg implements Serializable {
	
	private File audio;
	private Bitmap img;
	
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
	
	public Bitmap getImg() {
		return img;
	}
	public void setImg(Bitmap image) {
		this.img = image;
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
