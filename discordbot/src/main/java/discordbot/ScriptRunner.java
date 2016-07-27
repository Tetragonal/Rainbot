package discordbot;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.btobastian.javacord.entities.Channel;

public class ScriptRunner implements Runnable{
	private String script;
    public ScriptRunner(String script) {
            this.script = script;
    }

    public void run() {
    }
    
    public String processScript(){
        try {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        if(!script.contains("exit()")){
        	return engine.eval(script).toString();
        }
        } catch(ScriptException se) {
//                throw new RuntimeException(se);
        	//do nothing
        }
        return null;
    }

}
