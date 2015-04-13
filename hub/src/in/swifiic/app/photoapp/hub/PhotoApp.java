package in.swifiic.app.photoapp.hub;

import ibrdtn.example.api.DTNClient;
import in.swifiic.plat.helper.hub.Base;
import in.swifiic.plat.helper.hub.DatabaseHelper;
import in.swifiic.plat.helper.hub.Helper;
import in.swifiic.plat.helper.hub.SwifiicHandler;
import in.swifiic.plat.helper.hub.xml.Action;
import in.swifiic.plat.helper.hub.xml.Notification;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import com.mysql.jdbc.Blob;

public class PhotoApp extends Base implements SwifiicHandler {

	private static final Logger logger = LogManager.getLogManager().getLogger(
			"");
	private DTNClient dtnClient;

	protected ExecutorService executor = Executors.newCachedThreadPool();

	// Following is the name of the endpoint to register with
	protected String PRIMARY_EID = "photoapp";
	private static final int ROWS = 2;

	public PhotoApp() {
		// Initialize connection to daemon
		dtnClient = getDtnClient(PRIMARY_EID, this);
		logger.log(Level.INFO, dtnClient.getConfiguration());
	}

	static boolean exitFlag = false;

	@SuppressWarnings("null")
	public static void main(String args[]) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		PhotoApp photoApp = new PhotoApp();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				PhotoApp.exitFlag = true;
			}
		});

		if (args.length > 0 && args[0].equalsIgnoreCase("-D")) { // daemon mode
			while (!PhotoApp.exitFlag) {
				try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else
			while (!PhotoApp.exitFlag) {
				String input;
				System.out
						.print("\nEnter \"exit\" to exit application and \"send\" to send broadcast to devices: ");
				input = br.readLine();
				if (input.equalsIgnoreCase("exit")) {
					photoApp.exit();
				} else if (input.equalsIgnoreCase("send")) {
					// open connection to DB
					Connection connection = DatabaseHelper.connectToDB();
					Statement statement;
					String sql;
					String imgB64Str[] = new String[ROWS];
					ResultSet result;
					try {
						// get images from DB
						statement = connection.createStatement();
						sql = "(SELECT * FROM Photoapp_Uploads ORDER BY id DESC LIMIT "
								+ ROWS + ") ORDER BY id ASC;";
						result = statement.executeQuery(sql);
						System.out.println("done.");
						int i = 0;
						while (result.next()) {
							// Retrieve by column name
							String path = result.getString("path");
							imgB64Str[i] = fileToBase64(path);
							i++;
						}
						result.close();
						statement.close();
						// close connection to DB
						DatabaseHelper.closeDB(connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					Notification notif = new Notification("PhotosBroadcast",
							"PhotoApp", "2", "0.1", "Hub");
					notif.addArgument("count", Integer.toString(ROWS));
					for (int i = 0; i < ROWS; i++) {
						notif.addArgument("img" + i, imgB64Str[i]);
					}

					String payload = Helper.serializeNotification(notif);
					photoApp.sendGrp("dtn://in.swifiic.app.photoapp.andi/mc",
							payload);
					// Mark bundle as delivered...
					logger.log(Level.INFO, "Attempted to {0} send to {1}",
							new Object[] { payload,
									"dtn://in.swifiic.app.photoapp.andi/mc" });
				}
			}
	}

	@Override
	public void handlePayload(String payload, final Context ctx) {
		final String message = payload;
		System.out.println("Got Message:" + payload);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Action action = Helper.parseAction(message);
					Notification notif = new Notification(action);
					String imgString, fromUser;
					if (action.getOperationName().equalsIgnoreCase(
							"UploadPhoto")) {
						// get arguments from payload
						fromUser = action.getArgument("fromUser");
						imgString = action.getArgument("imageString");
						// write image to disk
						byte[] data = Base64.decodeBase64(imgString);
						DateFormat df = new SimpleDateFormat("yyMMddHHmmss");
						String sdt = df.format(new Date(System
								.currentTimeMillis()));
						String file_path = System.getProperty("user.home")
								+ "/swifiic/photoappUploads/" + sdt + fromUser
								+ ".jpg";
						File tmpFile = new File(file_path);
						try (FileOutputStream stream = new FileOutputStream(
								tmpFile)) {
							stream.write(data);
						}
						// open connection to DB
						Connection connection = DatabaseHelper.connectToDB();
						Statement statement;
						String sql;
						try {
							// add image to DB
							statement = connection.createStatement();
							sql = "INSERT INTO Photoapp_Uploads(user, path) VALUES (\""
									+ fromUser + "\", \"" + file_path + "\")";
							statement.executeUpdate(sql);
							System.out.println("done.");
							statement.close();
							// close connection to DB
							DatabaseHelper.closeDB(connection);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else if (action.getOperationName().equalsIgnoreCase(
							"UpvotePhoto")) {
					} else {
						logger.log(Level.SEVERE,
								"Invalid argument " + action.getOperationName());
						return;
					}
				} catch (Exception e) {
					logger.log(
							Level.SEVERE,
							"Unable to process message and send response\n"
									+ e.getMessage());
				}
			}
		});
	}

	/**
	 * borrowed from in.swifiic.plat.helper.andi.Helper TODO move to
	 * in.swifiic.plat.helper.hub.Helper
	 * 
	 * @param filePath
	 *            -> file location
	 * @return String -> Base64 encoded string of the file
	 */
	private static String fileToBase64(String filePath) {
		StringBuffer fileBuf = new StringBuffer();
		File readFile = new File(filePath);
		if (readFile.exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(readFile);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				try {
					for (int readNum; (readNum = fis.read(buf)) != -1;) {
						bos.write(buf, 0, readNum);
					}
				} catch (IOException ex) {
				}
				byte[] bytes = bos.toByteArray();
				fileBuf.append(Base64.encodeBase64String(bytes));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return fileBuf.toString();
	}
}
