package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

@RestController
public class AppController {

	@GetMapping("/test")
	public String sendMail()
	{

		Region region = Region.AP_SOUTH_1;

		AwsBasicCredentials cred = AwsBasicCredentials.create("accessKeyId", "accessKeySecret");

		AwsCredentialsProvider abc = StaticCredentialsProvider.create(cred);

		SesV2Client sesv2Client = SesV2Client.builder()
				.region(region)
//				.credentialsProvider(abc)
				.build();

		// The HTML body of the email.
		String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
				+ "<p> See the list of customers.</p>" + "</body>" + "</html>";

		
		send(sesv2Client, "soniipratik@gmail.com", "pratiksonii@yahoo.in", "SES Testing", bodyHTML);

		return "Hello";
	}


	public static void send(SesV2Client client,
			String sender,
			String recipient,
			String subject,
			String bodyHTML
			){

		Destination destination = Destination.builder()
				.toAddresses(recipient)
				.build();

		Content content = Content.builder()
				.data(bodyHTML)
				.build();

		Content sub = Content.builder()
				.data(subject)
				.build();

		Body body = Body.builder()
				.html(content)
				.build();

		Message msg = Message.builder()
				.subject(sub)
				.body(body)
				.build();

		EmailContent emailContent = EmailContent.builder()
				.simple(msg)
				.build();

		SendEmailRequest emailRequest = SendEmailRequest.builder()
				.destination(destination)
				.content(emailContent)
				.fromEmailAddress(sender)
				.build();

		try {
			System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
			client.sendEmail(emailRequest);
			System.out.println("email was sent");

		} catch (SesV2Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
}
