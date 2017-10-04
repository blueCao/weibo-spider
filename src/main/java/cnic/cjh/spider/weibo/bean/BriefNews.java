package cnic.cjh.spider.weibo.bean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cnic.cjh.spider.weibo.HotSummarySpiderConfig;
import cnic.cjh.utils.spring.ApplicationContextSupport;

/**
 * 
 * 
 * @author caojunhui
 * @date 2017年9月25日
 */
public class BriefNews
{
	
	/**
	 * 用于构造微博热搜的html页面，构造如下
	 * 		HEAD_HTML
	 * 			+
	 * 		BriefNews.toHtmlString()
	 * 			+
	 * 		BriefNews.toHtmlString()
	 * 			+
	 * 		BriefNews.toHtmlString()
	 * 			+
	 * 			.
	 * 			.
	 * 			.
	 * 			+
	 * 		FOOT_HTML
	 */
	public static final String HEAD_HTML = "<!DOCTYPE html><html><head><meta http-equiv=\"Content-Type\"content=\"text/html; charset=UTF-8\"><meta name=\"description\"content=\"sina\"><meta name=\"viewport\"content=\"initial-scale=1,maximum-scale=1,user-scalable=no\"><link rel=\"shortcut icon\"href=\"images/favicon.ico\"type=\"image/x-icon\"><meta name=\"format-detection\"content=\"telephone=no\"><title>微博搜索热搜榜</title><script src=\"http://js.t.sinajs.cn/t4/apps/search_m/js/base.js\"></script><link href=\"https://img.t.sinajs.cn/t4/appstyle/searchV45_m/css/common.css?version=201708071750\"type=\"text/css\"rel=\"stylesheet\"charset=\"utf-8\"><script type=\"text/javascript\">var $CONFIG={};$CONFIG.islogin=\"0\",$CONFIG.uid=\"0\",$CONFIG.entry=\"weisousuo\",$CONFIG.setCover=1,$CONFIG.version=\"201708071750\",$CONFIG.bigpipe=\"false\",$CONFIG.timeDiff=new Date-1502264528e3,$CONFIG.product=\"search\",$CONFIG.pageid=\"top_summary\",$CONFIG.skin=\"\",$CONFIG.lang=\"zh-cn\",$CONFIG.jsPath=\"https://js.t.sinajs.cn/t4/\",$CONFIG.cssPath=\"https://img.t.sinajs.cn/t4/\",$CONFIG.imgPath=\"https://img.t.sinajs.cn/t4/\",$CONFIG.servertime=1502264528,$CONFIG.ad_url_jump=\"\",$CONFIG.$webim=0,$CONFIG.mJsPath=[\"https://js{n}.t.sinajs.cn/t4/\",1,2],$CONFIG.mCssPath=[\"https://img{n}.t.sinajs.cn/t4/\",1,2],$CONFIG.s_domain=\"http://s.weibo.com\";</script></head><body class=\"S_Srankhot\"><div class=\"wrap search_hot\"><div id=\"pl_searchIndex\"><a href=\"/\"><img src=\"http://img.t.sinajs.cn/t4/appstyle/searchV45_m/images/banner3.jpg\"class=\"banner\"></a><section class=\"list\"><h1 class=\"title\">实时搜索热点，每分钟更新一次</h1><ul class=\"list_a\">";
	public static final String FOOT_HTML = "</ul></section><i class=\"icon_top\"action-type=\"page_movetop\"node-type=\"page_movetop\"style=\"display: none;\"></i><script src=\"https://js.t.sinajs.cn/t4/apps/search_m/js/pl/index.js?version=201708071750\"type=\"text/javascript\"></script></div><script src=\"https://js.t.sinajs.cn/t4/apps/search_m/js/pl/synthesize.js?version=201708071750\"type=\"text/javascript\"></script></body><!--SUDA_CODE_START--><noscript><img width=\"0\"height=\"0\"src=\"http://beacon.sina.com.cn/a.gif?noScript\"border=\"0\"alt=\"\"/></noscript><!--SUDA_CODE_END--></html>";	

	public static final String PREFIX_URL = "http://s.weibo.com";
	private static Logger l = LoggerFactory.getLogger(BriefNews.class);
	public static List<String> filter_words;
	
	static
	{
		HotSummarySpiderConfig config = ApplicationContextSupport.getBean(HotSummarySpiderConfig.class);
		if (config == null)
		{
			l.error("HotSummarySpiderConfig should not be null!");
		} else
		{
			if (config.getConfig() == null)
			{
				l.error("Config Map should not be null!");
			}
			String filtered_file_name = (String) config.getConfig()
					.get(HotSummarySpiderConfig.ConfigName.filtered_file);
			InputStream file_stream = BriefNews.class.getResourceAsStream(filtered_file_name);
			// file = new File("/opt/filter_words.txt");
			Scanner sc = new Scanner(file_stream, "UTF-8");
			String line = sc.next();
			sc.close();
			if (StringUtils.isEmpty(line))
			{
				l.error("Empty filter words file!Please check file :" + filtered_file_name);
			}
			String[] words = line.split(",");
			filter_words = new ArrayList<String>();
			for (String word : words)
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
	private Long timeInMills;
	/**
	 * users searc times during a period
	 */
	private int search_times;
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

	/**
	 * 过滤掉的字符
	 */
	private List<String> mapped_words = new ArrayList<String>();
	 
	/**
	 * 是否被过滤
	 */
	private boolean filtered;
	/**
	 * 日期
	 */
	private Date date;
	
	public void setDate(Date date)
	{
		this.date = date;
	}

	/**
	 * @return 匹配到的过滤字符用逗号分隔例如“陈小春,林心如,”
	 */
	public String getMapped_words()
	{
		StringBuilder b = new StringBuilder();
		for(String s : mapped_words)
		{
			b.append(s).append(",");
		}
		return b.length() > 0 ? b.toString() : null;
	}

	public void setMapped_words(List<String> mapped_words)
	{
		this.mapped_words = mapped_words;
	}

	public boolean isFiltered()
	{
		return filtered;
	}

	public void setFiltered(boolean filtered)
	{
		this.filtered = filtered;
	}

	public long getLog_id()
	{
		return log_id;
	}

	public void setLog_id(long log_id)
	{
		this.log_id = log_id;
	}

	/**
	 * 对应的Spider日志ID
	 */
	private long log_id;
	
	/**
	 * 新闻对应的UR
	 */
	private String url;
	
	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}

	public BriefNews()
	{
	}

	public BriefNews(String title, int search_times, String hot, int rank, String detailUrl)
	{
		super();
		this.title = title;
		this.search_times = search_times;
		this.hot = hot;
		this.rank = rank;
		this.suffixURL = detailUrl;
		this.url = BriefNews.PREFIX_URL + this.suffixURL;
		this.timeInMills = Calendar.getInstance().getTimeInMillis();
		this.date = new Date(timeInMills);
		this.hashCode = hashCode();
	}

	public BriefNews(String title, long date)
	{
		super();
		this.title = title;
		this.timeInMills = date;
		this.date = new Date(timeInMills);

	}

	/**
	 * determine wether a news is useless
	 * 
	 * @return
	 */
	public boolean useless()
	{
		boolean useless = false;
		
		if(mapped_words == null)
			mapped_words = new ArrayList<String>();
		for (String filter_word : filter_words)
		{
			if (title.contains(filter_word))
			{
				mapped_words.add(filter_word);
			}
		}
		if(mapped_words.size() > 0)
		{
			useless = true;
		}
		
		if (search_times > 1000000)
		{// 热搜实时搜索度超过1000000时，认为信息有用
			useless = false;
		}
		
		//信息无用，则过滤；有用，则不过滤
		this.filtered = useless;
		
		return useless;
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
//		builder.append(PREFIX_URL);
//		builder.append(suffixURL);
		builder.append(url);
		builder.append("\">");
		// rank
		builder.append("<strong>");
		builder.append(rank);
		builder.append("</strong>");
		// title and click times
		builder.append("<span>");
		builder.append(title);
		builder.append("<em>");
		builder.append(search_times);
		builder.append("</em>");
		builder.append("</span>");
		// hot or new
		// if ("��".equals(hot))
		// {
		// builder.append("<i class=\"icon new\"></i>");
		// } else if ("��".equals(hot))
		// {
		// builder.append("<i class=\"icon hot\"></i>");
		// }
		if ("热".equals(hot) || search_times > 1000000)
		{// hot label or clickTimes more than 1 million
			builder.append("<i class=\"icon hot\"></i>");
		} else if ("荐".equals(hot))
		{// recommend label
			builder.append("<i class=\"icon recommend\"></i>");
		} else if ("新".equals(hot) || Calendar.getInstance().getTimeInMillis() - getDate().getTime() < 7200000L)
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
		return hashCode == ((BriefNews) obj).getHashCode();
	}

	/**
	 * update the news info
	 * 
	 * @param news
	 *            the latest news info with the same title
	 */
	public void update(BriefNews news)
	{
		this.search_times = news.search_times;
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

	public int getSearch_times()
	{
		return search_times;
	}

	public void setSearch_times(int search_times)
	{
		this.search_times = search_times;
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
		// System.out.println(new BriefNews("�ܲ�", 10000, "��", 1,
		// "/abc").toHtmlString());
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

	public Long getTimeInMills()
	{
		return timeInMills;
	}

	public Date getDate()
	{
		return date;
	}

	public void setTimeInMills(Long timeInMills)
	{
		this.timeInMills = timeInMills;
	}
}
