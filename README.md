# PermissonManager
A Simple Library that searches for permissions in the merged Android Manifest file automatically and creates request for those permissions.

This Library also displays customizable Dialog for permissions when they are denied so that the dev may inform the user why the permission is needed.
If the app can no longer request permission due to "Don't ask again" on permission it will open the app specific settings allowing the user to grant the permission manually.

Setup:
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.Tc2r1:PermissonManager:{Latest_VERSION}'
	}



//To Automatically get Permissions needed and make request.

//It will Dynamically search for apps permission and request for the same.

//Add the below code in onCreate function.

```
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	      permission = new PermissionManager() {};
	      permissionManager.checkRequestPermissions(this);
    }	
```
// Add Below code in onRequestPermissionsResult function
```
@Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults, false);
        
        // The last param should be true if you want the denied permission to close the application.
        // The last peram should be false if your app ahs functionality to continue with the permission denied.
    }
```


//Customized by overriding method shown below.
```
permission=new PermissionManager() {
            @Override
            public void ifDeniedAndCanRequest(Activity activity)
            {
                // Customized code goes here for the condition in which a permission is denied withoutchecking "Don't ask again".
                // Use super.ifDeniedAndCanRequest(activity); or Don't override this method if not in use
            }

            @Override
            public void ifDeniedAndDontAskAgain(final Activity activity, boolean closeIfDenied)
            {
                // When user checks "Don't ask again" and denies a permission, this code block will execute.
                // Use super.ifDeniedAndDontAskAgain(activity); or Don't override this method if not in use
            }

            @Override
            public String[] getRequiredPermissions()
            {
                // Use super.getRequiredPermissions() or don't override this method if you want 
                // library to check manifest for permissions.
                
                String[] customPermissions = new String[]{Manifest.permission.READ_CONTACTS};
                return customPermissions;
            }
        };
```        


//To initiate checking permission
```
permissionManager.checkRequestPermissions(this);
```


//To get Granted Permission and Denied Permission
```
@Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults, false);
        //To get Granted Permission and Denied Permission
        ArrayList<String> allowed = permissionManager.getStatus().get(0).allowed;
        ArrayList<String> denied =  permissionManager.getStatus().get(0).denied;
    }
```
