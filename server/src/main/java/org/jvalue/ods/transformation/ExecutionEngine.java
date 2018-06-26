package org.jvalue.ods.transformation;

import com.fasterxml.jackson.databind.JsonNode;

import javax.script.ScriptException;
import java.io.IOException;

public interface ExecutionEngine
{
	JsonNode execute(JsonNode data, TransformationFunction transformationFunction) throws  IOException,
			ScriptException;
}
