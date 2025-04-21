package com.ibm.wssvt.acme.annuity.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class AnnuityUpdater {
	
	private static final String VERSION_FILE = "AcmeAnnuityBundleVersion.txt";
	private static final String BUNDLE_FILE = "AcmeAnnuityBundle.zip";
	private static final String DEFAULT_LOCATION = "http://svtwin48.austin.ibm.com/pyxis/acme/annuity/release/fixpack/current/";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String address = null;
		int myVersion;
		int currentVersion;
		try {
			if(args.length > 0) {
				address = args[0];
			}
			if(address == null) {
				address = DEFAULT_LOCATION;
			}
			
			myVersion = getLocalVersion(VERSION_FILE);			
			currentVersion = getCurrentVersion(address);
			
			if(currentVersion > myVersion) {
				System.out.println("There is a new version of ACME Annuity available.  The latest version will be retrieved.");
				url2File(new URL(address + BUNDLE_FILE), BUNDLE_FILE);
			} else {
				System.out.println("ACME Annuity is currently up-to-date.");
			}

		} catch (MalformedURLException e) {
			System.out.println("Address is not a valid URL.");
			e.printStackTrace();
		} catch (ConnectException e) {
			System.out.println("The server could not be reached.  Is this the correct location?");
			System.out.println("URL: " + address);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Something went horribly wrong!");
			e.printStackTrace();
		}
	}
	
	public static int getLocalVersion(String filepath) throws IOException {
		File bundleVersion = new File(filepath);
		FileInputStream fsIn = new FileInputStream(bundleVersion);
		Properties myProps = new Properties(); 
		myProps.load(fsIn);
		int myVersion = Integer.parseInt(myProps.getProperty("buildNumber"));
		System.out.println("local version: " + myVersion);
		fsIn.close();
		return myVersion;
	}
	
	public static int getCurrentVersion(String address) throws IOException {
		URL url = new URL(address + VERSION_FILE);
		URLConnection connection = url.openConnection();
		InputStream httpIn = connection.getInputStream();
		Properties currentProps = new Properties();
		currentProps.load(httpIn);
		int currentVersion = Integer.parseInt(currentProps.getProperty("buildNumber"));
		System.out.println("current version: " + currentVersion);
		httpIn.close();
		return currentVersion;
	}
	
	public static void url2File(URL url, String fileName) throws IOException {
		URLConnection urlCon = url.openConnection();

		InputStream in = urlCon.getInputStream();
		stream2File(in, fileName);
	}
	
	public static void stream2File(InputStream in, String fileName) throws IOException {
		FileOutputStream out = new FileOutputStream(fileName);
		 // Transfer bytes from in to out
        byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) >0){
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
		System.out.println(BUNDLE_FILE + " downloaded successfully.");
	}

}
