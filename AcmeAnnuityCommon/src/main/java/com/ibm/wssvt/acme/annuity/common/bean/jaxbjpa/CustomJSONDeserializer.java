package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;
 

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
//import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.JsonProcessingException;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import java.io.IOException;

public class CustomJSONDeserializer extends JsonDeserializer<AnnuityJAXRSReturn> {
	@Override
    public AnnuityJAXRSReturn deserialize(JsonParser jp,
            DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
       
        //String errClass = node.get("errorClass").getTextValue();
        //String errMsg = node.get("errorMsg").getTextValue();
        //String retObjClass = node.get("returnObjectClass").getTextValue();
        Object retObj = node.get("returnObject").getClass();
          
        AnnuityJAXRSReturn ret = new AnnuityJAXRSReturn();
        
        //ret.setErrorClass(errClass);
        //ret.setErrorMsg(errMsg);
        //ret.setReturnObjectClass(retObjClass);
        ret.setReturnObject((Contact)retObj);
       
        return ret;
        
    }
}