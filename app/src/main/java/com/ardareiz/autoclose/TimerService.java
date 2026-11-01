package com.ardareiz.autoclose;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import java.util.Locale;

public class TimerService extends Service {
    public static final String ACTION_START = "com.ardareiz.autoclose.START";
    public static final String ACTION_STOP = "com.ardareiz.autoclose.STOP";
    public static final String EXTRA_MINUTES = "minutes";
    private static final String CHANNEL = "autoclose_timer";
    private static final int NOTIFICATION_ID = 701;
    private final Handler handler = new Handler();
    private long endAt = 0L;

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            long left = endAt - System.currentTimeMillis();
            if (left <= 0) {
                finishTimer();
                return;
            }
            updateNotification(left);
            handler.postDelayed(this, 1000);
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? null : intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopTimer();
            return START_NOT_STICKY;
        }
        if (ACTION_START.equals(action)) {
            int minutes = intent.getIntExtra(EXTRA_MINUTES, 30);
            endAt = System.currentTimeMillis() + minutes * 60_000L;
            startForeground(NOTIFICATION_ID, buildNotification(endAt - System.currentTimeMillis()));
            handler.removeCallbacks(tick);
            handler.post(tick);
        }
        return START_NOT_STICKY;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, "AutoClose zamanlayıcısı",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Küçük geri sayım bildirimi");
            channel.setShowBadge(false);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(long remaining) {
        Intent stopIntent = new Intent(this, TimerService.class).setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(this, 2, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent openPending = PendingIntent.getActivity(this, 3, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String time = format(remaining);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("AutoClose · Roblox")
                .setContentText(time + " kaldı")
                .setContentIntent(openPending)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setShowWhen(false)
                .addAction(android.R.drawable.ic_media_pause, "Durdur", stopPending);
        return b.build();
    }

    private void updateNotification(long remaining) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, buildNotification(remaining));
    }

    private String format(long millis) {
        long sec = Math.max(0, millis / 1000);
        return String.format(Locale.getDefault(), "%02d:%02d", sec / 60, sec % 60);
    }

    private void finishTimer() {
        handler.removeCallbacks(tick);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
        if (AutoHomeAccessibilityService.instance != null) {
            AutoHomeAccessibilityService.instance.goHome();
        } else {
            Notification done = new Notification.Builder(this, CHANNEL)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle("AutoClose")
                    .setContentText("Süre doldu. Roblox'u kapatmak için ana ekrana dön.")
                    .setAutoCancel(true)
                    .build();
            nm.notify(NOTIFICATION_ID + 1, done);
        }
        stopForeground(true);
        stopSelf();
    }

    private void stopTimer() {
        handler.removeCallbacks(tick);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
        stopForeground(true);
        stopSelf();
    }

    @Override public void onTaskRemoved(Intent rootIntent) {
        // Keep the user-started foreground countdown running if the app task is swiped away.
        super.onTaskRemoved(rootIntent);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
