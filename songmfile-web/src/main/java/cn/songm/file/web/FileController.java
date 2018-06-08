package cn.songm.file.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.songm.acc.entity.User;
import cn.songm.common.beans.Result;
import cn.songm.common.service.GeneralErr;
import cn.songm.common.utils.PictureTool;
import cn.songm.common.utils.StringUtils;
import cn.songm.file.entity.FileUrl;
import cn.songm.file.service.FileError;

@Controller
public class FileController extends FileBaseController {
    
    /** 上传文件保存的路径 */
    private final String PATH = new StringBuilder(File.separator)
            .append("WEB-INF")
            .append(File.separator)
            .append("img").toString();

    /**
     * 文件访问
     * @param filename
     * @param format
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/**")
    public void download(HttpServletRequest request) throws IOException {
        String filePath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 0) {
            filePath = filePath + pathInfo;
        }
        
        File f = new File(getServletContext().getRealPath(PATH + filePath));
        if (!f.exists()) {
            f = new File(getServletContext().getRealPath(PATH + File.separator + "default.png"));
        }
        
        // 读取文件
        InputStream input = new FileInputStream(f);
        OutputStream client = new BufferedOutputStream(
                    this.getResponse().getOutputStream());
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = input.read(buffer)) != -1) {
            client.write(buffer, 0, len);
        }
        client.flush();
        input.close();
        client.close();
    }

    private String[] suffixs = {".jpg", ".gif", ".png"};
    
    /**
     * 头像上传
     * @param file
     * @return
     */
    @RequestMapping(value = "member/avatar/upload.json")
    @ResponseBody
    public Result<FileUrl> uploadAvatar(
            @RequestParam(value = "file")
            MultipartFile file) {
    	Result<FileUrl> result = new Result<FileUrl>();
    	
    	boolean flag = false;
    	for (String suf : suffixs) {
    		if (getSuffix(file.getOriginalFilename()).endsWith(suf)) {
    			flag = true;
    		}
    	}
    	if (!flag) {
    		result.setErrorCode(FileError.FIL_FORMAT.getErrCode());
            result.setErrorDesc("文件格式错误");
    		return result;
    	}
    	
        try {
            result.setData(saveFile(file, null));
        } catch (Exception e) {
            result.setErrorCode(GeneralErr.UNKNOW.getErrCode());
            result.setErrorDesc("文件上传失败");
        }
        return result;
    }
    
    /**
     * 头像截图
     * @param avatarServer
     * @param avatarOldPath
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    @RequestMapping(value = "member/avatar/crop.json")
    @ResponseBody
    public Result<FileUrl> cropAvatar(
    		@RequestParam(name = "avatar_old_server")
    		String avatarOldServer,
    		@RequestParam(name = "avatar_old_path")
    		String avatarOldPath,
    		@RequestParam(name = "x") int x,
    		@RequestParam(name = "y") int y,
    		@RequestParam(name = "width") int width,
    		@RequestParam(name = "height") int height) {
    	Result<FileUrl> result = new Result<FileUrl>();
    	
    	// 计算截图后的照片
    	File oldFile = new File(
                getServletContext().getRealPath(PATH + avatarOldPath));
    	// 原始图片不存在
        if (!oldFile.exists()) {
        	result.setErrorCode(FileError.FIL_NOEXIST.getErrCode());
        	result.setErrorDesc("原始文件不存在");
            return result;
        }
        int point = avatarOldPath.lastIndexOf(".");
        String suffix = avatarOldPath.substring(point);
		String avatarPath = avatarOldPath.substring(0, point)
				+ "_" + x + "_" + y + "_" + width + "_" + height
				+ suffix;
		File newFile = new File(
				getServletContext().getRealPath(PATH + avatarPath));
		if (!newFile.exists()) {
			newFile.mkdirs();
			try {
				ImageIO.write(PictureTool.crop(
					ImageIO.read(oldFile), x, y,
					width, height),
					suffix.substring(1), newFile);
			} catch (IOException e) {
				result.setErrorCode(GeneralErr.UNKNOW.getErrCode());
				result.setErrorDesc("服务器异常");
				return result;
			}
		}

		FileUrl fUrl = new FileUrl();
    	fUrl.setServer(getFileServer());
    	fUrl.setPath(avatarPath);
    	result.setData(fUrl);
    	
    	return result;
    }
    
    /**
     * 图片批量上传
     * @param files
     * @return
     */
//    @RequestMapping(value = "member/images/upload.json")
//    @ResponseBody
//    public Result<List<FileUrl>> uploadBatchImages(
//            @RequestParam(value = "files")
//            MultipartFile[] files) {
//        List<FileUrl> fileUrlList = new ArrayList<FileUrl>();
//        Result<List<FileUrl>> result = new Result<List<FileUrl>>();
//        try {
//            for (MultipartFile file : files) {
//                fileUrlList.add(saveFile(file, null));
//            }
//            result.setData(fileUrlList);
//        } catch (Exception e) {
//            result.setErrorCode(GeneralErr.UNKNOW.getErrCode());
//            result.setErrorDesc("文件上传失败");
//        }
//
//        return result;
//    }
    
    private String getSuffix(String fileName) {
    	return fileName.substring(fileName.lastIndexOf("."));
    }
    
    /**
     * 文件保存
     * @param file
     * @param path
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    private FileUrl saveFile(MultipartFile file, String path)
            throws IllegalStateException, IOException {
        User user = this.getSessionUser();
        
        // 计算文件的保存路径
        String fileName = file.getOriginalFilename();
        String newName = StringUtils.get32UUID() + getSuffix(fileName);
        String filePath = null;
        if (path != null) {
        	path = path.startsWith(File.separator) ? path : File.separator + path;
        	path = path.endsWith(File.separator) ? path : path + File.separator;
        	filePath = File.separator + user.getUserId() + path + newName;
        } else {
        	filePath = File.separator + user.getUserId() + File.separator + newName;
        }
        
        // 保存文件
        File tgtFile = new File(
                getServletContext().getRealPath(PATH + filePath));
        if (!tgtFile.exists()) {
            tgtFile.mkdirs();
        }
        
        // 计算文件访问的URL
        FileUrl fileUrl = new FileUrl();
        fileUrl.init();
        fileUrl.setServer(getFileServer());
        fileUrl.setPath(filePath);
        
        file.transferTo(tgtFile);
        
        return fileUrl;
    }
    
    private String getFileServer() {
    	HttpServletRequest request = this.getRequest();
    	return request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort() 
                + (request.getContextPath().endsWith("/")
             	? request.getContextPath()
             	: request.getContextPath() + "/");
    }
}
