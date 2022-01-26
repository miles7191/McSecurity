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
package com.t07m.mcsecurity.notifier.email;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailClient {

	private static final Logger logger = LoggerFactory.getLogger(EmailClient.class);

	private Session session;

	public void setup(String host, String port, String ssl, String username, String password) {
		logger.debug("Building new EmailClient");
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.ssl.enable", ssl);
		properties.put("mail.smtp.auth", "true");

		session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	private MimeMessage createMessage() {
		return session != null ? new MimeMessage(session) : null;	
	}

	private boolean sendMessage(MimeMessage message) {
		try {
			logger.debug("Sending Email");
			Transport.send(message);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return false;
	}

	private MimeMessage buildMessage(String senderEmail, String senderName, String recipient, String subject, String body, String imagePrefix, BufferedImage... images) {
		logger.debug("Building new email to " + recipient);
		try {
			MimeMessage message = createMessage();
			if(message != null) {
				message.setSender(new InternetAddress(senderEmail, senderName));
				message.setSubject(subject);
				Multipart multipart = new MimeMultipart();
				MimeBodyPart textPart = new MimeBodyPart();
				textPart.setContent(body, "text/html");
				multipart.addBodyPart(textPart);
				if(images != null && images.length > 0) {
					for(int i = 0; i < images.length; i++) {
						try {
							BufferedImage image = images[i];
							MimeBodyPart attachmentPart = new MimeBodyPart();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(image, "png", baos);
							byte[] bytes = baos.toByteArray();
							DataSource ds = new ByteArrayDataSource(bytes, "image/png");
							attachmentPart.setDataHandler(new DataHandler(ds));
							attachmentPart.setFileName(imagePrefix + "-" + (i+1));
							multipart.addBodyPart(attachmentPart);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				message.setContent(multipart);
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			}
			return message;
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean sendMessage(String senderEmail, String senderName, String recipient, String subject, String body){
		return sendMessage(senderEmail, senderName, recipient, subject, body, null);
	}

	public boolean sendMessage(String senderEmail, String senderName, String recipient, String subject, String body, String imagePrefix, BufferedImage... images) {
		MimeMessage message = buildMessage(senderEmail, senderName, recipient, subject, body, imagePrefix, images);
		return message != null && sendMessage(message);
	}

}
