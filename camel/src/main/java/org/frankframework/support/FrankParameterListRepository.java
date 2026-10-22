package org.frankframework.support;

import org.apache.camel.spi.VariableRepository;

import org.apache.http.io.SessionOutputBuffer;

import org.frankframework.core.ParameterException;
import org.frankframework.core.PipeLineSession;
import org.frankframework.parameters.IParameter;
import org.frankframework.parameters.Parameter;
import org.frankframework.parameters.ParameterList;

import org.frankframework.stream.Message;

public class FrankParameterListRepository implements VariableRepository {
	ParameterList parameterList;
	Message message;
	PipeLineSession session;

	public FrankParameterListRepository(ParameterList parameterList) {
		this.parameterList = parameterList;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setSession(PipeLineSession session) {
		this.session = session;
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public String getId() {
		return "frank-param";
	}

	@Override
	public Object getVariable(String name) {
		try {
			return parameterList.getValues(message, session).getValue(name);
		} catch (ParameterException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setVariable(String name, Object value) {
		// maybe this one should be disabled
		throw new RuntimeException("not supported");
	}

	@Override
	public Object removeVariable(String name) {
		throw new RuntimeException("not supported");
	}
}
