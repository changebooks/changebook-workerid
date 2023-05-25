# changebook-workerid
### 机器id

### pom.xml
```
<dependency>
  <groupId>io.github.changebooks</groupId>
  <artifactId>changebook-workerid</artifactId>
  <version>1.0.3</version>
</dependency>
```

### Zookeeper Client
```
zookeeper:
  connect-string: 127.0.0.1:2181
  base-path: worker_id
  max-retries: 3
  base-sleep-ms: 100
  max-sleep-ms: 1000
```

### Worker Id
```
worker-id:
  min-id: 0
  max-id: 1000
  group-id: ${spring.application.name}
```

### Worker Id Configurer
```
@Configuration
@EnableConfigurationProperties({WorkerIdProperties.class, ZookeeperProperties.class})
public class WorkerIdConfigurer {

    @Bean
    public WorkerId workerId(Zookeeper zookeeper, WorkerIdProperties workerIdProperties) {
        return new WorkerIdImpl(zookeeper, workerIdProperties);
    }

    @Bean(name = "zookeeper", initMethod = "start", destroyMethod = "close")
    public Zookeeper zookeeper(ZookeeperProperties zookeeperProperties) {
        return new Zookeeper(zookeeperProperties);
    }

}
```

### Hold Id
```
@Resource
private WorkerId workerId;

public int id() {
    return workerId.holdId();
}
```
