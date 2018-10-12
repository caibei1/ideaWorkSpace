package com.fuzamei.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.fuzamei.constant.HintMSG;
import com.mchange.util.AssertException;

public final class FileTransferUtil {
	private FileTransferUtil(){
		throw new AssertException("instaniation is not permitted");
	}
	
	/**
	 * 完成上传文件的功能
	 * @attention 只针对单个文件上传
	 * @param multipartFile	上传过来的文件对象
	 * @param attachmentFile	被修改后的文件名并且是要被添加到File文件对象
	 * @param directoryPath	文件夹路径
	 */
	public static final void transferSingle(MultipartFile multipartFile, File attachmentFile, String directoryPath){
		File dir = new File(directoryPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(!dir.isDirectory()){
			throw new RuntimeException("路径需要是文件夹路径");
		}
		try {
			multipartFile.transferTo(attachmentFile);
		} catch (Exception e) {
			if(null!=attachmentFile){
				attachmentFile.delete();
			}
			throw new RuntimeException(HintMSG.UPLOAD_FAIL+":"+e.getMessage());
		}
	}
	
	/**
	 * @author ylx
	 * @return 返回false说明没有重复的文件名，返回true说明有重复的文件名
	 */
	public static final boolean checkIfHasSameFileName(MultipartFile...files){
		int count = files.length;
		Set<String> set = new HashSet<String>();
		for (MultipartFile file : files) {
			set.add(file.getOriginalFilename());
		}
		int identity = set.size();
		return identity==count?false:true;
	}
	
	/**
	 * @author ylx
	 * @return 有空文件直接报错
	 */
	public static final void checkIfHasEmptyFile(MultipartFile...files){
		if(files.length==0){
			throw new RuntimeException("必须要有文件");
		}
		for (MultipartFile file : files) {
			if(null==file || file.isEmpty()){
				throw new RuntimeException(HintMSG.FILE_CANT_BE_NULL);
			}
		}
	}
	
	/**
	* @Title: transferMultiple
	* @Description: TODO(针对多个文件上传的功能，一旦出现异常将所有文件全部删除，防止出现垃圾文件)
	* @param @param files	客户端传过来的MultipartFile对象
	* @param @param attachmentFileList	已经设定好的File的集合对象
	* @param @param directoryPath   主文件夹目录
	* @return void    返回类型
	* @author ylx
	* @date 2018年1月11日 下午7:47:43
	 */
	public static final void transferMultiple(MultipartFile[] files, List<File> attachmentFileList, String directoryPath) {
		File dir = new File(directoryPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(!dir.isDirectory()){
			throw new RuntimeException("路径需要是文件夹路径");
		}
		for (int i = 0; i < files.length; i++) {
			if(files[i]==null) continue;
			File destinationFile = attachmentFileList.get(i);
			if(destinationFile==null) continue;
			try {
				files[i].transferTo(destinationFile);
			} catch (Exception e) {
				for (File file : attachmentFileList) {//文件一旦上传失败将所有文件全部删除
					if(null!=file){
						file.delete();
					}
				}
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	
	/**
	* @Title: checkSingleUploadFileSuffixes
	* @Description: TODO(检查上传文件格式是否正确)
	* @param @param file 上传的单个文件对象MultipartFile
	* @param @param suffixes    上传文件的后缀名格式
	* @return void    返回类型
	* @author ylx
	* @date 2018年1月12日 上午11:23:18
	 */
	public static final void checkSingleUploadFileSuffixes(MultipartFile file, String... suffixes){
		if(suffixes.length==0){
			throw new RuntimeException("必须确定文件后缀名");
		}
		boolean flag=false;
		String filename = file.getOriginalFilename();
		for (String suffix : suffixes) {
			flag = flag || (filename.endsWith(suffix));
		}
		if(!flag){
			throw new RuntimeException("文件格式错误");
		}
	}
	
	/**
	* @Title: checkMultipleUploadFilesSuffixes
	* @Description: TODO(检查上传文件格式是否正确)
	* @param @param files 上传的文件数组对象MultipartFile[]
	* @param @param suffixes    上传文件的后缀名格式
	* @return void    返回类型
	* @author ylx
	* @date 2018年1月12日 上午11:23:18
	 */
	public static final void checkMultipleUploadFilesSuffixes(MultipartFile[] files, String... suffixes){
		if(suffixes.length==0){
			throw new RuntimeException("必须确定文件后缀名");
		}
		for (MultipartFile file : files) {
			boolean flag=false;
			String filename = file.getOriginalFilename();
			for (String suffix : suffixes) {
				flag = flag || (filename.endsWith(suffix));
			}
			if(!flag){
				throw new RuntimeException("文件格式错误");
			}
		}
	}
	
	/**
	 * 
	 * @title closeStream
	 * @param c 各种可关闭对象
	 * @author ylx977
	 * @date 2018年1月13日 下午3:04:43
	 *
	 */
	public static final void closeStream(Closeable...c){//关闭流的内部方法
		for (Closeable closeable : c) {
			if(closeable!=null){
				try {
					closeable.close();
				} catch (IOException e) {
					closeable=null;
				}
			}
		}
	}
	
	/**
	 * 
	* @Title: checkSingleUploadFileAndSuffixed
	* @Description: TODO(如果文件为空就返回null，但是后缀名错误就报错)
	* @param @param file
	* @param @param suffixes
	* @param @return    设定文件
	* @return MultipartFile    返回类型
	* @author ylx
	* @date 2018年1月15日 下午8:15:40
	* @throws
	 */
	public static final MultipartFile checkSingleUploadFileAndSuffixes(MultipartFile file,String...suffixes){
		if(suffixes.length==0){
			throw new RuntimeException("必须确定文件后缀名");
		}
		if(null == file||file.isEmpty()){
			return null;//如果无内容直接返回null
		}
		boolean flag=false;
		String filename = file.getOriginalFilename();
		for (String suffix : suffixes) {
			flag = flag || (filename.endsWith(suffix));
		}
		if(!flag){
			throw new RuntimeException("文件格式错误");
		}
		return file;
	}

	/**
	* @Title: checkMultiUploadFilesAndSuffixes
	* @Description: TODO(如果数组文件为空就返回null，数组中文件都为空也返回null，但是后缀名错误就报错)
	* @param @param files:上传的多个文件
	* @param @param suffixes:支持的多个后缀名
	* @param @param supportNullUpload:是否支持空文件上传true表示支持，false表示不支持空文件上传
	* @param @return    设定文件（如果支持空文件上传，那么如果全是空，那么返回null，如果支持空文件上传，那么返回的数据中，如果有空文件，空文件以null站位）
	* @attention 空文件：内容为空或者压根连东西都没上传
	* @return MultipartFile[]  返回类型
	* @author ylx
	* @date 2018年1月15日 下午8:18:20
	* @throws
	 */
	public static final MultipartFile[] checkMultiUploadFilesAndSuffixes(MultipartFile[] files,boolean supportNullUpload,String...suffixes) {
		if(suffixes.length == 0){
			throw new RuntimeException("必须确定文件后缀名");
		}
		if(files == null||files.length == 0){
			if(!supportNullUpload){
				//如果不支持空文件上传
				throw new RuntimeException("上传文件不能为空");
			}else{
				//如果支持空文件上传
				return null;
			}
		}
		List<MultipartFile> list=new ArrayList<MultipartFile>();
		for (MultipartFile file : files) {
			if (!supportNullUpload) {
				//如果不支持空文件上传
				if (file==null||file.isEmpty()) {
					throw new RuntimeException("文件不能为空");
				} 
				list.add(file);
			}else{
				//如果支持空文件上传(其中有空文件，则插入null)
				list.add(checkEmptyFileAndReturn(file));
			}
			boolean flag = false;//判定文件格式的标记
			String filename = file.getOriginalFilename();
			for (String suffix : suffixes) {
				flag = flag || (filename.endsWith(suffix));
			}
			if(!flag){
				throw new RuntimeException("文件格式错误");
			}
		}
		if(list.size()==0){
			return null;
		}
		return list.toArray(new MultipartFile[list.size()]);
	}

	/**
	 * 
	* @Title: checkEmptyFileAndReturn
	* @Description: TODO(如果是空文件或者文件为null返回null)
	* @param @param file
	* @param @return    设定文件
	* @return MultipartFile    返回类型
	* @author ylx
	* @date 2018年1月16日 上午10:55:34
	* @throws
	 */
	public static final MultipartFile checkEmptyFileAndReturn(MultipartFile file){
		if(file==null||file.isEmpty()){
			return null;
		}
		return file;
	}
	
}
