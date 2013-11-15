package eu.europa.ec.jrc.lca.registry.domain;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
	public ObjectFactory() {
	}

	public NodeChangeLog createNodeChangeLog() {
		return new NodeChangeLog();
	}

	public DataSet createDataSet() {
		return new DataSet();
	}
}
