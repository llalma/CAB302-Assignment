import java.awt.*;
import java.util.ArrayList;

public abstract class Shape {
    private shape_Type shape;
    private ArrayList<Double> coords;
    private Color colour;

    public Shape(shape_Type shape, ArrayList<Double> coords){
        this.shape = shape;
        this.coords = coords;
    }

    public Shape(shape_Type shape, Color colour){
        this.shape = shape;
        this.colour = colour;
    }

    public abstract void draw(Graphics g, double width, double height);
    public abstract void fill(Graphics g,double width, double height);
    public abstract String save_Text();
    public shape_Type get_Shape() {
        return shape;
    }

}
