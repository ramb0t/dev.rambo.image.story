package dev.rambo.image.story;

import java.io.File;
import java.io.IOException;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

public class MainActivity extends Activity {
	
	
	public MediaRecorder mrec = null;
	MediaPlayer mPlayer = new MediaPlayer();
	
	// button defines
	private Button audio_Button = null;
	private Button play_Button = null;
	private Button next_Button = null;
	private Button prev_Button = null;
	
	
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	
    private int currentFormat = 0;
    

    AudioImg[] items = new AudioImg[10]; // default 10 items
	private int item_count = 0;
    
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    
    
	private static final String TAG = "ImgStoryApp";
	
	// flag for button states
	boolean recFlag = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // View mview = findViewById(R.layout.activity_main);

       // mview.setBackgroundColor(Color.BLACK);

         
        // find the buttons
        audio_Button = (Button)findViewById(R.id.audio);
        play_Button = (Button)findViewById(R.id.play);
        next_Button = (Button)findViewById(R.id.next);
        prev_Button = (Button)findViewById(R.id.prev);
        
        // inital setup
        prev_Button.setEnabled(false); //cant go back yet
        play_Button.setEnabled(false); // nothing to play back yet
        
        play_Button.setText("Play " + item_count);
        
        items[item_count] = new AudioImg();
        
        
     
        // onclick for record button
        audio_Button.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v) {
        	  if (!recFlag){ // start recording
		           try
		           {
		        	   
		        	   startRecording();
		        	   
		        	   recFlag = true;
		        	   audio_Button.setText(R.string.stopRecAudio);
		           }catch (Exception ee)
		           {
		        	   Log.e(TAG,"Caught io exception " + ee.getMessage());
		           }
        	  } else { // stop recording

        		  stopRecording();
                  
        		  recFlag = false;
        		  audio_Button.setText(R.string.startRecAudio);
        		  
        		  play_Button.setEnabled(true);
        	  }
          }
        });
        
        //playback button
        play_Button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	try {
            		mPlayer.reset();
            		mPlayer.setDataSource(items[item_count].getAudio().getAbsolutePath());
            		mPlayer.prepare();
            		mPlayer.start();
            	} catch (Exception e) {
            		Log.e(TAG, "Error playing back audio.");
            	}
      
            }
          });
        
        
        // Next button onclick
        next_Button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	if (item_count < 9){
            		
            		
            		item_count++;
            		if (items[item_count] == null){
            			items[item_count] = new AudioImg();
            			play_Button.setEnabled(false);
            		} else if(items[item_count].getAudio() != null){
            			play_Button.setEnabled(true);
            		} else {
            			play_Button.setEnabled(false);
            		}
            		
            		play_Button.setText("Play " + item_count);
            		
            		prev_Button.setEnabled(true);
            		
            	} else{
            		next_Button.setEnabled(false);
            	}
            }
          });
        
        // prev button onclick
        prev_Button.setOnClickListener(new View.OnClickListener(){      
        	public void onClick(View v) {
            	if (item_count > 0){
            		item_count--;
            		
            		if (items[item_count].getAudio() == null){
            			play_Button.setEnabled(false);
            		} else{
            			play_Button.setEnabled(true);
            		}
            		
            		play_Button.setText("Play " + item_count);
            		
            		next_Button.setEnabled(true);
            	} else{
            		prev_Button.setEnabled(false);
            	}
            		
        	      
            }
          });
        
       
    }
    
    protected void startRecording() throws IOException 
    {

		mrec = new MediaRecorder();   
    	mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
        mrec.setOutputFormat(output_formats[currentFormat]);
        mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        String file = getFilename();
        mrec.setOutputFile(file);
         
        File f = new File(file);
        items[item_count].setAudio(f); // store in object
        

        
        mrec.prepare();
        mrec.start();
        
    }

    protected void stopRecording() {
        mrec.stop();
        mrec.reset();
        mrec.release();
        mrec = null;
      }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    
    
    private String getFilename(){ // generates the filename/path
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
       
        if(!file.exists()){
                file.mkdirs();
        }
       
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();  // Always call the superclass method first
        
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
        deleteDir(file);


    }
    
    //Deleting the temperary folder and the file created in the sdcard
    public static boolean deleteDir(File dir) 
    {
        if (dir.isDirectory()) 
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) 
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) 
                {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }


    
}
