package rs.edu.ict.flirtapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Acer on 30.06.2015.
 */
public class GetAllUserFriends extends AsyncTask<String, Void, JSONObject> {
    private int userId;
    private Context context;
    private ProgressDialog progressDialog;
    public  GetAllUserFriends(int userID,Context context)
    {
        this.userId=userID;
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = ProgressDialog.show(context,"Plese wait","");
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://flirtapp.azurewebsites.net/allUserFriends.php");
        String forecastJsonStr = null;


        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder.encodedPath("https://flirtapp.azurewebsites.net/allUserFriends.php");
            uriBuilder.appendQueryParameter("mode", "POST");
            URL url = new URL(uriBuilder.build().toString());

            // Create the request to OpenWeatherMap, and open the connection



            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            URLConnection urlCon = url.openConnection();


            String data="userID="+this.userId;

            urlConnection.setDoOutput(true);

           OutputStreamWriter ow = new OutputStreamWriter(urlConnection.getOutputStream());
            ow.write(data);
            ow.flush();


            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                //return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                //return null;
            }
            forecastJsonStr = buffer.toString();
            Log.v("Prijatelji", "Forecast JSON string: " + forecastJsonStr);
            if(forecastJsonStr!=null)
            return  new JSONObject(forecastJsonStr);
            else
                return null;


        } catch (IOException e) {
            // Log.e("PlaceholderFragment", "Error ", e);
            Log.e("Error"," Exection type: "+e.getMessage().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("UserError", e.getMessage().toString());
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        ParseFriendData(jsonObject);
        this.progressDialog.dismiss();
        //go to swipe acctivity
    }

    public  void ParseFriendData(JSONObject jsonObject)
    {
        try {
            boolean status=jsonObject.getBoolean("success");
            if(status)
            {
                //if status is true
                JSONArray friends = jsonObject.getJSONArray("friends");

                AllUserFriends allUserFriends = new AllUserFriends();

                for (int i=0;i<friends.length();i++)
                {
                    JSONObject friend = friends.getJSONObject(i);
                    //taking the values

                    User profile = new User();
                    profile.setUser_name(friend.getString("UserName"));
                    profile.setUser_email(friend.getString("Email"));
                    profile.setUser_gender(friend.getString("Gender"));
                    profile.setUser_id(friend.optInt("ID", 0));
                    profile.setUser_birthday(friend.optInt("DayOfBirth", 0));
                    profile.setUser_profile_picture_path(friend.optString("AvatarPath", ""));
                    //insert user in all friends list
                    allUserFriends.AddUser(profile);
                }

                //syncing all friends in database and returning fresh list of friends
                //now you cal list all friends form database in all application with code
                //AllUserFriends allUserFriends = User.GetAllFriendsForUser(context);
                allUserFriends = User.SyncAllFriends(context,allUserFriends);

                //example of foreach for array of users
               /* for(User user : allUserFriends.GetListOfUser())
                {
                    Log.d("user",user.getUser_email());
                }*/


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
