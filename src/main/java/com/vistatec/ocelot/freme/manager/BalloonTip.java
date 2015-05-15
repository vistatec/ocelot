package com.vistatec.ocelot.freme.manager;

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

public class BalloonTip extends JDialog implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -7622023874939252456L;

    private Component component;

    private int x;

    private int y;

    //
    public BalloonTip(Window owner, Component component, int x, int y) {
        super(owner);
        setModal(true);
        this.x = x;
        this.y = y;
        this.component = component;
        // TODO Auto-generated constructor stub
    }

    public void makeUI() {

        setUndecorated(true);
        // setOpacity(0.1f);
        // panel.setOpaque(true);
        setBackground(new Color(0, 0, 0, 0));
        // TranslucentPanel contentPane = new TranslucentPanel();
        // setContentPane(contentPane);
        // JLabel label = new JLabel("Balloon");
        BalloonPanel panel = new BalloonPanel(0, 0, component.getWidth() + 50,
                component.getHeight() + 80, this);
        // JPanel panel = new JPanel();
        panel.add(component);
        // panel.setPreferredSize(new Dimension(label.getPreferredSize().width +
        // 20, label.getPreferredSize().height +20));

        add(panel);
        // setPreferredSize(new Dimension(200, 200));
        // setMinimumSize(new Dimension(200, 200));
        setSize(component.getWidth() + 50, component.getHeight() + 80);
        // setLocationRelativeTo(getOwner());
        setLocation(x - panel.getPointerOffset(), y);
        setVisible(true);

    }

    // @Override
    // public void paint(Graphics g) {
    // // TODO Auto-generated method stub
    // Graphics2D g2d = (Graphics2D) g.create();
    // g2d.setComposite(AlphaComposite.SrcOver.derive(0.1f));
    // g2d.setColor(getBackground());
    // g2d.fillRect(0, 0, getWidth(), getHeight());
    // super.paint(g);
    // }

    // @Override
    // public void paint(Graphics g) {
    // // TODO Auto-generated method stub
    // // super.paint(g);
    // g.setColor(Color.GRAY);
    // // int[] xPoints = {0, getWidth()/2 - 20, getWidth()/2, getWidth()/2+20,
    // getWidth(), getWidth(), 0, 0};
    // // int[] yPoints = {20, 20, 0, 20, 20, getHeight(), getHeight(), 20};
    // // g.drawPolygon(xPoints, yPoints, xPoints.length);
    // g.setColor(SystemColor.info);
    // g.fillRoundRect(x, 20, getWidth(), getHeight()-20, 20, 20);
    // int[] trXPoints = {getWidth()/2 - 20, getWidth()/2, getWidth()/2+20};
    // int[] trYPoints = {20, 0, 20};
    // g.fillPolygon(trXPoints, trYPoints, trXPoints.length);
    // // g.fillPolygon(xPoints, yPoints, xPoints.length);
    // //// g.drawLine(0, 0, getWidth(), getHeight());
    // // int[] xPoints = {getWidth()/2 - 20, getWidth()/2, getWidth()/2+20};
    // // int[] yPoints = {20, 0, 20};
    // // g.drawPolyline(xPoints, yPoints, xPoints.length);
    // // g.drawRect(0, 20, 200, 180);
    // // g.setColor(Color.white);
    // // g.fill(xPoints, yPoints, xPoints.length);
    // //// g.fillRect(0, 20, 200, 180);
    // }

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

    class BalloonPanel extends JPanel {

        private static final int POINTER_HALF_SIZE = 20;

        private static final int ROUND_BORDER_ARC_WIDTH = 20;

        private int x;

        private int y;

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
            JButton btnClose = new JButton() {

                /**
                 * 
                 */
                private static final long serialVersionUID = -6765879759340989663L;

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    // g2d.setColor(SystemColor.info);
                    // g2d.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 10,
                    // 10);
                    g2d.setColor(Color.lightGray);
                    g2d.setStroke(new BasicStroke(3f));
                    g2d.drawLine(7, 7, getWidth() - 7, getHeight() - 7);
                    // g2d.setStroke(new BasicStroke(2f));
                    g2d.drawLine(7, getHeight() - 7, getWidth() - 7, 7);
                    // super.paintComponent(g);
                }

                @Override
                protected void paintBorder(Graphics g) {

                    // super.paintBorder(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(Color.lightGray);
                    // g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 9,
                            9);
                    // g2d.drawOval(0, 0, getWidth()-1, getHeight() - 1);
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
            // btnClose.setBorder(BorderFactory.);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            
            //set the color of the line borders.
            g2d.setColor(Color.lightGray);
            //set the thickness of the borders
            g2d.setStroke(new BasicStroke(1.5f));
            //draw the rectangle with rounded corners.
            g2d.drawRoundRect(1, POINTER_HALF_SIZE, getWidth() - 2, getHeight()
                    - POINTER_HALF_SIZE - 2, ROUND_BORDER_ARC_WIDTH,
                    ROUND_BORDER_ARC_WIDTH);
            //draw the two lines for the pointer (upper triangle)
            int[] trLineXPoints = { getPointerOffset() - POINTER_HALF_SIZE,
                    getPointerOffset(), getPointerOffset() + POINTER_HALF_SIZE };
            int[] trLineYPoints = { POINTER_HALF_SIZE, 0, POINTER_HALF_SIZE };
            g2d.drawPolyline(trLineXPoints, trLineYPoints, trLineXPoints.length);
            
            //set the color for the filling. It is the system color used for info balloons.
            g2d.setColor(SystemColor.info);
            //fill the rounded rectangle.
            g2d.fillRoundRect(2, POINTER_HALF_SIZE + 1, getWidth() - 4,
                    getHeight() - POINTER_HALF_SIZE - 4,
                    ROUND_BORDER_ARC_WIDTH - 1, ROUND_BORDER_ARC_WIDTH - 1);
            //fill the poiinter.
            int[] trXPoints = { getPointerOffset() - POINTER_HALF_SIZE,
                    getPointerOffset(), getPointerOffset() + POINTER_HALF_SIZE };
            int[] trYPoints = { POINTER_HALF_SIZE + 1, 1, POINTER_HALF_SIZE + 1 };
            g2d.fillPolygon(trXPoints, trYPoints, trXPoints.length);

        }

        public int getPointerOffset() {

            return getWidth() / 2;
        }

    }

   
    @Override
    public void actionPerformed(ActionEvent e) {

        dispose();
        setVisible(false);
    }

}
