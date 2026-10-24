
package com.practice.minibank.health;

import java.net.HttpURLConnection;
import java.net.URI;

public class DockerHealthCheck {

	public static void main(String[] args) {
		try {
			HttpURLConnection connection = (HttpURLConnection) URI
					.create("http://localhost:8080/actuator/health/readiness").toURL().openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);

			int status = connection.getResponseCode();
			connection.disconnect();

			if (status != 200) {
				System.exit(1);
			}
		} catch (Exception ex) {
			System.exit(1);
		}
	}
}
