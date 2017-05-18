package cn.songm.file.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.songm.common.beans.Result;
import cn.songm.common.service.GeneralErr;
import cn.songm.common.utils.JsonUtils;
import cn.songm.common.utils.StringUtils;
import cn.songm.common.web.BaseController;
import cn.songm.file.entity.FileUrl;

@Controller
public class FileController extends BaseController {
    
    /** 上传文件保存的路径 */
    private String path = new StringBuilder(File.separator)
            .append("WEB-INF")
            .append(File.separator)
            .append("img")
            .append(File.separator).toString();

    @ResponseBody
    @RequestMapping(value = "/{filename}.{format}", method = RequestMethod.GET)
    public void download(@PathVariable("filename") String filename,
                         @PathVariable("format") String format)
            throws IOException {
        // 计算文件所在路径
        String filePath = filename.replace('_', File.separatorChar) + "." + format;
        File f = new File(getServletContext().getRealPath(path + filePath));
        if (!f.exists()) {
            f = new File(getServletContext().getRealPath(path + "default.png"));
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

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView upload(
            @RequestParam(value = "file", required = false)
            MultipartFile file) {
        Result<FileUrl> result = new Result<FileUrl>();
        try {
            result.setData(this.saveFile(file));
        } catch (Exception e) {
            result.setErrorCode(GeneralErr.UNKNOW.getErrCode());
            result.setErrorDesc("文件上传失败");
        }

        ModelAndView mv = new ModelAndView("/data");
        return mv.addObject("json", JsonUtils.toJson(result));
    }
    
    @RequestMapping(value = "/uploads", method = RequestMethod.POST)
    public ModelAndView uploads(
            @RequestParam(value = "files", required = false)
            MultipartFile[] files) {
        List<FileUrl> fileUrlList = new ArrayList<FileUrl>();
        Result<List<FileUrl>> result = new Result<List<FileUrl>>();
        try {
            for (MultipartFile file : files) {
                fileUrlList.add(saveFile(file));
            }
            result.setData(fileUrlList);
        } catch (Exception e) {
            result.setErrorCode(GeneralErr.UNKNOW.getErrCode());
            result.setErrorDesc("文件上传失败");
        }

        ModelAndView mv = new ModelAndView("/data");
        return mv.addObject("json", JsonUtils.toJson(result));
    }
    
    public FileUrl saveFile(MultipartFile file)
            throws IllegalStateException, IOException {
        HttpServletRequest request = this.getRequest();
        
        // 计算文件的保存路径
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String newName = StringUtils.get32UUID() + suffix;
        String filePath = "" + newName;
        
        // 保存文件
        File tgtFile = new File(
                getServletContext().getRealPath(path + filePath));
        if (!tgtFile.exists()) {
            tgtFile.mkdirs();
        }
        
        // 计算文件访问的URL
        FileUrl fileUrl = new FileUrl();
        fileUrl.setServer(request.getScheme() + "://"
                           + request.getServerName() + ":"
                           + request.getServerPort() 
                           + request.getContextPath());
        fileUrl.setPath(filePath.replace(File.separatorChar, '_'));
        
        file.transferTo(tgtFile);
        
        return fileUrl;
    }
}
