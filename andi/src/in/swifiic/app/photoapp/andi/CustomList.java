package in.swifiic.app.photoapp.andi;

import in.swifiic.plat.helper.andi.Helper;
import in.swifiic.plat.helper.andi.xml.Action;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class CustomList extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] imgPath;
	private final int[] imgId;
	private boolean[] upvoted;
	private LayoutInflater mInflater;

	public CustomList(Activity context, String[] imgPath, int[] imgId) {
		super(context, R.layout.photo_layout, imgPath);
		this.context = context;
		this.imgPath = imgPath;
		this.imgId = imgId;
		int size = imgId.length;
		upvoted = new boolean[size];
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ImageHolder holder = null;
		mInflater = context.getLayoutInflater();
		if (view == null) {
			view = mInflater.inflate(R.layout.photo_layout, null);
			holder = new ImageHolder(view);
			view.setTag(holder);
		} else {
			holder = (ImageHolder) view.getTag();
		}

		Bitmap imgBitmap = BitmapFactory.decodeFile(imgPath[position]);
		holder.imgView.setImageBitmap(imgBitmap);
		PhotoDB db = new PhotoDB(context);
		int upvote = db.getUpvotes(imgId[position]);
		if(upvote == 0) {
			holder.imgBtn.setImageResource(android.R.drawable.star_big_off);
			upvoted[position] = false;
		}
		else {
			holder.imgBtn.setImageResource(android.R.drawable.star_big_on);
			upvoted[position] = true;
		}
		
//		ImageButton likeBtn = (ImageButton) findViewById(R.id.likeButton);
		final int id = imgId[position];
		final boolean voted = upvoted[position];
		final ImageHolder imgHolder = holder;
		holder.imgBtn.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View v)
		   {
			  //XXX
			  if(!voted){
				  imgHolder.imgBtn.setImageResource(android.R.drawable.star_big_on);
				  Toast.makeText(context, "testing button " + id, Toast.LENGTH_SHORT).show();
				  upvote(id);
			  }
		   }
		});
		return view;
	}
	
	private void upvote(int id) {
		// XXX
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String fromUser = sharedPref.getString("my_identity", "anon");
		String hubAddress = sharedPref.getString("hub_address", "");
		Log.d("PhotoUpvote", "Upvoting Image: " + id);
		Action act = new Action("UpvotePhoto", Constants.aeCtx);
		act.addArgument("fromUser", fromUser);
		act.addArgument("img_id",
				Integer.toString(id));
		Helper.sendAction(act, hubAddress + Constants.hubEndpoint,
				context);
		PhotoDB db = new PhotoDB(context);
		db.setUpvotes(id);
	}
}
