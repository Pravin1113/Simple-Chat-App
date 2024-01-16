package com.pravin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Server extends JFrame {

	ServerSocket server;

	Socket socket;

	BufferedReader br;
	PrintWriter writer;
	private JLabel heading = new JLabel("Server area");
	private JTextArea msgArea = new JTextArea();
	private JTextField msgInput = new JTextField();

	private Font font = new Font("Roboto", Font.BOLD, 20);

	public Server() {
		try {
			server = new ServerSocket(9090);
			System.out.println("Server is ready to connection");
			System.out.println("waiting....");

			socket = server.accept();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			writer = new PrintWriter(socket.getOutputStream());

			createGui();
			handleevents();
			startReading();
			startWriting();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void startReading() {
		// thread for reading

		Runnable run = () -> {
			System.out.println("reading .....");
			try {
				while (true && !socket.isClosed()) {

					String msg = "";

					msg = br.readLine();

					if (msg.equals("bye")) {
						JOptionPane.showMessageDialog(this, "server terminited");
						msgInput.setEnabled(false);
						socket.close();
						break;
					}
					msgArea.append("Client: " + msg + "\n");
				}

			} catch (IOException e) {
				System.out.println("connection closed");
			}

		};
		new Thread(run).start();

	}

	public void startWriting() {
		// thread for writing

		Runnable run2 = () -> {
			System.out.println("Writing .....");
			try {
				while (true) {

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

	private void handleevents() {

		msgInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
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
		this.setTitle("Server Message[END]");
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

	public static void main(String[] args) {
		System.out.println("this is server");
		new Server();
	}

}
