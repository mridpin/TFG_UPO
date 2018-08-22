package es.upo.tfg.rol.unit;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.controller.service.WarService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WarServiceTest {

	@Autowired
	UserService us;
	@Autowired
	WarService ws;
	
	@Test
	public void copyMapTest_ok() {
		Map<String, Map<String, Map<String, Double>>> expected = this.generateMap();
		Map<String, Map<String, Map<String, Double>>> result = ws.copyMap(expected);
		assertEquals(expected, result);
	}

	private Map<String, Map<String, Map<String, Double>>> generateMap() {
		Map<String, Map<String, Map<String, Double>>> map = new HashMap<>();
		Map<String, Map<String, Double>> sub1860Attributes = new HashMap<>();
		Map<String, Map<String, Double>> sub1870Attributes = new HashMap<>();
		Map<String, Double> eco1860Attributes = new HashMap<>();
		Map<String, Double> eco1870Attributes = new HashMap<>();
		Map<String, Double> mil1860Attributes = new HashMap<>();
		Map<String, Double> mil1870Attributes = new HashMap<>();
		eco1860Attributes.put("PIB", 1000.0);
		eco1870Attributes.put("PIB", 2000.0);
		mil1860Attributes.put("Infraestructura desarrollada", 1.0);
		mil1860Attributes.put("Soldados", 1000.0);
		mil1870Attributes.put("Infraestructura desarrollada", 1.0);
		mil1870Attributes.put("Soldados", 2000.0);
		sub1860Attributes.put("economicos", eco1860Attributes);
		sub1860Attributes.put("militares", mil1860Attributes);
		sub1870Attributes.put("militares", mil1870Attributes);
		sub1870Attributes.put("economicos", eco1870Attributes);
		map.put("1860", sub1860Attributes);
		map.put("1870", sub1870Attributes);
		return map;
	}
	
}
