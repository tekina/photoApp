package in.swifiic.app.andi.photoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CustomList extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] imgPath;
	
	public CustomList(Activity context, String[] imgPath) {
		super(context, R.layout.photo_layout, imgPath);
		this.context = context;
		this.imgPath = imgPath;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.photo_layout, null, true);
		
		ImageButton imgBtn = (ImageButton) rowView.findViewById(R.id.likeButton);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imgView);

		Bitmap imgBitmap = BitmapFactory.decodeFile(imgPath[position]);
		imageView.setImageBitmap(imgBitmap);
//		imageView.setImageResource(imageList[position]);
			imgBtn.setImageResource(android.R.drawable.star_big_on);
//			imgBtn.setImageResource(android.R.drawable.star_big_off);
		return rowView;
	}
}
