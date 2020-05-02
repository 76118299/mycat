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
            
            
mycat术语
主机/实例
物理库：数据库服务
物理表
分片 ：将一张表的数据 存到不同的数据库中
分片节点：物理数据库的scheam ，数据库服务的database host/db
分片键：例如 日期，用户的id 根据这些进行分片
分片算法
逻辑库：
逻辑表：应用层的代理表，将多个数据库的表汇总成一张表 ，即逻辑表

mycat的配置文件
1.server.xml
    配置全局id 生成方式 ： <!-- 0 文件 1 数据库 2 本地时间戳 3 zk-->
                 		<property name="sequenceHandlerType">1</property>
2.schema.xml
3.rule.xml
4.sequence_conf.properties 全局id配置文件
    获取全局id select next value for MYCATSEQ_CUSTOMER
    使用 inset into tblnm() values(next value for  MYCATSEQ_CUSTOMER , "1");
    不写 next value for  MYCATSEQ_CUSTOMER 会使用全局的 GLOBAL
    <table name="user" primaryKey="id" dataNode="mdb_mydb" rule="sharding-by-intfile" autoIncrement="true"  >
    		</table>
    		autoIncrement="true" 是使用全局id
    		
    		
 mycat 核心配置 
 1.schema.xml 配置数据库服务
    	<!--配置mysql数据库服务-->
    	<dataHost name="mysql_2" maxCon="1000" minCon="10" balance="0"
    			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
    		<heartbeat>select user()</heartbeat>
    		<writeHost host="hostM2" url="jdbc:mysql://localhost:3306" user="root"  password="root">
    			<!-- 读写分离的 读库的配置
    			<readHOst>
    			</readHOst>-->
    		</writeHost>
    	</dataHost>
    	<dataHost name="mysql_1" maxCon="1000" minCon="10" balance="0"
            			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
            		<heartbeat>select user()</heartbeat>
            		<writeHost host="hostM2" url="jdbc:mysql://localhost:3306" user="root"  password="root">
            			<!-- 读写分离的 读库的配置
            			<readHOst>
            			</readHOst>-->
            		</writeHost>
          </dataHost>
 2.schema.xml dataNode和物理数据库映射
        <!--配置物理库-->
        <dataNode name="mdb_mydb1" dataHost="mysql_2" database="mydb" />
        <dataNode name="mdb_mydb2" dataHost="mysql_1" database="db1" />
        
 3.配置逻辑库和逻辑表
    	<!--配置逻辑库和逻辑表-->
    	<schema name="schema_db" checkSQLschema="true" sqlMaxLimit="100" >
    	<!--autoIncrement="true" 使用全局id   rule 数据分片规则 -->
    		<table name="user" primaryKey="id" dataNode="mdb_mydb1,mdb_mydb2" rule="sharding-by-intfile" autoIncrement="true"  >
    		</table>
    		<!--配置er表  主表      parentKey="order_id" 分片规则是主表的orderid 保证子表和主表再同一个 datanode 上-->  
    		<table name="order" primaryKey="id" dataNode="mdb_mydb1,mdb_mydb2" rule="sharding-by-intfile" >
    		        <!--配置er表     子表--> 
    				<childTable name = "orderDetail"  primaryKey="id" parentKey="order_id" joinKey="order_id" ></childTable>
    		</table>
    		<!--配置全局表  type = global -->
    		<table name="study" primaryKey="sid" dataNode="mdb_mydb"  type="global" >
    		</table>
    	</schema>
           
          
 


