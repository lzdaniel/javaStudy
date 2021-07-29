package com.lzdaniel.javastudy.testBSocket;

import jdk.internal.util.xml.impl.Input;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer2 {
    static {
        BasicConfigurator.configure();
    }

    private static final Log LOGGER = LogFactory.getLog(SocketServer2.class);

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(83);

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                // 这里使用线程池，因为线程创建非常消耗资源
                // 最终改变不了accept()只能一个一个接受socket的情况，并且被阻塞的情况
                SocketServerThread socketServerThread = new SocketServerThread(socket);
                new Thread(socketServerThread).start();
            }
        } catch (Exception e) {
            SocketServer2.LOGGER.error(e.getMessage(), e);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}

/**
 * 接收到客户端的socket后，业务处理过程可以交给一个线程来做，但是改变不了socket被一个一个的左accept()的情况
 */
class SocketServerThread implements Runnable {
    /**
     * 日志
     */
    private static final Log LOGGER = LogFactory.getLog(SocketServerThread.class);

    private Socket socket;

    public SocketServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            Integer sourcePort = socket.getPort();
            int maxLen = 1024;
            byte[] contextBytes = new byte[maxLen];
            // 使用线程同样无法解决read方法的阻塞问题，也就是说read方法处同样会被阻塞，直到操作系统有数据准备好
            int realLen = in.read(contextBytes, 0, maxLen);
            // 读取信息
            String message = new String(contextBytes, 0, realLen);
            // 打印信息
            SocketServerThread.LOGGER.info("服务器收到来自于端口：" + sourcePort + "的信息： " + message);

            // 发送信息
            out.write("回响信息发送！".getBytes());
        } catch (Exception e) {
            SocketServerThread.LOGGER.error(e.getMessage(), e);
        } finally {
            // 试图关闭
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (this.socket != null) {
                    this.socket.close();
                }
            } catch (IOException e) {
                SocketServerThread.LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
