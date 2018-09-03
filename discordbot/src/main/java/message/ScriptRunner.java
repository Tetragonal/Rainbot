package message;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptRunner implements Runnable{
	
	private String script;
	private String result;
	private ScriptEngine engine;
	
    public ScriptRunner(String script, ScriptEngine engine) {
            this.script = script;
            this.engine = engine;
    }

    public void run() {
		// evaluate JavaScript code from String
		try {
			result = engine.eval(
                    "importPackage = undefined;"
                    + "exit = undefined;"
                    + "quit = undefined;"
                    + "__FILE__ = undefined;"
                    + "__DIR__ = undefined;"
                    + "__LINE__ = undefined;"
                    + "Java = undefined;"
                    + "load = undefined;"
                    + "loadWithNewGlobal = undefined;"
                    + "com = undefined;"
                    + "java = undefined;"
                    + "org = undefined;"
                    + "edu = undefined;"
                    + "Packages = undefined;"
                    + "javax.script.filename = undefined;"
                    + "eval = undefined;"
                    + script).toString();
		} catch (ScriptException | NullPointerException e) {
			System.out.println("Invalid js");
			//e.printStackTrace();
		}
    }
    
    public String getResult(){
    	return result;
    }

}
