package ar.com.qsy.src.app.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;

import ar.com.qsy.src.app.network.MulticastReceiver;
import ar.com.qsy.src.app.network.ReceiverSelector;
import ar.com.qsy.src.app.network.SenderSelector;
import ar.com.qsy.src.app.protocol.QSYPacket;
import ar.com.qsy.src.app.terminal.Terminal;

public class CommandLineMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		final InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName("192.168.0.18"), QSYPacket.MULTICAST_PORT);
		MulticastReceiver multicastReceiver =  null;
		Terminal terminal = null;
		ReceiverSelector receiverSelector = null;
		SenderSelector senderSelector;
		Thread threadReceiveSelector = null;
		Thread threadTerminal = null;
		Thread threadSenderSelector = null;
		Thread threadMulticastReceiver = null;
		boolean up = false;
		
		
		System.out.println("Comandos:");
		System.out.println("s: Iniciar terminal.");
		System.out.println("p: Terminar terminal.");
		System.out.println("a: Iniciar búsqueda de nodos");
		System.out.println("x: Detener búsqueda de nodos");
		System.out.println("q: Salir");
		
		
		
		Scanner sc = new Scanner(System.in);
		char option = 0;
		while(option != 'q') {
			System.out.print("\nComando: ");
			option = sc.next().trim().charAt(0);
			
			switch (option) {
			case 's':
				if(up) {
					System.out.println("Terminal ya está corriendo.");
					break;
				}
				
				multicastReceiver = new MulticastReceiver(addr, QSYPacket.MULTICAST_ADDRESS);
				terminal = new Terminal();
				receiverSelector = new ReceiverSelector();
				senderSelector = new SenderSelector(terminal.getNodes());
				threadReceiveSelector = new Thread(receiverSelector, "Receive Selector");
				threadTerminal = new Thread(terminal, "Terminal");
				threadSenderSelector = new Thread(senderSelector, "Sender Selector");
				threadMulticastReceiver = new Thread(multicastReceiver, "Multicast Receiver");
				threadReceiveSelector.start();
				threadTerminal.start();
				threadSenderSelector.start();
				threadMulticastReceiver.start();
				
				multicastReceiver.addListener(terminal);
				receiverSelector.addListener(terminal);
				terminal.addListener(receiverSelector);
				terminal.addListener(senderSelector);
				up = true;
				System.out.println("Terminal iniciada.");
				break;
			case 'p':
			case 'q':
				if (!up) {
					System.out.println("Terminal no está corriendo.");
					break;
				}
				threadReceiveSelector.interrupt();
				threadTerminal.interrupt();
				threadSenderSelector.interrupt();
				threadMulticastReceiver.interrupt();
				threadReceiveSelector.join();
				threadTerminal.join();
				threadSenderSelector.join();
				threadMulticastReceiver.join();
				up = false;
				System.out.println("Terminal terminada.");
				break;
			case 'a':
				if (!up) {
					System.out.println("Terminal no está corriendo.");
					break;
				}
				terminal.searchNodes();
				System.out.println("Búsqueda de nodos iniciada.");
				break;
			case 'x':
				if (!up) {
					System.out.println("Terminal no está corriendo.");
					break;
				}
				terminal.finalizeNodesSearch();
				System.out.println("Búsqueda detenida.");
				break;
			default:
				break;
			}	
		}
		sc.close();
		System.out.print("Buh-bye");
	}

}
