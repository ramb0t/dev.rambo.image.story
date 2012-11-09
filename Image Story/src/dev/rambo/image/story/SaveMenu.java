package dev.rambo.image.story;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SaveMenu extends Activity {

	Button save_Button = null;
	Button del_Button = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_menu);
        
        save_Button = (Button)findViewById(R.id.saveBtn);
        del_Button = (Button)findViewById(R.id.deleteBtn);
        
        // save button onclick
        // Calls the save method... 
        save_Button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	save();
            }
            
        });

			
        
        // delete button onclick
        // deletes the current save file
        del_Button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	delete();
            }
            
        });

			
        
        
    }

    private void save() {
    	//TODO
		
			
	}


    private void delete() {
    	String FILENAME = "save_file.dat";
		deleteFile(FILENAME);
		// let the user know...
		Toast.makeText(getApplicationContext(), " Save deleted!", Toast.LENGTH_SHORT).show();
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_save_menu, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

}
