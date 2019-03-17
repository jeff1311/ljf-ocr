package com.ljf.ocr;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

public class Util {

	public static void returnInfo(HttpServletResponse response,JSONObject json){
		response.setContentType("text/html;charset=utf-8");
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.print(json.toJSONString());
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(pw != null){
				pw.close();
			}
		}
	}
	
	/**鑾峰彇classpath*/
	public static String getClassPath() {
		String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		System.out.println("classpath:" + classPath);
        String rootPath = "";
        //windows涓�
        if ("\\".equals(File.separator)) {
            rootPath = classPath.substring(1);
        }
        //linux涓�
        if ("/".equals(File.separator)) {
        	rootPath = classPath;
        }
        return rootPath;
    }
	
}
