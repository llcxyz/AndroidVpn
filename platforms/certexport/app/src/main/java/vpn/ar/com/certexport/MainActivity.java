package vpn.ar.com.certexport;

import android.app.Activity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.vpn.Poster;
import com.ar.vpn.Server;
import com.ar.vpn.TrustedCertificateManager;
import com.ar.vpn.security.TrustedCertificateEntry;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    private static String TAG = "MainActivity";
    private List<TrustedCertificateEntry> tces = new ArrayList<TrustedCertificateEntry>();

    private Spinner spinner ;

    private Spinner spinnerServer;

    private TextView txt;

    private TextView serverName;

    private Button upload;

    private Poster post;

    private ServerAdapter serverAdapter;

    private List<Server> servers=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        post=  new Poster();

        spinner = (Spinner)findViewById(R.id.spinner);
        spinnerServer = (Spinner)findViewById(R.id.spinner_server);
        serverName = (TextView)findViewById(R.id.serverName);
        txt = (TextView)findViewById(R.id.base64);
        txt.setText("请选择");
        upload = (Button)findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              upload.setEnabled(false);
              new Thread(new Runnable() {
                @Override
                public void run() {
                  Server serv = (Server)spinnerServer.getSelectedItem();
                  TrustedCertificateEntry entry = (TrustedCertificateEntry)spinner.getSelectedItem();
                  String base64 = null;
                  try {
                      base64 = Base64.encodeToString(entry.getCertificate().getEncoded(),Base64.NO_WRAP);
                      serv.setCert(base64);
                     final  boolean result = post.upload_cert(serv);
                    runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        if(result)  Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                        else  Toast.makeText(MainActivity.this, "上传失败!", Toast.LENGTH_LONG).show();
                      }
                    });


                  } catch (CertificateEncodingException e) {
                    e.printStackTrace();
                  }
                  if(base64==null){
                    runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        Toast.makeText(MainActivity.this, "未选中证书", 2000).show();
                      }
                    });


                  }

                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      upload.setEnabled(true);
                    }
                  });
                }
              }).start();



            }
        });

        spinnerServer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

          final Server entry = (Server) parent.getItemAtPosition(position);
            serverName.setText(entry.getName()+"("+entry.getHostip()+")");

        }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                final TrustedCertificateEntry  entry = (TrustedCertificateEntry)parent.getItemAtPosition(position);
                String base64 = null;

                    // base64 = Base64.encodeToString(entry.getCertificate().getEncoded(),Base64.NO_WRAP);
                    //Log.d(TAG, "base64="+base64);
                    txt.setText(entry.getSubjectPrimary()+"\n"+entry.getCertificate().getSubjectDN().getName());


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        try {
            loadCert();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        serverAdapter = new ServerAdapter(this, servers);
        spinnerServer.setAdapter(serverAdapter);

        spinner.setAdapter(new CertAdapter(this, tces));

      loadServer();

    }
    protected  void loadServer() {
        new Thread(new Runnable() {
          @Override
          public void run() {
            servers.clear();
            servers.addAll(post.get_servers());
            MainActivity.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                serverAdapter.notifyDataSetChanged();
              }
            });

          }
        }).start();

    }

    protected  void loadCert() throws CertificateEncodingException {

        TrustedCertificateManager.TrustedCertificateSource mSource = TrustedCertificateManager.TrustedCertificateSource.USER;
        Hashtable<String,X509Certificate> localObject = TrustedCertificateManager.getInstance().load().getCACertificates(mSource);
        System.out.println("OK->>>>>:"+localObject);

        ArrayList<TrustedCertificateEntry> localArrayList = new ArrayList();
        Iterator<Map.Entry<String, X509Certificate>> localObject2 = localObject.entrySet().iterator();
        while ((localObject2).hasNext())
        {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
            localArrayList.add(new TrustedCertificateEntry((String)localEntry.getKey(), (X509Certificate)localEntry.getValue()));
        }

        Collections.sort(localArrayList);

        for(TrustedCertificateEntry entry: localArrayList){
            tces.add(entry);
            Log.d(TAG, "证书别名->" + entry.getAlias() + ":" + entry.getSubjectPrimary()+","+entry.getSubjectSecondary()+","+entry.toString()+","+entry.getCertificate().getSubjectDN().getName());

            String base64 = Base64.encodeToString(entry.getCertificate().getEncoded(),Base64.NO_WRAP);
            System.out.println("Base64="+base64);

        }

    }
}
