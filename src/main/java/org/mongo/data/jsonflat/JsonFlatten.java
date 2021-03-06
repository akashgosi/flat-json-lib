/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mongo.data.jsonflat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Avinash Patil
 */
public class JsonFlatten {
    
    private String delimiter;
    public static final String DEFAULT_DELIMITER=".";
    public static final ObjectMapper MAPPER = new ObjectMapper();
    
    public JsonFlatten(){
        delimiter = DEFAULT_DELIMITER;
    }
    
    public JsonFlatten(String delimiter){
        this.delimiter = delimiter;
    }
    
    
    public static void main(String[] args) throws IOException {
       
       if( args == null || args.length == 0 ) {
           throw new IllegalArgumentException("Please pass atleast 1 json string as argument");
       }
       
       String input = args[0];       
       JsonNode flattenJson = new JsonFlatten().flattenJson(input);       
       System.out.println(flattenJson);
        
    }
    
    public JsonNode flattenJson(String json) {
        
        if( json == null || json.isEmpty() ) {
            throw new IllegalArgumentException("Input string cannot be empty or null");
        }
        
        JsonNode jsonNode;
        
        try {
            jsonNode = MAPPER.readTree(json);
        } catch( IOException ex) {
           throw new IllegalArgumentException("Error parsing Input Json ",ex); 
        }
        
        ObjectNode output = new ObjectNode(JsonNodeFactory.instance);
        List<String> path = new ArrayList<>();
        traverseJson(jsonNode,path,output);
        
        return output;
    }
    
    private  void traverseJson(final JsonNode node, List<String> currentPath,ObjectNode output) {
        
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            final String key = field.getKey();            
            final JsonNode value = field.getValue();
            currentPath.add(key);
            if (value.isContainerNode()) {                
                traverseJson(value,currentPath,output);                
            } else {
                output.set(getCurrentKeyPath(currentPath), value);
                System.out.println(currentPath+ ":" + value);
            }
            currentPath.remove(key);
        }
    }


    private  String getCurrentKeyPath(List<String> currentKeyPath) {
        return String.join(delimiter, currentKeyPath).trim();
    }
    
}
