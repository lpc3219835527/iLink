package com.ilink.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RuntimeUtils {
    private static Logger logger = Logger.getLogger(RuntimeUtils.class);
    /**
     *
     * @Title: exec
     * @Description: 简化执行命令行
     * @param  command 命令行
     * @param  envp 环境变量
     * @param  dir  路径
     * @return Process    返回类型
     * @throws IOException
     */
    public static Process exec(String command, String envp, String dir)
            throws IOException {
        logger.info("开始执行命令行："+command);
        String regex = "\\s+";
        String args[] = null;
        String envps[] = null;
        if (!StringUtils.isEmpty(command)) {
            args = command.split(regex);
        }

        if (!StringUtils.isEmpty(envp)) {
            envps = envp.split(regex);
        }

        Process process=Runtime.getRuntime().exec(args, envps, new File(dir));
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String result = sb.toString();

        logger.info(result);

        return process;

    }
}