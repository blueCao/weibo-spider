package cnic.cjh.utils.jsoup;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

/**
 * 为jsoup中的element包裹了一层工具 
 * 
 * @author caojunhui
 * @date 2017年9月22日
 */
public class DocumentWrapper
{
	private Document doc;
	private String content;

	public DocumentWrapper(boolean html, String content)
	{
		if (html)
		{
			this.doc = Jsoup.parse(content);
		}
		this.content = content;
	}

	public DocumentWrapper(Document doc)
	{
		this.doc = doc;
	}

	public <T> T toJSON(Class<T> cls)
	{
		return JSON.parseObject(content, cls);
	}

	public String title()
	{
		Elements els = doc.select("title");
		if (els != null && els.size() > 0)
		{
			return els.first().text();
		} else
		{
			return null;
		}
	}

	public String description()
	{
		// description
		Elements els = doc.select("meta");
		if (els != null && els.size() > 0)
		{
			for (Element el : els)
			{
				String name = el.attr("name");
				if (StringUtils.isBlank(name))
				{
					name = el.attr("property");
				}

				if (StringUtils.equalsIgnoreCase("description", name))
				{
					return el.attr("content");
				}
			}
		}
		return null;
	}

	public String keywords()
	{
		Elements els = doc.select("meta");
		if (els != null && els.size() > 0)
		{
			for (Element el : els)
			{
				String name = el.attr("name");
				if (StringUtils.isBlank(name))
				{
					name = el.attr("property");
				}

				if (StringUtils.equalsIgnoreCase("keywords", name))
				{
					return el.attr("content");
				}
			}
		}
		return null;
	}

	public String attr(String selector, String attr)
	{
		return attr(doc, selector, attr);
	}

	public String attr(Element el, String selector, String attr)
	{
		Elements els = el.select(selector);
		if (els != null)
		{
			el = els.first();
			if (el != null)
			{
				return el.attr(attr);
			}
		}
		return null;
	}

	public Map<String, String> attrs(String selector)
	{
		return attrs(doc, selector);
	}

	public Map<String, String> attrs(Element el, String selector)
	{
		Map<String, String> map = new HashMap<String, String>();
		Elements els = el.select(selector);
		if (els != null)
		{
			el = els.first();
			if (el != null)
			{
				Attributes attrs = el.attributes();
				if (attrs != null)
				{
					for (Attribute attr : attrs)
					{
						map.put(attr.getKey(), attr.getValue());
					}
				}
			}
		}

		return map;
	}

	public String text(String selector)
	{
		return text(doc, selector);
	}

	public String text(Element el, String selector)
	{
		if (el == null)
		{
			return null;
		}
		Elements els = el.select(selector);
		if (els != null && els.size() > 0)
		{
			el = els.first();
			if (el != null)
				return el.text();
		}
		return null;
	}

	public String text(Elements elements, String selector)
	{
		if (elements == null)
		{
			return null;
		}
		Elements els = elements.select(selector);
		if (els != null && els.size() > 0)
		{
			Element el = els.first();
			if (el != null)
				return el.text();
		}
		return null;
	}

	public String ownText(String selector)
	{
		return ownText(doc, selector);
	}

	public String ownText(Element el, String selector)
	{
		if (el == null)
		{
			return null;
		}
		Elements els = el.select(selector);
		if (els != null && els.size() > 0)
		{
			el = els.first();
			if (el != null)
				return el.ownText();
		}
		return null;
	}

	/**
	 * ����ڵ��ϵ��������Ժ�����Ϊ�յĽڵ�
	 * 
	 * @param el
	 * @return
	 */
	public Element cleanHtml(Element el)
	{
		return cleanHtml(el, true);
	}

	/**
	 * ����ڵ��ϵ��������Ժ�����Ϊ�յĽڵ�
	 * 
	 * @param el
	 * @param clone
	 *            �Ƿ񿽱�һ����Ϊ����, ��������Ӱ��ԭ�е��ĵ�����
	 * @return
	 */
	public Element cleanHtml(Element el, boolean clone)
	{
		if (clone)
		{
			el = el.clone();
		}
		Attributes ats = el.attributes();
		if (ats != null)
		{
			for (Attribute at : ats)
			{
				el.removeAttr(at.getKey());
			}
		}

		Elements childs = el.children();
		if (childs != null && childs.size() > 0)
		{
			for (Element c : childs)
			{
				cleanHtml(c, false);
			}
		}

		if (StringUtils.isBlank(el.text()))
		{
			el.remove();
		}

		return el;
	}

	public String html(String selector)
	{
		return html(doc, selector);
	}

	public String html(Element el, String selector)
	{
		Elements els = el.select(selector);
		if (els != null && els.size() > 0)
		{
			el = els.first();
			if (el != null)
				return el.html();
		}
		return null;
	}

	public String html(Elements elements, String selector)
	{
		Elements els = elements.select(selector);
		if (els != null && els.size() > 0)
		{
			Element el = els.first();
			if (el != null)
				return el.html();
		}
		return null;
	}

	public Map<String, String> tableNameProperties(Element ele, String selector)
	{
		Map<String, String> map = new HashMap<String, String>();

		Elements els = ele.select(selector).select("td");
		for (int i = 0; i < els.size(); i += 2)
		{
			Element el0 = els.get(i);
			Element el1 = els.get(i + 1);

			if (el0 != null && el1 != null)
			{
				String name = el0.text();
				String value = el1.text();
				if (StringUtils.isNotBlank(name))
				{
					map.put(name, value);
				}
			}
		}

		return map;
	}


	public Map<String, String> listNameProperties(String selector, String nameSelector, String valueSelector)
	{
		Map<String, String> map = new HashMap<String, String>();

		Elements els = doc.select(selector);
		for (int i = 0; i < els.size(); i++)
		{
			Element el = els.get(i);
			Element el0 = single(el, nameSelector);
			Element el1 = single(el, nameSelector);

			if (el0 != null && el1 != null)
			{
				String name = el0.text();
				String value = el1.text();
				if (StringUtils.isNotBlank(name))
				{
					map.put(name, value);
				}
			}
		}

		return map;
	}

	public Element single(String selector)
	{
		return single(doc, selector);
	}
	
	public Element single(Elements elements, String selector)
	{
		for (Element el : elements)
		{
			Element s = single(el, selector);
			if (s != null)
			{
				return s;
			}
		}
		return null;
	}

	public Element single(Element el, String selector)
	{
		Elements els = el.select(selector);
		if (els != null && els.size() > 0)
		{
			return els.get(0);
		}
		return null;
	}

	public Document getDocument()
	{
		return doc;
	}

	public Elements select(String selector)
	{
		return doc.select(selector);
	}

	public Elements select(Element el, String selector)
	{
		return el.select(selector);
	}

	public Elements select(Elements els, String selector)
	{
		return els.select(selector);
	}

	public Elements remove(String selector)
	{
		Elements els = select(selector);
		if (els != null)
		{
			els.remove();
		}
		return els;
	}

	public Elements remove(Element el, String selector)
	{
		Elements els = select(el, selector);
		if (els != null)
		{
			els.remove();
		}
		return els;
	}


	public Elements remove(Elements elements, String selector)
	{
		Elements els = select(elements, selector);
		if (els != null)
		{
			els.remove();
		}
		return els;
	}

	public static void main(String[] args) throws Exception
	{
		DocumentWrapper doc = new DocumentWrapper(Jsoup.parse(new URL("http://enjoy.ricebook.com/product/1005627"),
				100000));
		Element el = doc.select(".menu").first();
		System.out.println(doc.cleanHtml(el, false).html());
		System.out.println(doc.html(".menu"));
	}

	public String getContent()
	{
		return content;
	}

	public boolean isHtml()
	{
		return doc != null;
	}
}
