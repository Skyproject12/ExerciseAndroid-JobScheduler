package com.example.jobscheduler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonStart;
    Button buttonCancel;
    private int jobId= 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart= findViewById(R.id.button_start);
        buttonCancel= findViewById(R.id.button_cancel);

        buttonStart.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_start:
                startJob();
                break;
            case R.id.button_cancel:
                cancelJob();
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startJob(){
        ComponentName componentName= new ComponentName(this, GetCurrentWeatherJobService.class);
        // melakukan build  scheduler
        JobInfo.Builder builder= new JobInfo.Builder(jobId, componentName);
        // set network
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        // periodic yang dibutuhkan
        builder.setPeriodic(18000);

        JobScheduler jobScheduler= (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
        // toast status
        Toast.makeText(this, "Job Service started", Toast.LENGTH_SHORT).show();

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void cancelJob(){
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        // mengcancel jobid scheduler
        tm.cancel(jobId);
        //toast status
        Toast.makeText(this, "Job Service Canceled", Toast.LENGTH_SHORT).show();
        finish();
    }
}
