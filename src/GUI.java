/************************************************************************
 * @author Liam Hulsman-Benson, N9960392
 * @version 1.0, May 2019
 * CAB302 - Software development programming assignment
 ************************************************************************/

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import static java.lang.Math.*;
import static javax.swing.JOptionPane.getRootFrame;
import static javax.swing.JOptionPane.showMessageDialog;

public class GUI extends JFrame {

    //List of buttons to include, needs to be an images file
    //Path to image files that are displayed on the buttons
    private static final String[] tool_Buttons = new String[]{"resources/Line.jpg","resources/Rectangle.png","resources/Plot.png","resources/Ellipse.png","resources/Polygon.png"};
    private static final String[] file_Buttons = new String[]{"resources/Save.png", "resources/Load.png", "resources/Undo.png"};
    private static final String[] colour_Special_Buttons = new String[]{"resources/NoFill.jpg","resources/Rainbow.jpg"};

    private static final Color[] colour_Buttons = new Color[]{Color.BLACK,Color.RED,Color.GREEN,Color.YELLOW,Color.WHITE,Color.BLUE,Color.ORANGE,Color.MAGENTA};
    private static final int num_Tool_Buttons = tool_Buttons.length;

    //Button tool tips
    private static final String[] tool_Button_Tip = new String[]{"Drag and release to draw line.","Drag and release to draw rectangle.", "Click to place a point at location", "Drag and release to draw ellipse.", "Click on multiple points to draw a polygon. Finish near the starting point to complete the shape."};
    private static final String[] file_Button_Tip = new String[]{"Save the current file to the user selected location.", "Load the selected file from the specified user location", "Undo the last action"};

    //Positions of mouse pointer
    private int x_Previous,y_Previous,x_Current,y_Current;

    //Stores shapes drawn in the drawing area
    public ArrayList<Drawn_Shapes> drawn_Shapes = new ArrayList<>();

    //Array list for a single Polygons
    private ArrayList<Double> polygon = new ArrayList<>();
    private boolean polygon_Completed = true;

    //Selected drawing tool, defaults to Line
    public int selected_Tool = 0;

    //Selected Colour for Pen
    public Color pen_Colour = Color.BLACK;
    private JLabel colour_Pen;

    //Check the shape is to be filled and fill colour
    private boolean fill = false;
    private Color fill_Colour = Color.BLACK;
    private JLabel colour_Fill;

    /**
     * Default constructor,
     * creates a window at point 100,100 with a size of 400,400. Uses the computers default window style,
     * Adds all buttons and the drawing area to the window. Also populates drawn_Shapes with deafult data
     */
    public GUI()  {
        super("Paint");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Create the jpanels for the buttons and the drawing area
        buttons_Create();
        drawing_area();

        // Display the window.
        setPreferredSize(new Dimension(400, 400));
        setLocation(new Point(100, 100));

        //Listen for a screen size change. If one occours repaint the screen with correct sizes.
        //Set x_current and previous to -1, so the last drawn shape is not drawn.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                x_Current = -1;
                x_Previous = -1;
                repaint();
            }
        });
        pack();
        setVisible(true);

        //Populate drawn_Shapes with default data
       new_Drawn_Shapes();
    }

    /**
     * Creates the user input drawing area, adds a mouse listener and a mouse motion lister to the panel.
     * Then adds the panel to the GUI frame.
     */
    private void drawing_area() {
        JPanel panel = new Draw_Panel();

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                MouseClicked(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                if(selected_Tool != shape_Type.POLYGON.ordinal()) {
                    MouseReleased(evt);
                }
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //Dont want to repaint for dragging with the polygon tool as that cna allow the user to draw in a different position
                if(selected_Tool != shape_Type.POLYGON.ordinal()){
                    x_Current = e.getX();
                    y_Current = e.getY();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //Dont want to repaint for dragging with the polygon tool as that cna allow the user to draw in a different position
                if(selected_Tool == shape_Type.POLYGON.ordinal() && !polygon_Completed){
                    x_Current = e.getX();
                    y_Current = e.getY();
                    repaint();
                }
            }
        });

        getContentPane().add(panel,"Center");
    }

    //Mouse Events

    /**
     * Gets the position of the mouse when the click occoured.
     * If the user has the polygon tool selected, the coordinates that were
     * clicked are added to the current instance of the polygon shape.
     *
     * If the coordinates added to the polygon is within 10 pixels in any direction of the starting
     * coordinates. The newest coordinates are set to the starting postion and the polygon is completed.
     *
     * @param event -  The event that triggered the mouse listener.
     */
    private void MouseClicked(MouseEvent event){
        //Get the x and y coordinates when the click occours.
        x_Previous = event.getX();
        y_Previous = event.getY();
        x_Current = x_Previous;
        y_Current = y_Previous;

        if(selected_Tool == shape_Type.POLYGON.ordinal()) {
            polygon_Completed = false;
            Double coords = x_Current + 0.0;
            polygon.add(coords);
            coords = y_Current + 0.0;
            polygon.add(coords);

            //Ends the polygon if original and latest position is within 10 pixels in any direction.
            if(polygon_Ending_Check() && polygon.size() > 4){
                polygon_End();
            }
        }
    }

    /**
     * Gets the current x & y coordinates of the mouse when the event occoured.
     * If any tool besides the polygon tool is selected, the shape is added to drawn_Shapes.
     *
     * This occours as with any shape besides the polygon is drawn with dragging and releasing the mouse,
     * whereas the polygon requires clicks as each vertex.
     *
     * The entire drawing area is then repainted to update for the shape just added.
     *
     * @param event - The event that triggered the mouse listener.
     */
    private void MouseReleased(MouseEvent event){

        x_Current = event.getX();
        y_Current = event.getY();

        if(selected_Tool != shape_Type.POLYGON.ordinal()){
            //Add shape to previously drawn of shapes
            add_Shape(shape_Type.values()[selected_Tool],x_Previous,y_Previous,x_Current,y_Current);
        }
        repaint();
    }

    //Button creates

    /**
     * Creates a JPanel containing the tools in the program.
     * Additional tools buttons are added by adding another image file to the tool_Buttons variable.
     *
     * @return  - JPanel containing all the tool buttons in a column contain all the tools
     */
    private JPanel Tool_Buttons(){
        JPanel panel = new JPanel(new GridLayout(num_Tool_Buttons+2, 1));
        for (int i = 0; i < num_Tool_Buttons+2; i++) {
            JButton button = new JButton();

            if( i < num_Tool_Buttons){
                //Buttons

                //Load and add image to a button
                try {
                    Image img = ImageIO.read(getClass().getResource(tool_Buttons[i]));
                    Image scaled_img = img.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;

                    //Sets whats the button will do when pressed, this case change the tool selected for drawing
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //If the user selects another tool before polygon is completed make sure it ends
                            polygon_Completed = true;
                            //Clear the polygon
                            polygon = new ArrayList<>();

                            String x =e.getActionCommand();
                            selected_Tool = Integer.parseInt(x);
                            //System.out.println(selected_Tool);
                        }
                    });

                    //These need to be after setAction
                    button.setActionCommand(Integer.toString(i));
                    button.setIcon(new ImageIcon(scaled_img));
                    button.setToolTipText(tool_Button_Tip[i]);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                //Add button to panel
                panel.add(button);
            }else if(i == num_Tool_Buttons){
                //Current pen  colour display
                colour_Pen = new JLabel("PEN");
                colour_Pen.setBackground(pen_Colour);
                colour_Pen.setHorizontalAlignment(0);
                colour_Pen.setForeground(Color.WHITE);
                colour_Pen.setOpaque(true);
                panel.add(colour_Pen);
            }else{
                //Current fill colour display
                colour_Fill = new JLabel("FILL");
                colour_Fill.setBackground(null);
                colour_Fill.setHorizontalAlignment(0);
                colour_Fill.setOpaque(true);
                panel.add(colour_Fill);
            }
        }

        return panel;
    }

    /**
     * Creates a JPanel containing the File buttons of the program: Save,Load and Undo currently.
     * Additional File buttons are added by adding another image file to the file_Buttons variable.
     *
     * @return  - JPanel containing all the File buttons in a row.
     */
    private JPanel File_Buttons(){
        JPanel file_panel = new JPanel(new GridLayout(1, file_Buttons.length));
        for (int i = 0; i < file_Buttons.length; i++) {
            JButton button = new JButton();
            //Load and add image to a button
            try {
                Image img = ImageIO.read(getClass().getResource(file_Buttons[i]));
                Image scaled_img = img.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH ) ;

                //Sets whats the button will do when pressed, this save the file
                if(file_Buttons[i].contains("Save")){
                    //This is the save button, Give save functionality
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            save_File();
                        }
                    });
                }else if(file_Buttons[i].contains("Load")){
                    //This is the load button, Give load functionality
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            load_File();
                        }
                    });
                }else if(file_Buttons[i].contains("Undo")){
                    //This is the load button, Give load functionality
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            undo();
                        }
                    });
                }


                button.setIcon(new ImageIcon(scaled_img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            button.setPreferredSize(new Dimension(40, 40));
            button.setToolTipText(file_Button_Tip[i]);
            file_panel.add(button);
        }

        return file_panel;
    }

    /**
     * Creates a JPanel containing the quick access colours as-well as the no-fill and colour palette buttons.
     * Additional quick access colours can be added by adding more colours to the colour_Buttons variable.
     * The buttons are displayed in 2 rows with the columns growing as necessary.
     *
     * @return  - JPanel containing all the File buttons in a row.
     */
    private JPanel Colour_Buttons(){
        //Colour Buttons on south edge
        //Plus 2 for number of coloums due to the 2 extra buttons fro nofill and selector
        JPanel colour_panel = new JPanel(new GridLayout(2, colour_Buttons.length+2));
        for (int i = 0; i < colour_Buttons.length; i++) {
            JButton button = new JButton();

            button.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    int type = 0;
                    //0 is a fill command, 1 is a pen command
                    if(evt.getModifiers() == 18){
                        //18 is  a left control + left mouse click
                        //Fill
                        fill_Colour = button.getBackground();
                        fill = true;
                    }else{
                        //Pen
                        pen_Colour = button.getBackground();
                        type = 1;
                    }
                    Add_Colour(draw_Type.values()[type]);
                }
            });
            button.setBackground(colour_Buttons[i]);
            button.setToolTipText("Hold Left CTRL + click for fill or click for PEN.");
            button.setSize(10,10);
            colour_panel.add(button);
        }

        //Load and add image to a button, used for the Nofill and the selector tools
        //Also part of panel above
        for(int i = 0;i<colour_Special_Buttons.length;i++) {
            try {
                JButton button = new JButton();
                Image img = ImageIO.read(getClass().getResource(colour_Special_Buttons[i]));
                Image scaled_img = img.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);

                //Set action for buttons, makes each button get their specific functionality
                if(colour_Special_Buttons[i].contains("NoFill")){
                    button.setName("Fill");
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //Simple toggle of colours and fill states
                            if(fill){
                                fill = false;
                                Add_Colour(draw_Type.FILL_NULL);

                            }else{
                                fill = true;
                            }
                        }
                    });
                    button.setToolTipText("Click to enable and disable fill. Current selection is shown on left hand side in the fill box.");
                }else if(colour_Special_Buttons[i].contains("Rainbow")){
                    button.setToolTipText("Displays popup window that user can select more colour or insert RGB values. Hold Left CTRL + click for fill or click for PEN.");
                    button.addActionListener(new Colour_Chooser());
                }

                button.setIcon(new ImageIcon(scaled_img));
                colour_panel.add(button);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        colour_panel.setName("Colour_Panel");
        return colour_panel;
    }

    /**
     * Adds the JPanels returned from the above functions and adds them to the JFrame.
     */
    private void buttons_Create() {
        //Tool Buttons, on west edge
        getContentPane().add(Tool_Buttons(), "West");
        //Save buttons, new buttons ect, On north edge.
        getContentPane().add(File_Buttons(), "North");
        //Colour Buttons
        getContentPane().add(Colour_Buttons(), "South");
    }


    //Adding objects to draw_Shapes

    /**
     * Adds the fill or pen colour change to drawn_shapes. It is added to drawn_Shapes as this will allow
     * undo to simply delete the last member of drawn_Shapes to function. THis also makes saving easier
     * as all objects are in one list, and order does not to be kept track.
     *
     * @param type - draw_Type enum specifying what the selected colour is te be used for,
     *             either fill,pen or turn off the fill.
     */
    public void Add_Colour(draw_Type type){
        //Add colour to list of shapes, same arrayList as shapes as the order matters
        //and arrayLists keeps the order

        //Create array to store RGB values
        ArrayList<Double> RGB = new ArrayList<>();

        //Creates a new element to insert into the list of drawn shapes
        Drawn_Shapes shape;
        //dosent work for colour selector wheel
        if(type == draw_Type.FILL_COLOUR){
            //0 is a fill command
            RGB.add(fill_Colour.getRed() + 0.0);
            RGB.add(fill_Colour.getGreen() + 0.0);
            RGB.add(fill_Colour.getBlue() + 0.0);
            shape = new Drawn_Shapes(shape_Type.FILL, RGB);
            colour_Fill.setBackground(fill_Colour);

            //Change the colour of text based on the background colour
            if(fill_Colour == Color.WHITE){
                colour_Fill.setForeground(Color.BLACK);
            }else{
                colour_Fill.setForeground(Color.WHITE);
            }
        }else if(type == draw_Type.FILL_NULL){
            //3 is a no fill command
            RGB.add((double)'O');
            RGB.add((double)'F');
            RGB.add((double)'F');
            shape = new Drawn_Shapes(shape_Type.FILL, RGB);
            colour_Fill.setBackground(null);
            colour_Fill.setForeground(Color.BLACK);
        }else{
            //Pen colour command
            RGB.add(pen_Colour.getRed() + 0.0);
            RGB.add(pen_Colour.getGreen() + 0.0);
            RGB.add(pen_Colour.getBlue() + 0.0);
            shape = new Drawn_Shapes(shape_Type.PEN, RGB);
            colour_Pen.setBackground(pen_Colour);

            //Change the colour of text based on the background colour
            if(pen_Colour == Color.WHITE){
                colour_Pen.setForeground(Color.BLACK);
            }else{
                colour_Pen.setForeground(Color.WHITE);
            }
        }
        drawn_Shapes.add(shape);
    }

    /**
     * Adds a polygon shape to drawn_Shapes, cannot use add_shape as
     * polygon is saved differently and polygon can be any length.
     *
     * @param data - Specifying string of data to be added to a polygon instance.
     *             Format = {"POLYGON" "0.2565789473684211" "0.2975609756097561" "0.3355263157894737" "0.7268292682926829" "0.6973684210526315" "0.5902439024390244" "0.7335526315789473" "0.28780487804878047"}
     */
    public void add_Polygon(String data[]){
        double height = getContentPane().getComponent(3).getHeight();
        double width = getContentPane().getComponent(3).getWidth();

        //Add the coordinates of the polygon to a variable
        for(int i = 1;i<data.length;i++){
            //If odd will be an x value, else will be y value
            if(i%2 != 0){
                polygon.add(Double.parseDouble(data[i]) * width);
            }else{
                polygon.add(Double.parseDouble(data[i]) * height);
            }
        }

        //Reconnecting the last point to the original
        polygon.add(Double.parseDouble(data[1]) * width);
        polygon.add(Double.parseDouble(data[2]) * height);

        //Add the polygon read from the VEC file and add to the drawing variable
        Drawn_Shapes shape = new Drawn_Shapes(shape_Type.valueOf(data[0]), polygon);
        drawn_Shapes.add(shape);

        //Clear the polygon coordinate variable
        polygon = new ArrayList<>();
    }

    /**
     * Checks if the latest point of the polygon is withing 10 pixels in any direction of the starting point
     *
     * @return -  True or false.
     */
    private boolean polygon_Ending_Check(){
        boolean same_Spot = false;
        //Checks if the click is within 10 pixels either way of original point, for x coordinates
        if (x_Current>polygon.get(0)-10 && x_Current<polygon.get(0)+10){
            same_Spot = true;
        }
        //Checks if the click is within 10 pixels either way of original point, for y coordinates
        //Extra check to see if the x-coordinate was on the point
        if (y_Current>polygon.get(1)-10 && y_Current<polygon.get(1)+10 && same_Spot){
            same_Spot = true;
        }
        return same_Spot;

    }

    /**
     * Completes the polygon and adds it to the drawn_Shapes variable.
     *
     * Changes polygon_Completed to true, then replaces the last coordinates
     * of the polygon to the starting coordinates, then adds to drawn_Shapes.
     * Finally, clears "polygon" ready for the next polygon to being.
     */
    private void polygon_End(){
        polygon_Completed = true;
        //Remove last 2 inputs
        polygon.remove(polygon.size()-2);
        polygon.remove(polygon.size()-1);

        //Add the last 2 indexes as the original coordinates
        polygon.add(polygon.get(0));
        polygon.add(polygon.get(1));

        Drawn_Shapes shape = new Drawn_Shapes(shape_Type.POLYGON, polygon);
        drawn_Shapes.add(shape);

        //Clear the polygon
        polygon = new ArrayList<>();
    }

    /**
     * Adds the shape specified by tool and the coordinates to the drawn_shape variable.
     * If the shape is a users input the values are edited into vec format by dividing by the current height and width of the drawing area respectively.
     * If the shape is loaded from a file the values are not effected
     *
     * @param tool - enum of the shape that is being added.
     * @param x1 - First X-coordinate of shape
     * @param y1 - First Y-coordinate of shape
     * @param x2 - Second X-coordinate of shape
     * @param y2 - Second Y-coordinate of shape
     */
    public void add_Shape(shape_Type tool, double x1, double y1, double x2, double y2){
        //Size of drawing area, used convert coordinates to percentage of screen size
        double height = getContentPane().getComponent(3).getHeight();
        double width = getContentPane().getComponent(3).getWidth();

        //Add shape to previously drawn of shapes
        ArrayList<Double> Coords = new ArrayList<>();
        if(x1 == x_Previous && x2 == x_Current && y1 == y_Previous && y2 == y_Current){
            //Adding from users inputs
            Coords.add(x1/width);
            Coords.add(y1/height);
            Coords.add(x2/width);
            Coords.add(y2/height);
        }else{
            //Adding from loading from file
            Coords.add(x1);
            Coords.add(y1);
            Coords.add(x2);
            Coords.add(y2);
        }

        //Creates a new element to insert into the list of drawn shapes
        Drawn_Shapes shape = new Drawn_Shapes(tool, Coords);
        drawn_Shapes.add(shape);
    }

    /**
     * Undoes the last action in drawn_Shape, including change of fill or pen colours and drawing shapes.
     * Undoes by removing the last index from drawn_Shapes, also sets x,y previous and current values to -1
     * so the shape last drawn does not appear.
     * Drawing area is then repainted to remove the latest object from the drawing area.
     */
    public void undo(){
        //Remove the last thing added to draw_shapes.
        //this will undo the last thing done
        if(drawn_Shapes.size() > 2){
            drawn_Shapes.remove(drawn_Shapes.size()-1);
            x_Current = -1;
            x_Previous = -1;
            y_Current = -1;
            y_Previous = -1;
            repaint();
        }else {
            JOptionPane.showMessageDialog(getRootFrame(),
                    "Nothing left to undo.",
                    "Undo Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    /**
     * User browses system explorer to place file. Other VEC files can be selected to save over.
     * Only Folders and VEC files are shown in the explorer.
     *
     * @return - File object to the location the user selected in the popup interface.
     */
    private File find_Path(){
        JFileChooser chooser= new JFileChooser(System.getProperty("user.dir"));
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("VEC", "VEC");
        chooser.addChoosableFileFilter(filter);

        //Show GUI and return the path when the choice is confirmed
        int choice = chooser.showDialog(null,"Select");
        return chooser.getSelectedFile();
    }

    /**
     * Converts a polygon saved in drawn_Shapes to the appropriate format for a VEC file.
     *
     * @param shape - The shape being read from. Is a Drawn_Shapes object
     * @return - The StringBuilder that will be written to VEC file.
     */
    private StringBuilder save_Polygon(Drawn_Shapes shape){
        //Polygon saving is slightly different from other shapes
        double height = getContentPane().getComponent(3).getHeight();
        double width = getContentPane().getComponent(3).getWidth();
        StringBuilder line = new StringBuilder();

        line.append(shape.Type.toString());

        //Minus 2 from size as to not include the last point which just direct back to the starting position
        for (int i = 0; i < shape.coordinates.size() - 2; i++) {
            if (i % 2 == 0) {
                line.append(" " + shape.coordinates.get(i) / width);
            } else {
                line.append(" " + shape.coordinates.get(i) / height);
            }
        }
        return line;
    }

    /**
     * Converts a colour save in drawn_Shapes to the appropriate format for a VEC file.
     *
     * @param shape - The shape being read from. Is a Drawn_Shapes object
     * @return - The StringBuilder that will be written to VEC file.
     */
    private StringBuilder save_Colour(Drawn_Shapes shape){
        //Colour Saving
        StringBuilder line = new StringBuilder();
        Color colour_temp = new Color((int)round(shape.coordinates.get(0)),(int)round(shape.coordinates.get(1)),(int)round(shape.coordinates.get(2)));
        Color oFF = new Color((int)'O',(int)'F',(int)'F');
        String hex;

        if(colour_temp.getRGB() == oFF.getRGB()){
            //Save into the file as OFF instead of an RGB value
            hex = "OFF";
        }else{
            hex = "#"+Integer.toHexString(colour_temp.getRGB()).substring(2);
        }

        line.append(shape.Type + " " + hex);
        return line;
    }

    /**
     * Saves all objects in the drawn_Shapes variable. Saves to the location returned by
     * find_Path().
     */
    private void save_File(){
        //Saves the file to the path specified by the user
        File directory_Path = find_Path();
        try {
            PrintWriter writer;
            //Create or load a VEC file depending on the path
            if(directory_Path.toString().contains("VEC")){
                writer = new PrintWriter(directory_Path, "UTF-8");
            }else{
                writer = new PrintWriter(directory_Path + ".VEC", "UTF-8");
            }

            //Loop through each object drawn
            for (Drawn_Shapes shape:drawn_Shapes) {
                StringBuilder line = new StringBuilder();
                if(shape.Type == shape_Type.POLYGON) {
                    //Save polygon
                    line = save_Polygon(shape);
                }else if(shape.Type == shape_Type.PEN || shape.Type == shape_Type.FILL){
                    //Save colours
                    line = save_Colour(shape);
                }else{
                    //All other shapes saving
                    line.append(shape.Type + " " + shape.coordinates.get(0) + " " + shape.coordinates.get(1) + " " +  shape.coordinates.get(2) + " " +  shape.coordinates.get(3));
                }
                writer.println(line);
            }

            //Close connection to the created VEC file
            writer.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(getRootFrame(), e.getMessage(),"File-Read Error", JOptionPane.WARNING_MESSAGE);
        } catch (UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(getRootFrame(), e.getMessage(),"Unsupported Encoding Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Reads a line of the file specified by sc and adds it to drawn_shape using the correct method.
     * E.G. polygon uses add_Polygon().
     *
     * If in testing mode the function returns the exception string, else it will display an alert
     * in GUI operation
     *
     * @param sc - Scanner object for the file being read.
     * @param testing - boolean checking if the function is in testing mode.
     */
    public String read_Line(Scanner sc,boolean testing){
        //Reag each string seperated by space char
        String data[] = sc.nextLine().split(" ");

        try {
            switch (shape_Type.valueOf(data[0])) {
                case PLOT:
                    add_Shape(shape_Type.valueOf(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
                    break;
                case POLYGON:
                    add_Polygon(data);
                    break;
                case PEN:
                    int r = Integer.valueOf(data[1].substring(1, 3), 16);
                    int g = Integer.valueOf(data[1].substring(3, 5), 16);
                    int b = Integer.valueOf(data[1].substring(5, 7), 16);

                    pen_Colour = new Color(r, g, b);
                    Add_Colour(draw_Type.PEN);
                    break;
                case FILL:
                    if (data[1].equals("OFF")) {
                        Add_Colour(draw_Type.FILL_NULL);
                    } else {
                        r = Integer.valueOf(data[1].substring(1, 3), 16);
                        g = Integer.valueOf(data[1].substring(3, 5), 16);
                        b = Integer.valueOf(data[1].substring(5, 7), 16);

                        fill_Colour = new Color(r, g, b);
                        Add_Colour(draw_Type.FILL_COLOUR);
                    }
                    break;
                default:
                    add_Shape(shape_Type.valueOf(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]));
                    break;
            }
        }catch(IllegalArgumentException e){
            if(!testing){
                //Normal GUI operation
                JOptionPane.showMessageDialog(getRootFrame(), e.getMessage(),"Error", JOptionPane.WARNING_MESSAGE);
            }else{
                //Testing operation
                return e.getMessage();

            }
        }

        //returns null in GUI operation
        return null;
    }

    /**
     *
     * File explorer interface where user selects a VEC file to load.
     * Only VEC files and folders are displayed
     * Selected file is then looped through by line with each line being read by read_Line().
     *
     *
     */
    private void load_File(){
        //Prewritten GUI for file browsing, shows the users current directory on opening.
        //Only shows VEC files.
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Only VEC Files", "VEC");
        chooser.addChoosableFileFilter(filter);

        //Show GUI and return the path when the choice is confirmed
        chooser.showOpenDialog(null);
        File chosenFile = chooser.getSelectedFile();

        //Clear the drawing area by emptying drawn_Shapes.
        new_Drawn_Shapes();

        //Load the file
        try {
            Scanner sc = new Scanner(chosenFile);
            //Read each line in the file & add to drawn_Shapes
            while (sc.hasNextLine()){
                read_Line(sc,false);
            }
            //Ensures the latest drawing is not displayed
            x_Current = -1;
            x_Previous = -1;
            y_Current = -1;
            y_Previous = -1;
        } catch (FileNotFoundException e) {
             JOptionPane.showMessageDialog(getRootFrame(), e.getMessage(),"File-Read Error", JOptionPane.WARNING_MESSAGE);
        }

        //Repaint screen with new shapes
        repaint();
    }

    /**
     * Emptys drawn_Shapes object and adds the two default values of:
     * BLACK pen colour
     * Null fill colour
     */
    private void new_Drawn_Shapes(){
        //Sets the default values in every new instance of drawn_Shapes.
        drawn_Shapes = new ArrayList<>();

        //Default pen colour
        ArrayList<Double> RGB = new ArrayList<>();
        RGB.add(0.0);
        RGB.add(0.0);
        RGB.add(0.0);
        drawn_Shapes.add(new Drawn_Shapes(shape_Type.PEN, RGB));

        RGB = new ArrayList<>();
        RGB.add((double)'O');
        RGB.add((double)'F');
        RGB.add((double)'F');
        drawn_Shapes.add(new Drawn_Shapes(shape_Type.FILL, RGB));
    }


    /**
     * Class used when displaying the colour palette to the user, implements an ActionListener interface.
     * When the "ok" button is pressed, whatever colour that was selected will be used in Add_Colour.
     *
     * A fill command will be given if left Control + left click is used to press the colour palette button.
     * If only a click occours the colour will be used for a pen colour.
     */
    class Colour_Chooser implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                //Sets colour then adds colour to list
                int type = 0;
                //0 is a fill command, 1 is a pen command
                if(evt.getModifiers() == 18){
                    //18 is  a left control + left mouse click
                    fill_Colour = JColorChooser.showDialog(null, "Choose a Color", pen_Colour);
                }else{
                    pen_Colour = JColorChooser.showDialog(null, "Choose a Color", pen_Colour);
                    type = 1;
                }
                Add_Colour(draw_Type.values()[type]);
            }
        }

    /**
     * Class that the drawing panel uses, extends the JPanel class.
     */
    class Draw_Panel extends JPanel {

        /**
         * Default constructior for Draw_Panel.
         * Sets background to white, sets the name to "Paint Area", adds a black border.
         *
         * An input and action map are also added to react to listen to the keyboard,
         * used to activate undo from keyboard using "CTRL + Z"
         */
        Draw_Panel() {
            //Initilise Drawing panel
            setBackground(new java.awt.Color(255, 255, 255));
            setBorder(BorderFactory.createLineBorder(Color.black));
            setName("Paint Area");

            //Get the input and action maps for drawing panel
            InputMap inputmap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionmap = this.getActionMap();

            //Add an input of left control and Z
            inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z ,KeyEvent.CTRL_DOWN_MASK ), "Undo" );

            //Action when left control and z is pressed
            actionmap.put("Undo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    undo();
                }
            });
        }

        /**
         * Draws the shape the user is currently drawing, either being dragged or using the polygon tool.
         * Shape will have the last pen and fill colour while drawing.
         *
         * @param g - Graphics object of the class
         */
        private void draw_Users_Current_shape (Graphics g){
            //Draw shape user is in the process of drawing
            switch (shape_Type.values()[selected_Tool]) {
                case LINE:
                    g.drawLine(x_Previous,y_Previous,x_Current,y_Current);
                    break;
                case RECTANGLE:
                    g.drawRect(Math.min(x_Previous, x_Current),Math.min(y_Previous, y_Current),Math.abs(x_Previous - x_Current),Math.abs(y_Previous - y_Current));
                    if(fill){
                        g.setColor(fill_Colour);
                        g.fillRect(Math.min(x_Previous, x_Current)+1,Math.min(y_Previous, y_Current)+1,Math.abs(x_Previous - x_Current)-1,Math.abs(y_Previous - y_Current)-1);
                    }
                    break;
                case PLOT:
                    g.drawOval(x_Previous,y_Previous,1,1);
                    break;
                case ELLIPSE:
                    g.drawOval(Math.min(x_Previous, x_Current),Math.min(y_Previous, y_Current),Math.abs(x_Previous - x_Current),Math.abs(y_Previous - y_Current));
                    if(fill){
                        g.setColor(fill_Colour);
                        g.fillOval(Math.min(x_Previous, x_Current)+1,Math.min(y_Previous, y_Current)+1,Math.abs(x_Previous - x_Current)-2,Math.abs(y_Previous - y_Current)-2);
                    }
                    break;
                case POLYGON:
                    g.drawLine(x_Previous,y_Previous,x_Current,y_Current);
                    for(int i = 3;i<polygon.size();i+=2){
                        g.drawLine((int)round(polygon.get(i-3)),(int)round(polygon.get(i-2)), (int)round(polygon.get(i-1)),(int)round(polygon.get(i)));
                    }
                    break;
                default:
                    //Throw Error
                    break;
            }
        }

        /**
         * Gets all objects in drawn_Shapes and either draws in the case of a shape, or changes the pen or
         * fill colour in the event of a pen or fill command.
         *
         * @param g - Graphics object of the class
         */
        private void draw_Shapes_in_drawn_Shapes(Graphics g){
            //These are used to convert the percentage into the current screen size coordinates
            double height = getContentPane().getComponent(3).getHeight();
            double width = getContentPane().getComponent(3).getWidth();
            int x1,x2,y1,y2;

            //Draws shapes that have already been drawn or loaded
            for (Drawn_Shapes shape:drawn_Shapes) {
                if(shape.Type == shape_Type.PEN) {
                    pen_Colour = new Color((int) round(shape.coordinates.get(0)), (int) round(shape.coordinates.get(1)), (int) round(shape.coordinates.get(2)));
                }else if(shape.Type == shape_Type.FILL){
                    if(shape.coordinates.get(0) == 79.0 && shape.coordinates.get(2) == 70.0 && shape.coordinates.get(2) == 70.0){
                        //Need a betetr way to check this. possibly make rgb colours null?
                        fill = false;
                    }else{
                        fill_Colour = new Color((int)round(shape.coordinates.get(0)),(int)round(shape.coordinates.get(1)),(int)round(shape.coordinates.get(2)));
                        fill = true;
                    }
                }else{
                    x1 = (int) round(shape.coordinates.get(0) * width);
                    y1 = (int) round(shape.coordinates.get(1) * height);
                    x2 = (int) round(shape.coordinates.get(2) * width);
                    y2 = (int) round(shape.coordinates.get(3) * height);

                    switch (shape.Type){
                        case LINE:
                            g.setColor(pen_Colour);
                            g.drawLine(x1,y1,x2,y2);
                            break;
                        case RECTANGLE:
                            //Draw outline
                            g.setColor(pen_Colour);
                            g.drawRect(Math.min(x1, x2),Math.min(y1, y2),Math.abs(x1 - x2),Math.abs(y1 - y2));
                            //Fill rectangle with colour
                            if(fill){
                                g.setColor(fill_Colour);
                                g.fillRect(Math.min(x1, x2)+1,Math.min(y1, y2)+1,Math.abs(x1 - x2)-1,Math.abs(y1 - y2)-1);
                            }
                            break;
                        case PLOT:
                            //Draw outline
                            g.setColor(pen_Colour);
                            g.drawOval(x1,y1,1,1);
                            break;
                        case ELLIPSE:
                            //Draw outline
                            g.setColor(pen_Colour);
                            g.drawOval(Math.min(x1, x2)+1,Math.min(y1, y2)+1,Math.abs(x1 - x2)-2,Math.abs(y1 - y2)-2);
                            if(fill){
                                g.setColor(fill_Colour);
                                g.fillOval(Math.min(x1, x2),Math.min(y1, y2),Math.abs(x1 - x2),Math.abs(y1 - y2));
                            }
                            break;
                        case POLYGON:
                            int size = shape.coordinates.size();
                            g.setColor(pen_Colour);
                            for(int i = 3;i<size;i+=2){
                                g.drawLine((int)round(shape.coordinates.get(i-3)),(int)round(shape.coordinates.get(i-2)), (int)round(shape.coordinates.get(i-1)),(int)round(shape.coordinates.get(i)));
                            }

                            //Fill Polygon
                            if(fill){
                                g.setColor(fill_Colour);
                                int[] x_points = new int[size/2];
                                int[] y_points = new int[size/2];
                                for(int i = 0;i<size;i++){
                                    if(i%2 == 0){
                                        //Even
                                        x_points[(int)round(floor(i/2))] = (int) round(shape.coordinates.get(i));
                                    }else{
                                        y_points[(int)round(floor(i/2))] = (int) round(shape.coordinates.get(i));
                                    }
                                }

                                g.fillPolygon(x_points,y_points,x_points.length);
                            }

                            break;
                        default:
                            JOptionPane.showMessageDialog(getRootFrame(), "Selected tool is not a shape","Error", JOptionPane.WARNING_MESSAGE);
                            break;
                    }
                }
            }
        }

        /**
         * Overrides the paintComponent of JPanel, to work in this scenario.
         *
         * @param g - Graphics object of the class
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            //Draw objects in drawn_shapes
            draw_Shapes_in_drawn_Shapes(g);

            //Change the colour back to the current pen colour
            g.setColor(pen_Colour);

            //Draw the object hte user is in the process of drawing
            draw_Users_Current_shape(g);
        }
    }

    /**
     * Main of the GUI class.
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(false);
        new GUI();
    }
}