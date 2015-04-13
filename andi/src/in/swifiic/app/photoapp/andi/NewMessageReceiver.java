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
import android.os.Environment;
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
			// TODO get image, save it to storage and display it in app
			for(int i = 0; i < ROWS; i++) {
				String img = notif.getArgument("img" + i);
				Log.w(TAG, "Got img string as: " + img);
				saveFile(context, img, "img" + i);
				}
		}
	}
	
	protected boolean saveFile(Context context, String imgString, String imgVal) {
		DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
		String sdt = df.format(new Date(System
				.currentTimeMillis()));
//		String state = Environment.getExternalStorageState();
		String path = context.getExternalFilesDir(null).getAbsolutePath() + "/received/" + imgVal + "_" + sdt + ".jpg";//Environment.get"/sdcard/Android/data/in.swifiic.app.photoapp.andi/files/received/";
		Helper.b64StringToFile(imgString, path );
		return true;
	}
}
 