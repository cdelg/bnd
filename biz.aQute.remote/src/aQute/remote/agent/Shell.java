package aQute.remote.agent;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Converter;

public class Shell extends RedirectInput {
	CommandSession session;
	PrintStream out;
	boolean running;
	
	public void open(CommandSession session) {
		this.session = session;
		this.out = session.getConsole();
		prompt();
	}

	private void prompt()  {
		CharSequence prompt;
		try {
			Object value = session.get("prompt");
			if (value instanceof CharSequence) {
				value = session.execute((CharSequence) value);
			}
			if ( value != null) {
				prompt = session.format(value, Converter.LINE);
			} else {
				prompt = "> ";
			}
		} catch (Exception e) {
			prompt = "> ";
		}
		this.out.print(prompt);
		this.out.flush();
	}
	
	public void close() {
		
	}
	public synchronized void add(String s) throws IOException {
		if ( running )
			super.add(s);
		else {
			running = true;
			try {
				Object result = session.execute(s);
				if ( result != null) {
					out.println(session.format(result, Converter.INSPECT));
				}
			} catch( Exception e) {
				e.printStackTrace(out);
			} finally {
				running = false;
			}
			prompt();
		}
	}


}
