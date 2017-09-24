package cnic.cjh.utils.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从SqlSessionFactory中请求一个SqlSession
 * 
 * @author caojunhui
 * @date 2017年9月24日
 */
public class SqlSessionSupport
{
	private SqlSession sqlSession;
	private static SqlSessionFactory sqlSessionFactory;
	private static final Logger l = LoggerFactory.getLogger(SqlSessionSupport.class);

	public SqlSessionSupport(SqlSessionFactoryBean sqlSessionFactoryBean)
	{
		if (sqlSessionFactory == null)
		{
			try
			{
				sqlSessionFactory = sqlSessionFactoryBean.getObject();
			} catch (Exception e)
			{
				l.error("Exception occursed when sqlSessionFactoryBean.getObject() !",e);
			}
		}
		sqlSession = sqlSessionFactory.openSession();
	}
}
