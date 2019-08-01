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
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    private boolean isLoggedInFB =false;

    private URL profilePic;

    String username;
    String password;



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
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("response", response.toString());
                        isLoggedInFB = true;
                        getData(object);
                        checkUser(ParseUser.getCurrentUser(), true);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });

        if(isLoggedInFB == false){
            LoginManager.getInstance().logOut();
        }
    }

    // retrieving specific data from Facebook in graph request
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
                anonAcct();
                Intent noLoginIntent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(noLoginIntent);
                break;
        }
    }

    private void anonAcct() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d("MyApp", "Anonymous login failed.");
                } else {
                    Log.d("MyApp", "Anonymous user logged in.");
                }
            }
        });
        return;
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
                        newUser.put("firstName", firstName);
                        newUser.put("lastName", lastName);
                        convertImageFB(profilePic);
                        newUser.setEmail(email);
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


    // for later use: when app is made public and allowed to use on multiple devices (developer tools)
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



    private void convertImageFB(URL url) {

        Thread thread = new Thread(new Runnable() {

            public void run() {

                Bitmap mIcon = null;
                try {
                    mIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mIcon != null) {
                    final ParseFile imageParseFile = new ParseFile("image.jpg", encodeToByteArray(mIcon));
                    imageParseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                Log.e("EYEEE", "Error while saving");
                                e.printStackTrace();
                                return;
                            }
                            Log.e("EYEEE", "Success");

                        }
                    });
                    ParseUser.getCurrentUser().put("image", imageParseFile);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                Log.e("YEET", "Error while saving");
                                e.printStackTrace();
                                return;
                            } else{
                                Log.e("YEET", "Success");
                            }
                        }
                    });
                }
            }

        });
        thread.start();


    }

    public byte[] encodeToByteArray(Bitmap image) {
            Log.d("yep", "encodeToByteArray");
            Bitmap b= image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imgByteArray = baos.toByteArray();

            return imgByteArray ;
        }





    }





