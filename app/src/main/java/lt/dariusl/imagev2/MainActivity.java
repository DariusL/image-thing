package lt.dariusl.imagev2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private ImageView mImageView = null;
	private SharedPreferences mSettings = null;
	private Bitmap mImageBitmap = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences(OptionsActivity.PREFS_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image_view);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if(mSettings.contains(OptionsActivity.PATH_KEY)){
    		String path = mSettings.getString(OptionsActivity.PATH_KEY, "");
    		Display display = getWindowManager().getDefaultDisplay();
    		BitmapFactory.Options ops = new BitmapFactory.Options();
    		ops.inJustDecodeBounds = true;
    		BitmapFactory.decodeFile(path, ops);
    		long bitmapPixels = (long) (ops.outHeight * ops.outWidth * 4);
    		long maxHeap = Runtime.getRuntime().maxMemory();
    		maxHeap *= 0.8;
    		ops = new BitmapFactory.Options();
    		ops.inSampleSize = (int)((bitmapPixels / maxHeap) + 1);
    		mImageBitmap = getScaledBitmap(BitmapFactory.decodeFile(path, ops), 
    				mSettings.getInt(OptionsActivity.MODE_KEY, OptionsActivity.CROP));
    		mImageView.setImageBitmap(mImageBitmap);
    		mImageView.setScaleType(ImageView.ScaleType.CENTER);
            }
        else
        	mImageView.setImageDrawable(null);
        mImageView.invalidate();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	getMenuInflater().inflate(R.menu.options_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_select:
			startActivity(new Intent(this, OptionsActivity.class));
		}
    	return true;
    }
    
    private Bitmap getScaledBitmap(Bitmap orig, int mode){
    	Display display = getWindowManager().getDefaultDisplay();
    	float xScale = 1, yScale = 1;
    	Matrix matrix = new Matrix();
    	int origWidth = orig.getWidth();
    	int origHeight = orig.getHeight();
    	int displayWidth = display.getWidth();
    	int displayHeight = display.getHeight();
    	float xMiddle = displayWidth / 2.0f;
    	float yMiddle = displayHeight / 2.0f;
    	switch(mode){        	
    	case OptionsActivity.CROP:
    		xScale = ((float) displayWidth) / origWidth;
        	yScale = ((float) displayHeight) / origHeight;
        	
        	if(xScale > yScale)
        		yScale = xScale;
        	else
        		xScale = yScale;
        	
        	if(xScale > 1)
        		xScale = 1;
        	if(yScale > 1)
        		yScale = 1;
        	break;
        case OptionsActivity.FILL: 
        	xScale = ((float) displayWidth) / origWidth;
        	yScale = ((float) displayHeight) / origHeight;
        	break;
        case OptionsActivity.INSIDE: 
        	xScale = ((float) displayWidth) / origWidth;
        	yScale = ((float) displayHeight) / origHeight;
        	
        	if(xScale < yScale)
        		yScale = xScale;
        	else
        		xScale = yScale;
        	break;
    	}
    	Bitmap ret = Bitmap.createBitmap(displayWidth, displayHeight, Config.ARGB_8888);
    	Canvas canvas = new Canvas(ret);
    	matrix.postScale(xScale, yScale, xMiddle, yMiddle);
    	canvas.setMatrix(matrix);
    	canvas.drawBitmap(orig, xMiddle - origWidth / 2, yMiddle - origHeight / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
    	orig.recycle();
    	return ret;
    }
    
    public static Bitmap smoothDownsample(Bitmap bitmap, int scale){
    	int bitmapWidth = bitmap.getWidth();
    	int bitmapHeight = bitmap.getHeight();
    	int newWidth = bitmapWidth/scale;
    	int newHeight = bitmapHeight/scale;
		Bitmap ret = Bitmap.createBitmap(newWidth, newHeight , Config.ARGB_8888);
		int pixels[] = null;
		ret.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		for(int i = 0; i < bitmapWidth; i += scale){
			for(int j = 0; j < bitmapHeight; j += scale){
				
			}
		}
		return ret;
    }
}