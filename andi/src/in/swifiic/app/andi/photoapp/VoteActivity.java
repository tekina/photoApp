package in.swifiic.app.andi.photoapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class VoteActivity extends ListActivity {

	ListView list;
	String[] desc = {"1", "Two", "3", "Four", "Five"};
	Integer[] imageId = {R.drawable.img1,
			R.drawable.img2, R.drawable.img3,
			R.drawable.img4, R.drawable.img5};
	Integer[] liked = {0, 1, 3, 0, 1};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);

		CustomList adapter = new CustomList(VoteActivity.this, imageId, desc, liked);
		list = getListView();//(ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(VoteActivity.this,
						"You Clicked at position " + position, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
}