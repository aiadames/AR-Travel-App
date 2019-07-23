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

public class MainActivity extends AppCompatActivity {

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


    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.bLogin);
        signUpButton = findViewById(R.id.bSignUp);
        continueNoLoginButton = findViewById(R.id.bContinueNoLogin);
        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);


        callbackManager = CallbackManager.Factory.create();

        checkUser(ParseUser.getCurrentUser(), false);

        // else, leave on login screen, grab user input if tried to login with username and password
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                login(username, password);
            }
        });

        // else, take to new create an account activity where can put in info
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        continueNoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        mFacebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                isLoggedInFB = accessToken != null && !accessToken.isExpired();
                if (isLoggedInFB) {
                    Profile profile = Profile.getCurrentProfile();
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                    try {
                                        id = object.getString("id");
                                        profilePic =  new URL("https://graph.facebook.com/" + id + "/picture?width=500&height=500");
                                        firstName = object.getString("first_name");
                                        lastName = object.getString("last_name");
                                        email = object.getString("email");
                                        gender = object.getString("gender");
                                        birthday = object.getString("birthday");

                                        //do something with the data here
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (MalformedURLException f){
                                        f.printStackTrace();
                                    }

                                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                    checkUser(ParseUser.getCurrentUser(), isLoggedInFB);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, picture, first_name, last_name, email, gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();


                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        };

        facebookLoginButton.setReadPermissions("email");
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions((Activity) v.getContext(),Arrays.asList( "email"));
            }
        });
        facebookLoginButton.registerCallback(callbackManager, mFacebookCallback);
    }

    private void login(String username, String password) {
        // don't want to login on main thread, will clog up UI
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null){
                    Log.d("LoginActivity", "Login successful");
                    final Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else{
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();
                }
            }
        });
    }





    public void checkUser(ParseUser currentUser, boolean isLoggedInFB){
        // if there exists a current user, send intent automatically to main timeline screen for persistence
        if (currentUser != null) {
            final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (currentUser == null && isLoggedInFB == true) {
            try{
                login(firstName+lastName, "1234");
            }
            catch(Exception e){
                ParseUser user = new ParseUser();
                user.setUsername(firstName + lastName);
                user.setPassword("1234");
                user.setEmail(email);
                user.put("profilePicture", profilePic.toString());
                user.signUpInBackground(new SignUpCallback() {
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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


    private void getKeyHash() {
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



