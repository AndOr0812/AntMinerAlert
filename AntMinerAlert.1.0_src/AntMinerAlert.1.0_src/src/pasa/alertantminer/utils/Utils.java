package pasa.alertantminer.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

public class Utils {
    public static String getStackTrace(Throwable t)
    {
    	final StringWriter sw;
    	final PrintWriter pw;
    	
    	if(t == null)
    		return null;
    	
        sw = new StringWriter();
        pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        
        return sw.toString();
    }
    
    public Iterator<Object> iterate(Object...objects) {
    	return Arrays.asList(objects).iterator();
    }
}
