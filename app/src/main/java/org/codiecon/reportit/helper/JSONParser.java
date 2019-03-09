package org.codiecon.reportit.helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Arun on 05-10-2016.
 */
public class JSONParser {


    static InputStream is=null;
    static JSONObject jObj=null;
    static String json="";

    public JSONParser(){

    }

    public JSONObject getJSONFromUrl(final String url){
        //making http request
        try{
            //Consturct the client and the HTTP Request
            DefaultHttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(url);

            //Execute the post request and store the response locally
            HttpResponse httpResponse=httpClient.execute(httpPost);

            //Extract data from response
            HttpEntity httpEntity=httpResponse.getEntity();

            //Open an inputStream with data content
            is=httpEntity.getContent();

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        try{
            //Create BufferedReader to parse through the inputStream
            BufferedReader reader=new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);

            //Declare a string builder to help with the parsing
            StringBuilder sb=new StringBuilder();

            //Declare a string to store the JSON object data in string form
            String line=null;

            //Build the string until null
            while ((line=reader.readLine())!=null){
                sb.append(line+"\n");
            }

            //close the input stream
            is.close();

            //convert the string builder data to actual string
            json=sb.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        //parse the string to an JsON Object
        try {
            jObj=new JSONObject(json);
        }catch (JSONException e){
            Log.e("JSON Parser", "Error parsing data" + e.toString());
        }
        //return json object
        return jObj;
    }

    //function to get json from url by making get or post request

    public JSONObject makeHttpRequest(String url,String method,List<NameValuePair> params){

        //Making Http request
        try{
            //check for request method
            if(method=="POST"){
                //request method is post
                Log.d("parameters",""+params);
                //httpClient
                DefaultHttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse=httpClient.execute(httpPost);
                HttpEntity httpEntity=httpResponse.getEntity();
                is=httpEntity.getContent();
            }else if(method=="GET"){

                //request method is GET
                DefaultHttpClient httpClient=new DefaultHttpClient();
                String paramString= URLEncodedUtils.format(params, "utf-8");
                url+="?"+paramString;

                HttpGet httpGet=new HttpGet(url);

                HttpResponse httpResponse=httpClient.execute(httpGet);
                HttpEntity httpEntity=httpResponse.getEntity();
                is=httpEntity.getContent();
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        try{

            BufferedReader reader=new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb=new StringBuilder();
            String line=null;
            while ((line=reader.readLine())!=null){
                sb.append(line+"\n");
            }
            is.close();
            json=sb.toString();

        }catch (Exception e){
            Log.e("Buffer Error","Error converting results"+e.toString());
        }
        //parse the string to an Json object
        try{
            Log.d("Before parse",json);
            jObj=new JSONObject(json);
        }catch (JSONException e){
            Log.e("JSON Error","Error parsing data"+e.toString());
        }

        //return json object
        Log.d("json data:",jObj+"");
        return jObj;
    }
}
