package discordbot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class RainbotProperties {
	Properties prop = new Properties();
	public String getProperty(String key){
		String property = null;
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			property = prop.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return property;
	}
	
	public void setProperty(String key, String value) {
		OutputStream output = null;

		try {
			output = new FileOutputStream("config.properties");
			// set the properties value
			prop.setProperty(key, value);
			// save properties to project root folder
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void clearProperty(String key){
		OutputStream output = null;
		
		try {
			output = new FileOutputStream("config.properties");
			//remove property
			prop.remove(key);
			// save properties to project root folder
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
