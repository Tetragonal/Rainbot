package message;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptRunner implements Runnable{
	
	private String script;
	private String result;
	private ScriptEngineManager factory;
	
    public ScriptRunner(String script, ScriptEngineManager factory) {
            this.script = script;
            this.factory = factory;
    }

    public void run() {
        try {
	        // create a JavaScript engine
	        ScriptEngine engine = factory.getEngineByName("JavaScript");
	        // evaluate JavaScript code from String
	        if(!script.contains("exit(")){
	        	result = engine.eval(script).toString();
	        }else{
	            result = null;
	        }
        } catch(ScriptException se) {
//                throw new RuntimeException(se);
        }
    }
    
    public String getResult(){
    	return result;
    }

}
