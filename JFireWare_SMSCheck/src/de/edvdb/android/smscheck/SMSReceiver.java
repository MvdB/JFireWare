package de.edvdb.android.smscheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {



	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}

		// Adresse extrahieren
		String message = smsMessage[0].getMessageBody();
		if (!(message.contains(Config.START_PATTERN) && message.contains(Config.END_PATTERN))) {
			return;
		} else {
			int beginIndex = message.indexOf(Config.START_PATTERN) + Config.START_PATTERN.length();
			int endIndex = message.indexOf(Config.END_PATTERN);
			String address = message.substring(beginIndex, endIndex).trim();
			
			Intent i = new Intent(context, SMSActivity.class);
			i.putExtra(Config.ID_ADRESSE, address);
			i.putExtra(Config.ID_MESSAGE, message);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
