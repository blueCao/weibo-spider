package cnic.cjh.spider.weibo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import cnic.cjh.spider.log.SpiderLog;
import cnic.cjh.spider.weibo.bean.BriefNews;
import cnic.cjh.spider.weibo.bean.Html;
import cnic.cjh.utils.jsoup.DocumentWrapper;
import cnic.cjh.utils.mybatis.SqlSessionSupport;

/**
 * 抓取微博热搜中的信息 http://s.weibo.com/top/summary?cate=realtimehot
 * 
 * @author caojunhui
 * @date 2017年9月24日
 */
public class HotSummarySpider
{
	private static Logger l = LoggerFactory.getLogger(HotSummarySpider.class);
	private String URL;
	private SqlSession sqlSession;

	public String getURL()
	{
		return URL;
	}

	public SqlSession getSqlSession()
	{
		return sqlSession;
	}

	public void setSqlSession(SqlSession sqlSession)
	{
		this.sqlSession = sqlSession;
	}

	public HotSummarySpider(HotSummarySpiderConfig config, SqlSession sqlSession)
	{
		if (config == null)
		{
			l.error("HotSummarySpiderConfig should not be null!");
			return;
		}
		Object o = config.getConfig().get(HotSummarySpiderConfig.ConfigName.URL);
		if (o == null)
		{
			l.error("HotSummarySpiderConfig didn't contain URL");
			return;
		}
		if (o instanceof String)
			URL = (String) o;
		else
			l.error("Cannot convert from object to string!");

		this.sqlSession = sqlSession;
	}

	/**
	 * @return html页面字符
	 */
	public SpiderLog spide()
	{
		if (StringUtils.isEmpty(URL))
		{
			l.error("Spider doesnt work cause URL is empty!");
			return null;
		}
		String html = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(URL);
		CloseableHttpResponse response1 = null;
		SpiderLog log = new SpiderLog();
		// 记录抓取是否成功
		boolean successful = true;
		try
		{
			response1 = httpclient.execute(httpGet);
			HttpEntity entity1 = response1.getEntity();
			// do something useful with the response body
			html = (EntityUtils.toString(response1.getEntity()));
			// and ensure it is fully consumed
			EntityUtils.consume(entity1);
		} catch (ClientProtocolException e)
		{// 记录错误日志
			successful = false;
			Writer w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			log.setContent(w.toString());

			l.error("###ClientProtocolException###", e);
		} catch (IOException e)
		{// 记录错误日志
			successful = false;
			Writer w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			log.setContent(w.toString());

			l.error("###IOException###", e);
		} finally
		{
			try
			{
				if (response1 != null)
				{
					response1.close();
				}
			} catch (IOException e)
			{
				l.error("###IOException###", e);
			}
			// 记录日志
			log.setSuccessful(successful);
			if (log.getSuccessful())
				log.setContent(html);
			log.setUrl(URL);
			log.setDate(new Date());
			// 日志ID：    time_in_milli左移10位 + 0到1023的随机数（补足剩余10位）
			log.setLog_id(log.getDate().getTime() << 10 + Math.round((Math.random() * 1023)));
		}
		return log;
	}

	public List<BriefNews> handle(String html)
	{
		if (StringUtils.isEmpty(html))
		{
			return null;
		}

		DocumentWrapper document = new DocumentWrapper(true, html);
		Elements els = document.select("script");
		// 第14个element标签包含了所有的微博热搜排行榜信息
		html = els.get(14).html();

		// getting the json string between "(" and ")"
		int begin_index = html.indexOf("(");
		int end_index = html.lastIndexOf(")");
		if (begin_index == -1 || end_index == -1)
		{
			return null;
		}

		html = html.substring(begin_index + 1, end_index);

		Html obj = JSON.parseObject(html, Html.class);

		DocumentWrapper doc = new DocumentWrapper(true, obj.getHtml().replace("\\\"", "\""));
		Elements trs = doc.select("tr");
		List<BriefNews> result = new ArrayList<BriefNews>();
		for (int i = 1; i < trs.size(); i++)
		{
			Element e = doc.single(trs.get(i), "em");
			if (e == null)
				continue;
			int rank = NumberUtils.toInt(e.text().trim(), -1);

			e = doc.single(trs.get(i), "a");
			if (e == null)
				continue;
			String title = e.text();

			e = doc.single(trs.get(i), "a");
			if (e == null)
				continue;
			String url = e.attr("href");

			// href attribute is empty , redireck to href_to attribute
			// <a
			// href_to="/weibo/%25E7%2594%25B5%25E5%25BD%25B1%25E5%25BF%2583%25E7%2590%2586%25E7%25BD%25AA&amp;Refer=top"
			// href="javascript:void(0);"
			// action-data="ad_id=13401&amp;num=5&amp;type=&amp;cate=PC_realtime"
			// action-type="realtimehot_ad" word="电影心理罪" url_show=""
			// url_click=""
			// suda-data="key=tblog_search_list&amp;value=ad_hotword_realtime_13401">电影心理罪</a>
			if ("javascript:void(0);".equals(url))
			{
				url = e.attr("href_to");
			}

			e = doc.single(trs.get(i), ".star_num");
			if (e == null)
				continue;
			int search_times = NumberUtils.toInt(e.text().trim(), -1);

			e = doc.single(trs.get(i), ".icon_txt");
			String hot = null;
			if (e != null)
			{
				hot = e.text();
			}
			// check whether the news is complete
			if (rank < 1 || StringUtils.isEmpty(title) || StringUtils.isEmpty(url) || search_times < 1)
			{
				continue;
			}
			result.add(new BriefNews(title, search_times, hot, rank, url));
		}
//		l.info(JSON.toJSONString(result));
		return result.isEmpty() ? null : result;
	}

	public static void main(String[] args) throws Throwable
	{
		int i = 0;
		SqlSession sqlSession = SqlSessionSupport.getSqlSession();
		HotSummarySpider s = new HotSummarySpider(new HotSummarySpiderConfig("/weibo-spider-config.properties"),
				sqlSession);
		while (true)
		{
			i = i % Integer.MAX_VALUE +1;
			SpiderLog log = s.spide();
//			sqlSession.insert(SpiderLog.class.getName() + ".insertSpiderLog", log);
			List<BriefNews> list = s.handle((String) log.getContent());
			boolean insert_log = false;
			for (BriefNews news : list)
			{
				news.useless();
				if (sqlSession.selectOne(BriefNews.class.getName() + ".queryFromTitle", news) == null)
				{
l.info("insert news {}",JSON.toJSON(log));
				insert_log = true;
					sqlSession.insert(BriefNews.class.getName() + ".insertBriefNews", news);
				}
			}
			if(insert_log)
			{//有新的热搜则插入spider_log
				sqlSession.insert(SpiderLog.class.getName() + ".insertSpiderLog", log);
			}
			sqlSession.commit();
Thread.sleep(100);
		}
	}
}
