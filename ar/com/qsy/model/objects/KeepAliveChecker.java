package ar.com.qsy.model.objects;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Set;

public class KeepAliveChecker implements Runnable{

    private final short keepAlivePeriod = 500;
    private final short keepAlivePeriodChecker = (int)(keepAlivePeriod*1.5);

    private final Hashtable<InetAddress,Long> keepAliveRegistry;

    public KeepAliveChecker(Hashtable<InetAddress, Long> nodes){
        this.keepAliveRegistry=nodes;
    }

    @Override
    public void run() {
        boolean stopped = false;
        while (!stopped) {
            try {
                Thread.sleep(keepAlivePeriodChecker);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis()%10000+"\tTask");

            Set<InetAddress> ips = keepAliveRegistry.keySet();
            long now = System.currentTimeMillis();
            long aux = -1;
            for (InetAddress ip : ips) {
                aux = keepAliveRegistry.get(ip);
                if (aux + keepAlivePeriodChecker< now) {
                    stopped = true;
                    keepAliveRegistry.remove(ip);
                    break;
                }
            }
            if (stopped) {
                System.out.print("Se perdio algun nodo");
                System.out.println(aux % 10000 + " " + now % 10000);
            }
        }
    }
}
