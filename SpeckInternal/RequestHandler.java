import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RequestHandler implements HttpHandler{

	private Speck spk = new Speck(32);

	RequestHandler(){
	
	}

        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("Received Data at: " + System.currentTimeMillis());
            InputStream is = t.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
	    String data = "";
            while((data = reader.readLine()) != null){
                builder.append(data);
            }
            System.out.println("Before Decrypt: " + builder.toString());
            ArrayList<Integer> AL = spk.decrypt(StringToIntArray(builder.toString()));
            System.out.println("After Decrypt: " + AL.toString());
            String response = AL.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        public int[] StringToIntArray(String data){
            String[] s = data.split("\\[|\\]| |,");
	    int[] retVal = new int[s.length/2];
	    System.out.println(retVal.length);
	    int j = 0;
            for(int i = 1; i < s.length; i+=2){
                retVal[j] = Integer.parseInt(s[i]);
		System.out.println(retVal[j]);
                j++;
            }
            return retVal;
        }

}
