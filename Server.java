package FinalProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import SourceFile.FileInfo;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Toolkit;
import javax.swing.ImageIcon;


public class Server extends JFrame    {

	private JPanel contentPane;
	private JTextField msg_text;
	private TextArea msg_area;
	private JLabel lblNewLabel;
	private JLabel lblPort;

	static ServerSocket serverSocket;
	static Socket socket,socket1;
	static DataInputStream dataIn;
	static DataOutputStream dataOut;
	private static JComponent fame = null;
	
	public static ArrayList<Socket> listSK;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(49);
					JOptionPane.showMessageDialog(fame, "Server is created on port 49.", "Note", JOptionPane.WARNING_MESSAGE);
					Server frame = new Server();
					frame.setVisible(true);
					Server.listSK = new ArrayList<>();
					frame.execute();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			
			
		});
	}
	private void execute() throws IOException{
		WaitingClient waiting = new WaitingClient();
		waiting.start();
		WaitingFile wF = new WaitingFile();
		wF.start();
	}
	
	private boolean createFile(FileInfo fileInfo) {
        BufferedOutputStream bos = null;
         
        try {
            if (fileInfo != null) {
                File fileReceive = new File(fileInfo.getDestinationDirectory() 
                        + fileInfo.getFilename());
                bos = new BufferedOutputStream(
                        new FileOutputStream(fileReceive));
                // write file content
                bos.write(fileInfo.getDataBytes());
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(bos);
        }
        return true;
    }
	
	public static void closeStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
  public static void closeStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

	/**
	 * Create the frame.
	 */
	public Server() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Server.class.getResource("/Images/icon.png")));
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 567, 379);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		msg_area = new TextArea();
		msg_area.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		msg_area.setBounds(10, 47, 531, 222);
		contentPane.add(msg_area);

		msg_text = new JTextField();
		msg_text.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		msg_text.setBounds(10, 279, 450, 52);
		contentPane.add(msg_text);
		msg_text.setColumns(10);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		
				SmsOutServer write = new SmsOutServer();
				write.start();
				
			}
		});
		btnNewButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnNewButton.setForeground(Color.RED);
		btnNewButton.setBounds(462, 293, 89, 21);
		contentPane.add(btnNewButton);

		lblPort = new JLabel("Port Number");
		lblPort.setForeground(Color.YELLOW);
		lblPort.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblPort.setBounds(169, 16, 81, 21);
		contentPane.add(lblPort);
		
		JLabel lblNewLabel_1 = new JLabel("49");
		lblNewLabel_1.setForeground(Color.YELLOW);
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblNewLabel_1.setBounds(263, 20, 48, 13);
		contentPane.add(lblNewLabel_1);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(Server.class.getResource("/Images/server.jpg")));
		lblNewLabel.setBounds(0, 0, 551, 341);
		contentPane.add(lblNewLabel);
	}

	
	

	
	class WaitingClient extends Thread{
		public void run() {
			try {
				while(true) {
					socket = serverSocket.accept();
					Server.listSK.add(socket);
					JOptionPane.showMessageDialog(fame, socket +"is connected to the Server\n We can chat now", "Note",JOptionPane.WARNING_MESSAGE);
				//	System.out.println(socket);
					SmsInServer read = new SmsInServer(socket);
					read.start();
				}
		
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(fame, "Server Error", "Note", JOptionPane.WARNING_MESSAGE);
			}
			
		}
	}
	class WaitingFile extends Thread{
		public void run() {
			try {				
				while(true) {
					socket1 = serverSocket.accept();					
					System.out.println(socket1);
					ServerRecieve recieve = new ServerRecieve(socket1);
					recieve.start();
				}		
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(fame, "Server Error", "Note", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	class ServerRecieve extends Thread{
		private Socket socket1;
		public ServerRecieve(Socket socket) {
			this.socket1 = socket;
		}
		
		public void run() {
			
			ObjectInputStream ois = null;
	        ObjectOutputStream oos = null;
			try {
					ois = new ObjectInputStream(socket1.getInputStream());				
	                FileInfo fileInfo = (FileInfo) ois.readObject();
	                if (fileInfo != null) {
	                    createFile(fileInfo);
	                }
	  
	                // confirm that file is received
	                oos = new ObjectOutputStream(socket1.getOutputStream());
	                fileInfo.setStatus("success");
	                fileInfo.setDataBytes(null);
	                oos.writeObject(fileInfo);
				
						
			} catch (Exception e) {
				
					e.printStackTrace();	
					JOptionPane.showMessageDialog(fame,"Kết nối lỗi. Bấm Start để kết nối lại!","Note",JOptionPane.WARNING_MESSAGE);
				
			}
			finally {
                // close all stream
                closeStream(ois);
                closeStream(oos);
                
                // close session
                try {
					socket1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		}
	}
	class SmsInServer extends Thread{
		private Socket socket;
		public SmsInServer(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(socket.getInputStream());
				
				while(true) {
					String sms = dis.readUTF();
					if (sms.contains("exit")) {
						JOptionPane.showMessageDialog(fame,socket+" is disconnect","Note",JOptionPane.WARNING_MESSAGE);
						Server.listSK.remove(socket);
						dis.close();
						socket.close();
						continue;
					}
					for(Socket item: Server.listSK) {
						if(item.getPort()!= socket.getPort()) {
							DataOutputStream dos = new DataOutputStream(item.getOutputStream());
							dos.writeUTF(sms);
						}
					}
					msg_area.append("\n" +sms);			
					msg_text.setText("");
		
				}

			} catch (Exception e) {
				try {
					socket.close();
				}
				catch(IOException ex) {
					JOptionPane.showMessageDialog(fame,"Ngắt kết nối","Note",JOptionPane.WARNING_MESSAGE);
				}
			}
			finally {
				closeStream(dis);
			}
		}
	}

	class SmsOutServer extends Thread{
		
		
		public void run() {
			DataOutputStream dos = null;
			String sms = msg_text.getText();
				try {

					for(Socket item: Server.listSK) {
						
						dos = new DataOutputStream(item.getOutputStream());
						dos.writeUTF("Server: "+sms);
						
					}
					msg_area.append("\n Me" + ": " + sms);
					msg_text.setText("");
				}catch (IOException e) {
						// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(fame, "Sever gap van de ve I/O", "Note", JOptionPane.WARNING_MESSAGE);
				}

			}
	}
	  
}
