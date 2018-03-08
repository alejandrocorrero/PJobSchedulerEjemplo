package com.alejandrocorrero.pjobschedulerejemplo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;


public class ServicioTest extends JobService {
    public static final int DEFAULT_INTERVAL = 5000;

    private static final int TRABAJO_MENSAJE_ID = 1;
    private static final String KEY_MENSAJE = "key_mensaje";
    private static final int TRABAJO_ID = 1;
    private static final int RC_ENTENDIDO = 1;
    private static final int NC_AVISAR = 1;

    // Cuando se lanza un trabajo.
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // Se muestra el mensaje.
        if (jobParameters.getJobId() == TRABAJO_MENSAJE_ID) {
            PersistableBundle extras = jobParameters.getExtras();
            mostrarNotificacion(extras.getString(KEY_MENSAJE, "Trabajo lanzado"));
        }
        // Se indica qua el trabajo ha finalizado.
        return false;
    }

    // Cuando se para un trabajo en ejecución.
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Toast.makeText(this, "Trabajo parado", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void mostrarNotificacion(String mensaje) {
        NotificationManager mGestor = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setSmallIcon(R.drawable.ic_launcher_background);
        b.setContentTitle("Titulo");
        b.setContentText(mensaje);
        b.setDefaults(Notification.DEFAULT_ALL);
        b.setPriority(PRIORITY_MAX);
        b.setTicker("Ticker");
        b.setAutoCancel(true);


        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, RC_ENTENDIDO, i, 0);
        b.setContentIntent(pi);



        mGestor.notify(NC_AVISAR, b.build());
    }


    public static int planificarTrabajo(Context context, String mensaje, int intervalo) {



        JobInfo.Builder builder = new JobInfo.Builder(TRABAJO_ID,
                new ComponentName(context.getPackageName(), ServicioTest.class.getName()));



        builder.setMinimumLatency(intervalo);
        builder.setPersisted(true);
        PersistableBundle extras = new PersistableBundle();
        extras.putString(ServicioTest.KEY_MENSAJE, mensaje);
        builder.setExtras(extras);


        JobScheduler planificador = (JobScheduler) context.getSystemService(
                Context.JOB_SCHEDULER_SERVICE);
        int trabajoId = planificador.schedule(builder.build());

        if (trabajoId > 0) {


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putString("test", mensaje).apply();
            prefs.edit().putInt("Intervalo", intervalo).apply();
            prefs.edit().putBoolean("Activo", true).apply();
        }
        return trabajoId;
    }



    public static void cancelarPlanificacionTrabajo(Context context, int trabajoId) {


        JobScheduler planificador = (JobScheduler) context.getSystemService(
                Context.JOB_SCHEDULER_SERVICE);


        planificador.cancel(trabajoId);



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("Activo", false).apply();
    }
}
