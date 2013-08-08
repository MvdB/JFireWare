package de.edvdb.android.smscheck;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class SMSActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String adresse = getIntent().getExtras().getString(Config.ID_ADRESSE);
		final String message = getIntent().getExtras().getString(Config.ID_MESSAGE);
		
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
//					Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
					try {
						final String encodedurl = URLEncoder.encode(adresse, "ISO-8859-1");
//						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + encodedurl + " (FFW)"));
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + encodedurl));
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplicationContext().startActivity(i);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				    finish();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					finish();
					break;
				}
			}
		};

		System.out.println(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message + "\n--------------------------------\nNavigation beginnen?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
		
		
	}
}
