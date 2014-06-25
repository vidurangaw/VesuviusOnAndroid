package com.vpowerrc.vesuviusserver;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class VesuviusNotificationService extends Service {	
	private boolean isRunning = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		run(intent);
		return (START_NOT_STICKY);
	}

	@Override
	public void onDestroy() {
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (null);
	}

	@SuppressWarnings("deprecation")
	private void run(Intent i) {
		if (!isRunning) {

			isRunning = true;

			Notification note = new Notification(R.drawable.ic_launcher,"Vesuvius server is running",System.currentTimeMillis());
			
			i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:" + Server.serverPort + "/vesuvius"));

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);			
			
			note.setLatestEventInfo(this, "Vesuvius", "Open http://localhost:" + Server.serverPort + "/vesuvius", pi);
			
			note.flags |= Notification.FLAG_NO_CLEAR;

			startForeground(1337, note);
		}
	}

	private void stop() {
		if (isRunning) {

			isRunning = false;
			stopForeground(true);
		}
	}
}