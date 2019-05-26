import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.round;

public class Plot extends Shape{

    private double x1,y1;

    /**
     * Constructs the Plot shape.
     *
     * @param coords - ArrayList containing doubles which are the x and y coordinates of the shape. In that order
     */
    public Plot(ArrayList<Double> coords){
        super(shape_Type.PLOT,coords);
        x1 = coords.get(0);
        y1 = coords.get(1);
    }

    /**
     * Draw the outline of the shape with the current selected pen colour
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    public void draw(Graphics g,double width, double height){
        int x1_scaled = (int)round(x1 * width);
        int y1_scaled = (int)round(y1 * height);

        g.drawOval(x1_scaled,y1_scaled,1,1);
    }

    /**
     * No fill functionality for a plot
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    public void fill(Graphics g,double width, double height){
        //No Fill functionality
    }

    /**
     * Returns the string in the correct format for the VEC file, used for saving.
     *
     * @return - String that will be saved in the VEC file
     */
    public String save_Text(){
        return shape_Type.PLOT + " " + x1 + " " + y1 + " " +  x1 + " " +  y1;
    }

}
