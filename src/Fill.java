import java.awt.*;

public class Fill extends Shape{

   Color colour;

    /**
     * Default constructor, defaults to null.
     */
    public Fill(){
        super(shape_Type.FILL,new Color(255,255,255));
        this.colour = null;
    }

    /**
     * Constructor with a colour input to select the colour.
     *
     * @param colour - colour to set the fill colour as.
     */
    public Fill(Color colour){
        super(shape_Type.FILL,colour);
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
       // No draw functionality
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
       //No Fill functionality
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

        line.append(shape_Type.FILL + " " + hex);
        return line.toString();
    }

    /**
     * Returns the colour of the Fill.
     * @return - Fill colour.
     */
    public Color get_Colour(){
        return colour;
    }
}
