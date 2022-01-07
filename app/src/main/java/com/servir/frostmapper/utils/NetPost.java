package com.servir.frostmapper.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NetPost extends AsyncTask<String, Void, String> {

    String postVar = null;
    String msgDialog = null;
    String jsonString = null;
    String TableName = null;
    String FieldName = null;

    ProgressDialog mpd = null;

    Context context;
    private AsyncTaskCompleteListener listener;


    public NetPost(Context Context, String postVar, JSONArray JSONarray, String msgDialog, String TableName, String FieldName, AsyncTaskCompleteListener listener){
        this.context = Context;
        this.postVar = postVar;
        this.TableName = TableName;
        this.FieldName = FieldName;
        this.jsonString = JSONarray.toString();
        this.msgDialog = msgDialog;
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mpd = new ProgressDialog(context);
        mpd.setMessage(msgDialog);
        mpd.setIndeterminate(true);
        mpd.setCanceledOnTouchOutside(false);
        mpd.setMax(100);
        mpd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mpd.show();
    }



    @Override
    protected String doInBackground(String... urls) {

        String output1=null;
        for (String url:urls){
            output1 = getOutputFromUrl(url);
        }

        return output1;
    }

    private String getOutputFromUrl(String url){
        String output1=null;
        StringBuilder sb1 = new StringBuilder();


        try {

            HttpClient httpclient1 = new DefaultHttpClient();
            HttpPost httppost1 = new HttpPost(url);
            List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(1);
            nameValuePairs1.add(new BasicNameValuePair(postVar, jsonString));
            httppost1.setEntity(new UrlEncodedFormEntity(nameValuePairs1, "utf-8"));
            HttpResponse httpr1 = httpclient1.execute(httppost1);

            if (httpr1.getStatusLine().getStatusCode() != 200) {
                listener.AsyncTaskCompleteListener("Issue","","", "");
            }


            BufferedReader reader1 = new BufferedReader(new InputStreamReader(httpr1.getEntity().getContent(), "UTF8"));
            sb1 = new StringBuilder();
            sb1.append(reader1.readLine() + "\n");
            String line1 = null;

            while ((line1 = reader1.readLine()) != null) {
                sb1.append(line1 + "\n");
            }

            output1 = sb1.toString();

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output1;

    }

    @SuppressWarnings("unused")
    protected void onProgressUpdate(int...progress) {
        mpd.setProgress(progress[0]);

    }



    @Override
    protected void onPostExecute(String output1) {

        mpd.dismiss();

        String vitingapi = null;

        if (output1 != null && !output1.isEmpty()) {

            vitingapi = output1.toString().trim();
            Log.e(postVar, vitingapi);
            Log.e("matiangi", output1);
            Log.e("matiangi", postVar);
            Log.e("matiangi", TableName);
            Log.e("matiangi", FieldName);

            listener.AsyncTaskCompleteListener(vitingapi, postVar, TableName, FieldName);
        }else{
            listener.AsyncTaskCompleteListener("Issue",postVar,vitingapi, FieldName);
        }


        mpd.dismiss();

    }

}