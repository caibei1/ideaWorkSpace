package com.fuzamei.baseTest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/*
 * spring整合Junit4单元测试基类，
 * 其他类实现该类可以省略一些注解配置。
 * */
//使用junit4进行单元测试
@RunWith(SpringJUnit4ClassRunner.class)
//加载配置文件，可以指定多个配置文件，locations指定的是一个数组
@ContextConfiguration(locations={"classpath:spring/applicationContext*.xml","classpath:spring/springmvc-config.xml"})
//启动事务控制
@Transactional
//配置事务管理器，同时指定自动回滚,如果是true,不管成功与否全部回滚(默认是true)
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
public class BaseJunit4Test {
	//进行测试时，将测试类继承该类
    //注入service对象
    //然后在方法上使用@Test，@RollBack，@Transaction等注解单独修饰
}
