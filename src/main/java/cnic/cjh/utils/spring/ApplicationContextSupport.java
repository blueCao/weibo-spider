package cnic.cjh.utils.spring;


import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 加载Spring配置文件 applicationContext.xml
 * 提供调用Bean的整体接口
 * 
 * @author caojunhui
 * @date 2017年9月21日
 */
public class ApplicationContextSupport
{
	protected static ClassPathXmlApplicationContext applicationContext;
	
	public ApplicationContextSupport()
	{
		
	}
	
	public static void initContext()
	{
		ApplicationContextSupport.applicationContext = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
	}
	
	public static synchronized <T> T getBean(Class<T> class1)
	{
		if(applicationContext == null)
		{
			initContext();
		}
		return applicationContext.getBean(class1);
	}
	
	public static synchronized <T> T getBean(String beanName,Class<T> T)
	{
		if(applicationContext == null)
		{
			initContext();
		}
		return applicationContext.getBean(beanName,T);
	}
	
	public static synchronized Object getBean(String beanName)
	{
		if(applicationContext == null)
		{
			initContext();
		}
		return applicationContext.getBean(beanName);
	}	
}
