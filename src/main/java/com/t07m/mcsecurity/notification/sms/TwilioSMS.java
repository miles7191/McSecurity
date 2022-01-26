/*
 * Copyright (C) 2022 Matthew Rosato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t07m.mcsecurity.notification.sms;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMS implements SMSClient{

	public void init(String account_sid, String auth_token) {
		Twilio.init(account_sid, auth_token);
	}

	public boolean sendMessage(String sender, String recipient, String body) {
		try {
			Message message = Message.creator(new PhoneNumber(recipient), new PhoneNumber(sender), body).create();
			return message.getSid() != null;
		}catch(ApiException e) {
			e.printStackTrace();
		}
		return false;
	}
}
