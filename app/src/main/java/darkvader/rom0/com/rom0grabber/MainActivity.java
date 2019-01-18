package darkvader.rom0.com.rom0grabber;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;


public class MainActivity extends ActionBarActivity {
    Button button,buttondownload;
    TextView textView;
    private ProgressDialog progress;
    int lenghtOfFile;
    String sasas="";
    String value;
    File sdcard = Environment.getExternalStorageDirectory();
    File objInput = new File(sdcard,"rom0.bin");
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private static String file_url = "http://api.androidhive.info/progressdialog/hive.jpg";
    //private static String file_url = "http://192.168.1.1/rom-0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        buttondownload = (Button)findViewById(R.id.buttondownload);
        textView = (TextView)findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {

         new Contact().execute();




        }
    });
        buttondownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFileFromURL().execute(file_url);
            }
        });

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
    public class Contact extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            textView.setText(value);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(MainActivity.this);
            progress.setMessage("Parsing Rom0");
            progress.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.setCancelable(false);
            progress.show();

        }







        @Override
        protected Void doInBackground(Void... params) {



            byte abBuff[] = null;
            try {
                FileInputStream reader = new FileInputStream(objInput);
                abBuff = new byte[(int) objInput.length()];
                reader.read(abBuff);
                reader.close();
            }   catch (IOException e) {
                //Log.d("sdasd",e.toString());
            }

try {


    char abDecomp[] = null;
    for (int iNdx = 3; iNdx < abBuff.length; iNdx++) {
        if (abBuff[iNdx - 3] == (byte) 0xCE && abBuff[iNdx - 2] == (byte) 0xED &&
                abBuff[iNdx - 1] == (byte) 0xDB && abBuff[iNdx] == (byte) 0xDB) {
            char abBuff2[] = new char[abBuff.length - iNdx];
            for (int iNdx2 = iNdx - 3, iNdx3 = 0; iNdx3 < abBuff.length - iNdx; iNdx2++, iNdx3++)
                abBuff2[iNdx3] = (char) (abBuff[iNdx2] & 0xFF);
            abDecomp = decompress(abBuff2);
            break;
        }
    }
    for (int iNdx = 0; iNdx < abDecomp.length; iNdx++) {
        if (abDecomp[iNdx] < 32 || abDecomp[iNdx] > 0x7E) {

            sasas = sasas + ".";
            // Log.d("sdasd",sasas.toString());
        } else {
            sasas = sasas + abDecomp[iNdx];
            //Log.d("sdasd",sasas.toString());

        }


    }

    String arr[];

    arr = sasas.split("[.]");


    List<String> aa = Arrays.asList(arr);
    ArrayList<String> aaa = new ArrayList<>(aa);

    ArrayList<String> ad = new ArrayList<>();
    Iterator<String> it = aaa.iterator();
    while (it.hasNext()) {
        String s = it.next();
        if (!ad.contains(s)) {
            ad.add(s);
        }


    }
    value = ad.toString();



}
catch(Exception e)
{

    Toast.makeText(getApplicationContext(),"no vulnerble found!",Toast.LENGTH_LONG).show();

}
            return null;
        }
    }
    class DownloadFileFromURL extends AsyncTask<String, String, String>
    {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                lenghtOfFile = conection.getContentLength();

                Log.d("sadsadf",String.valueOf(lenghtOfFile));


                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/rom0.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/rom0.bin";


            // setting downloaded into image view

        }

    }
    private static char[] decompress(char abBuff[])
    {
        List<Character> mylist = new ArrayList<Character>();
        int iNdx = 0;
        int unknown = abBuff[iNdx++] << 24 | abBuff[iNdx++] << 16 | abBuff[iNdx++] << 8 | abBuff[iNdx++];
        int majorVersion = abBuff[iNdx++] << 8 | abBuff[iNdx++];
        int minorVersion = abBuff[iNdx++] << 8 | abBuff[iNdx++];
        int blockSize = abBuff[iNdx++] << 24 | abBuff[iNdx++] << 16 | abBuff[iNdx++] << 8 | abBuff[iNdx++];
        while(iNdx < abBuff.length)
        {
            int orgSize = abBuff[iNdx++] << 8 | abBuff[iNdx++];
            int rawSize = abBuff[iNdx++] << 8 | abBuff[iNdx++];
            char abCompress[] = new char[rawSize];
            for(int iNdx2=iNdx, iNdx3 = 0;iNdx3<abCompress.length;iNdx2++,iNdx3++)
                try {
                    abCompress[iNdx3] = (char)(abBuff[iNdx2] & 0xFF);
                }catch(ArrayIndexOutOfBoundsException a) {
                    break;
                }
            List<Character> l = null;
            try {
                l = decomp(abCompress);
            }catch(ArrayIndexOutOfBoundsException a) {
                break;
            }
            mylist.addAll(l);
            iNdx += rawSize;
        }
        char abRet[] = new char[mylist.size()];
        iNdx = 0;
        for(char b : mylist)
            abRet[iNdx++] = b;
        return abRet;
    }
    private static List<Character> decomp(char[] abBuff)
    {
        List<Character> objRet = new ArrayList<Character>();
        CircularFifoQueue<Character> window = new CircularFifoQueue<Character>(2048);
        BitReader objBitRead = new BitReader(abBuff);
        for(int iNdx=0;iNdx<2048;iNdx++)
            window.add('\0');
        while (true)
        {
            int bit = objBitRead.readBit();
            if (bit==0)
            {
                int character = objBitRead.readByte();
                objRet.add((char)character);
                window.add((char)character);
            }
            else
            {
                int offset;
                bit = objBitRead.readBit();
                if (bit==1)
                {
                    offset = (int)objBitRead.readBit(7);
                    if (offset == 0)
                    {
                        //end of file
                        break;
                    }
                }
                else
                {
                    offset = (int)objBitRead.readBit(11);
                }
                int len;
                int lenField = (int)objBitRead.readBit(2);
                if (lenField < 3)
                {
                    len = lenField + 2;
                }
                else
                {
                    lenField <<= 2;
                    lenField += (int)objBitRead.readBit(2);
                    if (lenField < 15)
                    {
                        len = (lenField & 0x0f) + 5;
                    }
                    else
                    {
                        int lenCounter = 0;
                        lenField = (int)objBitRead.readBit(4);
                        while (lenField == 15)
                        {
                            lenField = (int)objBitRead.readBit(4);
                            lenCounter++;
                        }
                        len = 15*lenCounter + 8 + lenField;
                    }
                }
                for (int i = 0; i < len; i++)
                {
                    char character = (char)window.get(offset);
                    objRet.add(character);
                    window.add(character);
                }
            }
        }
        return objRet;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
