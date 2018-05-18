package com.gskwin.authcenter.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletGzipUtil {
	public static boolean isGzipSupport(HttpServletRequest request) {
		String headEncoding = request.getHeader("Accept-Encoding");
		if ((headEncoding == null) || (headEncoding.indexOf("gzip") == -1)) {
			return false;
		}
		return true;
	}

	public static PrintWriter createGzipPw(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		PrintWriter pw = null;
		if (isGzipSupport(request)) {
			pw = new PrintWriter(new GZIPOutputStream(response.getOutputStream()));

			response.setHeader("content-encoding", "gzip");
		} else {
			pw = response.getWriter();
		}
		return pw;
	}
}
