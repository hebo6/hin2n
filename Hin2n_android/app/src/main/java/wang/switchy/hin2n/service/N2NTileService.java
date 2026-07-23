package wang.switchy.hin2n.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import wang.switchy.hin2n.Hin2nApplication;
import wang.switchy.hin2n.activity.MainActivity;
import wang.switchy.hin2n.model.EdgeStatus;
import wang.switchy.hin2n.model.N2NSettingInfo;
import wang.switchy.hin2n.storage.db.base.model.N2NSettingModel;

@RequiresApi(api = Build.VERSION_CODES.N)
public class N2NTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        if (N2NService.INSTANCE == null) {
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            EdgeStatus.RunningStatus status = N2NService.INSTANCE.getCurrentStatus();
            if (status == EdgeStatus.RunningStatus.CONNECTED || 
                status == EdgeStatus.RunningStatus.CONNECTING ||
                status == EdgeStatus.RunningStatus.SUPERNODE_DISCONNECT) {
                tile.setState(Tile.STATE_ACTIVE);
            } else {
                tile.setState(Tile.STATE_INACTIVE);
            }
        }
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        
        EdgeStatus.RunningStatus status = N2NService.INSTANCE == null ? EdgeStatus.RunningStatus.DISCONNECT : N2NService.INSTANCE.getCurrentStatus();
        
        if (N2NService.INSTANCE != null && status != EdgeStatus.RunningStatus.DISCONNECT && status != EdgeStatus.RunningStatus.FAILED) {
            Tile tile = getQsTile();
            if (tile != null) {
                tile.setState(Tile.STATE_INACTIVE);
                tile.updateTile();
            }
            N2NService.INSTANCE.stop(new Runnable() {
                @Override
                public void run() {
                    updateTile();
                }
            });
        } else {
            if (startVpn()) {
                Tile tile = getQsTile();
                if (tile != null) {
                    tile.setState(Tile.STATE_ACTIVE);
                    tile.updateTile();
                }
            }
        }
    }

    private boolean startVpn() {
        SharedPreferences n2nSp = getSharedPreferences("Hin2n", Context.MODE_PRIVATE);
        Long currentSettingId = n2nSp.getLong("current_setting_id", -1);
        
        if (currentSettingId == -1) {
            Toast.makeText(this, "No setting selected", Toast.LENGTH_SHORT).show();
            return false;
        }

        N2NSettingModel settingModel = Hin2nApplication.getInstance().getDaoSession().getN2NSettingModelDao().load(currentSettingId);
        if (settingModel == null) {
            Toast.makeText(this, "Setting not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        Intent vpnPrepareIntent = VpnService.prepare(this);
        if (vpnPrepareIntent != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityAndCollapse(intent);
            return false;
        }

        Intent intent = new Intent(this, N2NService.class);
        Bundle bundle = new Bundle();
        N2NSettingInfo n2NSettingInfo = Hin2nApplication.getInstance()
                .getSettingRepository()
                .toSettingInfo(settingModel);
        bundle.putParcelable("n2nSettingInfo", n2NSettingInfo);
        intent.putExtra("Setting", bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        
        return true;
    }

    public static void updateTileState(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            TileService.requestListeningState(context, new ComponentName(context, N2NTileService.class));
        }
    }
}
