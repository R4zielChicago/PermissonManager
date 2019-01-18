package tc2r.com.permissionmanager;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.*;

/**
 * Created by Nudennie D White on 01-15-2019.
 */
public abstract class PermissionManager {

    private static final String TAG = "Permission Manager";
    private static final int PERMISSIONS_REQUEST_CODE = 1985;

    private Activity activity;


    /**
     * Returns a set of Permissions that have yet to be granted by the user.
     * The activity argument must specific an active activity.
     * <p>
     * This method checks for Android M or greater in accordance with new
     * mandatory runtime permission request.
     *
     * @param  activity an absolute activity that is active on the user device.
     * @return  A HashSet of permissions that have been requested and are currently denied.
     */
    public Set<String> checkRequestPermissions(Activity activity) {
        this.activity = activity;
        if (Build.VERSION.SDK_INT >= 23) {

            Set<String> permissions = new HashSet<String>(Arrays.asList(getRequiredPermissions()));

            for (Iterator<String> i = permissions.iterator(); i.hasNext(); ) {
                String permission = i.next();
                if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission: " + permission + " already granted.");
                    i.remove();
                } else {
                    Log.d(TAG, "Permission: " + permission + " not yet granted.");
                }
            }
            activity.requestPermissions(getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
            return permissions;
        }
        return new HashSet<>();
    }


    /**
     * Returns a set of Permissions that have yet to be granted by the user.
     * The fragment argument must specific an active fragment.
     * <p>
     * This method checks for Android M or greater in accordance with new
     * mandatory runtime permission request.
     *
     * @param  fragment an absolute fragment that is active on the user device.
     * @return  A HashSet of permissions that have been requested and are currently denied.
     */
    public Set<String> checkRequestPermissions(Fragment fragment) {
        this.activity = fragment.getActivity();

        if (Build.VERSION.SDK_INT >= 23) {

            Set<String> permissions = new HashSet<String>(Arrays.asList(getRequiredPermissions()));

            for (Iterator<String> i = permissions.iterator(); i.hasNext(); ) {
                String permission = i.next();
                if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission: " + permission + " already granted.");
                    i.remove();
                } else {
                    Log.wtf(TAG, "Permission: " + permission + " not yet granted.");
                }
            }
            fragment.requestPermissions(getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
            return permissions;
        }
        return new HashSet<>();
    }


    /**
     * Returns an array of Strings representing of all permissions required by the application in accordance to the
     * Merged Manifest
     * <p>
     * This method returns an empty array if there are no permissions required by the Manifest.
     *
     * @return  An array full of permissions compiled from the Merged Manifest of the application.
     */
    public String[] getRequiredPermissions() {
        String[] permissions = null;
        try {
            permissions = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Permission Not Found: " + e.toString());
        }

        if (permissions == null) {
            return new String[0];
        } else {
            return permissions.clone();
        }
    }


    /**
     * Callback for the result from requesting permissions. Checks the result of a permission request.
     * This method should be invoked in an activity's onRequestPermissionsResult override when using
     * This PermissionManager.
     * <p>
     * Requests permissions to be granted to this application. If the app is denied, method ifCancelledAndCanREquest
     * is evoked. If permission is denied and set to "do not ask again", ifCancelledandCannotRequest is evoked.
     * @param  requestCode  int: Application specific request code to match with a result reported to
     *                      onRequestPermissionsResult(int, String[], int[]). Should be >= 0.
     * @param  permissions  String: The requested permissions. Must be non-null and not empty.
     * @param  grantResults int: The grant results for the corresponding permissions which is either
     *                      PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     * @param  closeIfDenied boolean: Tracks if the application should close when permission is denied
     */
    public void checkResult(int requestCode, String permissions[], int[] grantResults, boolean closeIfDenied) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            String[] listPermissionsNeeded = getRequiredPermissions();
            Map<String, Integer> permissionMap = new HashMap<>();

            for (String permission : listPermissionsNeeded) {
                permissionMap.put(permission, PackageManager.PERMISSION_GRANTED);
            }

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    permissionMap.put(permissions[i], grantResults[i]);
                }

                boolean isAllGranted = true;

                for (String permission : listPermissionsNeeded) {
                    if (permissionMap.get(permission) == PackageManager.PERMISSION_DENIED) {
                        isAllGranted = false;
                        break;
                    }
                }

                if (!isAllGranted) {
                    boolean shouldRequest = false;
                    for (String permission : listPermissionsNeeded) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                            shouldRequest = true;
                            break;
                        }
                    }

                    if (shouldRequest) {
                        ifDeniedAndCanRequest(activity);
                    } else {
                        ifDeniedAndDontAskAgain(activity, closeIfDenied);
                    }
                }
            }
        }
    }


    /**
     * Handles the case of a permission case being denied with "do not ask again" unnchecked.
     * <p>
     * When a permission is denied, a warning message is shown. Can be overridden for custom actions.
     * @param  activity  Activity: The target activity.
     */
    public void ifDeniedAndCanRequest(final Activity activity) {

        //   This message is displayed while the user hasn't checked never ask again checkbox.
        String warningMessage = "This feature is only available when the permission is allowed";
        String positiveLabel = "I Understand";

        new android.app.AlertDialog.Builder(activity).setMessage(warningMessage).setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }


    /**
     * Handles the case of a permission case being denied with "do not ask again" checked.
     * <p>
     * When a permission is denied, a warning message is shown. Can be overridden for custom actions.
     * @param  activity  Activity: The target activity.
     * @param closeIfDenied boolean: Tracks if the application should be closed when this permission is denied.
     */
    public void ifDeniedAndDontAskAgain(final Activity activity, final boolean closeIfDenied) {
        String warningMessage = "In order to use this app you must enable the permissions needed for it to function properly.";
        String positiveLabel = "Enable Permission";
        DialogInterface.OnClickListener posListener;

        String negativeLabel = "Cancel";
        DialogInterface.OnClickListener negListener;

        if (closeIfDenied) {
            negativeLabel = "Close App";
        }

        posListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //this will be executed if User clicks OK button. This is gonna take the user to the App Settings
                startAppSettingsConfigActivity(activity);
            }
        };

        negListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (closeIfDenied) {
                    activity.finishAffinity();
                    System.exit(0);
                }
            }
        };

        new android.app.AlertDialog.Builder(activity)
                .setMessage(warningMessage)
                .setPositiveButton(positiveLabel, posListener)
                .setNegativeButton(negativeLabel, negListener)
                .create().show();
    }


    /**
     * Takes user to the permission settings of the application.
     * <p>
     * After "Do not ask again" is checked, a permission can only be enabled manually via the settings menu.
     * @param  activity  Activity: The target activity.
     */
    public void startAppSettingsConfigActivity(Activity activity) {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + activity.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        activity.startActivity(i);
    }


    /**
     * Creates two arrays, one of permissions granted, the other of permissions denied.
     * <p>
     * This method can be invoked to ascertain if a particular permission is granted or not.
     * @return Array of Permission statuses showing which permissions are denied and which are allowed
     */
    public ArrayList<PermissionStatus> getStatus() {
        ArrayList<PermissionStatus> permissionStatusArray = new ArrayList<>();
        ArrayList<String> allow = new ArrayList<>();
        ArrayList<String> deny = new ArrayList<>();

        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                allow.add(permission);
            } else {
                deny.add(permission);
            }
        }

        permissionStatusArray.add(new PermissionStatus(allow, deny));
        return permissionStatusArray;
    }


    /**
     * This method exposes array list containing requested permissions that are allowed and another array with requested
     * permissions that are denied.
     * <p>
     */
    public class PermissionStatus {
        public ArrayList<String> allowed;
        public ArrayList<String> denied;

        PermissionStatus(ArrayList<String> allowed, ArrayList<String> denied) {
            this.denied = denied;
            this.allowed = allowed;
        }
    }
}