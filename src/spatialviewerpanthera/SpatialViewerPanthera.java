package spatialviewerpanthera;

/**
 *
 * @author Rahul
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection; 
import java.sql.DriverManager;
import oracle.sql.STRUCT;
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement; 
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import oracle.spatial.geometry.JGeometry;

public class SpatialViewerPanthera {

    public static final String DBURL = "jdbc:oracle:thin:@localhost:1521:XE"; 
    public static final String DBUSER = "system"; 
    public static final String DBPASS = "password";
    public static Connection con;
    public static JFrame frame;
    public static ArrayList<Circle> pondList;
    public static ArrayList<Circle> ambulanceList;
    public static ArrayList<PolygonRegion> regionList;
    public static ArrayList<Lion> lionList;
    public static JCheckBox check;
    public static MyCircle drawingPanel;
    public static boolean enableClicks = false;
    
    
    
    public static void connectToDatabase() throws SQLException {
        
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver()); 
        // Connect to Oracle Database 
        con = DriverManager.getConnection(DBURL, DBUSER, DBPASS); 

        
        
    }
    
    public static void getPonds(String shapeType) throws SQLException {
        
        if(shapeType == "pond")
            pondList = new ArrayList<Circle>();
        else
            ambulanceList = new ArrayList<Circle>();
        
        Statement statement = con.createStatement(); 
        ResultSet rs = statement.executeQuery("SELECT shape FROM sys." +
                shapeType);
        
        while(rs.next()) {
            
            STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
            //convert STRUCT into geometry
            JGeometry j_geom = JGeometry.load(st);
            double [] cordinates = j_geom.getOrdinatesArray();
            int radius = ((int) cordinates[0] - (int) cordinates[4]);
            int x = (int) cordinates[4];
            int y = (int) cordinates[5]- (int) radius/2;
            
            if(shapeType == "pond")
                pondList.add(new Circle(x,500-y-radius,radius));
            else {
                System.out.println(x+" y:"+(500-y)+" radius"+radius);
                ambulanceList.add(new Circle(x,500-y-radius,radius));
            }
        }
        
        
        rs.close();  
        statement.close();
    }
    
    public static void getLions() throws SQLException {
        
        lionList = new ArrayList<Lion>();
        Statement statement = con.createStatement(); 
        ResultSet rs = statement.executeQuery("SELECT shape FROM sys.lion");
        
        while(rs.next()) {
            
            STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
            //convert STRUCT into geometry
            JGeometry j_geom = JGeometry.load(st);
            double [] cordinates = j_geom.getOrdinatesArray();
            
            int x = (int) cordinates[0];
            int y = (int) cordinates[1];
            lionList.add(new Lion(x,500-y));
        }
        rs.close();  
        statement.close();
    }
    
    public static void getPolygon() throws SQLException {
        regionList = new ArrayList<PolygonRegion>();
        Statement statement = con.createStatement(); 
        ResultSet rs = statement.executeQuery("SELECT shape FROM sys.region");
        
        while(rs.next()) {
            
            STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
            //convert STRUCT into geometry
            JGeometry j_geom = JGeometry.load(st);
            double [] cordinates = j_geom.getOrdinatesArray();
            
          
            
            int [] xcordinates = new int[4];
            int [] ycordinates = new int[4];
            
            for(int i = 0;i<cordinates.length;i++) {
                if(i%2==0)
                    xcordinates[(int) i/2] = (int) cordinates[i];
                else
                    ycordinates[i/2] = 500 - (int) cordinates[i];
            }
       
            
            regionList.add(new PolygonRegion(xcordinates, ycordinates));
            
        }
        
        
        rs.close();  
        statement.close();
        
    }
    
    public static void main(String [] args) throws SQLException {
        
        connectToDatabase();
        
        
        Runnable runner = new Runnable() {
            
            public void run() {

                frame = new JFrame("Panthera Conservation Park");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JButton button = new JButton("Quit Application");
                
                // Define ActionListener
                ActionListener actionListener = new ActionListener() {
                  public void actionPerformed(ActionEvent actionEvent) {
                    System.out.println("I was selected.");
                  }
                };
                
                // Attach listeners
               

                button.addActionListener(actionListener);
                
                frame.setSize(518, 565);
                try {
                    getPonds("pond");
                    getPonds("ambulance");
                    getPolygon();
                    getLions();
                } catch (SQLException ex) {
                    Logger.getLogger(SpatialViewerPanthera.class.getName()).log(Level.SEVERE, null, ex);
                }
                check = new JCheckBox("show lions and ponds in selected region");
                drawingPanel = new MyCircle();
                frame.add(drawingPanel);
                
                check.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(check.isSelected()) {
                            System.out.println("checked");
                            enableClicks = true;
                        } else {
                            enableClicks = false;
                            System.out.println("not checked");
                            drawingPanel.repaint();
                        }
                    }
                });
                
                frame.add(check, BorderLayout.SOUTH);
                frame.setVisible(true);
            }
            
        };
        
        EventQueue.invokeLater(runner);



    }
    
    static class PolygonRegion {
        private int [] x;
        private int [] y;
        
        PolygonRegion(int [] x, int [] y) {
            this.x = x;
            this.y = y;
        }
    }
    
    static class Lion {
        
        private int x;
        private int y;
        
        Lion(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    static class Circle {
        
        private int x;
        private int y;
        private int radius;
        
        Circle(int x,int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }
    static class MyCircle extends JPanel  {
        
        ArrayList<Integer> clickedLionIndex;
        ArrayList<Integer> clickedPondIndex;
        
        MyCircle() {
            
            
            
            clickedLionIndex = new ArrayList<Integer>();
            clickedPondIndex = new ArrayList<Integer>();
            
            addMouseListener(new MouseAdapter() { 
                
                public void mousePressed(MouseEvent me) { 
                    int clickedX = me.getX();
                    int clickedY = me.getY();
                    System.out.println("here");
                    for(PolygonRegion polygon : regionList) {
                        Polygon p = new Polygon();
                
                        for (int i = 0; i < 4; i++)
                            p.addPoint(polygon.x[i], polygon.y[i]);
                        if(p.contains(clickedX, clickedY)) {
                            
                           for(Lion l : lionList) {
                               if(p.contains(l.x, l.y))
                                   clickedLionIndex.add(lionList.indexOf(l));
                           }
                           
                           for(Circle c : pondList) {
                               if(p.contains(c.x, c.y, c.radius, c.radius))
                                   clickedPondIndex.add(pondList.indexOf(c));
                           }
                        }
                    }
                    repaint();
                } 
                
            }); 
        }
        public void paint(Graphics g) {
            
            for (PolygonRegion polygon : regionList) {
                Polygon p = new Polygon();
                
                for (int i = 0; i < 4; i++)
                    p.addPoint(polygon.x[i], polygon.y[i]);
                g.setColor(Color.WHITE);
                g.fillPolygon(p);
                g.setColor(Color.BLACK);
                g.drawPolygon(p);
            }
            
            for (Circle circle : pondList) {
                
                
                if(clickedPondIndex.contains(pondList.indexOf(circle)) &&
                        enableClicks) {
                    g.setColor(Color.RED);
                    g.fillOval(circle.x,circle.y,circle.radius,circle.radius);
                    g.setColor(Color.BLACK);
                    g.drawOval (circle.x,circle.y,circle.radius,circle.radius);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillOval(circle.x,circle.y,circle.radius,circle.radius);
                    g.setColor(Color.BLACK);
                    g.drawOval (circle.x,circle.y,circle.radius,circle.radius);
                }
            }  
            
            for (Lion lion : lionList) {
                
                if(clickedLionIndex.contains(lionList.indexOf(lion)) 
                        && enableClicks) {
                    g.setColor(Color.red);
                    g.fillOval(lion.x, lion.y-5, 5, 5);
                }
                else {
                    g.setColor(Color.GREEN);
                    g.fillOval(lion.x, lion.y-5, 5, 5);
                }
            } 
            
            clickedLionIndex.clear();
            clickedPondIndex.clear();
        }
        
       
    }
  

}
