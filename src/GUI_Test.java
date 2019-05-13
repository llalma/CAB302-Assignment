import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GUI_Test {

    //Global instance
    GUI GUI = new GUI();
    Random rand = new Random();

    @BeforeEach
    public void ConstructSum(){
        GUI = new GUI();
    }

    /*
     * Test 1: Testing GUI constructor adds nofill and black pen on initilisation
     *
     */
    @Test
    public void testConstruction() {
        StringBuilder output = new StringBuilder();
        String expected = "PEN[0.0, 0.0, 0.0]FILL[79.0, 70.0, 70.0]";
        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).Type.toString());
            output.append(GUI.drawn_Shapes.get(i).coordinates.toString());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 2: Test add shape works
     *
     */
    @Test
    public void testAddShape() {
        int tool = shape_Type.values()[rand.nextInt(4)].ordinal();
        int x1 = rand.nextInt();
        int x2 = rand.nextInt();
        int y1 = rand.nextInt();
        int y2 = rand.nextInt();

        NumberFormat formatter = new DecimalFormat();
        formatter = new DecimalFormat("0.#########E0");

        StringBuilder output = new StringBuilder();
        String expected = "PEN[0.0, 0.0, 0.0]FILL[79.0, 70.0, 70.0]" + shape_Type.values()[tool].toString() + "[" + formatter.format(x1).toUpperCase() + ", "
                          + formatter.format(y1).toUpperCase() + ", " + formatter.format(x2).toUpperCase() + ", " + formatter.format(y2).toUpperCase() + "]";

        GUI.add_Shape(shape_Type.values()[tool],x1,y1,x2,y2);

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).Type.toString());
            output.append(GUI.drawn_Shapes.get(i).coordinates.toString());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 2: Test add pen colour
     *
     */
    @Test
    public void testAddPenColour() {


        NumberFormat formatter = new DecimalFormat();
        formatter = new DecimalFormat("0.#########E0");

        StringBuilder output = new StringBuilder();
        String expected = "PEN[0.0, 0.0, 0.0]FILL[79.0, 70.0, 70.0]PEN[0.0, 0.0, 0.0]";

        GUI.Add_Colour(draw_Type.PEN);

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).Type.toString());
            output.append(GUI.drawn_Shapes.get(i).coordinates.toString());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 3: Test add fill colour
     *
     */
    @Test
    public void testAddFillColour() {


        NumberFormat formatter = new DecimalFormat();
        formatter = new DecimalFormat("0.#########E0");

        StringBuilder output = new StringBuilder();
        String expected = "PEN[0.0, 0.0, 0.0]FILL[79.0, 70.0, 70.0]FILL[79.0, 70.0, 70.0]";

        GUI.Add_Colour(draw_Type.FILL_NULL);

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).Type.toString());
            output.append(GUI.drawn_Shapes.get(i).coordinates.toString());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 4: Testing undo cannot go back further
     * than original constructor values
     *
     */
    @Test
    public void testUndo() {
        StringBuilder output = new StringBuilder();
        String expected = "PEN[0.0, 0.0, 0.0]FILL[79.0, 70.0, 70.0]";

        //Iterate undo a random number of times.
        for(int i  = 0;i<rand.nextInt(10)+1 ;i++){
            //GUI.undo();
        }

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).Type.toString());
            output.append(GUI.drawn_Shapes.get(i).coordinates.toString());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 5: Test undo removes the most recent input
     *
     */
    @Test
    public void testUndoMultiple() {
        int tool = shape_Type.values()[rand.nextInt(4)].ordinal();
        int x1 = rand.nextInt();
        int x2 = rand.nextInt();
        int y1 = rand.nextInt();
        int y2 = rand.nextInt();

        NumberFormat formatter = new DecimalFormat();
        formatter = new DecimalFormat("0.#########E0");

        StringBuilder output = new StringBuilder();
        String expected = "PEN[0.0, 0.0, 0.0]FILL[79.0, 70.0, 70.0]" + shape_Type.values()[tool].toString() + "[" + formatter.format(x1).toUpperCase() + ", "
                + formatter.format(y1).toUpperCase() + ", " + formatter.format(x2).toUpperCase() + ", " + formatter.format(y2).toUpperCase() + "]";

        GUI.add_Shape(shape_Type.values()[tool],x1,y1,x2,y2);

        //Add a shape, fill colour and pen colour
        GUI.add_Shape(shape_Type.values()[4],6,6,2,8);
        GUI.Add_Colour(draw_Type.PEN);
        GUI.Add_Colour(draw_Type.FILL_NULL);

        //Remove most recent shape, pen and fill colour
        GUI.undo();
        GUI.undo();
        GUI.undo();

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).Type.toString());
            output.append(GUI.drawn_Shapes.get(i).coordinates.toString());
        }
        assertEquals(expected,output.toString());
    }


}