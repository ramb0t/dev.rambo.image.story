/*	AudioImg class to link audio and image files
 * Author: Rambo
 * 21/10/2012
 * 
 */

package dev.rambo.image.story;

import java.io.File;

public class AudioImg {
	
	private File audio;
	private File img;
	
	/**
	 * Blank constructor
	 */
	public AudioImg(){
		
	}
	
	
	public File getImg() {
		return img;
	}
	public void setImg(File img) {
		this.img = img;
	}
	
	public File getAudio() {
		return audio;
	}
	public void setAudio(File audio) {
		this.audio = audio;
	} 
	

}
