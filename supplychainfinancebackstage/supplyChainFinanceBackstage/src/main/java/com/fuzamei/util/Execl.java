package com.fuzamei.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


public class Execl<T> {
	 /**
	  * 
	  * @param headers  传入你导出execl表格的表头   类似 String[] hean={"姓名","年龄"}
	  * @param dataset  传入你的数据 list集合 <T> 可以指定传入任何类型  类似 list<User>
	  * @return
	  */
	 public HSSFWorkbook exportExcel(String[] headers,Collection<T> dataset) {  
	        // 声明一个工作薄  
	        HSSFWorkbook workbook = new HSSFWorkbook();  
	        // 生成一个表格  
	        HSSFSheet sheet = workbook.createSheet();  
	        // 设置表格默认列宽度为15个字节  
	        sheet.setDefaultColumnWidth((short) 15);  
	        // 生成一个样式  
	        HSSFCellStyle style = workbook.createCellStyle();  
	        // 设置这些样式  
	        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);  
	        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
	        // 生成一个字体  
	        HSSFFont font = workbook.createFont();  
	        font.setColor(HSSFColor.VIOLET.index);  
	        font.setFontHeightInPoints((short) 12);  
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
	        // 把字体应用到当前的样式  
	        style.setFont(font);  
	        // 生成并设置另一个样式  
	        HSSFCellStyle style2 = workbook.createCellStyle();  
	        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);  
	        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
	        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
	        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);  
	        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);  
	        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
	        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  
	        // 生成另一个字体  
	        HSSFFont font2 = workbook.createFont();  
	        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);  
	        // 把字体应用到当前的样式  
	        style2.setFont(font2);  
	        // 声明一个画图的顶级管理器  
	        HSSFRow row = sheet.createRow(0);  
	        for (short i = 0; i < headers.length; i++) {  
	            HSSFCell cell = row.createCell(i);  
	            cell.setCellStyle(style);  
	            HSSFRichTextString text = new HSSFRichTextString(headers[i]);  
	            cell.setCellValue(text);  
	        }  
	        // 遍历集合数据，产生数据行  
	        Iterator<T> it = dataset.iterator();  
	        int index = 0;  
	        while (it.hasNext()) {  
	            index++;  
	            row = sheet.createRow(index);  
	            T t = (T) it.next();  
	            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值  
	            Field[] fields = t.getClass().getDeclaredFields();  
	            for (short i = 0; i < fields.length; i++) {  
	                HSSFCell cell = row.createCell(i);  
	                cell.setCellStyle(style2);  
	                Field field = fields[i];  
	                String fieldName = field.getName();  
	                String getMethodName = "get"  
	                        + fieldName.substring(0, 1).toUpperCase()  
	                        + fieldName.substring(1);  
	                try {  
	                    Class tCls = t.getClass();  
	                    Method getMethod = tCls.getMethod(getMethodName,  
	                            new Class[] {});  
	                    Object value = getMethod.invoke(t, new Object[] {});
	                    //这个value 就是列表的每一个值
	                    // 判断值的类型后进行强制类型转换  
	                    String textValue = null;
	                    // 其它数据类型都当作字符串简单处理  
	                    if(value==null) {
	                    }else {
	                    	System.out.println("是"+value);
	                    	textValue = value.toString();
	                    	cell.setCellValue(textValue);
	                    }
	                    
	                } catch (SecurityException e) {  
	                    e.printStackTrace();  
	                } catch (NoSuchMethodException e) {  
	                    e.printStackTrace();  
	                } catch (IllegalArgumentException e) {  
	                    e.printStackTrace();  
	                } catch (IllegalAccessException e) {  
	                    e.printStackTrace();  
	                } catch (InvocationTargetException e) {  
	                    e.printStackTrace();  
	                } finally {  
	                    // 清理资源  
	                }  
	            }  
	        }  
	       /* try {  
	            workbook.write(out);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }*/
			return workbook;  
	    }  

	 public static void get(String title, String[] headers, Collection dataset, HttpServletResponse response) {  
		try {
		   	  Execl execl=new Execl<>();
		      HSSFWorkbook wb = execl.exportExcel(headers, dataset);
		      //response.reset();不要
		      //response.addHeader("Content-Disposition", URLEncoder.encode(title+".xls", "utf-8"));//77
		      //response.addHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(title+".xls", "UTF-8"));
		      response.addHeader("Content-Disposition", "attachment;filename="+new String((title+".xls").getBytes("UTF-8"),"ISO-8859-1"));
		      response.setContentType("application/vnd.ms-excel;charset=utf-8");//77
		      OutputStream stream = response.getOutputStream();
		      wb.write(stream);
		      stream.flush();
		      stream.close();
		      response.flushBuffer();
			 } catch (Exception e) {
			    e.printStackTrace();
			   }  
		   
		   
	   }

	
}
