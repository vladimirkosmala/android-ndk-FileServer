package vladimir.fileserver;

import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private Thread threadServer;
	private ToggleButton start_button;
	private TextView text_status;
	
	static {
		System.loadLibrary("ndkmain");
	}

	private native void startNativeServer(String path);
	private native void stopNativeServer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.start_button = (ToggleButton) findViewById(R.id.start_button);
		this.text_status = (TextView) findViewById(R.id.text_status);
		
		text_status.setText(R.string.ready);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startButtonClicked(View view) {
		if (this.start_button.isChecked()) {
			startServer();
		} else {
			stopServer();
		}
	}
	
	public void startServer() {
		if (this.threadServer == null) {
			if (!isExternalStorageReadable()) {
				new AlertDialog.Builder(this).setMessage(R.string.storage_not_found).show();
				start_button.setChecked(false);
				return;
			}
			
			String ip = getIP();
			if (ip == null) {
				new AlertDialog.Builder(this).setMessage(R.string.ip_not_found).show();
				start_button.setChecked(false);
				return;
			}
			
			this.threadServer = new Thread(new Runnable() {
				public void run() {
					File file = Environment.getExternalStorageDirectory();
					startNativeServer(file.getAbsolutePath());
				}
			});
			this.threadServer.start();
			
			Log.i("FileServer", "IP of Android: "+ip);
			text_status.setText(getString(R.string.view_address, ip));
		}
	}
	
	public void stopServer() {
		if (this.threadServer != null) {
			stopNativeServer();
			this.threadServer = null;
			text_status.setText("Ready");
		}
	}
	
	public String getIP() {
		try {
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ip = wifiInfo.getIpAddress();
			
			if (ip == 0) {
				Log.i("FileServer", "IP=0, wifi not connected ?");
				return null;
			}

			String ipString = String.format(
					"%d.%d.%d.%d",
					(ip & 0xff),
					(ip >> 8 & 0xff),
					(ip >> 16 & 0xff),
					(ip >> 24 & 0xff));

			return ipString;
		} catch (Exception e) {
			Log.i("FileServer", "Exception caught, IP not found: "+e.getMessage());
			return null;
		}
	}

	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
