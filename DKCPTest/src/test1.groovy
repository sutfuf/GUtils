import  moo.utils.cp2.*
import org.moo.bottle.*
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;



class foo
{
	ObjectPool<JMXgc> pool;
	
	foo(ObjectPool<JMXgc> pool)
	{
		this.pool = pool;
	}
	
	JMXgc getJMX(jmxurl)
	{
		println(this.pool.getCreatedCount())
		println(this.pool.getBorrowedCount())
		println(this.pool.getDestroyedCount())
		println(this.pool.getNumIdle())
		
		
		// TODO exception handling.  IOException, etc..
		JMXgc x = null;
		x = this.pool.borrowObject()
		try
		{
			x.ACNconnect(jmxurl)
		}
		catch (CannotConnectX e)
		{
			println(e.toString())	
		}
		return x
	}
	
	def releaseJMX(JMXgc x)
	{
		x.ACNdisconnect()
		this.pool.returnObject(x)
	}
	
}

def gpCfg = new GenericObjectPoolConfig()
gpCfg.setMaxIdle(4)
gpCfg.setMinIdle(1)
gpCfg.setBlockWhenExhausted(true)

// This goes in listener context.
// We get a context to the pool, then grab an object and use it.

// connection pool
def mxx = new foo(new GenericObjectPool<JMXgc>(new JMXgcFactory(),gpCfg));

// urls to test
def urls = ["service:jmx:rmi:///jndi/rmi://baylin:8842/jmxrmi","service:jmx:rmi:///jndi/rmi://bazinga:8842/jmxrmi"]

// ok not pararallel test, but good enough for now; real test in tomcat. 
//

def i = 0 
while (1 == 1)
{
	println("i = "+i)
	println(urls)
	if (i == 0) {i=1}else{i=0};
	println("i = "+i)
	
	def zz 
	try
	{	
		def theurl = urls.get(i)
		println(theurl)
		 zz = mxx.getJMX(theurl)
		 tmpC = new GroovyMBean(zz.mbConn, \
			 'Catalina:type=Server')
			 println(tmpC)
			 mxx.releaseJMX(zz)
	 
	}
	catch (CannotConnectX e)
	{
		println("Connection error: "+e.toString())
		mxx.releaseJMX(zz)
		
		sleep(1000)
	}
	
	catch ( NullPointerException e )
	{
		println("No connection: "+e.toString())
		mxx.releaseJMX(zz)
	}
	
	catch (IOException e)
	{
		println("Connection error: "+e.toString())
		mxx.releaseJMX(zz)
	}
	

	sleep(150)
	
}

