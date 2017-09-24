package cnic.cjh.spider.weibo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import cnic.cjh.utils.spring.ApplicationContextSupport;

/**
 * 抓取 新浪微博热门搜索 所用的配置
 * 
 * @author caojunhui
 *
 */
public class HotSummarySpiderConfig
{
	public static final class ConfigName
	{
		// 抓取的URL地址
		public static String URL = "URL";
		// 过滤文件所在的率
		public static String filtered_file = "filtered_file";
	}

	private Map<String, Object> config;
	private static Logger l = LoggerFactory.getLogger(HotSummarySpiderConfig.class);

	public Map<String, Object> getConfig()
	{
		return config;
	}

	public HotSummarySpiderConfig(String filePath)
	{
		loadConfigFromProperties(filePath);
	}

	private void loadConfigFromProperties(String filePath)
	{
		Properties pro = new Properties();
		InputStream input_stream = HotSummarySpiderConfig.class.getResourceAsStream("/weibo-spider-config.properties");
		try
		{
			pro.load(input_stream);
		} catch (IOException e)
		{
			l.error("Load properties file(" + filePath + ") failed !", e);
		}
		if (config == null)
			config = new TreeMap<String, Object>();
		if (!StringUtils.isEmpty(pro.getProperty(ConfigName.URL)))
		{//加载所有属性
			config.put(ConfigName.URL, pro.get(ConfigName.URL));
			config.put(ConfigName.filtered_file,pro.get(ConfigName.filtered_file));
		}
		l.debug(JSON.toJSONString(config));
	}

	public static void main(String[] args)
	{
		ApplicationContextSupport.initContext();
		ApplicationContextSupport.getBean(HotSummarySpiderConfig.class);
	}

}
