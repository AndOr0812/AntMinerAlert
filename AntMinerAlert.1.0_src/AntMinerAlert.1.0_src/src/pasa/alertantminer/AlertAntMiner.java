package pasa.alertantminer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import pasa.alertantminer.utils.LoggerSession;

public class AlertAntMiner {	
	private Session session = null;
	public static String COMMAND_STATS    = "cgminer-api -o stats";
	public static String FILE_PATH        = "conf/";
	public static String FILE_EXT         = ".properties";
	public static String APPLICATION_NAME = "AAM";
	public static List<String> fileList   = new Vector<String>();
	public static LoggerSession loggerSession = new LoggerSession("SES_" + System.nanoTime(), APPLICATION_NAME);

	private String fileName   = "";
	private String ip         = "";
	private String userName   = "";
	private String password   = "";
	private String stats      = "";
	private String recipients = null;

	private HashMap<String,String> statsMap = new HashMap<String,String>();
	private List<String> alertList = new Vector<String>();
	private List<String> alertMail = new Vector<String>();

	public static void main(String[] args) {		
		loggerSession.info("Start AlertAntMiner v.1.00");
		File currentPath = new File("./" + FILE_PATH);
		File[] files = currentPath.listFiles();
		for(File next : files) {
			if(next.isFile() && next.getName().endsWith(FILE_EXT)) {
				fileList.add(next.getName());
			}
		}

		for(String nextFile : fileList) {
			AlertAntMiner miner = null;
			try {
				miner = new AlertAntMiner(nextFile);
				miner.init();
				miner.connect();
				miner.getStatus(COMMAND_STATS);
				miner.parseResult();
				miner.checkAlert();
				miner.mail();
			} catch (Exception e) {
				loggerSession.error(e);
			} finally {
				if(miner != null) {
					miner.disconnect();
				}
			}
		}
	}

	public AlertAntMiner(String fileName) {
		this.fileName = fileName;		
	}
	
	public void init() throws IOException {
		loggerSession.info("Init: " + FILE_PATH + fileName);
		InputStream fis = null;
		InputStreamReader ir = null;
		BufferedReader br = null;
		
		try {

			fis = new FileInputStream(new File(FILE_PATH + fileName));
			ir = new InputStreamReader(fis);
			br = new BufferedReader(ir);
			String line;
			boolean alert = false;

			while ((line = br.readLine()) != null) {
				if(alert) {
					if(line.length() > 0) {
						alertList.add(line);
					}
				} else {
					if(line.startsWith("userName=")) 	userName   = line.substring(9);
					if(line.startsWith("password=")) 	password   = line.substring(9);
					if(line.startsWith("ip=")) 			ip         = line.substring(3);
					if(line.startsWith("recipients=")) 	recipients = line.substring(11);
					if(line.startsWith("#ALERT")) 		alert      = true;
				}
			}
		} finally {
			if(br != null) 	 
				br.close();
			if(ir != null) 	 
				ir.close();
			if(fis != null)  
				fis.close();
		}
	}

	public void connect() throws JSchException {
		loggerSession.info("-- Connect");

		JSch jsch=new JSch();
		session=jsch.getSession(userName, ip, 22);
		session.setPassword(password);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
	}

	public void disconnect() {
		loggerSession.info("-- disconnect");
		if(session != null) {
			session.disconnect();
		}
	}

	public void getStatus(String command) throws JSchException, IOException {
		loggerSession.info("-- GetStatus");

		StringBuffer result = new StringBuffer();
		ChannelExec channel=(ChannelExec) session.openChannel("exec");
		BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));
		channel.setCommand(command);
		channel.connect();

		String msg=null;
		while((msg=in.readLine())!=null){
			result.append(msg);
		}
		channel.disconnect();
		stats = result.toString();
	}

	public void parseResult() throws Exception  {
		loggerSession.info("-- Parse Result");
		loggerSession.info(stats);
		
		if(stats.length() < 10) {
			throw new Exception("ERROR: The result received from the AntMiner is not correct. Please check the configuration.!!!! ");
		}

		String[] part1 = stats.split("\\|");
		String[] part2 = part1[2].split(",");
		for(int j=0; j<part2.length; j++) {
			String[] keyval = part2[j].split("=");
			statsMap.put(keyval[0].trim(), keyval[1].trim());
		}
	}

	public void checkAlert() {
		loggerSession.info("-- Check Result");

		for(String alert : alertList) {
			if(alert.contains(">")) {
				String[] keyval = alert.split(">");
				BigDecimal valStat = new BigDecimal(statsMap.get(keyval[0].trim()));
				BigDecimal valAlert = new BigDecimal(keyval[1].trim());
				if(valStat.compareTo(valAlert) == 1) {
					loggerSession.info("!! Alert: " + alert + " Current Value: " + valStat);
					alertMail.add("<tr><td>!! Alert:</td><td>" + alert + "</td><td>Current Value: " + valStat + "</td></tr>");
				}
			}
			if(alert.contains("<")) {
				String[] keyval = alert.split("<");
				BigDecimal valStat = new BigDecimal(statsMap.get(keyval[0].trim()));
				BigDecimal valAlert = new BigDecimal(keyval[1].trim());
				if(valStat.compareTo(valAlert) == -1) {
					loggerSession.info("!! Alert: " + alert + " Current Value: " + valStat);
					alertMail.add("<tr><td>!! Alert:</td><td>" + alert + "</td><td>Current Value: " + valStat + "</td></tr>");
				}
			}
			if(alert.contains("=")) {
				String[] keyval = alert.split("=");
				BigDecimal valStat = new BigDecimal(statsMap.get(keyval[0].trim()));
				BigDecimal valAlert = new BigDecimal(keyval[1].trim());
				if(valStat.compareTo(valAlert) == 0) {
					loggerSession.info("!! Alert: " + alert + " Current Value: " + valStat);
					alertMail.add("<tr><td>!! Alert:</td><td>" + alert + "</td><td>Current Value: " + valStat + "</td></tr>");
				}
			}
			if(alert.contains("ISNOT")) {
				String[] keyval = alert.split("ISNOT");
				String valStat = statsMap.get(keyval[0].trim());
				String valAlert = keyval[1].trim();
				if(!valStat.equals(valAlert)) {
					loggerSession.info("!! Alert: " + alert.substring(0, alert.indexOf("ISNOT")+5) + " " + alert.substring(alert.indexOf("ISNOT")+5).trim());
					String c =    " Current: ";
					for(int i=-6; i<alert.indexOf("ISNOT"); i++) {
						c = c + " ";
					}
					loggerSession.info(c + valStat);
					alertMail.add("<tr><td>!! Alert:</td><td>" + alert.substring(0, alert.indexOf("ISNOT")+5) + "</td><td>" + alert.substring(alert.indexOf("ISNOT")+5).trim() + "</td></tr>");
					alertMail.add("<tr><td colspan=\"2\"><td>" + valStat.trim() + "</td></tr>");
				}
			}
		}
	}

	public void mail() throws AddressException, MessagingException {
		if(alertMail.isEmpty()) {
			loggerSession.info("-- No Alert, No Mail");
			return;
		}
		loggerSession.info("-- Mail Alert");

		String user = "XXXXXX@gmail.com";
		String pwd  = "XXXXXX";
		
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", true); // added this line
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.password", pwd);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);

		javax.mail.Session ses = javax.mail.Session.getInstance(props, new GMailAuthenticator(user, pwd));

        MimeMessage message = new MimeMessage(ses);
        message.setSender(new InternetAddress(user));
        message.setSubject("Alert Antminer (" + fileName + ")");

        StringBuffer htmlTxt = new StringBuffer();
        htmlTxt.append("<table>");
		for(String msg : alertMail) {
			htmlTxt.append(msg + "\r\n");
		}
		htmlTxt.append("</table>");

        message.setContent(htmlTxt.toString(), "text/html");

        if (recipients.indexOf(',') > 0)
        	message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
        	message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        Transport.send(message);
	}

}
