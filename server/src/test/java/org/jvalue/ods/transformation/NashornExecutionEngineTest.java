package org.jvalue.ods.transformation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import mockit.integration.junit4.JMockit;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public final class NashornExecutionEngineTest
{

	private static JsonNode jsonData;
	private static ExecutionEngine executionEngine;
	private static TransformationFunction transformationFunction;
	private static ObjectMapper mapper;
	private static String sampleData;


	@BeforeClass
	public static void initialize() throws IOException, URISyntaxException
	{
		mapper = new ObjectMapper();
		URL resource = NashornExecutionEngineTest.class.getClassLoader().getResource("js/SampleWeatherData");

		File sampleWeatherData = new File(resource.toURI());
		sampleData = FileUtils.readFileToString(sampleWeatherData);

		executionEngine = new NashornExecutionEngine();
		jsonData = mapper.readTree(sampleData);
	}


	private static final String simpleExtension =
			"function transform(dataString){"
			+ "	   var doc = JSON.parse(dataString);"
			+ "    if(doc.main != null){"
			+ "        doc.main.extension = \"This is an extension\";"
			+ "    }"
			+ "    return JSON.stringify(doc);"
			+ "};";


	@Test
	public void testExtensionTransformationExecution() throws ScriptException, IOException
	{
		transformationFunction = new TransformationFunction("1", simpleExtension);
		String result = executionEngine.execute(jsonData, transformationFunction);

		JsonNode jsonNode = mapper.readTree(result);
		Assert.assertEquals("This is an extension", jsonNode.get("main").get("extension").asText());
	}


	private static final String simpleReduction =
"		function transform(dataString){"
		+ "var doc = JSON.parse(dataString);"
		+ "if(doc != null){"
		+"		var result = Object.keys(doc).reduce("
		+ "			function(previous, key) {"
		+ "				previous.keycount ++;"
		+ "				return previous;"
		+ "			}, {keycount: 0});"
		+ "			return JSON.stringify(result);"
		+"		}"
		+"}";


	@Test
	public void testReduceTransformationExecution() throws ScriptException, IOException
	{
		transformationFunction = new TransformationFunction("1", simpleReduction);
		String result = executionEngine.execute(jsonData, transformationFunction);
		JsonNode jsonNode = mapper.readTree(result);
		Assert.assertEquals(12, jsonNode.get("keycount").intValue());
	}


	private static final String simpleMap =
"		function transform(dataString){"
		+" var doc = JSON.parse(dataString);"
		+ "if(doc != null){"
		+"		Object.keys(doc).map("
		+ "			function(key, index) {"
		+ "				if(key === 'coord' || key === 'main'){ "
		+ "					doc[key].newEntry = \"New Entry\";"
		+ "				}"
		+ "			});"
		+"	}"
		+ " return JSON.stringify(doc);"
		+"}";


	@Test
	public void testMapTransformationExecution() throws ScriptException, IOException
	{
		transformationFunction = new TransformationFunction("1", simpleMap);
		String result = executionEngine.execute(jsonData, transformationFunction);
		JsonNode jsonNode = mapper.readTree(result);

		Assert.assertEquals("New Entry", jsonNode.get("coord").get("newEntry").asText());
		Assert.assertEquals("New Entry", jsonNode.get("main").get("newEntry").asText());
	}


	@Test(expected = ScriptException.class)
	public void testInvalidTransformationExecution()
	throws ScriptException, IOException
	{
		transformationFunction = new TransformationFunction("2", "invalid Javascript Code");
		executionEngine.execute(jsonData, transformationFunction);
	}


	@Test(expected = ScriptException.class)
	public void testWrongFunctionSignatureTransformationExecution() throws ScriptException, IOException
	{
		transformationFunction = new TransformationFunction("10", "function test(hello){ return 1};");
		executionEngine.execute(jsonData, transformationFunction);
	}


	private static final String infiniteLoop =
			"function transform(dataString){"
			+"    while(true) { ; }"
			+"};";


	@Test(expected = ScriptCPUAbuseException.class)
	public void testInfiniteLoopTransformationExecution()
	throws ScriptException, IOException
	{
		transformationFunction = new TransformationFunction("3", infiniteLoop);
		executionEngine.execute(jsonData, transformationFunction);
	}


	private static final String javaClassAccess =
			"function transform(dataString){"
					+"    while(true) { "
					+ "		var ArrayList = Java.type('java.util.ArrayList');"
					+ " }"
					+"};";


	@Test(expected = RuntimeException.class)
	public void testAccessToJavaClassesTransformationExecution()
	throws ScriptException, IOException
	{
		transformationFunction = new TransformationFunction("3", javaClassAccess);
		executionEngine.execute(jsonData, transformationFunction);
	}
}