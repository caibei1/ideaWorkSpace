package com.fuzamei.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.fuzamei.constant.Path;

public class ConfReadUtil {
	private ConfReadUtil() {
		throw new AssertionError("can not be instaniated");
	}
	/**
	 * @author ylx
	 * @param key
	 * @param fileName
	 * @descri 读取指定配置文件中的某个key值对应的值
	 * @return
	 */
	public static final String getProperty(String key,String fileName){
		String path = Path.class.getClassLoader().getResource(fileName).getPath();
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(path)));
		} catch (Exception e) {
			throw new RuntimeException("配置文件读取异常");
		}
		return properties.getProperty(key);
	}
	/**
	 * @author ylx
	 * @param key
	 * @param fileName
	 * @descri 读取conf.properties配置文件中的某个key值对应的值
	 * @return
	 */
	public static final String getProperty(String key){
		return getProperty(key,"conf.properties");
	}
}
