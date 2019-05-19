import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.round;

public class Plot extends Shape{

    private double x1,y1;
    //Constructor
    public Plot(ArrayList<Double> coords){
        super(shape_Type.ELLIPSE,coords);
        x1 = coords.get(0);
        y1 = coords.get(1);
    }

    public void draw(Graphics g,double width, double height){
        int x1_scaled = (int)round(x1 * width);
        int y1_scaled = (int)round(y1 * height);

        g.drawOval(x1_scaled,y1_scaled,1,1);
    }

    public void fill(Graphics g,double width, double height){
        //No Fill functionality
    }

    public String save_Text(){
        return shape_Type.PLOT + " " + x1 + " " + y1 + " " +  x1 + " " +  y1;
    }

    public shape_Type get_Shape(){
        return shape_Type.PLOT;
    }
}
