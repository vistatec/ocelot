package com.vistatec.ocelot.freme.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Dialog having the appearance of a balloon tool tip. It is possible to specify
 * a custom Swing Component to be displayed into the balloon.
 */
public class BalloonTip extends JDialog implements ActionListener {

	/** Serial version UID. */
	private static final long serialVersionUID = -7622023874939252456L;

	/** The custom component. */
	private Component component;

	/** X position for the tool tip. */
	private int x;

	/** Y position for the tool tip. */
	private int y;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner window
	 * @param component
	 *            the custom component
	 * @param x
	 *            the x position for the tool tip.
	 * @param y
	 *            the y position for the tool tip.
	 */
	public BalloonTip(Window owner, Component component, int x, int y) {
		super();
		setModal(true);
		this.x = x;
		this.y = y;
		this.component = component;
		setUndecorated(true);
	}

	/**
	 * Makes the UI for this dialog.
	 */
	public void makeUI() {

		setBackground(new Color(0, 0, 0, 0));
		BalloonPanel panel = new BalloonPanel(0, 0, component.getWidth() + 50,
		        component.getHeight() + 80, this);
		panel.add(component);
		add(panel);
		setSize(component.getWidth() + 50, component.getHeight() + 80);
		setLocation(x - panel.getPointerOffset(), y);
		setVisible(true);
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		String marta = "Marta";
		JLabel label = new JLabel(marta);
		FontMetrics metrics = label.getFontMetrics(label.getFont());
		final int lblWidth = metrics.charsWidth(marta.toCharArray(), 0,
		        marta.length());
		final int lblHeight = metrics.getHeight();
		label.setSize(new Dimension(lblWidth, lblHeight));
		final BalloonTip bt = new BalloonTip(frame, label, 200, 300);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);
				bt.makeUI();

			}
		});
	}

	/**
	 * Panel shaped as balloon.
	 */
	class BalloonPanel extends JPanel {

		/** The serial version UID. */
		private static final long serialVersionUID = -2374203623407595249L;

		/** Balloon arrow half size constant. */
		private static final int POINTER_HALF_SIZE = 20;

		/** Width of the round border. */
		private static final int ROUND_BORDER_ARC_WIDTH = 20;

		/** x location for the balloon. */
		private int x;

		/** y location for the balloon. */
		private int y;

		/**
		 * Constructor.
		 * @param x the x location
		 * @param y the y location
		 * @param width the width
		 * @param height the height
		 * @param listener the action listener
		 */
		public BalloonPanel(final int x, final int y, int width, int height,
		        ActionListener listener) {

			this.x = x;
			this.y = y;
			setSize(new Dimension(width, height));
			setOpaque(true);
			JPanel panel = new JPanel();
			panel.setBackground(new Color(0, 0, 0, 0));
			panel.setPreferredSize(new Dimension(width, 20));
			add(panel, BorderLayout.NORTH);
			//close button
			JButton btnClose = new JButton() {
				private static final long serialVersionUID = -6765879759340989663L;
				
				/*
				 * (non-Javadoc)
				 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
				 */
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setColor(Color.lightGray);
					g2d.setStroke(new BasicStroke(3f));
					g2d.drawLine(7, 7, getWidth() - 7, getHeight() - 7);
					g2d.drawLine(7, getHeight() - 7, getWidth() - 7, 7);
				}
				/*
				 * (non-Javadoc)
				 * @see javax.swing.AbstractButton#paintBorder(java.awt.Graphics)
				 */
				@Override
				protected void paintBorder(Graphics g) {

					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setColor(Color.lightGray);
					g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 9,
					        9);
				}
			};
			btnClose.setOpaque(false);
			btnClose.setText("X");
			btnClose.setPreferredSize(new Dimension(20, 20));
			btnClose.addActionListener(listener);
			JPanel btnPanel = new JPanel(
			        new FlowLayout(FlowLayout.RIGHT, 10, 0));
			btnPanel.setBackground(new Color(0, 0, 0, 0));
			btnPanel.setPreferredSize(new Dimension(width, 20));
			btnPanel.add(btnClose);
			add(btnPanel);
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();

			// set the color of the line borders.
			g2d.setColor(Color.lightGray);
			// set the thickness of the borders
			g2d.setStroke(new BasicStroke(1.5f));
			// draw the rectangle with rounded corners.
			g2d.drawRoundRect(1, POINTER_HALF_SIZE, getWidth() - 2, getHeight()
			        - POINTER_HALF_SIZE - 2, ROUND_BORDER_ARC_WIDTH,
			        ROUND_BORDER_ARC_WIDTH);
			// draw the two lines for the pointer (upper triangle)
			int[] trLineXPoints = { getPointerOffset() - POINTER_HALF_SIZE,
			        getPointerOffset(), getPointerOffset() + POINTER_HALF_SIZE };
			int[] trLineYPoints = { POINTER_HALF_SIZE, 0, POINTER_HALF_SIZE };
			g2d.drawPolyline(trLineXPoints, trLineYPoints, trLineXPoints.length);

			// set the color for the filling. It is the system color used for
			// info balloons.
			g2d.setColor(SystemColor.info);
			// fill the rounded rectangle.
			g2d.fillRoundRect(2, POINTER_HALF_SIZE + 1, getWidth() - 4,
			        getHeight() - POINTER_HALF_SIZE - 4,
			        ROUND_BORDER_ARC_WIDTH - 1, ROUND_BORDER_ARC_WIDTH - 1);
			// fill the poiinter.
			int[] trXPoints = { getPointerOffset() - POINTER_HALF_SIZE,
			        getPointerOffset(), getPointerOffset() + POINTER_HALF_SIZE };
			int[] trYPoints = { POINTER_HALF_SIZE + 1, 1, POINTER_HALF_SIZE + 1 };
			g2d.fillPolygon(trXPoints, trYPoints, trXPoints.length);

		}

		/**
		 * Gets the balloon pointer offset.
		 * @return the balloon pointer offset.
		 */
		public int getPointerOffset() {

			return getWidth() / 2;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		dispose();
		setVisible(false);
	}

}
