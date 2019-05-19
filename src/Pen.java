import java.awt.*;

public class Pen extends Shape{

    Color colour;

    /**
     * Default constructor, defaults to black
     */
    public Pen(){
        super(shape_Type.PEN,new Color(0,0,0));
        this.colour = new Color(0,0,0);
    }

    /**
     * Constructor with a colour input to select the colour.
     *
     * @param colour - colour to set the pen colour as.
     */
    public Pen(Color colour){
        super(shape_Type.PEN,colour);
        this.colour = colour;
    }

    /**
     * No Draw functionality.
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    @Override
    public void draw(Graphics g, double width, double height) {
        //No draw functionality
    }

    /**
     * No Fill functionality.
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    @Override
    public void fill(Graphics g, double width, double height) {
        //No fill functionality
    }

    /**
     * Returns the string in the correct format for the VEC file, used for saving.
     *
     * @return - String that will be saved in the VEC file
     */
    public String save_Text(){
        //Colour Saving
        StringBuilder line = new StringBuilder();
        String hex;

        if(colour == null){
            hex = "OFF";
        }else{
            hex = "#"+Integer.toHexString(colour.getRGB()).substring(2);
        }

        line.append(shape_Type.PEN + " " + hex);
        return line.toString();
    }

    /**
     * Returns the shape type
     * @return - Returns the shape type, Pen.
     */
    public shape_Type get_Shape(){
        return shape_Type.PEN;
    }

    /**
     * Returns the colour of the pen.
     * @return - pen colour.
     */
    public Color get_Colour(){
        return colour;
    }
}
