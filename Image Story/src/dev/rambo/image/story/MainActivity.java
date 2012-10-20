package dev.rambo.image.story;

//import com.mwbrob001.imageviewer.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity{

	private Button gallery;
	private Button camera;
	private ImageView imageView;
	private Bitmap image;
	
	class ImagePickListener implements OnClickListener {

		public void onClick(View v)
		{
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(Intent.createChooser(intent, "Escolha uma Foto"), IMAGE_PICK);
		}
	}
	
	class TakePictureListener implements OnClickListener{
		
		public void onClick(View v)
		{
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, IMAGE_CAPTURE);
		}
	}
    
    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		gallery = (Button)findViewById(R.id.gallery);
		gallery.setOnClickListener(new ImagePickListener());
		camera = (Button)findViewById(R.id.camera);
		camera.setOnClickListener(new TakePictureListener());
		imageView = (ImageView) this.findViewById(R.id.imageView);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

	private void imageFromCamera(int resultCode, Intent data){
		this.imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
	}
	
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
	
	private void updateImageView(Bitmap newImage) {
    	BitmapProcessor bitmapProcessor = new BitmapProcessor(newImage, 1000, 1000, 90);
    	
    	this.image = bitmapProcessor.getBitmap();
    	this.imageView.setImageBitmap(this.image);
    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == Activity.RESULT_OK){
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
}
