package eu.europa.ec.jrc.lca.registry.view.model;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.view.model.ILCDLazyDataModel;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.domain.Node_;
import eu.europa.ec.jrc.lca.registry.service.NodeService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class ILCDLazyDataModelTest {
	@Autowired
	private NodeService nodeService;
	
	@Test
	public void testFindAll(){
		LazyDataModel<Node> model = new ILCDLazyDataModel<Node>(nodeService);
		model.setPageSize(2);
//		Assert.assertEquals(10, model.getRowCount());
	}
	
	@Test
	public void testFindWithPrecondition(){
		SearchParameters sp = new SearchParameters();
		sp.addFilter(Node_.status.getName(), NodeStatus.NOT_APPROVED);
		
		LazyDataModel<Node> model = new ILCDLazyDataModel<Node>(nodeService, sp);
		model.setPageSize(2);
//		Assert.assertEquals(7, model.getRowCount());
	}
}
