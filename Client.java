package FinalProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import SourceFile.FileInfo;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Toolkit;
import java.awt.TextArea;
import javax.swing.ImageIcon;

public class Client extends JFrame implements Runnable   {

	
	private JPanel contentPane;
	private JTextField msg_text;
	static TextArea textArea_history;
	private JLabel lblNewLabel;
	private JLabel lblName;
	private JTextField textName;
	private JLabel lblPort;
	private JTextField textPort;
	private JButton btnStart;
	private JButton btnBrowse;
	private JButton btnSend;
	private JTextField textFieldFilePath;
	private JButton btnSendFile;
	private JFileChooser fc;
	
	private JComponent fame = null;
	static Socket socket,socket1;
	static String IDname = null;
	static DataInputStream dataInput;
	static DataOutputStream dataOut;
	static Thread t;
	
	final File[] fileToSend = new File[1];
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			
			
		});
		
	}

	public static void sendFile(String sourceFilePath, String destinationDir) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        
        try {
        	socket1 = new Socket("localhost",49);
            // Lấy thông tin file
            FileInfo fileInfo = getFileInfo(sourceFilePath, destinationDir);
 
            // gửi file
            oos = new ObjectOutputStream(socket1.getOutputStream());
            oos.writeObject(fileInfo);
 
            // Xác nhận file đã gửi thành công
            ois = new ObjectInputStream(socket1.getInputStream());
            fileInfo = (FileInfo) ois.readObject();
            if (fileInfo != null) {
                textArea_history.append("\nSend file to server "+ fileInfo.getStatus() + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
           // Đóng các stream
            closeStream(oos);
            closeStream(ois);
            try {
				socket1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
	private static FileInfo getFileInfo(String sourceFilePath, String destinationDir) {
        FileInfo fileInfo = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(sourceFilePath);
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            fileInfo = new FileInfo();
            byte[] fileBytes = new byte[(int) sourceFile.length()];
            // lấy thông tin file
            bis.read(fileBytes, 0, fileBytes.length);
            fileInfo.setFilename(sourceFile.getName());
            fileInfo.setDataBytes(fileBytes);
            fileInfo.setDestinationDirectory(destinationDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return fileInfo;
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
 
    /**
     * close output stream
     */
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
	public Client() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Client.class.getResource("/Images/icon.png")));
		setTitle("Client");
		setFont(new Font("Times New Roman", Font.BOLD, 16));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 712, 490);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textPort = new JTextField();
		textPort.setText("49");
		textPort.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		textPort.setBounds(385, 17, 81, 27);
		contentPane.add(textPort);
		textPort.setColumns(10);

		textName = new JTextField();
		textName.setText("Thien An");
		textName.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		textName.setBounds(73, 17, 167, 27);
		contentPane.add(textName);
		textName.setColumns(10);

		textArea_history = new TextArea();
		textArea_history.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		textArea_history.setBounds(10, 71, 672, 218);
		contentPane.add(textArea_history);

		msg_text = new JTextField();
		msg_text.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		msg_text.setBounds(10, 389, 526, 35);
		contentPane.add(msg_text);
		msg_text.setColumns(10);

		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer portNo = Integer.parseInt(textPort.getText());
				String name = textName.getText();
				try {
					socket = new Socket("localhost", portNo);
					dataOut = new DataOutputStream(socket.getOutputStream());
					dataOut.writeUTF(name);
					dataOut.flush();
			//		System.out.print(socket);
					run();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnStart.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnStart.setForeground(Color.RED);
		btnStart.setBounds(561, 20, 89, 21);
		contentPane.add(btnStart);

		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textName.getText();
				String msgOut = msg_text.getText().trim();
				try {
					if (socket != null) {
						dataOut = new DataOutputStream(socket.getOutputStream());
						dataOut.writeUTF(name+": "+msgOut);
						dataOut.flush();
						textArea_history.append("\nMe: " + msgOut);
						msg_text.setText("");
						
					}
					
					else {
						JOptionPane.showMessageDialog(fame,"Chưa kết nối đến Server. Nhấn Start để kết nối.","Note",JOptionPane.WARNING_MESSAGE);
					}
					
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(fame, "IOException\n We can not chat now", "Note",JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		btnSend.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnSend.setForeground(Color.RED);
		btnSend.setBounds(561, 393, 121, 27);
		contentPane.add(btnSend);

		lblName = new JLabel("Name");
		lblName.setForeground(Color.YELLOW);
		lblName.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblName.setBounds(10, 24, 48, 13);
		contentPane.add(lblName);

		lblPort = new JLabel("Port Number");
		lblPort.setForeground(Color.YELLOW);
		lblPort.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblPort.setBounds(275, 20, 81, 21);
		contentPane.add(lblPort);
		
		textFieldFilePath = new JTextField();
		textFieldFilePath.setBounds(10, 317, 526, 35);
		contentPane.add(textFieldFilePath);
		textFieldFilePath.setColumns(10);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.setForeground(Color.RED);
		btnBrowse.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc = new JFileChooser();
				fc.showOpenDialog(btnBrowse);
			
				fileToSend[0] = fc.getSelectedFile();
	            textFieldFilePath.setText(fileToSend[0].getPath());
	
			}
		});
		
		btnBrowse.setBounds(561, 307, 121, 27);
        contentPane.add(btnBrowse);
        
        btnSendFile = new JButton("Send File");
        btnSendFile.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnSendFile.setForeground(Color.RED);
        btnSendFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String sourceFilePath = textFieldFilePath.getText();
				String destinationDir = "E:\\server\\"; // thư mục đích trên server
					if (sourceFilePath!=" ") {
							Client.sendFile(sourceFilePath, destinationDir);	
					}

        	}
        });
        btnSendFile.setBounds(561, 339, 121, 27);
        contentPane.add(btnSendFile);
        lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon(Client.class.getResource("/Images/client.jpg")));
        lblNewLabel.setBounds(0, 0, 696, 452);
        contentPane.add(lblNewLabel);
	}

	

	@Override
	public void run() {

		SmsInClient read = new SmsInClient(IDname);
		read.start();
		
	}
	
	class SmsInClient extends Thread{
		
		private Thread t ;
		private String userName;
		
		SmsInClient(String name){
			userName = name;
		}
		
		public void run() {
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(socket.getInputStream());
				while(true) {
					String sms = dis.readUTF();
					
			//		System.out.println(sms);
					if(userName != null) {
						
						textArea_history.append( "\n"+ userName  +":" + sms);
					}
					else {
						textArea_history.append( "\n" + sms);
					}
					
				}
			} catch (Exception e) {
				try {
					dis.close();
					socket.close();
				}
				catch(IOException ex) {
					JOptionPane.showMessageDialog(fame,"Ngắt kết nối","Note",JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		
		public void start() {
			if(t == null) {
				t = new Thread(this);
				t.start();
			}
		}
	}
	


	
}
