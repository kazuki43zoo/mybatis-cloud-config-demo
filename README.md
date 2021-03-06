# mybatis-cloud-config-demo

This demo application provide samples for following two features.

* Refreshable `DataSource` with MyBatis
* Refreshable MyBatis configuration

## Setup

### Creating config repository on Local

```
$ mkdir -p ${HOME}/work/config-local-repo
$ echo "message = Hello by config server!
db.name = v1
mybatis.configuration-properties.db.version=1.0.0
" > ${HOME}/work/config-local-repo/mybatis-cloud-config-demo.properties
$ git add -A
$ git commit -m "First commit"
```

### Run the config server for demo

```
$ ./mvnw -f config-server/pom.xml spring-boot:run
```

### Run the demo application

```
$ ./mvnw spring-boot:run
```


### Access the APIs

* Call the Refresh scoped controller (no using MyBatis)

```
$ curl http://localhost:8080/message
Hello by config server!
```


* Call the non Refresh scoped controller (using MyBatis)

```
$ curl http://localhost:8080/settings
[{"VERSION":"1.0.0","VALUE":"v1 database","KEY":"name"},{"VERSION":"1.0.0","VALUE":"2000","KEY":"timeout"}]
```

## Try to execute demo 

### Refreshable `DataSource` with MyBatis

This project enable 'refreshable-ds' profile for executing this feature.

* `application.properties` (default)

```properties
spring.profiles.active=refreshable-ds
```

>
> **NOTE:**
> 
> About bean configuration refer to [`RefreshableDataSourceConfig`](/src/main/java/com/example/demo/RefreshableDataSourceConfig.java)

>
> **IMPORTANT:**
> 
> **In this version, the spring-boot cannot enable auto-configure of MyBatis Spring Boot when DataSource is refresh scope ...**
>
> See https://github.com/spring-projects/spring-boot/issues/22038

#### Access before changing configuration

```
$ curl http://localhost:8080/settings
[{"VERSION":"1.0.0","VALUE":"default database","KEY":"name"},{"VERSION":"1.0.0","VALUE":"0","KEY":"timeout"}]
```

#### Change datasource configuration

Change 'db.name' to 'current' from 'v1'.

```
$ echo "message = Hello by config server!
db.name = current
mybatis.configuration-properties.db.version=1.0.0
" > ${HOME}/work/config-local-repo/mybatis-cloud-config-demo.properties
$ git commit -am "Change db.name to current"
```

#### Refresh demo application

```
$ curl http://localhost:8080/actuator/refresh -d {} -H "Content-Type: application/json"
["config.client.version","db.name"]
```

#### Access after refresh

```
$ curl http://localhost:8080/settings
[{"VERSION":"1.0.0","VALUE":"current database","KEY":"name"},{"VERSION":"1.0.0","VALUE":"1000","KEY":"timeout"}]
```


### Refreshable MyBatis configuration

You need change profile to 'refreshable-mybatis' in `application.properties` as follow:

* `application.properties`

```properties
spring.profiles.active=refreshable-mybatis
```

> **NOTE:**
> 
> You can change MyBatis's beans to refresh scope using 'spring.cloud.refresh.refreshable' property provided spring-cloud as follow:
>
> ```properties
> spring.cloud.refresh.refreshable=org.apache.ibatis.session.SqlSessionFactory,org.mybatis.spring.mapper.MapperFactoryBean
> ```
> 
> An actual configuration file refer to [`application-refreshable-mybatis.properties`](/src/main/resources/application-refreshable-mybatis.properties)
>
> Related with https://github.com/mybatis/spring/issues/476

#### Access before changing configuration

```
$ curl http://localhost:8080/settings
[{"VERSION":"1.0.0","VALUE":"default database","KEY":"name"},{"VERSION":"1.0.0","VALUE":"0","KEY":"timeout"}]
```

#### Change datasource configuration

Change 'mybatis.configuration-properties.db.version' to '2.0.0' from '1.0.0'.

```
$ echo "message = Hello by config server!
db.name = current
mybatis.configuration-properties.db.version=2.0.0
" > ${HOME}/work/config-local-repo/mybatis-cloud-config-demo.properties
$ git commit -am "Change db.version to 2.0.0"
```

#### Refresh demo application

```
$ curl http://localhost:8080/actuator/refresh -d {} -H "Content-Type: application/json"
["config.client.version","mybatis.configuration-properties.db.version"]
```

#### Access after refresh

```
$ curl http://localhost:8080/settings
[{"VERSION":"2.0.0","VALUE":"default database","KEY":"name"},{"VERSION":"2.0.0","VALUE":"0","KEY":"timeout"}]
```
