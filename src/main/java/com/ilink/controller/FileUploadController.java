package com.ilink.controller;

import java.io.*;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ilink.utils.CompressFileUtils;
import com.ilink.utils.FileUtil;
import com.ilink.utils.RuntimeUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;

@RestController // @RestController =@Controller+@ResponseBody
@RequestMapping("/file")
@Api(value = "文件管理接口", tags = {"文件管理接口"})
public class FileUploadController {
    private static Logger logger = Logger.getLogger(RuntimeUtils.class);

    @RequestMapping("/testView")
    @ApiOperation(value = "页面跳转", httpMethod = "GET", notes = "页面跳转")
    public ModelAndView rdTest() {
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }

    @RequestMapping("/download")
    @ApiOperation(value = "跳转至下载页面", httpMethod = "GET", notes = "跳转至下载页面")
    public ModelAndView download() {
        ModelAndView mv = new ModelAndView("download");
        return mv;
    }

    //上传文件会自动绑定到MultipartFile中
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation(value = "上传单个文件", httpMethod = "POST", notes = "上传单个文件")
    public String upload(HttpServletRequest request,
                         @RequestParam("description") String description,
                         @RequestParam("file") MultipartFile file) throws Exception {
        logger.info(description);
        String path = request.getSession().getServletContext().getRealPath("/WEB-INF/uploadfiles/");
        String fileName = file.getOriginalFilename();

        //上传文件
        Boolean uploadTage= FileUtil.uploadOne(file,path);

        if (uploadTage){
            String a[] = fileName.split("\\.");
            String saveUnZipPath=a[0];

            //解压缩,上传的压缩包存放在zips目录下，解压后的文件存在projects目录下
            Boolean compressTage=CompressFileUtils.unZipFiles(path+fileName,path);
            if (compressTage){
                //执行配置文件
                Process process =null;

                String command1 = "chmod 777 " + path+saveUnZipPath+"hello.sh";
                process = Runtime.getRuntime().exec(command1);
                process.waitFor();
                logger.info(path+saveUnZipPath);
                String command = "sh "+path+saveUnZipPath+"/hello.sh";

                //执行命令行打印执行结果
                process= RuntimeUtils.exec(command, null, path+saveUnZipPath);

                int i = process.waitFor();
                logger.info(i);
            }else {
                return "compress fail";
            }
            return "success";
        }else {
            return "upload fail";
        }
    }


    /**
     * 文件下载功能
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/down",method = RequestMethod.GET)
    @ApiOperation(value = "下载文件", httpMethod = "GET", notes = "下载文件")
    public void down(HttpServletRequest request, HttpServletResponse response
            ,@RequestParam("downfilename") String downfilename) throws Exception {
        logger.info("下载文件名："+downfilename);
        //模拟文件，myfile.txt为需要下载的文件
        String fileName = request.getSession().getServletContext().getRealPath("/WEB-INF/uploadfiles/") + downfilename;
        //获取输入流
        InputStream bis = new BufferedInputStream(new FileInputStream(new File(fileName)));
        //假如以中文名下载的话
        String filename = downfilename;
        //转码，免得文件名中文乱码
        filename = URLEncoder.encode(filename, "UTF-8");
        //设置文件下载头
        response.addHeader("Content-Disposition", "attachment;filename=" + filename);
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }


    //多文件上传
    @RequestMapping(value = "/filesUpload", method = RequestMethod.POST)
    @ApiOperation(value = "多个文件上传", httpMethod = "POST", notes = "多个文件上传")
    public String filesUpload(HttpServletRequest request,
                              @RequestParam("files") MultipartFile[] files) throws Exception {
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String path = request.getSession().getServletContext().getRealPath("/WEB-INF/uploadfiles/");
                logger.info(path);
                String fileName = file.getOriginalFilename();
                File dir = new File(path, fileName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //MultipartFile自带的解析方法
                file.transferTo(dir);
            }
        }

        return "success";

    }

    //上载FTP服务器
    /*@RequestMapping("/uploadFTP")
    public String save(HttpServletRequest request, @RequestParam("uploadFile") MultipartFile[] uploadFile) {
        StringBuffer sb = new StringBuffer();
        String basePath = request.getSession().getServletContext().getRealPath("/WEB-INF/uploadfiles/");//设置服务器中文件保存的根目录
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String filePath = dateFormat.format(now); //根据当前时间设置文件保存的子目录
        if (uploadFile != null && uploadFile.length > 0) {
            if (!ftp.uploadFile(uploadFile, basePath, filePath)) {
                return "error";
            }
            for (int i = 0; i < uploadFile.length; i++) {
                String fileName;
                fileName = new String(uploadFile[i].getOriginalFilename());
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(fileName);//用逗号连接文件名存入数据库
            }

        }
        *//**...*//*//存入数据库处理
        return "error";
    }*/
}