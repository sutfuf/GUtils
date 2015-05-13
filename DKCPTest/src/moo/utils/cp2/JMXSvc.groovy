package moo.utils.cp2;

import org.utils.bottle.*;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


class JMXgcFactory extends BasePooledObjectFactory<JMXgc> {
	
	JMXgc create()
	{
		return new JMXgc();
	}

	public PooledObject<JMXgc> wrap(JMXgc buffer) {
		return new DefaultPooledObject<JMXgc>(buffer);
	}
	
	public void passivateObject(PooledObject<JMXgc> pooledObject) {
		//pooledObject.getObject().setLength(0);
	}
}










