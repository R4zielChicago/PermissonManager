package tc2r.com.permexample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import tc2r.com.permissionmanager.PermissionManager;

public class MainActivity extends AppCompatActivity
{
    private Activity thisActivity;
    private PermissionManager permissionManager;
    private boolean settingsCalledFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        permissionManager = new PermissionManager() {

            @Override
            public String[] getRequiredPermissions()
            {
                String[] customPermissions = new String[]{Manifest.permission.READ_CONTACTS};
                return customPermissions;
            }

            @Override
            public void ifDeniedAndCanRequest(Activity activity)
            {
                //   This message is displayed while the user hasn't checked never ask again checkbox.
                String message = "You've declined this permission, but we really really need it, in order for this app to work you should enable it next time!";

                new AlertDialog.Builder(thisActivity)
                        .setMessage(message)
                        .setPositiveButton("I Understand", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(thisActivity, "Continue With App Flow!", Toast.LENGTH_SHORT).show();
                                continueAppFlow();
                            }
                        })
                        .create()
                        .show();

            }

            @Override
            public void ifDeniedAndDontAskAgain(final Activity activity, boolean closeIfDenied)
            {
                //  the user has just denied one or all of the permissions
                // use this message to explain why he needs to grant these permissions in order to proceed
                // This message is displayed after the user has checked never ask again checkbox.
                String warningMessage = activity.getString(R.string.permission_denied_never_ask_again_dialog_message);
                String positiveLabel = "Enable Location";

                String negativeLabel = "Cancel";
                DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this will be executed if User clicks OK button. This is gonna take the user to the App Settings
                        startAppSettingsConfigActivity(activity);
                        settingsCalledFlag = true;
                    }
                };

                DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity, "Location Based Promos Disabled.", Toast.LENGTH_SHORT).show();
                        continueAppFlow();
                    }
                };

                new android.app.AlertDialog
                        .Builder(activity)
                        .setMessage(warningMessage)
                        .setPositiveButton(positiveLabel, posListener)
                        .setNegativeButton(negativeLabel, negListener)
                        .create()
                        .show();
            }
            };
        }

    private void continueAppFlow()
    {
        Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionManager.checkResult(requestCode, permissions, grantResults, false);

        ArrayList<String> allowed = permissionManager.getStatus().get(0).allowed;
        ArrayList<String> denied =  permissionManager.getStatus().get(0).denied;

        Log.wtf("Permissions Allowed:", allowed.toString());
        Log.wtf("Permissions Denied:", denied.toString());

    }


    /**
     * start the App Settings Activity so that the user can change
     * settings related to the application such as permissions.
      */
    private void startAppSettingsConfigActivity() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + this.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        this.startActivity(i);
    }
}
