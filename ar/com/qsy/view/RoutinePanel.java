package ar.com.qsy.view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;


public class RoutinePanel extends JPanel implements AutoCloseable {
    private final JButton btnStartCustom;
    private final JButton btnStartPlayer;
    private final JButton btnStop;

    public RoutinePanel(QSYFrame parent){
        this.setLayout(new GridLayout(0,1,2,2));
        this.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Routine"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        btnStartCustom = new JButton("Start Custom");
        this.add(btnStartCustom);
        btnStartCustom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //TODO parametros correspondientes
                //parent.getTerminal().executeCustom();
                startRoutine();
            }
        });

        btnStartPlayer = new JButton("Start Player");
        this.add(btnStartPlayer);
        btnStartPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ArrayList<Color> playersAndColors = new ArrayList<>();
                //TODO un solo jugador
                playersAndColors.add(Color.red);
                startRoutine();
                try {
                    int steps=10;
                    int timeout=1000;
                    parent.getTerminal().executePlayer(playersAndColors, null, false,
                            false, 0, steps,timeout, 1, 0, "&");
                } catch (Exception exc) {
                    exc.printStackTrace();
                }

            }
        });

        btnStop = new JButton("Stop");
        this.add(btnStop);
        btnStop.setEnabled(false);
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.getTerminal().stopExecutor();
                stopRoutine();
            }
        });
    }
    private void startRoutine(){
        btnStartPlayer.setEnabled(false);
        btnStartCustom.setEnabled(false);
        btnStop.setEnabled(true);
    }
    private void stopRoutine(){
        btnStartPlayer.setEnabled(true);
        btnStartCustom.setEnabled(true);
        btnStop.setEnabled(false);
    }


    @Override
    public void close() throws Exception {
        return;
    }
}
