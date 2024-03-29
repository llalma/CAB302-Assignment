import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.text.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
        String expected = "PEN #000000FILL OFF";
        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).save_Text());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 2: Test add shape works
     *
     */
    @Test
    public void testAddShape() {

        //Prevents plot being selected as plot is returned differently
        int randnum = rand.nextInt(4);
        while(randnum == shape_Type.PLOT.ordinal()){
            randnum = rand.nextInt(4);
        }
        int tool = shape_Type.values()[randnum].ordinal();
        int x1 = rand.nextInt();
        int x2 = rand.nextInt();
        int y1 = rand.nextInt();
        int y2 = rand.nextInt();

        NumberFormat formatter = new DecimalFormat();
        formatter = new DecimalFormat("0.#########E0");

        StringBuilder output = new StringBuilder();
        String expected = "PEN #000000FILL OFF" + shape_Type.values()[tool].toString() + " " + formatter.format(x1).toUpperCase() + " "
                + formatter.format(y1).toUpperCase() + " " + formatter.format(x2).toUpperCase() + " " + formatter.format(y2).toUpperCase()
                ;

        GUI.add_Shape(shape_Type.values()[tool],x1,y1,x2,y2);

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).save_Text());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 3: Test add pen colour
     *
     */
    @Test
    public void testAddPenColour() {


        NumberFormat formatter = new DecimalFormat();
        formatter = new DecimalFormat("0.#########E0");

        StringBuilder output = new StringBuilder();
        String expected = "PEN #000000FILL OFFPEN #000000";

        GUI.Add_Colour(draw_Type.PEN);

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).save_Text());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 4: Test add fill colour
     *
     */
    @Test
    public void testAddFillColour() {


        NumberFormat formatter = new DecimalFormat();
        formatter = new DecimalFormat("0.#########E0");

        StringBuilder output = new StringBuilder();
        String expected = "PEN #000000FILL OFFFILL OFF";

        GUI.Add_Colour(draw_Type.FILL_NULL);

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).save_Text());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 5: Testing undo cannot go back further
     * than original constructor values
     *
     */
    @Test
    public void testUndo() {
        StringBuilder output = new StringBuilder();
        String expected = "PEN #000000FILL OFF";

        //Iterate undo a random number of times.
        for(int i  = 0;i<rand.nextInt(10)+1 ;i++){
            //GUI.undo();
        }

        for(int i = 0;i<GUI.drawn_Shapes.size();i++){
            output.append(GUI.drawn_Shapes.get(i).save_Text());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 6: Test undo removes the most recent input
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
        String expected = "PEN #000000FILL OFFPEN #000000FILL OFF";
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
            output.append(GUI.drawn_Shapes.get(i).save_Text());
        }
        assertEquals(expected,output.toString());
    }

    /*
     * Test 7: Test Loading VEC file with error
     *
     * Error of a enum that does not exist is placed in a temporary VEC file,
     * to see if the load file method catches the error.
     *
     */
    @Test
    public void testFileErrorLoad() {

        File f;

        try {
            // creates temporary file
            f = File.createTempFile("VEC_Test_Error", ".VEC", new File(System.getProperty("user.dir")));
            //Set the file to readable and writable
            f.setWritable(true);

            //Open the file
            PrintWriter writer = new PrintWriter(f.getPath(), "UTF-8");

            //Write to the file
            writer.println("PEN #000000");
            writer.println("FILL OFF");
            writer.println("Error 0.23355263157894737 0.15121951219512195 0.7269736842105263 0.7317073170731707");

            //Close the writer connection
            writer.close();

            Scanner sc = new Scanner(f.getPath());

            String expected = "No enum constant shape_Type";
            StringBuilder output = new StringBuilder(GUI.read_Line(sc,true));
            output.replace(27,output.length(),"");

            assertEquals(expected, output.toString());

            // deletes file when the virtual machine terminate
            f.deleteOnExit();

        } catch(Exception e) {
            // if any error occurs
            e.printStackTrace();
        }

    }

    /*
     * Test 8: Test Saving VEC file
     *
     * Attempts to save a file normally.
     */
    @Test
    public void testSaveFile() {
        File f = new File(System.getProperty("user.dir"));

        assert(GUI.save_File(f));
    }

    /*
     * Test 9: Test return type of classes
     *
     * Test the return shape type of classes. e.g. rectangle returns the correct value.
     */
    @Test
    public void testClassTest() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(10.0);
        list.add(10.0);
        list.add(10.0);
        list.add(10.0);

        //Boolean that will only be false if a get_shape returns the wrong value.
        //This is used to make all the class etsts are in one test.
        boolean testBool = true;

        //Rectangle
        Rectangle rect = new Rectangle(list);
        testBool = rect.get_Shape() == shape_Type.RECTANGLE;

        if(testBool) {
            //Polygon
            Polygon poly = new Polygon(list);
            testBool = poly.get_Shape() == shape_Type.POLYGON;
        }

        if(testBool) {
            //Plot
            Plot plot = new Plot(list);
            testBool = plot.get_Shape() == shape_Type.PLOT;
        }

        if(testBool) {
            //Line
            Line line = new Line(list);
            testBool = line.get_Shape() == shape_Type.LINE;
        }

        if(testBool) {
            //Ellipse
            Ellipse ellipse = new Ellipse(list);
            testBool = ellipse.get_Shape() == shape_Type.ELLIPSE;
        }

        assertEquals(true,testBool);
    }

    /*
     * Test 10: Test save text of classes
     *
     * Test the return save text of each class
     */
    @Test
    public void testSaveText() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(10.0);
        list.add(10.0);
        list.add(10.0);
        list.add(10.0);

        //Boolean that will only be false if a get_shape returns the wrong value.
        //This is used to make all the class etsts are in one test.
        boolean testBool = true;

        //Rectangle
        Rectangle rect = new Rectangle(list);
        testBool = rect.save_Text().equals("RECTANGLE 10.0 10.0 10.0 10.0");

        if(testBool) {
            //Polygon
            Polygon poly = new Polygon(list);
            testBool = poly.save_Text().equals("POLYGON 10.0 10.0");
        }

        if(testBool) {
            //Plot
            Plot plot = new Plot(list);
            testBool = plot.save_Text().equals("PLOT 10.0 10.0 10.0 10.0");
        }

        if(testBool) {
            //Line
            Line line = new Line(list);
            testBool = line.save_Text().equals("LINE 10.0 10.0 10.0 10.0");
        }

        if(testBool) {
            //Ellipse
            Ellipse ellipse = new Ellipse(list);
            testBool = ellipse.save_Text().equals("ELLIPSE 10.0 10.0 10.0 10.0");
        }

        assertEquals(true,testBool);
    }
}