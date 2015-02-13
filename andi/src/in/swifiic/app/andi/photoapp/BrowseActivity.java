package in.swifiic.app.andi.photoapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class BrowseActivity extends ListActivity {

	ListView list;
	String[] desc = { "1", "Two", "3", "Four", "Five" };
	Integer[] imageId = { R.drawable.img1, R.drawable.img2, R.drawable.img3,
			R.drawable.img4, R.drawable.img5 };
	Integer[] liked = { 0, 1, 3, 0, 1 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);

		CustomList adapter = new CustomList(BrowseActivity.this, imageId, desc,
				liked);
		list = getListView();// (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(BrowseActivity.this,
						"You Clicked at position " + position,
						Toast.LENGTH_SHORT).show();
			}
		});
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
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, 1337);
			// TODO Get image from camera and create a bundle from it
			return true;
		} else if (itemId == R.id.action_settings) {
			Intent selectedSettings = new Intent(this, SettingsActivity.class);
			startActivity(selectedSettings);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}

// CountDownTimer countDownTimer = new CountDownTimer(
// 4 * 1000, 1000) {
// public void onTick(long leftTimeInMilliseconds) {
// long i = leftTimeInMilliseconds / 1000;
// String j = Long.toString(i);
// cntr.setText(j);
// }
//
// public void onFinish() {
// cntr.setText("Get Ready!");
// Intent intent = new Intent(BrowseActivity.this,
// in.swifiic.app.andi.photoapp.VoteActivity.class);
// startActivity(intent);
// finish();
// Intent cameraIntent = new
// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
// startActivityForResult(cameraIntent , 1337);
// }
// }.start();
// }
// }