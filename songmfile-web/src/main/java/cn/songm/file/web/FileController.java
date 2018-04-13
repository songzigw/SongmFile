package cn.songm.file.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.songm.acc.entity.User;
import cn.songm.acc.service.UserService;
import cn.songm.common.beans.Result;
import cn.songm.common.service.GeneralErr;
import cn.songm.common.utils.JsonUtils;
import cn.songm.common.utils.StringUtils;
import cn.songm.file.entity.FileUrl;
import cn.songm.file.service.FileError;

@Controller
public class FileController extends FileBaseController {
    
    /** 上传文件保存的路径 */
    private final String PATH = new StringBuilder(File.separator)
            .append("WEB-INF")
            .append(File.separator)
            .append("img")
            .append(File.separator).toString();

    @Autowired
    private UserService userService;

    /**
     * 文件访问
     * @param filename
     * @param format
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/{filename}.{format}")
    public void download(@PathVariable("filename") String filename,
                         @PathVariable("format") String format)
            throws IOException {
        String filePath = filename + "." + format;
        File f = new File(getServletContext().getRealPath(PATH + filePath));
        if (!f.exists()) {
            f = new File(getServletContext().getRealPath(PATH + "default.png"));
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

    /**
     * 图片上传
     * @param file
     * @return
     */
    @RequestMapping(value = "image/upload.json")
    @ResponseBody
    public Result<FileUrl> uploadImage(
            @RequestParam(value = "file")
            MultipartFile file) {
        Result<FileUrl> result = new Result<FileUrl>();
        try {
            result.setData(this.saveFile(file, null));
        } catch (Exception e) {
            result.setErrorCode(GeneralErr.UNKNOW.getErrCode());
            result.setErrorDesc("文件上传失败");
        }
        return result;
    }
    
    @RequestMapping(value = "image/cut")
    @ResponseBody
    public Result<FileUrl> cutImage(
    		@RequestParam(name = "avatarServer")
    		String avatarServer,
    		@RequestParam(name = "avatarOldPath")
    		String avatarOldPath,
    		@RequestParam(name = "x") int x,
    		@RequestParam(name = "y") int y,
    		@RequestParam(name = "width") int width,
    		@RequestParam(name = "height") int height) {
    	Result<FileUrl> result = new Result<FileUrl>();
    	
    	// 计算截图后的照片 原始图片不存在
    	File oldFile = new File(
                getServletContext().getRealPath(PATH + avatarOldPath));
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
		File newFile = new File(avatarPath);
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
    	fUrl.setServer(avatarServer);
    	fUrl.setPath(avatarServer + avatarPath);
    	result.setData(fUrl);
    	
    	User user = this.getSessionUser();
    	user.setAvatarServer(avatarServer);
    	user.setAvatarOldPath(avatarOldPath);
    	user.setAvatarPath(avatarPath);
    	user.setAvatar(fUrl.getPath());
		userService.editUserPhoto(user.getUserId(), avatarServer, avatarOldPath, avatarPath, user.getAvatar());
		songmSsoService.editUserInfo(this.getSessionId(), JsonUtils.getInstance().toJson(user));
    	return result;
    }
    
    /**
     * 图片批量上传
     * @param files
     * @return
     */
    @RequestMapping(value = "images/upload.json")
    @ResponseBody
    public Result<List<FileUrl>> uploadImages(
            @RequestParam(value = "files")
            MultipartFile[] files) {
        List<FileUrl> fileUrlList = new ArrayList<FileUrl>();
        Result<List<FileUrl>> result = new Result<List<FileUrl>>();
        try {
            for (MultipartFile file : files) {
                fileUrlList.add(saveFile(file, null));
            }
            result.setData(fileUrlList);
        } catch (Exception e) {
            result.setErrorCode(GeneralErr.UNKNOW.getErrCode());
            result.setErrorDesc("文件上传失败");
        }

        return result;
    }
    
    public FileUrl saveFile(MultipartFile file, String path)
            throws IllegalStateException, IOException {
        HttpServletRequest request = this.getRequest();
        User user = this.getSessionUser();
        
        // 计算文件的保存路径
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String newName = StringUtils.get32UUID() + suffix;
        String filePath = null;
        if (path != null) {
        	path = path.startsWith(File.separator) ? path : File.separator + path;
        	path = path.endsWith(File.separator) ? path : path + File.separator;
        	filePath = user.getUserId() + path + newName;
        } else {
        	filePath = user.getUserId() + File.separator + newName;
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
        fileUrl.setServer(request.getScheme() + "://"
                           + request.getServerName() + ":"
                           + request.getServerPort() 
                           + (request.getContextPath().endsWith("/")
                        	? request.getContextPath()
                        	: request.getContextPath() + "/"));
        fileUrl.setPath(filePath);
        
        file.transferTo(tgtFile);
        
        return fileUrl;
    }
}
