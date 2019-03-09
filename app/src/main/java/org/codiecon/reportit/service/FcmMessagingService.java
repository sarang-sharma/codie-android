package org.codiecon.reportit.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.codiecon.reportit.R;
import org.codiecon.reportit.auth.SharedPrefManager;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.core.app.NotificationCompat;

public class FcmMessagingService extends FirebaseMessagingService {

    String Updatedtime, hours, min;
    String[] parts;


    @Override
    public void onNewToken(String s) {
        String FCMTOKEN = s;
        Log.d("some", "some info");
        Log.d("ff", FCMTOKEN);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String title, message, img_url, click_action, num, largeIcon;
            int number;

            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
            img_url = remoteMessage.getData().get("img_url");
            click_action = remoteMessage.getData().get("click_action");
            num = remoteMessage.getData().get("number");
            largeIcon = remoteMessage.getData().get("large_icon");
            number = Integer.parseInt(num);

            Log.d("payload data", remoteMessage.getData().toString());

            if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                Intent intent = new Intent(click_action);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try {
                    Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                    Date currentLocalTime = cal1.getTime();
                    DateFormat date1 = new SimpleDateFormat("HH:mm a");
                    date1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                    String localTime = date1.format(currentLocalTime);
                    Log.d("localTime", localTime);

                    try {
                        final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                        final Date dateObj = sdf.parse(localTime);
                        System.out.println(dateObj);
                        System.out.println(new SimpleDateFormat("Kk:mm").format(dateObj));
                        Log.d("12hr", new SimpleDateFormat("KK:mm").format(dateObj));
                        Updatedtime = new SimpleDateFormat("K:mm").format(dateObj);
                        parts = Updatedtime.split(":");
                        hours = parts[0];
                        min = parts[1];
                        Log.d("hourMin", hours + " " + min);
                        if (hours.equals("0")) {
                            hours = "12";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    URL url = new URL(img_url);
                    URL url1 = new URL(largeIcon);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Bitmap image1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification);

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat dateFormat = new SimpleDateFormat("aa");
                    Calendar cal = Calendar.getInstance();
                    String time = hours + ":" + min + " " + dateFormat.format(cal.getTime());
                    Log.d("Time from", "" + dateFormat.format(cal.getTime()));

                    contentView.setImageViewBitmap(R.id.image_news, image);
                    contentView.setTextViewText(R.id.tv_news_time, time);
                    contentView.setTextViewText(R.id.tv_news_title, message);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        NotificationChannel channel = new NotificationChannel("org.codiecon.reportit","androidfcm1", NotificationManager.IMPORTANCE_DEFAULT);

                        NotificationManager manager = getSystemService(NotificationManager.class);
                        manager.createNotificationChannel(channel);
                    }




                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"org.codiecon.reportit");

                    builder.setCustomBigContentView(contentView);
                    // builder.setContent(contentView);
                    builder.setContentTitle(title);
                    builder.setContentText(message);
                    builder.setContentIntent(pendingIntent);
                    builder.setSound(soundUri);
                    builder.setSmallIcon(R.drawable.ic_hodm_notification_icon);
                    builder.setLargeIcon(getCircularBitmap(image1));
                    builder.setAutoCancel(true);

                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(number, builder.build());
                } catch (IOException e) {
                    Log.d("ss", e.getMessage());
                }

            } else {
                click_action = "org.codiecon.reportit.TARGET_LOGIN";
                Intent intent1 = new Intent(click_action);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                    Date currentLocalTime = cal1.getTime();
                    DateFormat date1 = new SimpleDateFormat("HH:mm a");
                    date1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                    String localTime = date1.format(currentLocalTime);
                    Log.d("localTime", localTime);


                    try {
                        final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                        final Date dateObj = sdf.parse(localTime);
                        System.out.println(dateObj);
                        System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
                        Log.d("12hr", new SimpleDateFormat("K:mm").format(dateObj));
                        Updatedtime = new SimpleDateFormat("K:mm").format(dateObj);
                        parts = Updatedtime.split(":");
                        hours = parts[0];
                        min = parts[1];
                        Log.d("hourMin", hours + " " + min);
                        if (hours.equals("0")) {
                            hours = "12";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    URL url = new URL(img_url);
                    URL url1 = new URL(largeIcon);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Bitmap image1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification);
                    Date currentTime = Calendar.getInstance().getTime();
                    Log.d("current", currentTime + "");

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat dateFormat = new SimpleDateFormat("aa");
                    Calendar cal = Calendar.getInstance();
                    String time = hours + ":" + min + " " + dateFormat.format(cal.getTime());
                    Log.d("Time from", "" + dateFormat.format(cal.getTime()));

                    contentView.setImageViewBitmap(R.id.image_news, image);
                    contentView.setTextViewText(R.id.tv_news_time, time);
                    contentView.setTextViewText(R.id.tv_news_title, message);

                    PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        NotificationChannel channel = new NotificationChannel("com.fmp.findmyproperty", "androidfcm1", NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager manager = getSystemService(NotificationManager.class);
                        manager.createNotificationChannel(channel);
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "com.fmp.findmyproperty");
                    builder.setCustomBigContentView(contentView);
                    builder.setContentTitle(title);
                    builder.setContentText(message);
                    builder.setContentIntent(pendingIntent1);
                    builder.setSound(soundUri);
                    builder.setSmallIcon(R.drawable.ic_hodm_notification_icon);
                    builder.setLargeIcon(getCircularBitmap(image1));
                    builder.setAutoCancel(true);


                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(number, builder.build());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }


    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
