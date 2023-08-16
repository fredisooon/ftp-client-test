package com.fyodor.util;


import com.fyodor.ftp.FtpClient;

import java.io.IOException;

public class ResourceUtil {
    public static void closingResources() {
        InputUtil.closeResources();
        FtpClient ftpClient = FtpClient.getInstance();
        if (ftpClient != null)
            ftpClient.closeAllResources();
    }
}
