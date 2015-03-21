package in.swifiic.app.photoapp.andi;

import in.swifiic.plat.helper.andi.Helper;
import in.swifiic.plat.helper.andi.xml.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NewMessageReceiver extends BroadcastReceiver {

	private static final String TAG = "NewMessageReceiver";
	private static final int ROWS = 2;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("notification")) {
			String payload = intent.getStringExtra("notification");
			Log.d(TAG, "Handling incoming messages: " + payload);
			Notification notif = Helper.parseNotification(payload);
			handleNotification(notif);
		} else {
			Log.w(TAG,
					"Broadcast Receiver ignoring message - no notification found");
		}
	}

	protected void handleNotification(Notification notif) {
		if (notif.getNotificationName().equals("PhotosBroadcast")) {
			// TODO get image, save it to storage and display it in app
			for(int i = 0; i < ROWS; i++) {
				String path = notif.getArgument("path" + i);
				Log.w(TAG, "Got file path as: " + path);
				
			}
		}

	}

}
