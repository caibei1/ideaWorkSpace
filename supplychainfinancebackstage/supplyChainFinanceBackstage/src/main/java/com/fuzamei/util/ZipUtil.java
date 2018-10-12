package com.fuzamei.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author ylx
 * @descri 压缩单个文件，多个文件(含有文件夹)或文件夹的工具类
 */
public class ZipUtil {
	private ZipUtil() {
		throw new AssertionError("instaniation is not permitted");
	}
	
	/**
	 * 针对整个文件夹的递归压缩文件
	 * @param srcFile 源文件(包括文件或文件夹)
	 * @param destFile 目标要压缩到的文件
	 * @throws IOException 
	 */
	public static final void compressDir(File file, ZipOutputStream zos,String basePath) throws IOException{
		if(file.isFile()){//如果是单纯的文件的话
			FileInputStream fis = new FileInputStream(file);
			zos.putNextEntry(new ZipEntry(basePath+file.getName()));
			byte[] bs = new byte[1024];
			int len = -1;
			while((len=fis.read(bs))!=-1){
				zos.write(bs, 0, len);
			}
			zos.closeEntry();
			fis.close();
		}else{//如果是文件夹的话
			File[] listFiles = file.listFiles();
			if(listFiles.length==0){
				zos.putNextEntry(new ZipEntry(basePath+file.getName()));//表示创建空的压缩文件
				zos.closeEntry();
				zos.close();
				return;
			}
			String dirPath = basePath+file.getName()+File.separator;
			for (File commentfile : listFiles) {
				compressDir(commentfile,zos,dirPath);
			}
		}
	}
	
	/**
	 * 针对整个文件夹的递归压缩文件（不带主文件夹的压缩）
	 * @param srcFile 源文件(包括文件或文件夹)
	 * @param destFile 目标要压缩到的文件
	 * @throws IOException 
	 */
	public static final void compressFiles(File[] files,ZipOutputStream zos,String basePath) throws IOException{
		for (File file : files) {
			compressDir(file, zos, basePath);
		}
	}
	
	/**
	 * 
	 * @param files	多个文件File对象数组
	 * @param zipPath 指定压缩文件本身的绝对路径
	 * @param basePath 压缩文件中的起始目录
	 */
	public static final void compressFiles(File[] files,String zipPath,String basePath){
		File zipFile = new File(zipPath);
		File zipDirFile = zipFile.getParentFile();
		if(!zipDirFile.exists()){
			zipDirFile.mkdirs();
		}
		ZipOutputStream zos=null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(new File(zipPath)));
			ZipUtil.compressFiles(files, zos, "");
		} catch (Exception e) {
			throw new RuntimeException("文件压缩出错");
		}finally{
			if(zos!=null){
				try {
					zos.close();
				} catch (IOException e) {
					zos=null;
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
//		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File("C:\\Users\\fuzamei\\Desktop\\test.zip")));
		File file = new File("C:\\Users\\fuzamei\\Desktop\\ziptest");
//		File[] listFiles = file.listFiles();
		compressFiles(file.listFiles(),"C:\\Users\\fuzamei\\Desktop\\test.zip","");
//		zos.close();
	}
	
}
