package ar.com.qsy.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

import ar.com.qsy.model.objects.Color;
import ar.com.qsy.model.utils.RoutineManager;

public final class RoutinePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JButton btnCustomRoutineStart;
	private final JButton btnPlayerRoutineStart;

	public RoutinePanel(final QSYFrame parent) {
		this.setLayout(new GridLayout(0, 1, 2, 2));

		this.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Routine"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		btnCustomRoutineStart = new JButton("Start CustomRoutine");
		this.add(btnCustomRoutineStart);

		btnCustomRoutineStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					parent.getTerminal().executeCustom(RoutineManager.loadRoutine("ar/com/qsy/io/routine1.json"), null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		btnPlayerRoutineStart = new JButton("Start PlayerRoutine");
		this.add(btnPlayerRoutineStart);

		btnPlayerRoutineStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final ArrayList<Color> playersAndColors = new ArrayList<>();
					playersAndColors.add(new Color((byte) 0xF, (byte) 0, (byte) 0));
					parent.getTerminal().executePlayer(null, 1, playersAndColors, true, 2000, 500, 7000, 0, false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

	}

}
