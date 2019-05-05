import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import static java.lang.Math.abs;


public class GUI extends JFrame {

    //Find vector images with no background
    //List of buttons to include, needs to be na images file
    static final String[] tool_Buttons = new String[]{"resources/Line.jpg","resources/Rectangle.png","resources/Circle.png"};
    static final int num_Tool_Buttons = tool_Buttons.length;

    static final String[] file_Buttons = new String[]{"resources/Save.png"};
    static final int num_File_Buttons = file_Buttons.length;


    //Positions of mouse pointer
    private int x_Previous,y_Previous,x_Current,y_Current;

    //Stores shapes drawn in the drawing area
    enum shape_Type{
        LINE, RECTANGLE, CIRCLE
    }
    private ArrayList<Drawn_Shapes> drawn_Shapes = new ArrayList<>();
    public class Drawn_Shapes{
        shape_Type Type;
        ArrayList<Integer> coordinates;

        Drawn_Shapes(shape_Type Type, ArrayList<Integer> coordinates){
            this.Type = Type;
            this.coordinates = coordinates;
        }
    }

    //Selected drawing tool, defaults to Line
    private int selected_Tool = 0;

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
            @Override
            public void mouseDragged(MouseEvent e) {
                x_Current = e.getX();
                y_Current = e.getY();
                repaint();
            }
        });
        getContentPane().add(panel,"Center");
    }

    private void buttons_Create(){
        //Tool Buttons, on west edge
        JPanel panel = new JPanel(new GridLayout(num_Tool_Buttons, 1));
        for (int i = 0; i < num_Tool_Buttons; i++) {
            JButton button = new JButton();

            //Load and add image to a button
            try {
                Image img = ImageIO.read(getClass().getResource(tool_Buttons[i]));
                Image scaled_img = img.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;

                //Sets whats the button will do when pressed, this case change the tool selected for drawing
                button.setAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String x =e.getActionCommand();
                        selected_Tool = Integer.parseInt(x);
                        System.out.println(selected_Tool);
                    }
                });

                //These need to be after setAction
                button.setActionCommand(Integer.toString(i));
                button.setIcon(new ImageIcon(scaled_img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            panel.add(button);
        }
        getContentPane().add(panel,"West");

        /* Need to make it so buttons can be a smaller size */
        //Save buttons, new buttons ect.
        JPanel file_panel = new JPanel(new GridLayout(1, num_File_Buttons));
        for (int i = 0; i < num_File_Buttons; i++) {
            JButton button = new JButton();
            //Load and add image to a button
            try {
                Image img = ImageIO.read(getClass().getResource(file_Buttons[i]));
                Image scaled_img = img.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH ) ;

                button.setIcon(new ImageIcon(scaled_img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            button.setPreferredSize(new Dimension(40, 40));
            file_panel.add(button);
        }
        getContentPane().add(file_panel,"North");
    }

    private void MouseClicked(MouseEvent event){
        //get the x and y coordinates when the click occours
        x_Previous = event.getX();
        y_Previous = event.getY();
        x_Current = x_Previous;
        y_Current = y_Previous;
        //System.out.println("mouse clicked");
    }

    private void MouseReleased(MouseEvent event){

        x_Current = event.getX();
        y_Current = event.getY();
        //System.out.println("mouse released");

        //Add shape to previously drawn of shapes
        add_Shape();
        repaint();

    }

    private void MouseDragged(MouseEvent event){
        x_Current = event.getX();
        y_Current = event.getY();
        repaint();
    }

    private void add_Shape(){
        //Add shape to previously drawn of shapes
        ArrayList<Integer> Coords = new ArrayList<>();
        Coords.add(x_Previous);
        Coords.add(y_Previous);
        Coords.add(x_Current);
        Coords.add(y_Current);

        //Creates a new element to insert into the list of drawn shapes
        Drawn_Shapes shape = new Drawn_Shapes(shape_Type.values()[selected_Tool], Coords);
        drawn_Shapes.add(shape);
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

            //Draws shapes currently drawn to the screen
            for (Drawn_Shapes shape:drawn_Shapes) {
                switch (shape.Type){
                    case LINE:
                        g.drawLine(shape.coordinates.get(0), shape.coordinates.get(1), shape.coordinates.get(2), shape.coordinates.get(3));
                        break;
                    case RECTANGLE:
                        //Is longer than Line as width and height are used not x and y coords
                        g.drawRect(Math.min(shape.coordinates.get(0), shape.coordinates.get(2)),Math.min(shape.coordinates.get(1), shape.coordinates.get(3)),Math.abs(shape.coordinates.get(0) - shape.coordinates.get(2)),Math.abs(shape.coordinates.get(1) - shape.coordinates.get(3)));
                        break;
                    default:
                        System.out.println("Not a shape");
                        //put exception throw here.
                        break;
                }
            }

            //Draw shape currently being drawn
            switch (shape_Type.values()[selected_Tool]) {
                case LINE:
                    g.drawLine(x_Previous,y_Previous,x_Current,y_Current);
                    break;
                case RECTANGLE:
                    g.drawRect(Math.min(x_Previous, x_Current),Math.min(y_Previous, y_Current),Math.abs(x_Previous - x_Current),Math.abs(y_Previous - y_Current));
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(false);
        new GUI();
    }

}