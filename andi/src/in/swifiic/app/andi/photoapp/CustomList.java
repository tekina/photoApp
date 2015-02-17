package in.swifiic.app.andi.photoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CustomList extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] imgPath;
	private LayoutInflater mInflater;

	public CustomList(Activity context, String[] imgPath) {
		super(context, R.layout.photo_layout, imgPath);
		this.context = context;
		this.imgPath = imgPath;
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
		holder.imgBtn.setImageResource(android.R.drawable.star_big_on);
		return view;
	}
}
