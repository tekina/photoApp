package in.swifiic.app.photoapp.andi;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageHolder {

	ImageButton imgBtn;
	ImageView imgView;

	public ImageHolder(View root) {
		this.imgBtn = (ImageButton) root.findViewById(R.id.likeButton);
		this.imgView = (ImageView) root.findViewById(R.id.imgView);
	}
}
