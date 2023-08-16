package com.fyodor.util;

import com.fyodor.ftp.FtpClient;
import com.fyodor.menu.CRUDMenu;

public class InfoUtil {

    public static String currentHostAndMode() {
        FtpClient client = FtpClient.getInstance();
        String hostAddress = client.getCommandSocket().getInetAddress().toString().split("/")[0];
        String activeMode = "";
        if (client.getMode() != null) {
            activeMode = " | Mode: " + client.getMode().name();
        }
        return "| Host: " + hostAddress + activeMode;
    }

    public static String currentFile() {
        FtpClient client = FtpClient.getInstance();
        if (client.getCurrentFilePath() != null) {
            return " | File: " + client.getCurrentFilePath();
        }
        else return "";
    }

    public static String currentSort() {
        if (CRUDMenu.ascOrder) {
            return " | ASC - on";
        }
        else return " | ASC - off";
    }
}
