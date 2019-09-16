package com.example.jobscheduler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetCurrentWeatherJobService extends JobService {

    // http://openweathermap.org/

    public static final String  TAG= GetCurrentWeatherJobService.class.getSimpleName();
    // take your key from weather free api
    final String APP_ID= "ff728c2cf44b15078267a46442290055";
    // definition city default
    final String CITY= "Jakarta";
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        getCurrentWeather(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
    private void getCurrentWeather(final JobParameters job){
        Log.d(TAG, "Running");
        // take api use clict Aync library
        AsyncHttpClient client= new AsyncHttpClient();
        String url ="http://api.openweathermap.org/data/2.5/weather?q="+CITY+"&appid="+APP_ID;
        Log.e(TAG, "getCurrentWeather"+url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result= new String(responseBody);
                Log.d(TAG, result);
                try {
                    // parse json file 
                    JSONObject responseObject= new JSONObject(result);
                    String currentWeather= responseObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    String description= responseObject.getJSONArray("weather").getJSONObject(0).getString("description") ;
                    double tempinKelvin= responseObject.getJSONObject("main").getDouble("temp");

                    double tempInCelcius= tempinKelvin- 273;
                    String temperature= new DecimalFormat("##.##").format(tempInCelcius);
                    String title= "Current Weather";
                    String message= currentWeather+ "" + description + "with" + temperature + "celcius" ;
                    int notifId= 100;
                    // show notification with message or title
                    showNotification(getApplicationContext(), title, message, notifId);
                    jobFinished(job, false);

                } catch (Exception e){
                    jobFinished(job, true);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                jobFinished(job, true);
            }
        });
    }

    // set show notification
    private void showNotification(Context context, String title, String message, int notifId) {
        String CHANNEL_ID= "Channel_1";
        String CHANNEL_NAME= "Job scheduler channel";

        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_assignment)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setSound(alarmSound);
        // ketika version dari android di atas dari android o
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000,1000,1000,1000,1000});
            builder.setChannelId(CHANNEL_ID); // ketika suatu notification manager tidak kosong
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }
            Notification notification= builder.build();
            if(notificationManager!= null){
                // membuat aplikasi auto refresg ketika mengubah notification
                notificationManager.notify(notifId, notification);
            }
        }
    }
}
