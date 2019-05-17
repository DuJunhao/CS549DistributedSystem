package edu.stevens.cs549.ftpserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Stack;
import java.util.logging.Logger;

import edu.stevens.cs549.ftpinterface.IServer;

/**
 *
 * @author dduggan
 */
public class Server extends UnicastRemoteObject implements IServer {

	static final long serialVersionUID = 0L;

	public static Logger log = Logger.getLogger("edu.stevens.cs.cs549.ftpserver");

	/*
	 * For multi-homed hosts, must specify IP address on which to bind a server
	 * socket for file transfers. See the constructor for ServerSocket that allows
	 * an explicit IP address as one of its arguments.
	 */
	private InetAddress host;

	final static int backlog = 5;

	/*
	 *********************************************************************************************
	 * Current working directory.
	 */
	static final int MAX_PATH_LEN = 1024;
	private Stack<String> cwd = new Stack<String>();

	/*
	 *********************************************************************************************
	 * Data connection.
	 */

	enum Mode {
		NONE, PASSIVE, ACTIVE
	};

	private Mode mode = Mode.NONE;

	/*
	 * If passive mode, remember the server socket.
	 */

	private ServerSocket dataChan = null;

	private InetSocketAddress makePassive() throws IOException {
		dataChan = new ServerSocket(0, backlog, host);
		writeTolog("the localsocket is:" + dataChan.getLocalSocketAddress()+":"+dataChan.getLocalPort() + " backlog is" + backlog + " and the host is " + host + " MODE is passive");
		log.info("the localsocket is:" + dataChan.getLocalSocketAddress()+":"+dataChan.getLocalPort() + " backlog is "+ backlog + " and the host is " + host + " MODE is passive");
		mode = Mode.PASSIVE;
		//dataChan.bind(dataChan.getLocalSocketAddress());
		return (InetSocketAddress) (dataChan.getLocalSocketAddress());
	}

	/*
	 * If active mode, remember the client socket address.
	 */
	private static InetSocketAddress clientSocket = null;

	private void makeActive(InetSocketAddress s) {
		clientSocket = s;
		mode = Mode.ACTIVE;
		
		try {
			writeTolog("the clientsocket is:" +clientSocket.getHostName()+" the clientsocket port is"+clientSocket.getPort());
			log.info("the clientsocket is:" +clientSocket.getHostName()+" the clientsocket port is"+clientSocket.getPort());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 **********************************************************************************************
	 */

	/*
	 * The server can be initialized to only provide subdirectories of a directory
	 * specified at start-up.
	 */
	private final String pathPrefix;

	@SuppressWarnings("static-access")
	public Server(InetAddress host, int port, String prefix) throws IOException {
		super(port);
		this.host = host;
		this.pathPrefix = prefix + "/";
		log.info("A client has bound to a server instance."+"the hostName is:"+host.getHostName()+" the hostAddress is:"+host.getHostAddress()+" raw host is:"+host+"\n the local host is:"+host.getLocalHost()+"the port is:"+port+" and the pathPrefix is"+prefix+"/");
	}

	public Server(InetAddress host, int port) throws IOException {
		this(host, port, "/");
	}

	private boolean valid(String s) {
		// File names should not contain "/".
		return (s.indexOf('/') < 0);
	}

	private static class GetThread implements Runnable {
		private ServerSocket dataChan = null;
		private FileInputStream file = null;
		private FileOutputStream uploadedfile = null;

		public GetThread(ServerSocket s, FileInputStream f) {
			dataChan = s;
			file = f;
		}

		public GetThread(ServerSocket s, FileOutputStream f) {
			dataChan = s;
			uploadedfile = f;
		}

		public void run() {
			/*
			 * TOD: Process a client request to transfer a file.
			 */
			try {
				log.info("the mode is passive and the serverSocket dataChan is"+dataChan.getLocalSocketAddress()+":"+dataChan.getLocalPort());
				Socket xfer = dataChan.accept();
				log.info("serverSocket accepted!!!");
				InputStream iStream = null;
				OutputStream oStream = null;
				if (file == null) {
					iStream = xfer.getInputStream();
				} else
					iStream = file;
				if (uploadedfile == null) {
					oStream = xfer.getOutputStream();
				} else
					oStream = uploadedfile;
				byte[] buf = new byte[512];
				int nbytes = iStream.read(buf, 0, 512);
				while (nbytes > 0) {
					oStream.write(buf, 0, nbytes);
					nbytes = iStream.read(buf, 0, 512);
				}
				iStream.close();
				oStream.close();
			} catch (Exception e) {
				System.out.print(e);
				e.printStackTrace();
			}
		}
	}

	public void get(String file) throws IOException, FileNotFoundException, RemoteException {
		if (!valid(file)) {
			throw new IOException("Bad file name: " + file);
		} else if (mode == Mode.ACTIVE) {
			Socket xfer = new Socket(clientSocket.getAddress(), clientSocket.getPort());
			writeTolog("the mode is active and the clientsocket address is" + clientSocket.getAddress() + "and the client socket port is"
					+ clientSocket.getPort());
			log.info("the mode is active and the clientsocket address is" + clientSocket.getAddress() + "and the client socket port is"
					+ clientSocket.getPort());
			/*
			 * TOD: connect to client socket to transfer file.
			 */
			InputStream in = new FileInputStream(path() + file);
			OutputStream os = xfer.getOutputStream();
			byte[] buf = new byte[512];
			int nbytes = in.read(buf, 0, 512);
			while (nbytes > 0) {
				os.write(buf, 0, 512);
				nbytes = in.read(buf, 0, 512);
			}
			in.close();
			os.close();
			xfer.close();
			/*
			 * End TOD.
			 */
		} else if (mode == Mode.PASSIVE) {
			FileInputStream f = new FileInputStream(path() + file);
			log.info("serverSocket in passive mode is running and localSocketAddress is:"+dataChan.getLocalSocketAddress()+" port is:"+dataChan.getLocalPort()+" is Closed?:"+dataChan.isClosed());
			log.info("the mode is in the passive");
			new Thread(new GetThread(dataChan, f)).start();
		}
	}

	public void put(String file) throws IOException, FileNotFoundException, RemoteException {
		/*
		 * TOD: Finish put (both ACTIVE and PASSIVE).
		 */
		if (!valid(file)) {
			throw new IOException("Bad file name: " + file);
		} else if (mode == Mode.ACTIVE) {
			Socket xfer = new Socket(clientSocket.getAddress(), clientSocket.getPort());

			InputStream in = xfer.getInputStream();
			OutputStream os = new FileOutputStream(path() + file);
			byte[] buf = new byte[512];
			int nbytes = in.read(buf, 0, 512);
			while (nbytes > 0) {
				os.write(buf, 0, 512);
				nbytes = in.read(buf, 0, 512);
			}
			in.close();
			os.close();
			xfer.close();
		} else if (mode == Mode.PASSIVE) {
			FileOutputStream f = new FileOutputStream(path() + file);
			new Thread(new GetThread(dataChan, f)).start();
		}
	}

	public String[] dir() throws RemoteException {
		// List the contents of the current directory.
		return new File(path()).list();
	}

	public void cd(String dir) throws IOException, RemoteException {
		// Change current working directory (".." is parent directory)
		if (!valid(dir)) {
			throw new IOException("Bad file name: " + dir);
		} else {
			if ("..".equals(dir)) {
				if (cwd.size() > 0)
					cwd.pop();
				else
					throw new IOException("Already in root directory!");
			} else if (".".equals(dir)) {
				;
			} else {
				File f = new File(path());
				if (!f.exists())
					throw new IOException("Directory does not exist: " + dir);
				else if (!f.isDirectory())
					throw new IOException("Not a directory: " + dir);
				else
					cwd.push(dir);
			}
		}
	}

	public String pwd() throws RemoteException {
		// List the current working directory.
		String p = "/";
		for (Enumeration<String> e = cwd.elements(); e.hasMoreElements();) {
			p = p + e.nextElement() + "/";
		}
		return p;
	}

	public void logFile() throws IOException {
		File logFile = new File(path() + "log.txt");
		if (logFile.exists() == false) {
			logFile.createNewFile();
		}
	}

	private void writeTolog(String logContent) throws RemoteException {
		FileWriter fw = null;
		File logfile = new File(path() + "log.txt");
		try {
			if (!logfile.exists()) {
				logfile.createNewFile();
			}
			fw = new FileWriter(logfile);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(logContent, 0, logContent.length() - 1);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("writeToLog end");

	}

	private String path() throws RemoteException {
		return pathPrefix + pwd();
	}

	public void port(InetSocketAddress s) {
		makeActive(s);
	}

	public InetSocketAddress pasv() throws IOException {
		return makePassive();
	}

}
