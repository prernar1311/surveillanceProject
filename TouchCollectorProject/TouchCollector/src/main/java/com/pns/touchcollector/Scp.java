package com.pns.touchcollector;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;

/**
 * Created by nicolascrowell on 2014/1/14.
 */
public class Scp {
    private JSch ssh = new JSch();
    private Session session;
    private ChannelSftp channel;

    private boolean closed;
    
    public Scp(String username, String password, String host) throws SftpException, JSchException {
        ssh.setConfig("StrictHostKeyChecking", "no");
        session = ssh.getSession(username, host);
        session.setPassword(password);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
    }

    public void cd(String path) throws SftpException {
        checkState();
        channel.cd(path);
    }

    public void put(InputStream stream, String name) throws SftpException {
        checkState();
        channel.put(stream, name);
    }

    private void checkState() {
        if (closed) throw new IllegalStateException("scp session closed!");
    }

    public void close() {
        if (channel != null) channel.disconnect();
        if (session != null) session.disconnect();
        closed = true;
    }
    /*
    public void downloadFtp(String userName, String password, String host, int port, String path) {
        Session session = null;
        Channel channel = null;
        try {
            JSch ssh = new JSch();
            
            session = ssh.getSession(userName, host, port);
            session.setPassword(password);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            sftp.get(path, "specify path to where you want the files to be output");
        } catch (JSchException e) {
            System.out.println(userName);
            e.printStackTrace();


        } catch (SftpException e) {
            System.out.println(userName);
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }

    }*/

}
