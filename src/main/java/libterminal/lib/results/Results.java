package libterminal.lib.results;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import libterminal.lib.routine.Color;

import java.io.*;

public abstract class Results {
	public abstract void start();
	public abstract void touche(final int logicID, final int stepId, final Color color, final long delay);
	public abstract void stepTimeout();
	//TODO: agregar un executionTimeout, pero para eso agregar timeout a Custom Execution para que el executor lo sepa
	public abstract void finish();

	public void bufferToFile(final StringBuilder buffer, final String fileName){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName+".txt")));
			writer.write(buffer.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected void classToJSON(Results results, String fileName){
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		try {
			Writer writer = new FileWriter(fileName+".json");
			gson.toJson(results, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
