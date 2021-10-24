# Biometric Authentication Demo
## Setting up your device for fingerprint authentication
1. Navigate to ```(Settings > Security > Fingerprint)``` on your device
2. Choose a backup method (this demo will be using PIN for simplicity)
3. Configure your backup method (for PIN this is just entering the PIN twice)
4. Configure lock screen display options (this demo uses the default "Show all notification content")
5. Configure your fingerprint [using the emulator's touch sensor](#using-the-emulators-touch-sensor)

## Using the emulator's touch sensor
1. Click the three dots in the toolbar next to the emulator's display
2. Select ```FingerPrint``` from the options on the left
3. Click the ```Finger``` dropdown and select the fingerprint to use
4. Clikc the ```Touch Sensor``` button to simulate touching the sensor with the selected finger

## Creating a biometric prompt in your app
1. Add the biometric permission to the ```AndroidManifest.xml``` file
```xml
<uses-permission android:name="android.permission.USE_BIOMETRIC"/>
```
2. Create methods to be used by the biometric prompt's authentication method
```java
// Notifies the user if the authentication was cancelled
    private CancellationSignal getCancellationSignal(){
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> Toast.makeText(MainActivity.this, "Authentication was cancelled", Toast.LENGTH_SHORT).show());
        return cancellationSignal;
    }

    // What the prompt does based on it's result
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallBack() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                Toast.makeText(MainActivity.this, "You've been Authenticated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RequiresAuthActivity.class);
                startActivity(intent);
            }
        };
    }
```

3. Create method to check if the biometrics have been enabled
```java
// Confirms that biometric authentication has been configured
    private boolean biometricsEnabled(){
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        if(!keyguardManager.isKeyguardSecure()){
            Toast.makeText(this, "Fingerprint authentication has not been enabled in settings", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
```

4. Create method for launching our biometric prompt
```java
// Launches the biometric prompt
    private void launchBiometricPrompt(){
        BiometricPrompt prompt = new BiometricPrompt.Builder(this)
                .setTitle("Authentication Prompt")
                .setSubtitle("This app requires Authentication")
                .setDescription("Authentication is required to view all content")
                .setNegativeButton("Cancel", this.getMainExecutor(), (dialogInterface, i) -> {
                    Toast.makeText(this, "Authentication cancelled", Toast.LENGTH_SHORT).show();
                })
                .build();

        prompt.authenticate(getCancellationSignal(), getMainExecutor(), getAuthenticationCallBack());
    }
```

5. For this demo, we're using a simple button to test our prompt, but it can be launched however you'd like. Here's how I have my button configured:
```java
// Configures the authentication button
    private void configureAuthenticationButton() {
        Button authenticationButton = findViewById(R.id.btnAuthenticateMe);

        authenticationButton.setOnClickListener(view -> {
            if(biometricsEnabled()){
                launchBiometricPrompt();
            }
        });
    }
```
