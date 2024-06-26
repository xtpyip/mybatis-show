- 框架之间的关系
    - 见文件ORM框架类.drawio
- ORM框架实现的核心类包括
  - 加载配置文件
  - 解析XML文件
  - 获取数据库session
  - 操作数据库及返回结果

- SqlSession是对数据库进行定义和处理的类，包括常用的方法，如selectOne和selectList等
- DefaultSqlSessionFactory是对数据库配置的开启会话的工厂处理类，这里的工厂会操作DefaultSqlSession
- SqlSessionFactoryBuilder是对数据库进行操作的核心类，包括处理工厂，解析文件和获取会话等

### ORM简易框架运行流程总结
- 读取配置文件mybatis-config-datasource.xml，获取xNode信息存储在XNode类中，connection,dataSource,mapperElement信息存储进Configuration类中
- SqlSessionFactoryBuilder读取xml信息存储入Configuration中，并生成SqlSessionFactory接口的默认实现类DefaultSqlSessionFactory类
- DefaultSqlSessionFactory使用Configuration配置信息创建SqlSession实现类DefaultSqlSession
- sqlSession调用方法，内部使用反射利用mapperElement内容调用XNode的sql语句，中途进行sql填充，及返回值处理
- 最后打印出返回的结果（完）
- help
  - XNode中存储的是一个又一个定义在xml中的\<select>,\<update>,\<delete>,\<insert>标签的具体内容，包括参数，返回值，sql语句，唯一标识（调用），以及要填充的位置及对应的字符串
  - Configuration中存储的是数据库连接，数据库信息（driver,username,password,url）以及所有的XNode类（可调用的方法）




