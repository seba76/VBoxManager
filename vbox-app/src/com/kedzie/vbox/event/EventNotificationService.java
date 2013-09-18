package com.kedzie.vbox.event;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.inject.Inject;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentActivity;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineFragment;
import com.kedzie.vbox.soap.VBoxSvc;
import roboguice.inject.InjectExtra;
import roboguice.service.RoboIntentService;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventNotificationService extends RoboIntentService {

	private static final String TAG = "EventNotificationService";
	private static final int NOTIFICATION_ID=1;

    @Inject
    private NotificationManager mNotificationManager;
	
	public EventNotificationService() {
		super("Event Notification Service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Sending notification");
		IMachine eventMachine = BundleBuilder.getProxy(intent, IMachine.BUNDLE, IMachine.class);
        FragmentElement fragment = new FragmentElement(eventMachine.getName(), MachineFragment.class,
                new BundleBuilder().putVBoxSvc(eventMachine.getAPI()).putProxy(IMachine.BUNDLE, eventMachine).create());
		Intent i = new Intent(EventNotificationService.this, FragmentActivity.class)
                .putExtra(FragmentElement.BUNDLE, fragment);
		Utils.cacheProperties(eventMachine);
		BundleBuilder.addProxy(i, IMachine.BUNDLE, eventMachine);
		String title = getString(R.string.notification_title, eventMachine.getName(), eventMachine.getState());
		Notification n =  new NotificationCompat.Builder(EventNotificationService.this)
				.setContentTitle(title)
				.setContentText(getString(R.string.notification_text, eventMachine.getName(), eventMachine.getState()))
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_notif_vbox)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), ((VBoxApplication)getApplication()).getDrawable(eventMachine.getState())))
				.setContentIntent(PendingIntent.getActivity(EventNotificationService.this, 0, i, 0))
				.setTicker(title)
				.setAutoCancel(true)
				.build();
		((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, n);
	}
}
