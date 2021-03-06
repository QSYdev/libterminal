package libterminal.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import libterminal.lib.routine.Color;
import libterminal.lib.routine.NodeConfiguration;
import libterminal.lib.routine.Routine;
import libterminal.lib.routine.Step;

public final class RoutineManager {

	static {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Routine.class, new RoutineSerializer());
		gsonBuilder.registerTypeAdapter(Step.class, new StepSerializer());
		gsonBuilder.registerTypeAdapter(NodeConfiguration.class, new NodeConfigurationSerializer());
		gsonBuilder.registerTypeAdapter(Color.class, new ColorSerializer());
		gsonBuilder.setPrettyPrinting();
		gson = gsonBuilder.create();
	}

	private static final Gson gson;

	public static Routine loadRoutine(final String path) throws UnsupportedEncodingException, IOException {
		Reader reader = null;
		Routine routine = null;
		try {
			reader = new FileReader(path);
			routine = gson.fromJson(reader, Routine.class);
		} finally {
			if (reader != null)
				reader.close();
		}

		return routine;
	}

	public static void storeRoutine(final String path, final Routine routine) throws IOException {
		Writer writer = null;
		try {
			writer = new FileWriter(path);
			gson.toJson(routine, writer);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public static String storeRoutine(final Routine routine) {
		return gson.toJson(routine);
	}

	static final class RoutineSerializer implements JsonDeserializer<Routine>, JsonSerializer<Routine> {

		private static final String PLAYERS_COUNT_ATT = "playerCount";
		private static final String NUMBER_OF_NODES_ATT = "numberOfNodes";
		private static final String STEPS_ATT = "steps";
		private static final String TOTAL_TIME_OUT_ATT = "totalTimeOut";
		private static final String NAME_ATT = "name";

		public RoutineSerializer() {
		}

		@Override
		public Routine deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
			final JsonObject jsonObject = json.getAsJsonObject();
			final byte playersCount = jsonObject.get(PLAYERS_COUNT_ATT).getAsByte();
			final byte numberOfNodes = jsonObject.get(NUMBER_OF_NODES_ATT).getAsByte();
			final long totalTimeOut = jsonObject.get(TOTAL_TIME_OUT_ATT).getAsLong();
			final Step[] steps = context.deserialize(jsonObject.get(STEPS_ATT), Step[].class);
			String name = jsonObject.get(NAME_ATT).getAsString();

			return new Routine(playersCount, numberOfNodes, totalTimeOut, new ArrayList<>(Arrays.asList(steps)), name);
		}

		@Override
		public JsonElement serialize(final Routine routine, final Type typeOfSrc, final JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty(PLAYERS_COUNT_ATT, routine.getPlayersCount());
			jsonObject.addProperty(NUMBER_OF_NODES_ATT, routine.getNumberOfNodes());
			jsonObject.addProperty(TOTAL_TIME_OUT_ATT, routine.getTotalTimeOut());
			jsonObject.add(STEPS_ATT, context.serialize(routine.getSteps()));
			jsonObject.addProperty(NAME_ATT, routine.getName());

			return jsonObject;
		}
	}

	static class StepSerializer implements JsonDeserializer<Step>, JsonSerializer<Step> {

		private static final String EXPRESSION_ATT = "expression";
		private static final String TIME_OUT_ATT = "timeOut";
		private static final String STOP_ON_TIMEOUT_ATT = "stopOnTimeout";
		private static final String NODES_CONFIGURATION_ATT = "nodesConfigurations";

		public StepSerializer() {
		}

		@Override
		public Step deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
			final JsonObject jsonObject = json.getAsJsonObject();

			final String expression = jsonObject.get(EXPRESSION_ATT).getAsString();
			final long timeOut = jsonObject.get(TIME_OUT_ATT).getAsLong();
			final boolean stopOnTimeout = jsonObject.get(STOP_ON_TIMEOUT_ATT).getAsBoolean();
			final NodeConfiguration[] nodesConfiguration = context.deserialize(jsonObject.get(NODES_CONFIGURATION_ATT), NodeConfiguration[].class);

			return new Step(new LinkedList<>(Arrays.asList(nodesConfiguration)), timeOut, expression, stopOnTimeout);
		}

		@Override
		public JsonElement serialize(final Step step, final Type typeOfSrc, final JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty(EXPRESSION_ATT, step.getExpression());
			jsonObject.addProperty(TIME_OUT_ATT, step.getTimeOut());
			jsonObject.addProperty(STOP_ON_TIMEOUT_ATT, step.getStopOnTimeout());
			jsonObject.add(NODES_CONFIGURATION_ATT, context.serialize(step.getNodesConfiguration()));

			return jsonObject;
		}

	}

	static class NodeConfigurationSerializer implements JsonDeserializer<NodeConfiguration>, JsonSerializer<NodeConfiguration> {

		private static final String ID_ATT = "id";
		private static final String DELAY_ATT = "delay";
		private static final String COLOR_ATT = "color";

		public NodeConfigurationSerializer() {
		}

		@Override
		public NodeConfiguration deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
			final JsonObject jsonObject = json.getAsJsonObject();

			final int id = jsonObject.get(ID_ATT).getAsInt();
			final long delay = jsonObject.get(DELAY_ATT).getAsLong();
			final Color color = context.deserialize(jsonObject.get(COLOR_ATT), Color.class);

			return new NodeConfiguration(id, delay, color);
		}

		@Override
		public JsonElement serialize(final NodeConfiguration nodeConfiguration, final Type typeOfSrc, final JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty(ID_ATT, nodeConfiguration.getId());
			jsonObject.addProperty(DELAY_ATT, nodeConfiguration.getDelay());
			jsonObject.add(COLOR_ATT, context.serialize(nodeConfiguration.getColor()));

			return jsonObject;
		}

	}

	static class ColorSerializer implements JsonDeserializer<Color>, JsonSerializer<Color> {

		private static final String RED_ATT = "red";
		private static final String GREEN_ATT = "green";
		private static final String BLUE_ATT = "blue";

		public ColorSerializer() {
		}

		@Override
		public Color deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
			final JsonObject jsonObject = json.getAsJsonObject();

			final byte red = jsonObject.get(RED_ATT).getAsByte();
			final byte green = jsonObject.get(GREEN_ATT).getAsByte();
			final byte blue = jsonObject.get(BLUE_ATT).getAsByte();

			return new Color(red, green, blue);
		}

		@Override
		public JsonElement serialize(final Color color, final Type typeOfSrc, final JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty(RED_ATT, color.getRed());
			jsonObject.addProperty(GREEN_ATT, color.getGreen());
			jsonObject.addProperty(BLUE_ATT, color.getBlue());

			return jsonObject;
		}
	}

}
