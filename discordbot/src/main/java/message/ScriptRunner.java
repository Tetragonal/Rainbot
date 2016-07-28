package message;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptRunner implements Runnable{
	
	private String script;
	private String result;
	
    public ScriptRunner(String script) {
            this.script = script;
    }

    public void run() {
        try {
	        // create a script engine manager
	        ScriptEngineManager factory = new ScriptEngineManager();
	        // create a JavaScript engine
	        ScriptEngine engine = factory.getEngineByName("JavaScript");
	        // evaluate JavaScript code from String
	        if(!script.contains("exit()")){
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
