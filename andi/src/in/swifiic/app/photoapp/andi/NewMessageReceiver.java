package in.swifiic.app.photoapp.andi;

import in.swifiic.plat.helper.andi.Helper;
import in.swifiic.plat.helper.andi.xml.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NewMessageReceiver extends BroadcastReceiver {

	private static final String TAG = "NewMessageReceiver";
	private static int ROWS;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("notification")) {
			String payload = intent.getStringExtra("notification");
			Log.d(TAG, "Handling incoming messages: " + payload);
			Notification notif = Helper.parseNotification(payload);
			handleNotification(notif, context);
		} else {
			Log.w(TAG,
					"Broadcast Receiver ignoring message - no notification found");
		}
	}

	protected void handleNotification(Notification notif, Context context) {
		if (notif.getNotificationName().equals("PhotosBroadcast")) {
			ROWS = Integer.parseInt(notif.getArgument("count"));
			// XXX get image, save it to storage and display it in app
			for(int i = 0; i < ROWS; i++) {
				String img = notif.getArgument("img" + i);
				int imgId = Integer.parseInt(notif.getArgument("img_id" + i));
				int imgUpvotes = Integer.parseInt(notif.getArgument("img_upvotes" + i));
				Log.w(TAG, "Got img with ID: " + imgId);
				saveFile(context, img, imgId, imgUpvotes);
				} 
		}
	}
	
	protected boolean saveFile(Context context, String imgString, int imgId, int upvotes) {
		DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
		String sdt = df.format(new Date(System
				.currentTimeMillis()));
		PhotoDB photoDB = new PhotoDB(context);
		if(!photoDB.checkImgId(imgId)){
//			String state = Environment.getExternalStorageState();
			String imgName = imgId + "_" + sdt + ".jpg";
			String path = context.getExternalFilesDir(null).getAbsolutePath() + "/received/" + imgName;//"/sdcard/Android/data/in.swifiic.app.photoapp.andi/files/received/";
			Helper.b64StringToFile(imgString, path );
			photoDB.insertRow(context, imgId, imgName, 0);
			return true;
		}
		else
			return false;
	}
}
 