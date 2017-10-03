package cnic.cjh.utils.mybatis;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 从SqlSessionFactory中请求一个SqlSession
 * 
 * @author caojunhui
 * @date 2017年9月24日
 */
public class SqlSessionSupport
{
	private static SqlSessionFactory sqlSessionFactory;

	private static void init()
	{
		String resource = "/mybatis-config.xml";
		InputStream inputStream = SqlSessionSupport.class.getResourceAsStream(resource);

		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	}

	public static SqlSession getSqlSession()
	{
		if (sqlSessionFactory == null)
		{
			init();
		}
		return sqlSessionFactory.openSession();
	}
}
