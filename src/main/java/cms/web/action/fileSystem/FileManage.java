package cms.web.action.fileSystem;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cms.bean.setting.SystemSetting;
import cms.bean.thumbnail.Thumbnail;
import cms.bean.topic.ImageInfo;
import cms.service.setting.SettingService;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import cms.utils.SecureLink;
import cms.web.action.TextFilterManage;
import cms.web.action.thumbnail.ThumbnailManage;
import cms.web.taglib.Configuration;
import cms.web.action.fileSystem.localImpl.LocalFileManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 文件管理
 * @author Gao
 *
 */
@Component("fileManage")
public class FileManage {
	
	@Resource SettingService settingService;
	@Resource LocalFileManage localFileManage;
	@Resource TextFilterManage textFilterManage;
	@Resource ThumbnailManage thumbnailManage;
	
	
	/**
	 * 获得正在使用的文件系统
	 * @return 0.本地系统 10.SeaweedFS 20.MinIO 30.阿里云OSS
	 */
	public int getFileSystem(){

    	return 0;//本地文件系统
		
	}
	
	/**
	 * 未实现
	 * 如有多个地址，则随机返回一个地址，分布式文件存储系统服务器会自动302跳转(302暂时性转移)到正确的地址
	 * 本地文件存储系统返回‘空值’
	 * @return
	 */
    public String fileServerAddress(){
    	return this.fileServerAddress(null);
    }
	/**
	 * 未实现
	 * 获取文件服务器地址 如http://s3-1.diyhi.com/
	 * 如有多个地址，则随机返回一个地址，分布式文件系统服务器会自动302跳转(302暂时性转移)到正确的地址
	 * 本地文件存储系统返回‘访问地址’
	 * @return
	 */
    public String fileServerAddress(HttpServletRequest request){
    	if(request != null){
			return Configuration.getUrl(request);//本地文件存储系统
		}
    	return "";//本地文件系统
    }
    
    /**
     * 未实现
	 * 获取文件服务器所有地址 如http://s3-1.diyhi.com/
	 * 如有多个地址，分布式文件存储系统服务器会自动302跳转(302暂时性转移)到正确的地址
	 * 本地文件存储系统返回‘空值’
	 * @return
	 */
    public List<String> fileServerAllAddress(){
    	return this.fileServerAllAddress(null);
    }
    /**
     * 未实现
	 * 获取文件服务器所有地址 如http://s3-1.diyhi.com/
	 * 如有多个地址，分布式文件存储系统服务器会自动302跳转(302暂时性转移)到正确的地址
	 * 本地文件存储系统返回‘访问地址’
	 * @return
	 */
    public List<String> fileServerAllAddress(HttpServletRequest request){
    	if(request != null){
    		List<String> newEndpoint = new ArrayList<String>();
			newEndpoint.add(Configuration.getUrl(request)); //本地文件存储系统
			return newEndpoint;
		}
    	return null;
    }
    
	
	/**
	 * 处理富文本文件路径
	 * @param html 富文本内容
	 * @param item 项目
	 * @return
	 */
	public String processRichTextFilePath(String html,String item){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  

		if(html != null && !"".equals(html.trim())){
			return textFilterManage.processFilePath(html, item, this.fileServerAddress(request));
		}
    	
		return html;
	}
	
    
	/**
	 * 生成文件夹
	 * @param path路径
	 */   
	public Boolean createFolder(String path){
		//本地文件系统
		return FileUtil.createFolder(path);
	}
	/**
	 * 未实现
     * 创建预签名
     * @param pathName 文件路径名称 例如 file/links/111.jpg
     * @param fileSize 允许上传文件大小 单位/K
     * @return
     */
     public String createPresigned(String pathName,Long fileSize){

     	return "";
     }
	
	/**  
     * 写文件
     * @param path 路径
     * @param newFileName 新文件名称
     * @param content 内容
     */   
    public void writeFile(String path,String newFileName,byte[] content){
    	//本地文件系统
		localFileManage.writeFile(path, newFileName, content);
		
    }
	
    /**  
     * 复制文件
     * @param resFilePath 源文件路径  
     * @param distFolder  目标文件夹  
     * @IOException 当操作发生异常时抛出  
     */   
    public void copyFile(String resFilePath, String distFolder) throws IOException {   
    	//本地文件系统
		localFileManage.copyFile(resFilePath,distFolder);
    }  
    /**  
     * 复制目录
     * @param resDirectory 源目录路径  
     * @param distFolder  目标文件夹  
     * @IOException 当操作发生异常时抛出  
     */   
    public void copyDirectory(String resDirectory, String distFolder) throws IOException {  
    	//本地文件存储系统
    	localFileManage.copyDirectory(resDirectory, distFolder);
    	
    }  
    
    /**
	 * 删除失败状态文件
	 * @param path 路径
	 */
	public void failedStateFile(String path){
		//本地文件系统
		localFileManage.failedStateFile(path);
		
	}
    
	
	/**
	 * 删除文件
	 * @param path 路径
	 * @return
	 */
	public Boolean deleteFile(String path){
		//本地文件系统
		return localFileManage.deleteFile(path);
		
	}
	
	
	
	
	/**
	 * 删除目录
	 * @param path 路径 (格式"file"+File.separator+"目录"+File.separator)
	 */
	public Boolean removeDirectory(String path){
		//本地文件系统
		return localFileManage.removeDirectory(path);
		
		
	}
	

	
	/**
	 * 添加锁
	 * @param path 路径(格式"file"+File.separator+"目录"+File.separator+"lock"+File.separator)
	 * @param lockFileName 锁文件名称
	 */
	public void addLock(String path,String lockFileName)throws IOException{
		//本地文件系统
		localFileManage.addLock(path, lockFileName);
	}

	
	/**
	 * 删除锁
	 * @param path 路径(格式"file"+File.separator+"目录"+File.separator+"lock"+File.separator)
	 * @param fileName 文件名称
	 */
	public void deleteLock(String path,String fileName){
		//本地文件系统
		localFileManage.deleteLock(path, fileName);
	}
	
	
	/**
     * 图片格式转换
     * @param resFilePath 原文件路径
     * @param newFilePath 生成文件路径
     * @param suffix 新文件后缀    jpg  bmp
     * @throws IOException
     */
    public void converterImage(String resFilePath,String newFilePath,String suffix)
            throws IOException{
    	//本地文件系统
		localFileManage.converterImage(resFilePath, newFilePath, suffix);
    }
	
	
    /**
	 * 异步增加缩略图
	 * @param thumbnailList 缩略图对象集合
	 * @param imageInfoList 图片信息集合
	 */
	public void addThumbnail(List<Thumbnail> thumbnailList,List<ImageInfo> imageInfoList){
		thumbnailManage.addThumbnail(thumbnailList, imageInfoList);
	}
    /**
	 * 异步删除缩略图
	 * @param thumbnailList 缩略图对象集合
	 * @param imageInfoList 图片信息集合
	 */
	public void deleteThumbnail(List<Thumbnail> thumbnailList,List<ImageInfo> imageInfoList){
		//本地文件系统
		thumbnailManage.deleteThumbnail(thumbnailList, imageInfoList);
		
	}

	/**
	 * 生成缩略图
	 * @param sourcePath 源图片路径
	 * @param outputPath 输出图片路径
	 * @param extension 后缀名
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public void createImage(InputStream sourceInputStream,String outputPath,String extension,int scaleWidth,int scaleHeight) {
		//本地文件系统
		thumbnailManage.createImage(sourceInputStream, PathUtil.defaultExternalDirectory()+File.separator+outputPath, extension, scaleWidth, scaleHeight);
	}
	/**
	 * 生成缩略图
	 * @param sourcePath 源图片路径
	 * @param outputPath 输出图片路径
	 * @param extension 后缀名
	 * @param x 坐标X轴
	 * @param y 坐标Y轴
	 * @param width 剪裁区域宽
	 * @param high 剪裁区域高
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public void createImage(InputStream sourceInputStream,String outputPath,String extension,int x,int y,int width,int height,int scaleWidth,int scaleHeight) {
		//本地文件系统
		thumbnailManage.createImage(sourceInputStream, PathUtil.defaultExternalDirectory()+File.separator+outputPath, extension, x, y, width, height, scaleWidth, scaleHeight);
	}

	
    /**----------------------------------- 定时删除文件 ----------------------------------**/
    
	
	/**
     * 删除无效文件
     */
    public void deleteInvalidFile(){
    	SystemSetting systemSetting = settingService.findSystemSetting_cache();
    	if(systemSetting != null){
    		if(systemSetting.getTemporaryFileValidPeriod() != null && systemSetting.getTemporaryFileValidPeriod() >0){
    			//最大删除时间
    			Long maxDeleteTime = new Date().getTime()-systemSetting.getTemporaryFileValidPeriod()*60*1000;
    			localFileManage.lockRemoveFile("template",maxDeleteTime);//删除模板 文件
    			
    			localFileManage.lockRemoveFile("topic",maxDeleteTime);//删除话题文件
    			localFileManage.lockRemoveFile("comment",maxDeleteTime);//评论文件
    			localFileManage.lockRemoveFile("question",maxDeleteTime);//删除问题文件
    			localFileManage.lockRemoveFile("answer",maxDeleteTime);//答案文件
    			
    			localFileManage.lockRemoveFile("help",maxDeleteTime);//删除帮助文件
    			localFileManage.lockRemoveFile("helpType",maxDeleteTime);//删除帮助分类文件
    			localFileManage.lockRemoveFile("links",maxDeleteTime);//删除友情链接文件
    			localFileManage.lockRemoveFile("membershipCard",maxDeleteTime);//会员卡
    			
    			localFileManage.lockRemoveFile("report",maxDeleteTime);//删除举报图片文件
    			
    			localFileManage.lockRemoveFile("topicTag",maxDeleteTime);//删除话题标签图片文件
    			localFileManage.lockRemoveFile("questionTag",maxDeleteTime);//删除问答标签图片文件
    		}
    	}
    	
    }
	
    
    
    /**
	 * 生成签名链接
	 * @param link 链接
	 * @param fileName 文件名称
	 * @param secret 密钥
	 * @param expires 有效时间 单位/秒
	 * @return
	 */
 	public String createSignLink(String link,String fileName,String secret,Long expires){
 
		String newSecureLink = SecureLink.createSecureLink(link,fileName,secret,expires);
		if(Configuration.getPath() != null && !"".equals(Configuration.getPath().trim())){
			//删除虚拟路径
			newSecureLink = StringUtils.removeStartIgnoreCase(newSecureLink, Configuration.getPath()+"/");//移除开始部分的相同的字符,不区分大小写
		}
		return newSecureLink;
		
 	}
    
    
 

}
