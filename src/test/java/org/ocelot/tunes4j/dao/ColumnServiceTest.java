package org.ocelot.tunes4j.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocelot.tunes4j.config.JpaConfiguration;
import org.ocelot.tunes4j.dto.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;



@ContextConfiguration(classes={JpaConfiguration.class})
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class ColumnServiceTest {

	
	@Autowired
	private ColumnRepository reository;
	
	
	@Test
	public void shouldSave() {
		reository.deleteAll();
		
		reository.save(createColumn(1));
		reository.save(createColumn(2));
		reository.save(createColumn(3));
		List<Column> list = Lists.newArrayList(reository.findAll());
		assertNotNull(list);
		assertEquals(3,list.size());
	}
	
	
	@Test
	public void shouldGetById() {
		
		reository.deleteAll();
		
		reository.save(createColumn(1));
		assertNotNull(reository.findById(1));
	}

	
	@Test
	public void shouldRemove() {
		
		reository.deleteAll();
		
		reository.save(createColumn(1));
		reository.delete(reository.findById(1));
		assertNull(reository.findById(1));
	}
	
	
	private Column createColumn(int index){
		Column object = new Column();
		object.setId(index);
		object.setSize(100 + index);
		return object;
	}
}
