import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;

public String postJson(String url,JSONObject obj)
{
    String string = "";
    HttpGet confirmGet;
    try
    {
        HttpClient hc = new DefaultHttpClient();
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        try
        {
            if(obj != null)
            {
                params.add(new BasicNameValuePair("", obj.toString()));
                Log.d("request", " " + obj.toString());
            }
        }catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    return string;
}