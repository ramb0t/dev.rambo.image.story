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
	private Button audio_Button = null;
	private Button play_Button = null;
	
	
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	
    private int currentFormat = 0;

    AudioImg ai = new AudioImg(); 
	
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    
    
	private static final String TAG = "SoundRecordingDemo";
	
	// flag for button states
	boolean recFlag = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
         
        
        audio_Button = (Button)findViewById(R.id.audio);
        play_Button = (Button)findViewById(R.id.play);
     
        
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
        	  }
          }
        });
        
        //playback button
        play_Button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	try {
            		mPlayer.reset();
            		mPlayer.setDataSource(ai.getAudio().getAbsolutePath());
            		mPlayer.prepare();
            		mPlayer.start();
            	} catch (Exception e) {
            		Log.e(TAG, "Error playing back audio.");
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
        ai.setAudio(f); // store in object
        

        
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

    
}
