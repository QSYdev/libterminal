package ar.com.qsy.model.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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

import ar.com.qsy.model.objects.Color;
import ar.com.qsy.model.objects.NodeConfiguration;
import ar.com.qsy.model.objects.Routine;
import ar.com.qsy.model.objects.Step;

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
		InputStreamReader reader = null;
		Routine routine = null;
		try {
			reader = new InputStreamReader(RoutineManager.class.getResourceAsStream(path), "UTF-8");
			routine = gson.fromJson(reader, Routine.class);
		} finally {
			reader.close();
		}

		return routine;
	}

	public static void storeRoutine(final String path, final Routine routine) throws IOException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
			gson.toJson(routine, writer);
		} finally {
			writer.close();
		}
	}

	private static class RoutineSerializer implements JsonDeserializer<Routine>, JsonSerializer<Routine> {

		private static final String NUMBER_OF_NODES_ATT = "numberOfNodes";
		private static final String STEPS_ATT = "steps";

		public RoutineSerializer() {
		}

		@Override
		public Routine deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
			final JsonObject jsonObject = json.getAsJsonObject();
			final byte numberOfNodes = jsonObject.get(NUMBER_OF_NODES_ATT).getAsByte();
			final Step[] steps = context.deserialize(jsonObject.get(STEPS_ATT), Step[].class);

			return new Routine(numberOfNodes, new ArrayList<>(Arrays.asList(steps)));
		}

		@Override
		public JsonElement serialize(final Routine routine, final Type typeOfSrc, final JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty(NUMBER_OF_NODES_ATT, routine.getNumberOfNodes());
			jsonObject.add(STEPS_ATT, context.serialize(routine.getSteps()));

			return jsonObject;
		}
	}

	private static class StepSerializer implements JsonDeserializer<Step>, JsonSerializer<Step> {

		private static final String EXPRESSION_ATT = "expression";
		private static final String TIME_OUT_ATT = "timeOut";
		private static final String NODES_CONFIGURATION_ATT = "nodesConfigurations";

		public StepSerializer() {
		}

		@Override
		public Step deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
			final JsonObject jsonObject = json.getAsJsonObject();

			final String expression = jsonObject.get(EXPRESSION_ATT).getAsString();
			final long timeOut = jsonObject.get(TIME_OUT_ATT).getAsLong();
			final NodeConfiguration[] nodesConfiguration = context.deserialize(jsonObject.get(NODES_CONFIGURATION_ATT), NodeConfiguration[].class);

			return new Step(new LinkedList<>(Arrays.asList(nodesConfiguration)), timeOut, expression);
		}

		@Override
		public JsonElement serialize(final Step step, final Type typeOfSrc, final JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty(EXPRESSION_ATT, step.getExpression());
			jsonObject.addProperty(TIME_OUT_ATT, step.getTimeOut());
			jsonObject.add(NODES_CONFIGURATION_ATT, context.serialize(step.getNodesConfiguration()));

			return jsonObject;
		}

	}

	private static class NodeConfigurationSerializer implements JsonDeserializer<NodeConfiguration>, JsonSerializer<NodeConfiguration> {

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

	private static class ColorSerializer implements JsonDeserializer<Color>, JsonSerializer<Color> {

		private static final String RED_ATT = "red";
		private static final String GREEN_ATT = "green";
		private static final String BLUE_ATT = "blue";

		public ColorSerializer() {
		}

		@Override
		public Color deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
			final JsonObject jsonObject = json.getAsJsonObject();

			final short red = jsonObject.get(RED_ATT).getAsShort();
			final short green = jsonObject.get(GREEN_ATT).getAsShort();
			final short blue = jsonObject.get(BLUE_ATT).getAsShort();

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

	public static void main(final String[] args) throws IOException {
		final String INPUT_PATH = "/ar/com/qsy/model/utils/input.json";
		final String OUTPUT_PATH = "src/ar/com/qsy/model/utils/output.json";
		final String PATH = "/ar/com/qsy/model/utils/output.json";

		RoutineManager.storeRoutine(OUTPUT_PATH, RoutineManager.loadRoutine(INPUT_PATH));

		final Routine routine = RoutineManager.loadRoutine(PATH);
		byte id = 0;
		System.out.println("NUMBER OF NODES = " + routine.getNumberOfNodes());
		for (final Step step : routine.getSteps()) {
			System.out.println("Step " + (++id));
			System.out.println(step);
		}
	}

}
