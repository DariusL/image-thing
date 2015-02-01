package lt.dariusl.imagev2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class OptionsActivity extends Activity {
	private Button mSelectButton = null;
	private Spinner mSpinner = null;
	private final static int SELECT_PHOTO = 500;
	public static final String PREFS_NAME = "MyPrefs";
	public static final String PATH_KEY = "Path";
	public static final String MODE_KEY = "Mode";
	private SharedPreferences mSettings = null;
	private SharedPreferences.Editor mEditor = null;
	public static final int CROP = 0;
	public static final int FILL = 1;
	public static final int INSIDE = 2;
	public static final int ORIGINAL = 3;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mSettings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		mEditor = mSettings.edit();
		
		if(!mSettings.contains(MODE_KEY))
			mEditor.putInt(MODE_KEY, CROP).commit();
		
		setContentView(R.layout.options_screen);
		
		mSelectButton = (Button)findViewById(R.id.button_select);
		mSelectButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, SELECT_PHOTO);
			}
		});
		
		mSpinner = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.image_modes,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
				mEditor.putInt(MODE_KEY, (int) id).commit();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		mSpinner.setSelection(mSettings.getInt(MODE_KEY, CROP));
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            mEditor.putString(PATH_KEY, getPath(data.getData())).commit();
        }
    }
	public String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
        cursor.moveToFirst();

        //Link to the image
        final String imageFilePath = cursor.getString(0);
        cursor.close();

        return imageFilePath;
	}
}