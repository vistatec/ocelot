package com.vistatec.ocelot.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class JsonConfigTransferService implements ConfigTransferService {

	protected static final Logger LOG = LoggerFactory.getLogger(JsonConfigTransferService.class);
	
	protected ObjectMapper mapper;
	
	protected File confFile;
	
	public JsonConfigTransferService(File confFile) {
		
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
//		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		mapper.setSerializationInclusion(Include.NON_NULL );
		mapper.setSerializationInclusion(Include.NON_EMPTY);
//		mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);
//		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
//		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		this.confFile = confFile;
    }
	
	public void setConfFile(File confFile ){
		
		this.confFile = confFile;
	}

}
