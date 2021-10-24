package com.example.demo1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureAuthenticationButton();
    }

    // Configures the authentication button
    private void configureAuthenticationButton() {
        Button authenticationButton = findViewById(R.id.btnAuthenticateMe);

        authenticationButton.setOnClickListener(view -> {
            if(biometricsEnabled()){
                launchBiometricPrompt();
            }
        });
    }

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

        // getMainExecutor is a built-in method of the Context class -> Returns the Executor that runs tasks on the Main Thread
        prompt.authenticate(getCancellationSignal(), getMainExecutor(), getAuthenticationCallBack());
    }

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

    // Confirms that biometric authentication has been configured
    private boolean biometricsEnabled(){
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        if(!keyguardManager.isKeyguardSecure()){
            Toast.makeText(this, "Fingerprint authentication has not been enabled in settings", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}