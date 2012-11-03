package dev.rambo.image.story;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	
	public MediaRecorder mrec = null;
	MediaPlayer mPlayer = new MediaPlayer();
	
	// button defines
	private Button audio_Button = null;
	private Button play_Button = null;
	private Button next_Button = null;
	private Button prev_Button = null;
	private Button delete_Button = null;
	private Button save_Button = null; 
	
	// image view defines
	private ImageView mainImage = null;
	
	private ImageView rightThumbArr[] = new ImageView[4];
	private ImageView leftThumbArr[] = new ImageView[4];
	
	
	// constants
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	
	private static final int IMAGE_PICK 	= 1;
	private static final int IMAGE_CAPTURE 	= 2;
	
	private static final int ITEM_SIZE = 10; // number of item objects that can be created 
	
    private int currentFormat = 0;
    

    AudioImg[] items = new AudioImg[ITEM_SIZE]; // default 10 items
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
        
        // try recover save?
        try{
	        FileInputStream inStream = openFileInput("save_file.dat");
	        ObjectInputStream objectInStream = new ObjectInputStream(inStream);
	        int count = objectInStream.readInt(); // Get the number of objects
	        for (int c=0; c < count; c++)
	            items[c] = (AudioImg) objectInStream.readObject();
	        objectInStream.close();
	        
	        Toast toast = Toast.makeText(getApplicationContext(), "Save Restored!", Toast.LENGTH_SHORT);
	        toast.show();
        } catch (Exception ex){
        	Log.e(TAG, "save open error :(");
        	Log.e(TAG, ex.toString());
        	for (String s : fileList()){
        		Log.e(TAG, s);
        	}
        }
        

         
        // find the buttons
        audio_Button = (Button)findViewById(R.id.audio);
        play_Button = (Button)findViewById(R.id.play);
        next_Button = (Button)findViewById(R.id.next);
        prev_Button = (Button)findViewById(R.id.prev);
        delete_Button = (Button)findViewById(R.id.delete);
        save_Button = (Button)findViewById(R.id.save);
        
        // find imageviews
        mainImage = (ImageView)findViewById(R.id.mainImage);
        
        rightThumbArr[0] = (ImageView)findViewById(R.id.rightImg1);
        rightThumbArr[1] = (ImageView)findViewById(R.id.rightImg2);
        rightThumbArr[2] = (ImageView)findViewById(R.id.rightImg3);
        rightThumbArr[3] = (ImageView)findViewById(R.id.rightImg4);
        leftThumbArr[0] = (ImageView)findViewById(R.id.leftImg1);
        leftThumbArr[1] = (ImageView)findViewById(R.id.leftImg2);
        leftThumbArr[2] = (ImageView)findViewById(R.id.leftImg3);
        leftThumbArr[3] = (ImageView)findViewById(R.id.leftImg4);
        
        
        // inital setup
        prev_Button.setEnabled(false); //cant go back yet
        
        if(items[item_count].getAudio() == null){
        	play_Button.setEnabled(false); // nothing to play back yet
        }
        
        
        play_Button.setText("Play " + item_count);
        
        if (items[item_count] == null){
        	items[item_count] = new AudioImg();
        }
        
        updateThumbs();
     
        // onclick for record button
        audio_Button.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v) {
        	  if (!recFlag){ // start recording
		           try
		           {
		        	   // delete the old file first if it exists?
		        	   File audio = items[item_count].getAudio();
		        	   if(audio != null){
		        		   audio.delete();
		        	   }
		        	   
		        	   
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
            	if (item_count < ITEM_SIZE-1){
            		
            		
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
            		
            		// Update thumbnails 
            		updateThumbs();
            		
            			
            		
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
            		
            		// update the image thumbnails :D
            		updateThumbs();
            		
            		
            	} else{
            		prev_Button.setEnabled(false);
            	}
            		
        	      
            }
          });
        
        // delete button onclick
        delete_Button.setOnClickListener(new View.OnClickListener(){      
        	public void onClick(View v) {
            	items[item_count].setBitmapPath(null);
            	mainImage.setImageResource(R.drawable.no_pic);
            		
        	      
            }
          });
        
        // save button onclick
        save_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String FILENAME = "save_file.dat";
				try{
					FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
					
					
					ObjectOutputStream objectOutStream = new ObjectOutputStream(fos);
					objectOutStream.writeInt(items.length); // Save size first
					for(AudioImg a :items)
					    objectOutStream.writeObject(a);
					objectOutStream.close();
					fos.close();
					Log.d(TAG, "File Saved!");
					
				} catch(Exception ex){
					Log.e(TAG, "Save error... :(");
				}
				
				
			}
		});
        
        // imageview onclick:
        mainImage.setOnClickListener(new TakePictureListener());
        
        
       
    }
    
    /**
     * Little function to update all the thumbnail previews
     */
    private void updateThumbs(){
    	try{
	    	// check if the main image exists
	    	if(items[item_count].fetchImg() != null){
				mainImage.setImageBitmap(items[item_count].fetchImg()); // update the view
				
			} else{
				mainImage.setImageResource(R.drawable.no_pic);
			}
	    	
	    	// right loop
	    	// first figure out how many values to process
	    	int num = 0;
	    	if (ITEM_SIZE - (item_count+1) > 4){ // more than 4 pics ahead
	    		num = 4;
	    	} else{ // only a few pics left
	    		num = ITEM_SIZE - (item_count+1) ;
	    	}
	    	
	    	for (int i = 0; i < num ; i++){ // update all dem piccies :D
	    		if (items[item_count+i+1] != null){
					if(items[item_count+i+1].fetchImg() != null){
						rightThumbArr[i].setImageBitmap(items[item_count+i+1].fetchImg());
					} else{
						rightThumbArr[i].setImageResource(R.drawable.no_pic);
					}
				} else{
					rightThumbArr[i].setImageResource(android.R.color.transparent);
				}
	    	}
	    	
	    	if ( 4 != num ){ // yoda says: deal with leftovers ;) 
	    		for (int i = 3; i >= num; i-- ){
	    			rightThumbArr[i].setImageResource(android.R.color.transparent);
	    		}
	    	}
	    	
	    	
	    	
	    	// left loop
	    	// first figure out how many values to process
	    	num = 0;
	    	if (item_count > 4){ // more than 4 pics behind
	    		num = 4;
	    	} else if( item_count != 4){ // only a few pics left
	    		num = item_count;
	    	} else{
	    		num = 3;
	    	}
	    	
	    	for (int i = 0; i < num ; i++){ // update all dem piccies :D
	    		if (items[item_count-i-1] != null){
					if(items[item_count-i-1].fetchImg() != null){
						leftThumbArr[i].setImageBitmap(items[item_count-i-1].fetchImg());
					} else{
						leftThumbArr[i].setImageResource(R.drawable.no_pic);
					}
				} else{
					leftThumbArr[i].setImageResource(android.R.color.transparent);
				}
	    	}
	    	
	    	if ( 4 != num ){ // yoda says: deal with leftovers ;) 
	    		for (int i = 3; i >= num; i-- ){
	    			leftThumbArr[i].setImageResource(android.R.color.transparent);
	    		}
	    	}
	    	
			
    	} catch(Exception ex){
    		// just to catch any index errors?
    		Log.e(TAG, "Opps! index error!");
    	}
    	
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
       
        // save the audio file as the current timestamp
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();  // Always call the superclass method first
        
    	// not using anymore
        //String filepath = Environment.getExternalStorageDirectory().getPath();
        //File file = new File(filepath,AUDIO_RECORDER_FOLDER);
        //deleteDir(file);


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
    
    
    /**
     * Click listener for taking new picture
     * @author tscolari
     *
     */
    class TakePictureListener implements OnClickListener {
		public void onClick(View v) {
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, IMAGE_CAPTURE);
			
		}
    }
    
    /**
     * Receive the result from the startActivity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	if (resultCode == Activity.RESULT_OK) { 
	    	switch (requestCode) {
			case IMAGE_PICK:	
				this.imageFromGallery(resultCode, data);
				break;
			case IMAGE_CAPTURE:
				this.imageFromCamera(resultCode, data);
				break;
			default:
				break;
			}
    	}
    }
    
    /**
     * Image result from camera
     * @param resultCode
     * @param data
     */
    private void imageFromCamera(int resultCode, Intent data) {
    	this.updateImageView((Bitmap) data.getExtras().get("data"));
    }
    
    /**
     * Image result from gallery
     * @param resultCode
     * @param data
     */
    private void imageFromGallery(int resultCode, Intent data) {
    	Uri selectedImage = data.getData();
    	String [] filePathColumn = {MediaStore.Images.Media.DATA};
    	
    	Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
    	cursor.moveToFirst();
    	
    	int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    	String filePath = cursor.getString(columnIndex);
    	cursor.close();
    	
    	this.updateImageView(BitmapFactory.decodeFile(filePath));
    }
    
    /**
     * Save the image to a file
     * Save the path to the object
     * Update the imageView with new bitmap
     * @param newImage
     */
    private void updateImageView(Bitmap newImage) {
    	
    	String path = getFilesDir().toString() + "/" + item_count + ".png";
    	
    	FileOutputStream out;
		try {
			
			out = new FileOutputStream(path);
			newImage.compress(CompressFormat.PNG, 100, out);
			out.close();
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Something went wrong taking the pic and saving it... ");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Something went wrong closing the pic file output");
			e.printStackTrace();
		}
		items[item_count].setBitmapPath(path); // store the path to the new image
    	this.mainImage.setImageBitmap(items[item_count].fetchImg()); // update the view
    }


    
}
