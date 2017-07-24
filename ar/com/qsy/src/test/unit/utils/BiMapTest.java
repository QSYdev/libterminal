package ar.com.qsy.src.test.unit.utils;

import ar.com.qsy.src.utils.BiMap;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class BiMapTest {
	private BiMap biMap;
	private final String UNEXPECTED_NOT_NULL = "La clave deberia ser null";
	private final String UNEXPECTED_NULL = "La clave no deberia ser null";
	private final String UNEXPECTED_PHYSICAL_ID = "La clave fisica obtenida no es la correcta";
	private final String UNEXPECTED_LOGICAL_ID = "La clave logica obtenida no es la correcta";

	@Test
	public void numberOfIdsConstructor() {
		biMap = new BiMap(2);
		assertNull(biMap.getPhysicalId(1), UNEXPECTED_NOT_NULL);
	}

	@Test
	public void associationsConstructor(){
		setUpWithAssociations();
		assertNotNull(biMap.getLogicalId(1), UNEXPECTED_NULL);
		assertNull(biMap.getLogicalId(5), UNEXPECTED_NOT_NULL);
	}

	@Test
	public void addEntry(){
		biMap = new BiMap(2);
		biMap.addEntry(1,1);
		assertNotNull(biMap.getLogicalId(1), UNEXPECTED_NULL);
	}

	@Test
	public void getPhysicalId(){
		setUpWithAssociations();
		assertEquals(new Integer(1), biMap.getPhysicalId(1), UNEXPECTED_PHYSICAL_ID);
		assertNotEquals(new Integer(5), biMap.getPhysicalId(2), UNEXPECTED_PHYSICAL_ID);
	}

	@Test
	public void getLogicalId(){
		setUpWithAssociations();
		assertEquals(new Integer(1), biMap.getLogicalId(1), UNEXPECTED_LOGICAL_ID);
		assertNotEquals(new Integer(4), biMap.getLogicalId(3), UNEXPECTED_LOGICAL_ID);
	}

	@Test
	public void removeByPhysicalId(){
		setUpWithAssociations();
		biMap.removeByPhysicalId(1);
		assertNull(biMap.getLogicalId(1), UNEXPECTED_NOT_NULL);
	}

	@Test
	public void removeByLogicalId(){
		setUpWithAssociations();
		biMap.removeByLogicalId(1);
		assertNull(biMap.getLogicalId(1), UNEXPECTED_NOT_NULL);
	}

	@Test
	public void clear(){
		setUpWithAssociations();
		biMap.clear();
		assertNull(biMap.getLogicalId(1), UNEXPECTED_NOT_NULL);
	}

	private void setUpWithAssociations(){
		TreeMap<Integer, Integer> associations = new TreeMap<>();
		associations.put(1,1); associations.put(2,3);
		biMap = new BiMap(2, associations);
	}

}
