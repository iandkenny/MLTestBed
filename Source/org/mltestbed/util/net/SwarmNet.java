package org.mltestbed.util.net;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.mltestbed.data.Experiment;
import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.Util;

public class SwarmNet
{
	class SwarmBroadcast extends Thread
	{
		boolean running = true;
		public String broadcast(String broadcastMessage, InetAddress address)
				throws IOException
		{
			DatagramSocket socket = null;
			socket = new DatagramSocket();
			socket.setBroadcast(true);

			byte[] buffer = broadcastMessage.getBytes();

			DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
					address, PORT + 1);
			socket.send(packet);
			socket.setBroadcast(false);
			socket.close();
			socket = null;
			System.out.println("Reply from:"
					+ InetAddress.getLocalHost().getHostAddress());
			return InetAddress.getLocalHost().getHostAddress();
		}
		/**
		 * @return the running
		 */
		public boolean isRunning()
		{
			return running;
		}
		public void run()
		{
			try
			{
				DatagramSocket serverSocket = new DatagramSocket(PORT + 1);
				byte[] receiveData = new byte[8];
				byte[] sendData = new byte[8];

				while (running)
				{
					DatagramPacket receivePacket = new DatagramPacket(
							receiveData, receiveData.length);
					serverSocket.receive(receivePacket);
					String sentence = new String(receivePacket.getData());
					System.out.println("RECEIVED: " + sentence);
					InetAddress IPAddress = receivePacket.getAddress();
					String sendString = "polo";
					sendData = sendString.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData,
							sendData.length, IPAddress, PORT + 1);
					serverSocket.send(sendPacket);
				}
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		/**
		 * @param running
		 *            the running to set
		 */
		public void setRunning(boolean running)
		{
			this.running = running;
		}
	}

	public class SwarmMulticastPublisher
	{
		private byte[] buf;
		private InetAddress group;
		private DatagramSocket socket;

		public void multicast(String multicastMessage) throws IOException
		{
			socket = new DatagramSocket();
			group = InetAddress.getByName("230.0.0.0");
			buf = multicastMessage.getBytes();

			DatagramPacket packet = new DatagramPacket(buf, buf.length, group,
					PORT - 1);
			socket.send(packet);
			socket.close();
		}
	}
	public class SwarmMulticastReceiver extends Thread
	{
		protected byte[] buf = new byte[256];
		protected MulticastSocket socket = null;

		public void run()
		{
			try
			{
				socket = new MulticastSocket(PORT - 1);
				InetAddress group = InetAddress.getByName("230.0.0.0");
				socket.joinGroup(group);
				while (true)
				{
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);
					String received = new String(packet.getData(), 0,
							packet.getLength());
					if ("end".equals(received))
					{
						break;
					}
				}
				socket.leaveGroup(group);
				socket.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class SwarmServer extends Thread
	{
		private String cmd;
		private Experiment exp;
		private Particle particle;
		private Properties props;
		private boolean running = true;

		private Socket serverSocket;

		private BaseSwarm swarm;

		SwarmServer()
		{
			sb = new SwarmNet().new SwarmBroadcast();
			sb.start();
			try
			{
				ip = sb.broadcast("HELO Swarm",
						InetAddress.getByName("255.255.255.255"));
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		SwarmServer(Socket socket)
		{
			this.serverSocket = socket;
		}
		SwarmServer(Socket socket, BaseSwarm swarm)
		{
			this.serverSocket = socket;
			this.swarm = swarm;
		}
		/**
		 * @return the cmd
		 */
		public String getCmd()
		{
			return cmd;
		}

		/**
		 * @return the exp
		 */
		public Experiment getExp()
		{
			return exp;
		}

		/**
		 * @return the particle
		 */
		public Particle getParticle()
		{
			return particle;
		}
		/**
		 * @return the props
		 */
		public Properties getProps()
		{
			return props;
		}

		/**
		 * @return the running
		 */
		public boolean isRunning()
		{
			return running;
		}
		@Override
		public void run()
		{
			System.out.println("Connected: " + serverSocket);
			try
			{
				ObjectInputStream inputStream = new ObjectInputStream(
						serverSocket.getInputStream());

				InetAddress address = serverSocket.getInetAddress();
				int port = serverSocket.getPort();

				while (running)
				{
					Object obj = inputStream.readObject();
					if (obj instanceof String)
					{
						String received = (String) obj;
						if (received.equals("start"))
						{
							running = true;
							if (exp != null)
							{
								Util.getSwarmui().addExperiment(exp);
								exp.startRunning();
							}
						} else if (received.equals("end"))
						{
							running = false;
							if (exp != null)
								exp.stopRunning();
						}
					} else if (obj instanceof Experiment)
					{
						if (exp == null || !exp.isRunning())
						{
							exp = (Experiment) obj;
							exp.setSwarmNet(getSn());
						}
					} else if (obj instanceof Properties)
						props = (Properties) obj;
					else if (obj instanceof Particle)
					{
						Particle particle = (Particle) obj;
						String key = serverSocket.getInetAddress() + ":"
								+ serverSocket.getPort();
						if (swarm != null)
						{
							if (hashMap.containsKey(key))
								particle.setIdentityNumber(hashMap.get(key));
							else
								synchronized (swarm)
								{
									int identityNumber = swarm.getNoMembers()
											+ 1;
									hashMap.put(key, identityNumber);
									particle.setIdentityNumber(identityNumber);
								}
							swarm.setParticle(particle);
						}
					}
				}
				serverSocket.close();
			} catch (Exception e)
			{
				Log.log(Level.SEVERE, e);
			} finally
			{
				try
				{
					serverSocket.close();
				} catch (IOException e)
				{
				}
				System.out.println("Closed: " + serverSocket);
			}
		}
		public boolean sendObject(Object obj)
		{
			boolean ret = false;
			ObjectOutputStream outputStream = null;
			try
			{
				outputStream = new ObjectOutputStream(
						serverSocket.getOutputStream());
				outputStream.writeObject(obj);
				outputStream.close();
				ret = true;
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
//				e.printStackTrace();
			} finally
			{
				try
				{
					if (outputStream != null)
						outputStream.close();
				} catch (IOException e)
				{
				}
			}

			return ret;

		}
		/**
		 * @param cmd
		 *            the cmd to set
		 */
		public void setCmd(String cmd)
		{
			synchronized (this.cmd)
			{
				this.cmd = cmd;
				sendObject(this.cmd);
			}
		}

		/**
		 * @param exp
		 *            the exp to set
		 */
		public void setExp(Experiment exp)
		{
			synchronized (this.exp)
			{
				this.exp = exp;
				sendObject(this.exp);
			}
		}

		/**
		 * @param particle
		 *            the particle to set
		 */
		public void setParticle(Particle particle)
		{
			synchronized (this.particle)
			{
				this.particle = particle;
				sendObject(this.particle);
			}
		}

		/**
		 * @param props
		 *            the props to set
		 */
		public void setProps(Properties props)
		{
			synchronized (this.props)
			{
				this.props = props;
				sendObject(this.props);
			}
		}

		/**
		 * @param running
		 *            the running to set
		 */
		public void setRunning(boolean running)
		{
			this.running = running;
		}
	}

	private static final int MAX_CONNECTS = 5;

	private static final int PORT = 59898;

	private static boolean running = true;

	private static SwarmNet sn = null;
	private static SwarmBroadcast sb = null;
	private static BaseSwarm swarm = null;
	private static ArrayList<SwarmServer> swarmServers;

	public SwarmNet(SwarmNet sn)
	{
		this.setSn(sn);
	}
	public SwarmNet()
	{
		// TODO Auto-generated constructor stub
	}
	public static HashMap<Integer, Particle> getParticles()
	{
		HashMap<Integer, Particle> phash = new HashMap<Integer, Particle>();
		int key = 0;
		for (Iterator<SwarmServer> iterator = swarmServers.iterator(); iterator
				.hasNext();)
		{
			SwarmServer swarmServer = (SwarmServer) iterator.next();
			phash.put(key++, swarmServer.getParticle());
		}
		return phash;
	}
	/**
	 * @return the swarmServers
	 */
	public static ArrayList<SwarmServer> getSwarmServers()
	{
		return swarmServers;
	}
	/**
	 * Runs the server. When a client connects, the server spawns a new thread
	 * to do the servicing and immediately returns to listening. The application
	 * limits the number of threads via a thread pool (otherwise millions of
	 * clients could cause the server to run out of resources by allocating too
	 * many threads).
	 */
	public void goServer()
	{
		try
		{
			swarmServers = new ArrayList<SwarmServer>();
			if (sb == null || !sb.isAlive())
			{
				sb = new SwarmNet().new SwarmBroadcast();
				sb.start();
			}
			ServerSocket listener = new ServerSocket(PORT);
			System.out.println("The swarm broadcast server is running...");
			ExecutorService pool = Executors.newFixedThreadPool(MAX_CONNECTS);
			while (running)
			{
				SwarmServer swarmServer = new SwarmNet().new SwarmServer(
						listener.accept(), swarm);
				pool.execute(swarmServer);
				swarmServers.add(swarmServer);
				if (swarmServers.size() == MAX_CONNECTS)
					running = false;
			}
			sb.running = false;
			listener.close();
			listener = null;
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
		}
	}

	private SwarmServer clientConnection;

	private Socket clientSocket;

	public HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

	private String ip = "";

	public void destroy()
	{
		if (sb != null)
		{
			sb = null;
		}
		if (swarmServers != null)
		{
			swarmServers.clear();
			swarmServers = null;
		}
	}

	/**
	 * @return the swarm
	 */
	public BaseSwarm getSwarm()
	{
		return swarm;
	}

	public void goClient()
	{
		try
		{
			ip = sb.broadcast("HELO: SwarmClient",
					InetAddress.getByName("255.255.255.255"));

			if (clientSocket == null || clientSocket.isClosed())
				clientSocket = new Socket(ip, PORT);
			clientConnection = new SwarmServer(clientSocket);
			clientConnection.start();

		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private List<InetAddress> listAllBroadcastAddresses() throws SocketException
	{
		List<InetAddress> broadcastList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface
				.getNetworkInterfaces();
		while (interfaces.hasMoreElements())
		{
			NetworkInterface networkInterface = interfaces.nextElement();

			if (networkInterface.isLoopback() || !networkInterface.isUp())
			{
				continue;
			}

			networkInterface.getInterfaceAddresses().stream()
					.map(a -> a.getBroadcast()).filter(Objects::nonNull)
					.forEach(broadcastList::add);
		}
		return broadcastList;
	}

	/**
	 * @param swarm
	 *            the swarm to set
	 */
	public void setSwarm(BaseSwarm swarm)
	{
		this.swarm = swarm;
	}
	public static void main(String[] args) throws IOException
	{
		SwarmNet sn = new SwarmNet();
		Thread t = new Thread()
		{
			public void run()
			{
				sn.goServer();
			}
		};
		t.start();
		try
		{
			Thread.sleep(2000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (sb != null)
		{
			for (int i = 0; i < 5; i++)
				sn.ip = sb.broadcast("Hello:" + i,
						InetAddress.getByName("255.255.255.255"));
			sb.running = false;
		}
		sn.goClient();
		t = null;
		running = false;
	}
	/**
	 * @return the sn
	 */
	public static SwarmNet getSn()
	{
		return sn;
	}
	/**
	 * @param sn the sn to set
	 */
	public static void setSn(SwarmNet sn)
	{
		SwarmNet.sn = sn;
	}
}