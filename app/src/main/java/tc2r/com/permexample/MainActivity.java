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

public class MainActivity extends AppCompatActivity
{
    private Activity thisActivity;
    private PermissionManager permissionManager;

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
            public void ifCancelledAndCanRequest(Activity activity)
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
                            }
                        })
                        .create()
                        .show();

            }
        };
        permissionManager.checkAndRequestPermissions(this);


//        checkPermission();
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


    //    /**
//     * called after permissions are requested to the user. This is called always, either
//     * has granted or not the permissions.
//     * @param requestCode  int code used to identify the request made. Was passed as parameter in the
//     *                     requestPermissions() call.
//     * @param permissions  Array containing the permissions asked to the user.
//     * @param grantResults Array containing the results of the permissions requested to the user.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return;
//        }
//
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
//                boolean anyPermissionDenied = false;
//                boolean neverAskAgainSelected = false;
//                // Check if any permission asked has been denied
//                for (int i = 0; i < grantResults.length; i++) {
//                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                        anyPermissionDenied = true;
//                        //check if user select "never ask again" when denying any permission
//                        if (!shouldShowRequestPermissionRationale(permissions[i])) {
//                            neverAskAgainSelected = true;
//                        }
//                    }
//                }
//                if (!anyPermissionDenied) {
//                    // All Permissions asked were granted! Yey!
//                    // DO YOUR STUFF
//                } else {
//                    // the user has just denied one or all of the permissions
//                    // use this message to explain why he needs to grant these permissions in order to proceed
//                    String message = "";
//                    DialogInterface.OnClickListener listener = null;
//                    if (neverAskAgainSelected) {
//                        //This message is displayed after the user has checked never ask again checkbox.
//                        message = getString(R.string.permission_denied_never_ask_again_dialog_message);
//                        listener = new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //this will be executed if User clicks OK button. This is gonna take the user to the App Settings
//                                startAppSettingsConfigActivity();
//                            }
//                        };
//
//                        new AlertDialog.Builder(thisActivity)
//                                .setMessage(message)
//                                .setPositiveButton("Enable Permission", listener)
//                                .setNegativeButton("Close App", new DialogInterface.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which)
//                                    {
//                                        thisActivity.finishAffinity();
//                                        System.exit(0);
//                                    }
//                                })
//                                .create()
//                                .show();
//
//                    } else {
//                        //This message is displayed while the user hasn't checked never ask again checkbox.
//                        message = "You've declined this permission, but we really really need it, in order for this app to work you should enable it next time!";
//
//                        new AlertDialog.Builder(thisActivity)
//                                .setMessage(message)
//                                .setPositiveButton("I Understand", new DialogInterface.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which)
//                                    {
//                                        Toast.makeText(thisActivity, "Continue With App Flow!", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .create()
//                                .show();
//                    }
//
//                }
//            }
//            break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//
//    void checkPermission()
//    {
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
//        {
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.READ_CONTACTS))
//            {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Message")
//                        .setMessage("We need to make calls")
//                        .setNeutralButton("OK", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//                            }
//                        });
//
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else
//            {
//                Toast.makeText(thisActivity, "should not show request permission rational", Toast.LENGTH_SHORT).show();
//                // No explanation needed; request the permission
//                ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else
//        {
//            Toast.makeText(thisActivity, "WE HAVE PERMISSION YEAH BABY YEAH", Toast.LENGTH_SHORT).show();
//            // Permission has already been granted
//        }
//    }
//
//    /**
//     * start the App Settings Activity so that the user can change
//     * settings related to the application such as permissions.
//     */
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
