package in.swifiic.app.andi.photoapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {
	private final Activity context;
	private final Integer[] imageId;
	private final Integer[] liked;

	public CustomList(Activity context, Integer[] imageId, String[] desc, Integer[] liked) {
		super(context, R.layout.photo_layout, desc);
		this.context = context;
		this.imageId = imageId;
		this.liked = liked;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.photo_layout, null, true);
		ImageButton imgBtn = (ImageButton) rowView.findViewById(R.id.likeButton);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		imageView.setImageResource(imageId[position]);
		if (liked[position] > 0 )
			imgBtn.setImageResource(android.R.drawable.star_big_on);
		else
			imgBtn.setImageResource(android.R.drawable.star_big_off);
		return rowView;
	}
}
