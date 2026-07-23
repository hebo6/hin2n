package wang.switchy.hin2n.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import wang.switchy.hin2n.Hin2nApplication;
import wang.switchy.hin2n.R;
import wang.switchy.hin2n.activity.MainActivity;
import wang.switchy.hin2n.model.EdgeStatus;
import wang.switchy.hin2n.model.N2NSettingInfo;
import wang.switchy.hin2n.service.N2NService;
import wang.switchy.hin2n.storage.db.base.model.N2NSettingModel;

public class N2NWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_WIDGET_CLICK = "wang.switchy.hin2n.action.WIDGET_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_WIDGET_CLICK.equals(intent.getAction())) {
            handleWidgetClick(context);
        }
    }

    private void handleWidgetClick(Context context) {
        EdgeStatus.RunningStatus status = N2NService.INSTANCE == null ? EdgeStatus.RunningStatus.DISCONNECT : N2NService.INSTANCE.getCurrentStatus();

        if (N2NService.INSTANCE != null && status != EdgeStatus.RunningStatus.DISCONNECT && status != EdgeStatus.RunningStatus.FAILED) {
            N2NService.INSTANCE.stop(new Runnable() {
                @Override
                public void run() {
                    updateAllWidgets(context);
                }
            });
        } else {
            startVpn(context);
        }
    }

    private void startVpn(Context context) {
        SharedPreferences n2nSp = context.getSharedPreferences("Hin2n", Context.MODE_PRIVATE);
        Long currentSettingId = n2nSp.getLong("current_setting_id", -1);

        if (currentSettingId == -1) {
            Toast.makeText(context, "No setting selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }

        N2NSettingModel settingModel = Hin2nApplication.getInstance().getDaoSession().getN2NSettingModelDao().load(currentSettingId);
        if (settingModel == null) {
            Toast.makeText(context, "Setting not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent vpnPrepareIntent = VpnService.prepare(context);
        if (vpnPrepareIntent != null) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }

        Intent intent = new Intent(context, N2NService.class);
        Bundle bundle = new Bundle();
        N2NSettingInfo n2NSettingInfo = Hin2nApplication.getInstance()
                .getSettingRepository()
                .toSettingInfo(settingModel);
        bundle.putParcelable("n2nSettingInfo", n2NSettingInfo);
        intent.putExtra("Setting", bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, N2NWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.n2n_widget_layout);

        EdgeStatus.RunningStatus status = N2NService.INSTANCE == null ? EdgeStatus.RunningStatus.DISCONNECT : N2NService.INSTANCE.getCurrentStatus();

        if (status == EdgeStatus.RunningStatus.CONNECTED ||
                status == EdgeStatus.RunningStatus.CONNECTING ||
                status == EdgeStatus.RunningStatus.SUPERNODE_DISCONNECT) {
            views.setImageViewResource(R.id.widget_image, R.mipmap.ic_launcher_connect);
        } else {
            views.setImageViewResource(R.id.widget_image, R.mipmap.ic_launcher_disconnect);
        }

        Intent intent = new Intent(context, N2NWidgetProvider.class);
        intent.setAction(ACTION_WIDGET_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
