import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;


public class GUI extends JFrame {

    static final int num_Buttons = 2;
    static final String[] image_Buttons = new String[]{"resources/Rectangle.png","resources/Circle.png"};
    /**
     *
     */
    private static final long serialVersionUID = 4145047606482760810L;
    private int x_Previous,y_Previous,x_Current,y_Current;

    

    public GUI() {
        super("Paint");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        buttons_Create();
        drawing_area();

        // Display the window.
        setPreferredSize(new Dimension(400, 200));
        setLocation(new Point(100, 100));
        pack();
        setVisible(true);
    }

    private void drawing_area(){
        JPanel panel = new Draw_Panel();

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                MouseClicked(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                MouseReleased(evt);
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                //jPanel2MouseDragged(evt);
            }
        });
        getContentPane().add(panel,"Center");
    }

    private void buttons_Create(){
        JPanel panel = new JPanel(new GridLayout(num_Buttons, 1));
        for (int i = 0; i < num_Buttons; i++) {
            JButton button = new JButton();

            //Load and add image to a button
            try {
                Image img = ImageIO.read(getClass().getResource(image_Buttons[i]));
                Image scamled_img = img.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;
                button.setIcon(new ImageIcon(scamled_img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            panel.add(button);
        }
        getContentPane().add(panel,"West");
    }

    private void MouseClicked(MouseEvent event){
        //get the x and y coordinates when the click occours
        x_Previous = event.getX();
        y_Previous = event.getY();
        //System.out.println("mouse clicked");
    }

    private void MouseReleased(MouseEvent event){

        x_Current = event.getX();
        y_Current = event.getY();
        //System.out.println("mouse released");

        //Redraw the paint area
        repaint();
    }


    class Draw_Panel extends JPanel {

        Draw_Panel() {
            //Initilise Drawing panel
            setBackground(new java.awt.Color(255, 255, 255));
            setBorder(BorderFactory.createLineBorder(Color.black));
            setName("Paint Area");
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawLine(x_Previous,y_Previous,x_Current,y_Current);
        }
    }

    public static void main(String[] args) {
           EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(false);
                new GUI();
            }
        });
    }

}