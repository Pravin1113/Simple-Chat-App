package com.pravin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client extends JFrame {

	Socket socket;

	BufferedReader br;
	PrintWriter writer;

	private JLabel heading = new JLabel("Client area");
	private JTextArea msgArea = new JTextArea();
	private JTextField msgInput = new JTextField();

	private Font font = new Font("Roboto", Font.BOLD, 20);

	public Client() {
		try {
			System.out.println("sending request to server");
			socket = new Socket("127.0.0.1", 9090);
			System.out.println("Connection Done");

			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			writer = new PrintWriter(socket.getOutputStream());
			createGui();
			handleevents();
			startReading();
			startWriting();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void handleevents() {

		msgInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					System.out.println("you have pressed enter butten");
					String contToSend = msgInput.getText();
					msgArea.append("me :" + contToSend + "\n");
					writer.println(contToSend);
					writer.flush();
					msgInput.setText("");

				}
			}

		});
	}

	private void createGui() {
		this.setTitle("Client Message[END]");
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		heading.setFont(font);
		msgArea.setFont(font);
		msgInput.setFont(font);

		this.setLayout(new BorderLayout());

		heading.setHorizontalAlignment(SwingConstants.CENTER);
		heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		msgArea.setEditable(false);
		this.add(heading, BorderLayout.NORTH);
		JScrollPane scoll = new JScrollPane(msgArea);
		this.add(scoll, BorderLayout.CENTER);
		this.add(msgInput, BorderLayout.SOUTH);

	}

	public void startReading() {
		// thread for reading

		Runnable run = () -> {
			System.out.println("reading .....");
			while (true) {

				String msg = "";
				try {
					msg = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (msg.equals("bye")) {
					JOptionPane.showMessageDialog(this, "server terminited");
					msgInput.setEnabled(false);
					try {
						socket.close();
					} catch (IOException e) {
						System.out.println("connection closed");
					}
					break;
				}
				msgArea.append("Server: " + msg + "\n");
			}
		};
		new Thread(run).start();

	}

	public void startWriting() {
		// thread for writing

		Runnable run2 = () -> {
			System.out.println("Writing .....");
			try {
				while (true && !socket.isClosed()) {

					BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

					String content = reader.readLine();
					writer.println(content);
					writer.flush();
				}

			} catch (Exception e) {
				System.out.println("connection closed");
			}
		};

		new Thread(run2).start();

	}

	public static void main(String[] args) {
		System.out.println("this is Client");

		new Client();
	}
}
