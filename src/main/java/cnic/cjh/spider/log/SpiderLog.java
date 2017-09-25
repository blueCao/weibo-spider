package cnic.cjh.spider.log;

import java.util.Date;

public class SpiderLog
{
	private Date date;
	private String content;
	private String url;
	private long log_id;
	private boolean successful;
	public boolean getSuccessful()
	{
		return successful;
	}
	public void setSuccessful(boolean successful)
	{
		this.successful = successful;
	}
	public long getLog_id()
	{
		return log_id;
	}
	public void setLog_id(long log_id)
	{
		this.log_id = log_id;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
}
