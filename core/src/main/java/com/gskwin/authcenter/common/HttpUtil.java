package com.gskwin.authcenter.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class HttpUtil {
	private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

	public static String sendGet(String url, String param) {
		return sendGet(url, param, null);
	}

	public static String sendGet(String url, String param, Map<String, String> headers) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);

			URLConnection connection = realUrl.openConnection();

			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if (null != MDC.get("REQUEST_ID")) {
				connection.setRequestProperty("REQUEST_ID", MDC.get("REQUEST_ID"));
			}
			Iterator localIterator;
			if (headers != null) {
				for (localIterator = headers.entrySet().iterator(); localIterator.hasNext();) {
					Map.Entry header = (Map.Entry) localIterator.next();
					connection.setRequestProperty((String) header.getKey(), (String) header.getValue());
				}
			}
			Map.Entry<String, String> header;
			connection.connect();

			/*Object map = connection.getHeaderFields();
			for (String key : ((Map) map).keySet()) {
				System.out.println(key + "--->" + ((Map) map).get(key));
			}*/
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result = result + line;
			}
		} catch (Exception e) {
			LOG.warn("HttpUtil sendGet exception:", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				LOG.error("BufferedReader close exception:", e2);
			}
		}
		return result;
	}

	public static String sendPost(String url, Map<String, String> headers) {
		return sendPost(url, "", headers, ContentType.DEFAULT_TEXT);
	}

	public static String sendPost(String url, String body, ContentType contentType) {
		return sendPost(url, body, null, contentType);
	}

	public static String sendPost(String url, String body, Map<String, String> headers, ContentType contentType) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);

			URLConnection conn = realUrl.openConnection();

			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", contentType.toString());
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if (null != MDC.get("REQUEST_ID")) {
				conn.setRequestProperty("REQUEST_ID", MDC.get("REQUEST_ID"));
			}
			if (headers != null) {
				for (Map.Entry<String, String> header : headers.entrySet()) {
					conn.setRequestProperty((String) header.getKey(), (String) header.getValue());
				}
			}
			conn.setDoOutput(true);
			conn.setDoInput(true);

			out = new PrintWriter(conn.getOutputStream());

			out.print(body);

			out.flush();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result = result + line;
			}
		} catch (IOException e) {
			LOG.warn("HttpUtil post exception:", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String sendGzipPost(String url, String body, Map<String, String> headers, String token) {
		BufferedReader bufferReader = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(url);

			URLConnection conn = realUrl.openConnection();

			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("access_token", token);
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if (headers != null) {
				for (Map.Entry<String, String> header : headers.entrySet()) {
					conn.setRequestProperty((String) header.getKey(), (String) header.getValue());
				}
			}
			conn.setDoOutput(true);
			conn.setDoInput(true);

			GZIPOutputStream gzips = new GZIPOutputStream(conn.getOutputStream());

			gzips.write(body.getBytes());

			gzips.finish();
			gzips.flush();

			bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				result.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferReader != null) {
					bufferReader.close();
				}
			} catch (IOException localIOException3) {
			}
		}
		return result.toString();
	}

	public static String sendGzipPost(String url, String body, Map<String, String> headers, ContentType contentType) {
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);

			URLConnection conn = realUrl.openConnection();

			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", contentType.toString());
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if (null != MDC.get("REQUEST_ID")) {
				conn.setRequestProperty("REQUEST_ID", MDC.get("REQUEST_ID"));
			}
			if (headers != null) {
				for (Map.Entry<String, String> header : headers.entrySet()) {
					conn.setRequestProperty((String) header.getKey(), (String) header.getValue());
				}
			}
			conn.setDoOutput(true);
			conn.setDoInput(true);

			GZIPOutputStream gzips = new GZIPOutputStream(conn.getOutputStream());

			gzips.write(body.getBytes());

			gzips.finish();
			gzips.flush();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result = result + line;
			}
		} catch (IOException e) {
			LOG.warn("HttpUtil GzipPost exception:", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static void writeHttpResponse(HttpServletResponse response, CloseableHttpResponse httpResponse) {
		int responseStatus = httpResponse.getStatusLine().getStatusCode();
		try {
			Header[] headers = httpResponse.getAllHeaders();
			for (Header header : headers) {
				response.addHeader(header.getName(), header.getValue());
			}
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType(ContentType.APPLICATION_JSON.toString());
			response.setStatus(responseStatus);

			PrintWriter out = response.getWriter();
			out.print(EntityUtils.toString(httpResponse.getEntity()));
			out.flush();
			return;
		} catch (IOException e) {
			throw new IllegalStateException("Write HttpResponse error", e);
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getParameter(HttpServletRequest httpRequest, String parameterName) {
		String parameterValue = httpRequest.getHeader(parameterName);
		LOG.debug("Try to get {} from header, parameterValue: {}", parameterName, parameterValue);
		if (parameterValue == null) {
			parameterValue = httpRequest.getParameter(parameterName);
			LOG.debug("Try to get {} from parameter, parameterValue: {}", parameterName, parameterValue);
		}
		return parameterValue;
	}

	public static Boolean responseCsvGzipFile(HttpServletRequest request, HttpServletResponse response,
			byte[] content) {
		try {
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setContentType("text/csv; charset=GB2312");
			if (ServletGzipUtil.isGzipSupport(request)) {
				response.setHeader("Unzip-Length", "" + content.length);
				response.setHeader("Content-Encoding", "gzip");
				GZIPOutputStream gs = new GZIPOutputStream(os);
				gs.write(content);
				gs.finish();
				gs.flush();
				gs.close();
			} else {
				os.write(content);
				os.flush();
				os.close();
			}
			response.setStatus(200);
		} catch (IOException e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

}
