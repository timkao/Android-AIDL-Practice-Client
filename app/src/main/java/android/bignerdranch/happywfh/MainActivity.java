package android.bignerdranch.happywfh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private android.bignerdranch.timkaoaidlserver.ICommon common;

    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            common = android.bignerdranch.timkaoaidlserver.ICommon.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("@tim", "Service has unexpectedly disconnected");
            common = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnBind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("@tim", "try to bind");
                Intent intent = new Intent("android.bignerdranch.timkaoaidlserver.AIDL");
                bindService(convertImplicitIntentToExplicitIntent(intent), serviceCon, BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.btnCalculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.i("@tim", "should start caculating!");
                    Log.i("@tim", "Addition is - " + common.calculate(100, 10));
                } catch (RemoteException e) {
                    Log.i("@tim", "not sure what went wrong!");
                    e.printStackTrace();
                }
            }
        });
    }

    public Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent) {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        Log.i("@tim", "setting component!");
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
