import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class csieiodns {

	static String CsieIoDNSVersion = "csie.io DDNS Updater";
	String TOOL_TIP = CsieIoDNSVersion;
	static String StatusMsg = "";
	String MESSAGE_HEADER = CsieIoDNSVersion;
	TrayIcon trayIcon;
	String ipaddress = "";
	static int timercount = 0; 
	static TrayIcon processTrayIcon = null;
	String OldIp = "";

	public static void main(String[] args) {
		try {
			System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
			System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
			
			csieiodns systemTrayExample = new csieiodns();
			systemTrayExample.createAndAddApplicationToSystemTray();
			systemTrayExample.startProcess();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 */
	 private void createAndAddApplicationToSystemTray() throws IOException {
		 // Check the SystemTray support
		 if (!SystemTray.isSupported()) {
			 return;
		 }

		 final PopupMenu popup = new PopupMenu();
		 ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		 InputStream inputStream = classLoader.getResourceAsStream("ccu_logo.png");
		 Image img = ImageIO.read(inputStream);

		 trayIcon = new TrayIcon(img, TOOL_TIP);
		 this.processTrayIcon = trayIcon;
		 final SystemTray tray = SystemTray.getSystemTray();

		 MenuItem aboutItem = new MenuItem("����");
		 MenuItem settingsItem = new MenuItem("�]�w");
		 MenuItem forceupdateItem = new MenuItem("�j����sIP");
		 MenuItem whatsmyipaddress = new MenuItem("��ܥثeIP��m"); 
		 MenuItem exitItem = new MenuItem("�����{��");

		 popup.add(settingsItem);
		 popup.add(forceupdateItem);
		 popup.add(whatsmyipaddress);
		 popup.addSeparator();
		 popup.add(aboutItem);
		 popup.add(exitItem);

		 trayIcon.setPopupMenu(popup);
		 trayIcon.setImageAutoSize(true);

		 try {
			 tray.add(trayIcon);
		 } catch (AWTException e) {
			 return;
		 }

		 trayIcon.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 JOptionPane.showMessageDialog(null,
						 "�Цbicon�W�I��k��i��]�w!");
			 }
		 });

		 settingsItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {

				 Preferences prefs = Preferences.userNodeForPackage(csieiodns.class);


				 JPanel panel=new JPanel();  
				 panel.setLayout(new GridLayout(5,1));  
				 JLabel domain=new JLabel("Hostname");  
				 JLabel token=new JLabel("Token");
				 JLabel sett=new JLabel("�z�i�H�b�����z���]�w: ");
				 JLabel minute=new JLabel("�ˬd�W�v (����)");
				 JLabel update=new JLabel("��ܧ�s��T");  
				 JTextField domainField=new JTextField(30);  
				 JTextField tokenField=new JTextField(30);  

				 String[] minuteStrings = { "5", "10", "15", "30", "60" };
				 JComboBox minuteField =new JComboBox(minuteStrings);


				 String[] UpdateMessages = { "YES", "NO"};
				 JComboBox UpdateField=new JComboBox(UpdateMessages);  


				 domainField.setText(prefs.get("domain", ""));
				 tokenField.setText(prefs.get("token", ""));
				 minuteField.setSelectedItem(prefs.get("refresh", "5"));
				 UpdateField.setSelectedItem(prefs.get("updatemessages", "YES"));

				 // html content
				 JEditorPane ep2 = new JEditorPane("text/html","<html><a href=\"https://csie.io/dashboard\">https://csie.io/dashboard</a></html>");
				 JLabel label = new JLabel();
				 Font font = label.getFont();
				 StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
				 style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
				 style.append("font-size:" + font.getSize() + "pt;");

				 // handle link events
				 ep2.addHyperlinkListener(new HyperlinkListener()
				 {
					 public void hyperlinkUpdate(HyperlinkEvent e)
					 {
						 if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
							 launchUrl(e.getURL().toString()); 
						 }
					 }
				 });
				 ep2.setEditable(false);
				 ep2.setBackground(label.getBackground());

				 panel.add(domain);  
				 panel.add(domainField);  
				 panel.add(token);  
				 panel.add(tokenField);
				 panel.add(minute);  
				 panel.add(minuteField);
				 panel.add(update);  
				 panel.add(UpdateField);
				 panel.add(sett);
				 panel.add(ep2);


				 //Create a window using JFrame with title ( Two text component in JOptionPane )  
				 JFrame frame=new JFrame("csie.io DDNS�]�w");  

				 //Set default close operation for JFrame  
				 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  

				 //Set JFrame size  
				 frame.setSize(300,300);  

				 //Set JFrame locate at center  
				 frame.setLocationRelativeTo(null);  

				 //Make JFrame visible  
				 frame.setVisible(false);  

				 //Show JOptionPane that will ask user for username and password  
				 int a=JOptionPane.showConfirmDialog(frame,panel,"csie.io DDNS�]�w",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);


				 //Operation that will do when user click 'OK'  
				 if(a==JOptionPane.OK_OPTION)  
				 {  

					 if (domainField.getText().length()>0 && tokenField.getText().length()>0)  
					 {  
						 prefs.put("domain", domainField.getText() );
						 prefs.put("token", tokenField.getText() );
						 prefs.put("refresh", minuteField.getSelectedItem().toString());
						 String ip =  getip();
						 //  prefs.put("oldipaddress", ip);
						 prefs.put("updatemessages", UpdateField.getSelectedItem().toString());

						 String resultofipcheck = updateCsieIoDNS(prefs.get("domain", ""), prefs.get("token", ""), ip);

						 if (resultofipcheck.equals("KO")) {
							 JOptionPane.showMessageDialog(frame,"csie.io DDNS Error: ���~��Hostname��token!\n�Эץ��z����J��A�դ@��!",CsieIoDNSVersion,JOptionPane.INFORMATION_MESSAGE);
							 prefs.put("domain", "");
						 }

						 if (resultofipcheck.equals("OK")) {
							 JOptionPane.showMessageDialog(frame,"csie.io DDNS�]�w���\!",CsieIoDNSVersion,JOptionPane.INFORMATION_MESSAGE);
							 updatetraytip();
							 timercount=(Integer.parseInt(prefs.get("refresh", "5")) *60);
						 }

						 if ((!resultofipcheck.equals("OK"))  && (!resultofipcheck.equals("KO")))  {
							 JOptionPane.showMessageDialog(frame,"�L�k�s�u��csie.io�еy��A��!",CsieIoDNSVersion,JOptionPane.INFORMATION_MESSAGE);
						 }
					 } else if ((domainField.getText().length()<1) && (tokenField.getText().length()<1)) {
						 JOptionPane.showMessageDialog(frame,"���~��Hostname��token!\n�Эץ��z����J��A�դ@��!","False",JOptionPane.ERROR_MESSAGE);
						 prefs.put("domain", domainField.getText() );
						 prefs.put("token", tokenField.getText() );
						 prefs.put("refresh", minuteField.getSelectedItem().toString());
					 } else if (domainField.getText().length()<1) {
						 JOptionPane.showMessageDialog(frame,"�п�J�z��csie.io DDNS hostname","False",JOptionPane.ERROR_MESSAGE);
						 prefs.put("domain", domainField.getText() );
						 prefs.put("token", tokenField.getText() );
						 prefs.put("refresh", minuteField.getSelectedItem().toString());

					 } else if (tokenField.getText().length()<1) {
						 JOptionPane.showMessageDialog(frame,"�п�J�z��csie.io DDNS Token","False",JOptionPane.ERROR_MESSAGE);
						 prefs.put("domain", domainField.getText() );
						 prefs.put("token", tokenField.getText() );
						 prefs.put("refresh", minuteField.getSelectedItem().toString());

					 }  
				 }  

				 //Operation that will do when user click 'Cancel'  
				 else if(a==JOptionPane.CANCEL_OPTION)  
				 {  
					 JOptionPane.showMessageDialog(frame,"�����]�w!");  
				 }  
			 }    });

		 // Add listener to aboutItem.
		 aboutItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {

				 // for copying style
				 JLabel label = new JLabel();
				 Font font = label.getFont();

				 // create some css from the label's font
				 StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
				 style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
				 style.append("font-size:" + font.getSize() + "pt;");

				 // html content
				 JEditorPane ep = new JEditorPane("text/html",CsieIoDNSVersion+"<br>���A�ȥ�csie.io����, �Բӻ����еn�J: <br><html><a href=\"https://csie.io/dashboard\">csie.io DNS�޲z����</a></html>");

				 // handle link events
				 ep.addHyperlinkListener(new HyperlinkListener()
				 {
					 public void hyperlinkUpdate(HyperlinkEvent e)
					 {
						 if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
							 launchUrl(e.getURL().toString()); // roll your own link launcher or use Desktop if J6+
						 }
					 }
				 });
				 ep.setEditable(false);
				 ep.setBackground(label.getBackground());

				 // show
				 JOptionPane.showMessageDialog(null, ep);
			 }
		 });

		 // Add listener to forceupdateItem.
		 forceupdateItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 Preferences prefs = Preferences.userNodeForPackage(csieiodns.class);
				 timercount=(Integer.parseInt(prefs.get("refresh", "5")) *60);
				 updatetraytip();


			 }
		 });


		 // Add listener to whatsmyipaddress.
		 whatsmyipaddress.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {


				 String ip = getip();

				 // for copying style
				 JLabel label = new JLabel();
				 Font font = label.getFont();

				 // create some css from the label's font
				 StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
				 style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
				 style.append("font-size:" + font.getSize() + "pt;");

				 // html content
				 JEditorPane ep = new JEditorPane("text/html","�z���~��IP��m��: \n<html><a href=\"http://checkip.amazonaws.com\">"+ip+"</a></html>");

				 // handle link events
				 ep.addHyperlinkListener(new HyperlinkListener()
				 {
					 public void hyperlinkUpdate(HyperlinkEvent e)
					 {
						 if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
							 launchUrl(e.getURL().toString()); // roll your own link launcher or use Desktop if J6+
						 }
					 }
				 });
				 ep.setEditable(false);
				 ep.setBackground(label.getBackground());

				 // show
				 JOptionPane.showMessageDialog(null, ep);

			 }
		 });

		 exitItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 tray.remove(trayIcon);
				 System.exit(0);
			 }
		 });
	 }



	 private static String getip()
	 {
		 Document doc = null;   
		 String url = "http://checkip.amazonaws.com";
		 String ip = "";

		 try {
			 doc = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).timeout(10 * 1000).get();
			 ip = doc.text();
		 } catch (IOException e) {e.printStackTrace();}
		 if (doc.text().length() < 7) {
			 // can't get ip address, let CsieIoDNS to resolve it
			 ip = "";
		 }
		 return ip;
	 }


	 private static String updateCsieIoDNS(String domain, String token, String ipaddress)
	 {
		 String url = "https://csie.io/update?hn="+domain+"&token="+token+"&ip="+ipaddress;
		 Document doc = null;
		 String ua = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2"; 
		 try {
			 //	doc = Jsoup.connect(url).ignoreHttpErrors(true).timeout(10 * 1000).get();
			 doc = Jsoup.connect(url).userAgent(ua).ignoreHttpErrors(true).ignoreContentType(true).timeout(10 * 1000).get();

		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 System.out.println(doc.text());
		 return doc.text();
	 }


	 private static void updatetraytip()
	 {
		 Preferences prefs = Preferences.userNodeForPackage(csieiodns.class);

		 DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
		 Calendar cal2 = Calendar.getInstance();
		 cal2.add(Calendar.SECOND, ((Integer.parseInt(prefs.get("refresh", "5"))*60)-timercount) );

		 processTrayIcon.setToolTip(CsieIoDNSVersion+StatusMsg+"\n�W����s�ɶ�: "+dateFormat.format(cal2.getTime()) + "\n�ˬd�W�v: "+prefs.get("refresh", "5")+" ����.");

	 }

	 private static void launchUrl(String urlToLaunch)
	 {
		 try {
			 if ( !Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported( java.awt.Desktop.Action.BROWSE) ){
				 JOptionPane.showMessageDialog(null, "On this computer java cannot open automatically url in browser, you have to copy/paste it manually.");
				 return;
			 }

			 Desktop desktop = Desktop.getDesktop();
			 URI uri = new URI(urlToLaunch);

			 desktop.browse(uri);
		 } catch (URISyntaxException ex) {
			 JOptionPane.showMessageDialog(null, "Url ["  + urlToLaunch + "] seems to be invalid ");
		 } catch (IOException ex) {
			 JOptionPane.showMessageDialog(null, "There was some error opening the url. \n Details:\n" + ex.getMessage());
		 }


	 }

	 private void startProcess() {

		 Thread thread = new Thread(new Runnable() {

			 @Override
			 public void run() {
				 Preferences prefs = Preferences.userNodeForPackage(csieiodns.class);

				 Timer timer = new Timer("Repeater");
				 MyTask t = new MyTask();
				 timer.schedule(t, 0, 1000);

				 timercount=(Integer.parseInt(prefs.get("refresh", "5")) *60);

			 }
		 });

		 thread.start();
	 }


	 class MyTask extends TimerTask {

		 @Override public void run() {

			 Preferences prefs = Preferences.userNodeForPackage(csieiodns.class);
			 timercount++;

			 if (timercount > (Integer.parseInt(prefs.get("refresh", "5")) *60) ) {


				 // repeat on interval
				 try {

					 //Thread.sleep(Integer.parseInt(prefs.get("refresh", "5"))*60000);

					 String resultofipcheck;
					 String ip;

					 if ((prefs.get("domain", "").length()>0) && (prefs.get("token", "").length()>0)) {
						 ip =  getip();
						 OldIp = InetAddress.getByName(prefs.get("domain", "")+".csie.io").toString().replace(prefs.get("domain", "")+".csie.io/", "");

						 // check if IP address is different from the last one. Only update is it differs (saving calls to CsieIoDNS).
						 if (!ip.equals(OldIp)) {

							 resultofipcheck = updateCsieIoDNS(prefs.get("domain", ""), prefs.get("token", ""), ip);
							 if (resultofipcheck.equals("KO")) {
								 processTrayIcon.displayMessage(CsieIoDNSVersion,"CsieIoDNS Error: ���~��Hostname��token!\n�I���k����]�w!", TrayIcon.MessageType.INFO);                
							 }

							 if (resultofipcheck.equals("OK")) {

								 if (prefs.get("updatemessages", "YES").equals("YES")) {
									 processTrayIcon.displayMessage(CsieIoDNSVersion,"csie.io DDNS Client�w�g���\��s\n�z�ثe��IP��m��: "+ip+"!", TrayIcon.MessageType.INFO);
									 StatusMsg = "\n��IP: "+OldIp+"\n�sIP: "+ip;
									 // prefs.put("oldipaddress", ip);
								 }

							 }

							 if ((!resultofipcheck.equals("OK"))  && (!resultofipcheck.equals("KO")))  {
								 processTrayIcon.displayMessage(CsieIoDNSVersion,"�L�k�s�u��csie.io!", TrayIcon.MessageType.INFO);                
							 }

						 } else {
							 if (prefs.get("updatemessages", "YES").equals("YES")) {
								 //processTrayIcon.displayMessage(CsieIoDNSVersion,"Update not necessary!\nOld IP Address: "+prefs.get("oldipaddress", "")+"!\nNew IP Address: "+ip+"!", TrayIcon.MessageType.INFO);
								 StatusMsg = "\nIP�S������: "+OldIp+"";
							 }

						 }
						 updatetraytip();
					 } else {
						 processTrayIcon.displayMessage(CsieIoDNSVersion,"CsieIoDNS Error: ���~��Hostname��token!\n�I���k����]�w!", TrayIcon.MessageType.INFO);
					 }

				 } catch (Exception e) {
					 e.printStackTrace();
				 }

				 //after ran on schedule reset back to 0
				 timercount=0;
			 }
		 }


	 }
}
