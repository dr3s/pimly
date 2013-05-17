package db;

import java.util.logging.Logger
import com.netflix.astyanax.AstyanaxContext
import com.netflix.astyanax.AstyanaxContext._
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.thrift.ThriftFamilyFactory
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl
import com.netflix.astyanax.connectionpool.impl.SmaLatencyScoreStrategyImpl
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.entitystore.DefaultEntityManager
import com.netflix.astyanax.serializers.StringSerializer
import com.netflix.astyanax.entitystore.EntityManager

object Cassandra {
  private val logger = Logger.getLogger(getClass.getName)

  val context: AstyanaxContext[Keyspace] = new AstyanaxContext.Builder()
    .forCluster("Test Cluster")
    .forKeyspace("pimly")
    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
      .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
      .setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE))
    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("CassConnectionPool")
      .setPort(9160)
      .setMaxConnsPerHost(3)
      .setSeeds("127.0.0.1:9160"))
    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
    .buildKeyspace(ThriftFamilyFactory.getInstance())

  
    context.start()
  
  def entityManager [E,K] (
      clazz:Class[E], 
      family:ColumnFamily[K, String]) : 
      EntityManager[E, K] = { 
	  
			
    
     new DefaultEntityManager.Builder()
			.withEntityType(clazz)
			.withKeyspace(context.getEntity())
			.withColumnFamily(family)
			.build()
  }
  
  def test() {
    entityManager(this.getClass(), ColumnFamily.newColumnFamily("test", StringSerializer.get(), StringSerializer.get()))
  }

}


