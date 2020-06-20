# mybatis-cloud-config-demo

This demo application provide two sample as follow features:

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
[{"VERSION":"1.0.0","VALUE":"default database","KEY":"name"},{"VERSION":"1.0.0","VALUE":"0","KEY":"timeout"}]
```

## Hoe to execute demo 

### Refreshable `DataSource` with MyBatis

This project enable 'refreshable-ds' profile for executing this feature.

* `application.properties` (default)

```properties
spring.profiles.active=refreshable-ds
```

>
> **NOTE:**
> 
> About bean configuration refer to `RefreshableDataSourceConfig`

>
> **IMPORTANT:**
> 
> **In this version, the spring-boot cannot enable auto-configure of MyBatis Spring Boot when DataSource is refresh scope ...**

#### Access before changing configuration

```
$ curl http://localhost:8080/settings
[{"VERSION":"1.0.0","VALUE":"default database","KEY":"name"},{"VERSION":"1.0.0","VALUE":"0","KEY":"timeout"}]
```

#### Change datasource configuration

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
> About bean configuration refer to `RefreshableMyBatisConfig`

>
> **IMPORTANT:**
> 
> **In this version, The mybatis-spring does not support to scan mappers as refresh scope bean(scoped proxy bean) in current version ...**
> (Related with https://github.com/mybatis/spring/issues/476)

#### Access before changing configuration

```
$ curl http://localhost:8080/settings
[{"VERSION":"1.0.0","VALUE":"default database","KEY":"name"},{"VERSION":"1.0.0","VALUE":"0","KEY":"timeout"}]
```

#### Change datasource configuration

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
