### 本章知识
- 映射器（代理类）
  - 因为正常开发的时候，一个mapper包下会有对各种实体类的接口Dao来操作数据表，对每一个接口来说，都需要一个代理类（映射器）来对其进行代理增加
  - 我们由此，使用了一个MapperRegistry，来对所有的接口进行统一管理
  - 每一个接口Class唯一对应一个代理类（映射器）工厂对象
  - 当我们需要这个接口映射器时，我们只需要对MapperRegistry进行查找，生成指定实例即可
- 本章流程
  - 如下代码所示
  - ```java
    MapperRegistry registry = new MapperRegistry();
        registry.addMappers("com.pyip.mybatis.dao");
        // 从SqlSession工厂获取session
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(registry);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 测试映射器对象（代理类）
        String res = userDao.queryUserName("10001");
        logger.info("测试结束：{}",res);```
  - addMappers是对指定包下的接口进行统一注册，统一管理
  - 对每一个SqlSession对象，sqlSession.getMapper内部调用的都是映射器的方法
  - ```java
    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knowMappers.get(type);
        if(mapperProxyFactory == null){
            throw new RuntimeException("Type "+type+" is not know to the MapperRegistry.");
        }
        try{
            return mapperProxyFactory.newInstance(sqlSession);
        }catch (Exception e){
            throw new RuntimeException("Error getting mapper instance.Cause: "+e,e);
        }
    }```
  - 通过newInstance来创建指定类的实例
  - 最后通过代理类来调用方法，测试效果