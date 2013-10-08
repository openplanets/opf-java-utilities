package org.opf_labs.utils;
/**
 * 
 */


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Static utility class that gathers environment details for reporting.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1
 * 
 *          Created 27 Jul 2012:12:56:47
 */

public final class Environments {
	static final String JAVA_CPU_ISA_PROP = "sun.cpu.isalist";
	static final String JAVA_ARCH_PROP = "sun.arch.data.model";
	static final String JAVA_PROP_ROOT = "java";
	static final String JAVA_VM_PROP_ROOT = JAVA_PROP_ROOT + ".vm";
	static final String JAVA_HOME_PROP = JAVA_PROP_ROOT + ".home";
	static final String JAVA_VM_VENDOR_PROP = JAVA_VM_PROP_ROOT + ".vendor";
	static final String JAVA_VERSION_PROP = JAVA_PROP_ROOT + ".version";
	static final String USER_PROP_ROOT = "user";
	static final String USER_NAME_PROP = USER_PROP_ROOT + ".name";
	static final String USER_HOME_PROP = USER_PROP_ROOT + ".home";
	static final String USER_COUNTRY_PROP = USER_PROP_ROOT + ".country";
	static final String USER_LANGUAGE_PROP = USER_PROP_ROOT + ".language";
	static final String OS_PROP_ROOT = "os";
	static final String OS_NAME_PROP = OS_PROP_ROOT + ".name";
	static final String OS_VERSION_PROP = OS_PROP_ROOT + ".version";
	static final String OS_ARCH_PROP = OS_PROP_ROOT + ".arch";
	static final String HOST_NAME;
	static final String HOST_ADDRESS;
	static final String MACH_ADDRESS;
	static final String CPU_ISA;
	static final String JAVA_ARCH;
	static final String JAVA_HOME;
	static final String JAVA_VENDOR;
	static final String JAVA_VERSION;
	static final String USER_NAME;
	static final String USER_HOME;
	static final String USER_COUNTRY;
	static final String USER_LANGUAGE;
	static final String OS_NAME;
	static final String OS_VERSION;
	static final String OS_ARCH;
	static {
		try {
			InetAddress address = InetAddress.getLocalHost(); 
			HOST_NAME = address.getHostName();
			HOST_ADDRESS = address.getHostAddress();
			NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
			if (nwi != null) {
				byte[] mac = nwi.getHardwareAddress();
				StringBuilder sb = new StringBuilder();
				if (mac != null) {
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", Byte.valueOf(mac[i]), (i < mac.length - 1) ? "-" : ""));
					}
				}
				MACH_ADDRESS = sb.toString();
			} else
				MACH_ADDRESS = "";
			CPU_ISA = System.getProperty(JAVA_CPU_ISA_PROP);
			JAVA_ARCH = "x" + System.getProperty(JAVA_ARCH_PROP);
			JAVA_HOME = System.getProperty(JAVA_HOME_PROP);
			JAVA_VENDOR = System.getProperty(JAVA_VM_VENDOR_PROP);
			JAVA_VERSION = System.getProperty(JAVA_VERSION_PROP);
			USER_NAME = System.getProperty(USER_NAME_PROP);
			USER_HOME = System.getProperty(USER_HOME_PROP);
			USER_COUNTRY = System.getProperty(USER_COUNTRY_PROP);
			USER_LANGUAGE = System.getProperty(USER_LANGUAGE_PROP);
			OS_NAME = System.getProperty(OS_NAME_PROP);
			OS_VERSION = System.getProperty(OS_VERSION_PROP);
			OS_ARCH = System.getProperty(OS_ARCH_PROP);
		} catch (UnknownHostException excep) {
			throw new IllegalStateException(
					"Illegal length IP address, check your setup.", excep);
		} catch (SocketException excep) {
			throw new IllegalStateException(
					"Socket Exception when finding MACH address.", excep);
		}
	}
	/** ISO Date pattern for ISO 8601 Date */
	static final String ISO8601_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	/** {@link DateFormat} for ISO8601 date */
	static DateFormat IOS8601_DATE_FORMATTER = new SimpleDateFormat(
			ISO8601_DATE_PATTERN);

	private Environments() {
		throw new AssertionError("In Environments no-arg constructor.");
	}
	/**
	 * @return the machine host name
	 */
	public static final String getHostName() {
		return HOST_NAME;
	}
	
	/**
	 * @return the hosts ip address
	 */
	public static final String getHostAddress() {
		return HOST_ADDRESS;
	}

	/**
	 * @return the hosts mach address
	 */
	public static final String getMachAddress() {
		return MACH_ADDRESS;
	}

	/**
	 * @return the CPU details
	 */
	public static final String getCPUIsa() {
		return CPU_ISA;
	}

	/**
	 * @return summary of host details
	 */
	public static final String getHostSummary() {
		return "{\"name\":\"" + Environments.getHostName() + "\",\"CPU\":\"" + CPU_ISA
				+ "\"}";
	}

	/**
	 * @return java vendor
	 */
	public static final String getJavaVendor() {
		return JAVA_VENDOR;
	}

	/**
	 * @return java version
	 */
	public static final String getJavaVersion() {
		return JAVA_VERSION;
	}

	/**
	 * @return java architecture
	 */
	public static final String getJavaArch() {
		return JAVA_ARCH;
	}

	/**
	 * @return java home
	 */
	public static final String getJavaHome() {
		return JAVA_HOME;
	}

	/**
	 * @return summary of java details
	 */
	public static final String getJavaSummary() {
		return "Java [vendor:" + JAVA_VENDOR + ", version:" + JAVA_VERSION
				+ JAVA_ARCH + ", home:" + JAVA_HOME + "]";
	}

	/**
	 * @return user name
	 */
	public static final String getUserName() {
		return USER_NAME;
	}

	/**
	 * @return user home dir
	 */
	public static final String getUserHome() {
		return USER_HOME;
	}

	/**
	 * @return user country
	 */
	public static final String getUserCountry() {
		return USER_COUNTRY;
	}

	/**
	 * @return user language
	 */
	public static final String getUserLanguage() {
		return USER_LANGUAGE;
	}

	/**
	 * @return summary of user details
	 */
	public static final String getUserSummary() {
		return "user [name:" + USER_NAME + ", country:" + USER_COUNTRY
				+ ", lang:" + USER_LANGUAGE + ", home:" + USER_HOME + "]";
	}

	/**
	 * @return the operating system name
	 */
	public static final String getOSName() {
		return OS_NAME;
	}

	/**
	 * @return the operating system verison
	 */
	public static final String getOSVersion() {
		return OS_VERSION;
	}

	/**
	 * @return the operating system architecture
	 */
	public static final String getOSArch() {
		return OS_ARCH;
	}

	/**
	 * @return a summary of os details
	 */
	public static final String getOSSummary() {
		return "os [name:" + OS_NAME + ", version:" + OS_VERSION + ", arch:"
				+ OS_ARCH + "]";
	}

	/**
	 * @param date a java.util.Date instance to format
	 * @return the date as an ISO formatted string
	 */
	public static final String toISO8601String(Date date) {
		return IOS8601_DATE_FORMATTER.format(date);
	}

	/**
	 * @return a summary of the detected Environments.
	 */
	public static final String getSummary() {
		return getHostSummary() + "\n" + getJavaSummary() + "\n"
				+ getUserSummary() + "\n" + getOSSummary();
	}

	/**
	 * @return true if OS is windows
	 */
	public static boolean isWindows() {
		return (OS_NAME.toLowerCase().indexOf("win") >= 0);
	}
 
	/**
	 * @return true if OS is mac
	 */
	public static boolean isMac() {
 		return (OS_NAME.toLowerCase().indexOf("mac") >= 0);
	}
 
	/**
	 * @return true if os is *nix
	 */
	public static boolean isUnix() {
		return (OS_NAME.toLowerCase().indexOf("nix") >= 0 || OS_NAME.indexOf("nux") >= 0);
	}
 
	/**
	 * @return true if OS is solaris
	 */
	public static boolean isSolaris() {
 		return (OS_NAME.toLowerCase().indexOf("sunos") >= 0);
 	}
 }
