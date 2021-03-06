package br.com.moip.mockkid.service;

import br.com.moip.mockkid.model.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ResponseEntityFactoryTest {

    private ResponseEntityFactory responseEntityFactory = new ResponseEntityFactory();
    private HashMap<String, String> headerMap = new HashMap<>();

    @Before
    public void setUp() {
        headerMap.clear();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("x-queijo", "true");
    }

    @Test
    public void testFromResponse() throws Exception {
        Response response = new Response(201, headerMap, "{\"hello\": \"world\"}");

        ResponseEntity responseEntity = responseEntityFactory.fromResponse(response);

        assertEquals("{\"hello\": \"world\"}", responseEntity.getBody());
        assertEquals(201, responseEntity.getStatusCodeValue());

        headerMap.keySet().forEach(key -> assertEquals(headerMap.get(key), responseEntity.getHeaders().get(key).get(0)));
    }

}