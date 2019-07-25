package com.example.artravel;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.Activities.SignupActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button signUpButton;
    private Button continueNoLoginButton;
    private LoginButton facebookLoginButton;
    private String id, firstName, lastName, email,gender,birthday;
    private CallbackManager callbackManager;
    FacebookCallback<LoginResult> mFacebookCallback;
    private boolean isLoggedInFB;

    private URL profilePic;

    String username;
    String password;


    private static final String EMAIL = "email";



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);


        if (ParseUser.getCurrentUser() != null){
            checkUser(ParseUser.getCurrentUser(), false);
        } else if (AccessToken.getCurrentAccessToken() != null){
            // just set user id
            Log.d("Login Activity", "Access token valid, already logged in via Facebook");
            checkUser(ParseUser.getCurrentUser(), true);
        }


        callbackManager = CallbackManager.Factory.create();

        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.bLogin);
        signUpButton = findViewById(R.id.bSignUp);
        continueNoLoginButton = findViewById(R.id.bContinueNoLogin);
        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);


        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        continueNoLoginButton.setOnClickListener(this);


        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("fb", "request");

              //  Intent intent = new Intent(MainActivity.this, HomeActivity.class);
              //   String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("response", response.toString());
                        isLoggedInFB = true;
                        getData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
                Log.d("fb", "request user log in now");
                checkUser(ParseUser.getCurrentUser(), true);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }



    // retrieving FB data in graph request
    private void getData(JSONObject object) {
        try{
            profilePic = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=500&height=500");
            firstName = object.getString("first_name");
            lastName = object.getString("last_name");
            email = object.getString("email");

        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bLogin:
                // leave on login screen, grab user input if trying to login with username and password
                username = usernameInput.getText().toString();
                password = passwordInput.getText().toString();
                checkUser(ParseUser.getCurrentUser(), false);
                break;
            case R.id.bSignUp:
                // else, take to new create an account activity where can put in info
                Intent signUpIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signUpIntent);
                break;
            case R.id.bContinueNoLogin:
                Intent noLoginIntent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(noLoginIntent);
                break;
        }
    }

    public void checkUser(ParseUser currentUser, boolean isLoggedInFB) {
        // if there exists a current user, send intent automatically to main timeline screen for persistence
        if (currentUser != null) {
            final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (currentUser == null && isLoggedInFB == true) {
            ParseUser.logInInBackground(firstName + lastName, "1234", new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.d("LoginActivity", "Login successful");
                        final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (e != null) {
                        Log.d("fb", "create new account on parse");
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(firstName + lastName);
                        newUser.setPassword("1234");
                        newUser.setEmail(email);
                        newUser.put("profilePicture", profilePic.toString());
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("LoginActivity", "Login successful");
                                    final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.e("LoginActivity", "Login failure");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }
            });
        } else {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.d("LoginActivity", "Login successful");
                        final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("LoginActvitiy", "login failure");
                    }
                }
            });
        }
    }



    private void getKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.artravel",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) { }
        catch (NoSuchAlgorithmException e) { }
    }

}



