数据库优化
1.sql 索引 优化
2.表存储引擎优化
3.主从，读写分离
4.数据库的配置优化

分库分表的类型
1.水平，（单库）分表。 user表，分成 user_d (存当天的数据) user_month(到了夜间将user_d ，通过定时任务存到user_month)。到了一个月
将user_month 表存放到 user_history表。或者按照月份进行分表。user202001，user202002....
2.水平，(多库) 分表。将同一个表的数据，分到多个数据库中，进行数据均摊。不同的库存相同的表
2.垂直(单库)，分表。表的结构是不同的，例如，商户信息表分成.2个表。商户基本信息表，商户结算信息表。
3.垂直(多库)，分库。不同的库存不同的表

跨库查询
1.做冗余字段
2.做数据同步
3.全局表，每个数据库都放一份，例如字典表
4.er表 有关联的数据 父表和子表。都落到相同的数据库中

CAP
1.C一致性
2.A可用性
3.P分区容错
base 理论
1.基本可用
2.软状态
3.最终一致性

全局事务
1.xa 二阶段提交
2.基于消息的分布式事务
3.柔性事务try confirm cancel

排序，分页，函数计算

全局主键
UUID
redis 
雪花算法 （位 bit）
    41位时间戳+10位工作机器ID（5位的数据中心id + 5位的机器id）+12位序列（毫秒级别的）
    
    
 动态数据源路由
 
 1.创建数据源路由 DynamicDataSource 继承 AbstractRouting 重写 determineCurrentLookupKey() 
 设置数据源，放入map中  
  public DynamicDatasource(DataSource defautlTargetDataSource, Map<Object,Object> targetDataSource) {
               super.setDefaultTargetDataSource(defautlTargetDataSource);
               super.setTargetDataSources(targetDataSource);
               super.afterPropertiesSet();
           }
    @Override
       protected Object determineCurrentLookupKey() {
           return getDatasource();
       }
       
   2. @Bean 的方式配置多个数据源 创建多个数据源和 DynamicDatasource 
        /**
         * 创建动态数据源
         * @return
         */
        @Bean
        @Primary
        public DynamicDatasource dynamicDataSource(DataSource firstDataSource,DataSource sendDataSource){
            Map<Object,Object> objectObjectMap = new HashMap<>();
            objectObjectMap.put("fiest",firstDataSource);
            objectObjectMap.put("send",sendDataSource);
    
            return  new DynamicDatasource(firstDataSource,objectObjectMap);
        }
        
   3.自定义注解，创建aop 拦截改注解 进行动态数据源key的拦截，拦截后设置给DynamicDatasource。
         @Around("dataSourcePointCut()")
            public Object aroud(ProceedingJoinPoint proceedingJoinPoint){
                MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
                Method method = signature.getMethod();
                TargetDatasource annotation = method.getAnnotation(TargetDatasource.class);
                if(annotation == null){
                    DynamicDatasource.setDatasource("first");
                }else {
                    DynamicDatasource.setDatasource(annotation.name());
                }
                try {
                    return proceedingJoinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }finally {
                    DynamicDatasource.clearDatasource();
                }
                return  null;
            }
            
            
            
            
主机/实例物理库：数据库服务

   物理表分片 ：将一张表的数据 存到不同的数据库中分片节点：

   物理数据库的scheam ，数据库服务的database host/db分片键：例如 日期，用户的id 根据这些进行分片分片算法

   逻辑库：

   逻辑表：应用层的代理表，将多个数据库的表汇总成一张表 ，即逻辑表

   mycat的配置文件

   1.server.xml    配置全局id 生成方式 ：

    <!-- 0 文件 1 数据库 2 本地时间戳 3 zk-->                      <property name="sequenceHandlerType">1</property> 

   2.schema.xml

   3.rule.xml

   4.sequence_conf.properties 全局id配置文件 

      获取全局id select next value for MYCATSEQ_CUSTOMER  
    
     使用 inset into tblnm() values(next value for  MYCATSEQ_CUSTOMER , "1");  
    
     不写 next value for  MYCATSEQ_CUSTOMER 会使用全局的 GLOBAL  
    
     <table name="user" primaryKey="id" dataNode="mdb_mydb" rule="sharding-by-intfile" autoIncrement="true"  >          </table>          autoIncrement="true" 是使用全局id      

   


     mycat 核心配置  

   1.schema.xml 配置数据库服务    

      <!--配置mysql数据库服务-->   

<!--配置mysql数据库服务  （读操作） balance="1"  （写操作）writeType="1" -->

switchType 属 性 - -1 表示不自动切换。 - 1 默认值，自动切换。



balance 属性 负载均衡类型，目前的取值有 3 种：

1. balance="0", 不开启读写分离机制，所有读操作都发送到当前可用的 writeHost 上。
2. 2. balance="1"，全部的 readHost 与 stand by writeHost 参与 select 语句的负载均衡，简单的说，当双 主双从模式(M1->S1，M2->S2，并且 M1 与 M2 互为主备)，正常情况下，M2,S1,S2 都参与 select 语句的负载 均衡。
   3. 3. balance="2"，所有读操作都随机的在 writeHost、readhost 上分发。
      4. 4. balance="3"，所有读请求随机的分发到 wiriterHost 对应的 readhost 执行，writerHost 不负担读压力， 注意 balance=3 只在 1.4 及其以后版本有，1.3 没有

负载均衡类型，目前的取值有 3 种：

1. writeType="0", 所有写操作发送到配置的第一个 writeHost，第一个挂了切到还生存的第二个 writeHost， 重新启动后已切换后的为准，切换记录在配置文件中:dnindex.properties . 

   1. switchType 属 性 ：-1 表示不自动切换。 1 默认值，自动切换。  2 基于 MySQL 主从同步的状态决定是否切换。

2. 2. writeType="1"，所有写操作都随机的发送到配置的 writeHost，1.5 以后废弃不推荐。

      

```
<dataHost name="mdb" maxCon="1000" minCon="10"  
		  writeType="0" switchType="1"   balance="2"  dbType="mysql" dbDriver="native"  slaveThreshold="100"   >
	<heartbeat>select user()</heartbeat>
	<!-- can have multi write hosts -->
<writeHost host="hostM2" url="jdbc:mysql://localhost:3306" user="root"
			   password="root">
 <!-- 读写分离的 读库的配置-->
	<readHost host="hostS2" url="jdbc:mysql://localhost:3306" user="root"
			   password="root" >
	</readHOst>
</writeHost>
 <!-- 可以写多个主机-->
 <writeHost host="hostM2" url="localhost:3316" user="root" password="123456"/> 
</dataHost>  

 <dataHost name="mysql_1" maxCon="1000" minCon="10" balance="0"                       writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">          

           <heartbeat>select user()</heartbeat>       

        <writeHost host="hostM2" url="jdbc:mysql://localhost:3306" user="root"  password="root">          

<!-- 读写分离的 读库的配置      <readHOst>    </readHOst>-->               

  </writeHost>     

 </dataHost> 
```

   




   2.schema.xml dataNode和物理数据库映射     

  <!--配置物理库-->       

<dataNode name="mdb_mydb1" dataHost="mysql_2" database="mydb" />        <dataNode name="mdb_mydb2" dataHost="mysql_1" database="db1" />       

   


     3.配置逻辑库和逻辑表      

   


    <!--配置逻辑库和逻辑表-->     
    
    <schema name="schema_db" checkSQLschema="true" sqlMaxLimit="100" >   

   ​    <!--autoIncrement="true" 使用全局id   rule 数据分片规则 -->    

         <table name="user" primaryKey="id" dataNode="mdb_mydb1,mdb_mydb2" rule="sharding-by-intfile" autoIncrement="true"  >          </table>      

   ​    <!--配置er表  主表      parentKey="order_id" 分片规则是主表的orderid 保证子表和主表再同一个 datanode 上-->       

        <table name="order" primaryKey="id" dataNode="mdb_mydb1,mdb_mydb2" rule="sharding-by-intfile" >                  <!--配置er表     子表-->             
            
        </table>

   ​    <childTable name = "orderDetail"  primaryKey="id" parentKey="order_id" joinKey="order_id" ></childTable>          </table>         

    <!--配置全局表  type = global -->      
    
       <table name="study" primaryKey="sid" dataNode="mdb_mydb"  type="global" >          </table>     
    
     </schema>                      

##### mysql 主从复制

binglog记录了操作语句

1. binlog配置
2. binglog格式 
   1. statement  记录操作的sql
   2. row 记录的每条数据的修改
   3. mixed 上面两种的结合
3. 查看binlog sql :show binog events in 'mysql-bin.000001'

```shell
vim /etc/my.conf
//主节点的配置

//主从复制 指定binlog存放的目录
log-bin=/var/lib/mysql/mysql-bin
//指定唯一的servierid
sever-id=1


//在主节点创建用户给子节点使用
SQL:GRANT REPLICATION SLAVE,REPLICATION CLIENT ON *.* 'relp'@ '192.168.147' IDENTIFIED BY '123456';
FLUSH PRIVILEGES;

grant 权限 on 数据库对象 to 用户
权限： REPLICATION SLAVE,REPLICATION CLIENT
数据库对象： *.*
用户：'relp'@ '192.168.147' IDENTIFIED BY '123456'

//从节点的配置
vim /etc/my.cnf

log_bin=mysql-bin  // binlog的存储位置
server_id=2 //servierid
//开启中继日志
relay_log=/var/lib/mysql/mysql-bin
//从机只能读 只读模式
read_only=1 
//其他节点可用在这复制数据
log-slave-updates=1

//从节点通过用户连接主节点
// 从节点执行一下命令
stop slave;
change master to
master_host='192.168.8.146'
,master_user='relp'
,master_password='123456'
,//master_log_file='mysql-bin.000001'
,master_log_pos=4;
start slave;//开始同步数据

//master_log_file 主机上的二进制文件 
可以同show master status 获取
//master_log_pos 偏移量

//查看状态
show slave status
 slave_io
 slave_sql 两个线程 就是对了
 原理：
从节点去请求同步bin_log
先写到中继日志 relay_log ，在写到从节点的binlog

```



##### mycat  注解

1.不配置er表的关联查询，er表的目的就是将主表和子表 主外键相同的数据。路由到同一个节点。

​	如果不配置er表 相同的父 id有关联关系的数据，会分散到不同的节点上。

```mysql
<!--指定语句在某个物理节点上创建存储过程-->
/*!mycat:sql=select * from customer where id =1 */
create procedure test_proc() begin end; //创建存储过程

/*!mycat:sql=select * from customer where id =1 */
create teble test(id int); //创建表
<!--inster into 语句-->
/*!mycat:sql=select * from customer where id =1 */
insert into test(if) select id from order_detail


<!--去所有物理节点收集符合sql关联查询的数据进行聚合-->
/*!mycat:catlet = io.mycat.catlets.ShareJoin*/
select a.order_id ,b.goods_id from order_info a,order_detail b 
where a.order_id = b.order_id

<!--强制走读库-->
/*balance*/
select *from tetst
/*!mycat:db_type=master*/
select *from tetst





```

##### mycat 数据分片策略

- 连续分片

  - 范围分片
  - 时间分片

- 离散分片

  - 枚举

  - 一致哈希

  - 十进制取模

  - 固定分片哈希

  - 取模范围

  - 取模范围

  - 其他

    











