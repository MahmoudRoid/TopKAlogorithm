import Algorithm.Algorithm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {
        new Algorithm( fileToPath( "test_0.txt" ) , 5 );
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Main.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }

}



