package dev.rambo.image.story;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class PlayStory extends Activity {
	
	private static final String TAG = "ImgStoryApp";
	
	private int count = 0; 


	// AudioImg Array Object
	 private AudioImg[] item =  null;
	 
	 
	 private ImageView showImage = null;
	 
	 
	 MediaPlayer mPlayer = new MediaPlayer();

	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_story);
        
        
        mPlayer.reset();
        mPlayer.setOnCompletionListener(new OnCompletionListener(){
          public void onCompletion(MediaPlayer arg0) {
              playNext();
          }
        });
        
        
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        
        playShow();
    }
    
    /**
     * Plays the story, one image/audio object at a time
     * does the setup
     * @author Rambo
     */
    private void playShow(){
    	
    	// try recover save?
        try{
	        FileInputStream inStream = openFileInput("save_file.dat");
	        ObjectInputStream objectInStream = new ObjectInputStream(inStream);
	        int count = objectInStream.readInt(); // Get the number of objects
	        item = new AudioImg[count]; // create the array
	        
	        for (int c=0; c < count; c++)
	            item[c] = (AudioImg) objectInStream.readObject();
	        objectInStream.close();
	        
	        // check the size of the imported array
	        //ITEM_SIZE = count;
	        

        } catch (Exception ex){
        	Log.e(TAG, "save open error :(");
        	Log.e(TAG, ex.toString());
        	for (String s : fileList()){
        		Log.e(TAG, s);
        	}
        }
        
        // setup the views
        showImage = (ImageView)findViewById(R.id.showImage);
        
        showImage.setImageResource(R.drawable.no_pic);
        
        playNext();
    }
    
    /**
     * plays the next image/audio
     * @author Rambo
     */
    private void playNext(){
    	if (count < item.length){
    	// get the image and display
    		if(item[count].getBitmapPath() != null){
    			showImage.setImageBitmap(item[count].fetchImg());
    		} else{
    			showImage.setImageResource(R.drawable.no_pic);
    		}
    			
		
			//play the audio
    		if(item[count].getAudio() != null){
				try {
		    		mPlayer.reset();
		    		mPlayer.setDataSource(item[count].getAudio().getAbsolutePath());
		    		mPlayer.prepare();
		    		mPlayer.setVolume(1.0f, 1.0f);
		    		mPlayer.start();
		    		
		    	} catch (Exception e) {
		    		Log.e(TAG, "Error playing back audio.");
		    	}
    		}
			
			count++;
	    	} else{ // exit?
	    		mPlayer.release();
	    	}
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

}
