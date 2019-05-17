package edu.stevens.cs549.ftpinterface;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServerFactory extends Remote {

	public IServer createServer() throws IOException;

}
