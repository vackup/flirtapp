package rs.edu.ict.flirtapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Stefan on 7/4/2015.
 */
public class SignUpTask extends AsyncTask<String, Void, JSONObject> {
    private String username, password, email, sex;
    private JSONObject resultObject;

    public SignUpTask(String username, String password, String email, String sex) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.sex = sex;
    }


    @Override
    protected JSONObject doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://flirtapp.azurewebsites.net/login.php");

        String forecastJsonStr = null; //Pazi pise forecastJsonStr promeni ime

        try {
            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder.encodedPath("https://flirtapp.azurewebsites.net/register.php");  //Ne znam da li gadjam dobru stranicu PROVERI!!!
            uriBuilder.appendQueryParameter("mode", "POST");
            URL url = new URL(uriBuilder.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            //URLConnection urlCon = url.openConnection();

            String data = "username="+this.username+"&password="+this.password+"&email="+this.email+"&gender="+this.sex;

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
            Log.v("Nestoooo", "Forecast JSON string: " + forecastJsonStr);
            return  new JSONObject(forecastJsonStr);

        }

        catch (IOException e) {
            // Log.e("PlaceholderFragment", "Error ", e);
            Log.e("Error", " Exection type: " + e.getMessage().toString());
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        setResultObject(jsonObject);

    }

    public JSONObject getResultObject() {
        return resultObject;
    }

    public void setResultObject(JSONObject resultObject) {
        this.resultObject = resultObject;
    }
}
