package cnic.cjh.spider.weibo.bean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cnic.cjh.spider.weibo.HotSummarySpiderConfig;
import cnic.cjh.utils.spring.ApplicationContextSupport;

/**
 * 短消息实体
 * 
 * @author caojunhui
 * @date 2017年9月22日
 */
public class BriefNews
{
	public static final String PREFIX_URL = "http://s.weibo.com";
	private static Logger l = LoggerFactory.getLogger(BriefNews.class);
	public static List<String> filter_words;
	static
	{
		HotSummarySpiderConfig config = ApplicationContextSupport.getBean(HotSummarySpiderConfig.class);
		if(config == null)
		{
			l.error("HotSummarySpiderConfig should not be null!");
		}
		else
		{
			if(config.getConfig() == null)
			{
				l.error("Config Map should not be null!");
			}
			String filtered_file_name = (String)config.getConfig().get(HotSummarySpiderConfig.ConfigName.filtered_file);
			InputStream file_stream = BriefNews.class.getResourceAsStream("/"+filtered_file_name);
//			file = new File("/opt/filter_words.txt");
			Scanner sc = new Scanner(file_stream,"UTF-8");
			String line = sc.next();
			sc.close();
			if(StringUtils.isEmpty(line))
			{
				l.error("Empty filter words file!Please check file :"+filtered_file_name);
			}
			String[] words = line.split(",");
			filter_words = new ArrayList<String>();
			for(String word : words)
			{
				filter_words.add(word);
			}
			l.info("filter_words loaded success! FilePath  " + filtered_file_name);
		}

	}
	/**
	 * news title
	 */
	private String title;
	/**
	 * the date news produced,milliSecond
	 */
	private Long date;
	/**
	 * users click times
	 */
	private int clickTimes;
	/**
	 * hot degree
	 */
	private String hot;
	/**
	 * news current rank
	 */
	private int rank;
	private String suffixURL;
	/**
	 * suffixURL's hashCode
	 */
	private int hashCode;
	public BriefNews()
	{
	}

	public BriefNews(String title, int clickTimes, String hot, int rank, String detailUrl)
	{
		super();
		this.title = title;
		this.clickTimes = clickTimes;
		this.hot = hot;
		this.rank = rank;
		this.suffixURL = detailUrl;
		this.date = Calendar.getInstance().getTimeInMillis();
		this.hashCode = hashCode();
	}

	public BriefNews(String title, long date)
	{
		super();
		this.title = title;
		this.date = date;
	}

	/**
	 * determine wether a news is useless
	 * 
	 * @return
	 */
	public boolean useless()
	{
		if(clickTimes > 1000000)
		{// hot news contained
			return false;
		}
		for(String filter_word : filter_words)
		{
			if(title.contains(filter_word))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return html format string
	 */
	public String toHtmlString()
	{
		StringBuilder builder = new StringBuilder();

		// url
		builder.append("<li>");
		builder.append("<a href=\"");
		builder.append(PREFIX_URL);
		builder.append(suffixURL);
		builder.append("\">");
		// rank
		builder.append("<strong>");
		builder.append(rank);
		builder.append("</strong>");
		// title and click times
		builder.append("<span>");
		builder.append(title);
		builder.append("<em>");
		builder.append(clickTimes);
		builder.append("</em>");
		builder.append("</span>");
		// hot or new
//		if ("��".equals(hot))
//		{
//			builder.append("<i class=\"icon new\"></i>");
//		} else if ("��".equals(hot))
//		{
//			builder.append("<i class=\"icon hot\"></i>");
//		}
		if ("热".equals(hot) || clickTimes > 1000000)
		{//  hot label or clickTimes more than 1 million
			builder.append("<i class=\"icon hot\"></i>");
		}
		else if("荐".equals(hot))
		{// recommend label
			builder.append("<i class=\"icon recommend\"></i>");			
		}
		else if("新".equals(hot) || Calendar.getInstance().getTimeInMillis() - date < 7200000L)
		{// new label or happened less than 2 hours, settting new label
			builder.append("<i class=\"icon new\"></i>");
		}
		builder.append("</a>");
		builder.append("</li>");

		return builder.toString();
	}

	/**
	 * specify the key
	 */
	@Override
	public int hashCode()
	{
		return suffixURL.hashCode();
	}

	/**
	 * whether two titles is equal
	 */
	@Override
	public boolean equals(Object obj)
	{
		return hashCode == ((BriefNews)obj).getHashCode();
	}

	/**
	 * update the news info
	 * 
	 * @param news
	 *            the latest news info with the same title
	 */
	public void update(BriefNews news)
	{
		this.clickTimes = news.clickTimes;
		this.rank = news.rank;
		this.hot = news.hot;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getClickTimes()
	{
		return clickTimes;
	}

	public void setClickTimes(int clickTimes)
	{
		this.clickTimes = clickTimes;
	}

	public String getHot()
	{
		return hot;
	}

	public void setHot(String hot)
	{
		this.hot = hot;
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public static void main(String[] args)
	{
//		System.out.println(new BriefNews("�ܲ�", 10000, "��", 1, "/abc").toHtmlString());
		new BriefNews();
	}

	public String getSuffixURL()
	{
		return suffixURL;
	}

	public void setSuffixURL(String suffixURL)
	{
		this.hashCode = hashCode();
		this.suffixURL = suffixURL;
	}

	public int getHashCode()
	{
		return suffixURL.hashCode();
	}

	public Long getDate()
	{
		return date;
	}

	public void setDate(Long date)
	{
		this.date = date;
	}
}
