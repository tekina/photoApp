package in.swifiic.app.photoapp.andi;

import in.swifiic.plat.helper.andi.Helper;
import in.swifiic.plat.helper.andi.xml.Action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class BrowseActivity extends ListActivity {

	ListView list;
	int[] imgId;
	String[] imgPath;
	String[] usrInfo;
	String recdFilePath;
	final String recdFiles = recdFilePath;
	File uploadFile, recdFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);
		recdFilePath = getExternalFilesDir(null).getAbsolutePath()
				+ "/received/";
		uploadFile = new File(getExternalFilesDir(null).getAbsolutePath()
				+ "/uploads/");
		File[] listFile;
		if (!uploadFile.isDirectory()) {
			uploadFile.mkdirs();
		}
		recdFile = new File(getExternalFilesDir(null).getAbsolutePath()
				+ "/received/");
		if (!recdFile.isDirectory()) {
			recdFile.mkdirs();
		}

		listFile = recdFile.listFiles();
		imgPath = new String[listFile.length];
		imgId = new int[listFile.length];
		usrInfo = new String[listFile.length];
		for (int i = 0; i < listFile.length; i++) {
			imgPath[i] = listFile[i].getAbsolutePath();
			String fileName = listFile[i].getName();
			imgId[i] = Integer.parseInt(fileName.substring(0,fileName.indexOf("_")));
			usrInfo[i] = "<Username>";
		}

		list = getListView();// (ListView) findViewById(R.id.list);
		final CustomList adapter = new CustomList(this, imgPath, imgId);
		list.setAdapter(adapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_browse_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_shoot) {
			dispatchTakePictureIntent();
			return true;
		} else if (itemId == R.id.action_settings) {
			Intent selectedSettings = new Intent(this, SettingsActivity.class);
			startActivity(selectedSettings);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/* -----------Logic for taking and processing image for upload----------- */
	String mCurrentPhotoPath;
	static final int REQUEST_TAKE_PHOTO = 100;

	private File createImageFile(boolean temp) throws IOException {
		File storageDir = getExternalFilesDir(null);
		// Create temp and uploads Directories
		File uploadDir;
		if (temp)
			uploadDir = new File(storageDir.getAbsolutePath() + "/temp/");
		else
			uploadDir = new File(storageDir.getAbsolutePath() + "/uploads/");
		if (!uploadDir.isDirectory()) {
			uploadDir.mkdirs();
		}
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyMMdd_HHMMSS")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		String ext = null;
		if (!temp) {
			ext = ".jpg";
		}
		File image = File.createTempFile(imageFileName, ext, uploadDir);
		// XXX Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile(true);
			} catch (IOException e) {
				// Error occurred while creating the File
				e.printStackTrace();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TAKE_PHOTO) {
			if (resultCode == RESULT_OK) {
				// Bitmap photo = (Bitmap) data.getExtras().get("data");
				Bitmap photo = BitmapFactory.decodeFile(mCurrentPhotoPath);
				File tmpFile = new File(mCurrentPhotoPath);
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = createImageFile(false);
				} catch (IOException e1) {
					// Error occurred while creating the File
					e1.printStackTrace();
				}
				if (photoFile != null) {
					try {
						FileOutputStream fOut = new FileOutputStream(photoFile);
						photo.compress(Bitmap.CompressFormat.JPEG, 10, fOut);
						fOut.flush();
						fOut.close();
						// delete tmp file
						tmpFile.delete();
					} catch (IOException e) {
						tmpFile.delete();
						// FileOutputStream failed
						e.printStackTrace();
						Toast.makeText(this, "Image save compromised",
								Toast.LENGTH_SHORT).show();
					}
				}
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(
						this,
						"Image saved!\nWidth: " + photo.getWidth()
								+ "\nHeight: " + photo.getHeight(),
						Toast.LENGTH_LONG).show();
				// XXX
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				String fromUser = sharedPref.getString("my_identity", "anon");
				String hubAddress = sharedPref.getString("hub_address", "");
				Log.d("PhotoUpload", "Sending file :" + mCurrentPhotoPath);
				Action act = new Action("UploadPhoto", Constants.aeCtx);
				act.addArgument("fromUser", fromUser);
				act.addArgument("imageString",
						Helper.fileToB64String(mCurrentPhotoPath));
				Helper.sendAction(act, hubAddress + Constants.hubEndpoint,
						list.getContext());
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
				Toast.makeText(this,
						"Oops! Something went wrong. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
