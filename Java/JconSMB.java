package main.java;

import jcifs.smb.*;

import java.io.*;
import java.lang.invoke.StringConcatFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class jconSMB {

    private String sIP;
    private String sUsername;
    private String sPassword;
    private String sSharedFolder;

    public jconSMB () {
        System.out.println( "┌──────────────────────────┐\n" +
                            "│       .:: JCON ::.       │\n" +
                            "└──────────────────────────┘\n");
    }

    /**
     * *************************************************
     *  Read
     * *************************************************
     */
    public String read (String fileName) throws IOException {
        return read(getsIP(),getsUsername(),getsPassword(),getsSharedFolder(),fileName);
    }

    public String read (String IP, String user, String pass, String sharedFolderPath, String fileName) throws IOException {
        String output=validateParameters(getsIP(),getsUsername(),getsPassword(),fileName);
        if (!"".equalsIgnoreCase(output)) return output;

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",user, pass);
        String path="smb://"+IP+"/"+sharedFolderPath+"/"+fileName;
        SmbFile smbFile = new SmbFile(path,auth);
        SmbFileInputStream smbfin = new SmbFileInputStream(smbFile);

        long t0 = System.currentTimeMillis();

        byte[] b = new byte[8192];
        int n, tot = 0;
        long t1 = t0;
        while(( n = smbfin.read( b )) > 0 ) {
            output +=b;
            tot += n;
        }
        smbfin.close();
        long t = System.currentTimeMillis() - t0;
        System.out.println();
        System.out.println(tot + " bytes transfered in " + ( t / 1000 ) + " seconds at " + (( tot / 1000 ) / Math.max( 1, ( t / 1000 ))) + "Kbytes/sec");
        return output;
    }

    /**
     * *************************************************
     *  Write
     * *************************************************
     */
    public String write (String fileName, String content) throws IOException {
        return write(getsIP(), getsUsername(), getsPassword(), getsSharedFolder(), fileName, content);
    }

    public String write (String IP, String user, String pass, String sharedFolder, String fileName, String content) throws IOException {
        String output=validateParameters(IP, user, pass, fileName);
        if (!"".equalsIgnoreCase(output)) return output;
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",user, pass);
        String path="smb://"+IP+"/"+sharedFolder+"/"+fileName;
        SmbFile smbFile = new SmbFile(path,auth);
        SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFile);

        smbfos.write(content.getBytes());
        smbfos.close();

        output="Escrita concluída com sucesso.";
        return output;
    }

    /**
     * *************************************************
     *  Copy to remote
     * *************************************************
     */
    public String copyFromLocalToRemote(String remoteFileName,String localFilePath) throws IOException {
        return copyToRemote(getsIP(),getsUsername(),getsPassword(),getsSharedFolder(),remoteFileName,localFilePath,false);
    }

    public String copyFromURLToRemote(String URL, String remoteFileName) throws IOException {
        return copyToRemote(getsIP(),getsUsername(),getsPassword(),getsSharedFolder(),remoteFileName,URL,true);
    }

    public String copyToRemote(String IP, String user, String pass, String sharedFolder, String remoteFileName, String localFilePath, boolean isURL) throws IOException {
        String output=validateParameters(IP, user, pass, remoteFileName);
        if (!"".equalsIgnoreCase(output)) return output;

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",user, pass);
        String path="smb://"+IP+"/"+sharedFolder+"/"+remoteFileName;
        SmbFile smbFile = new SmbFile(path,auth);
        SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFile);
        if (isURL) {
            URL url = new URL(localFilePath);
            InputStream URLInputStream = url.openStream();
            smbfos.write(URLInputStream.readAllBytes());
            URLInputStream.close();
        }else {
            FileInputStream localFile = new FileInputStream(localFilePath);
            smbfos.write(localFile.readAllBytes());
            localFile.close();
        }
        smbfos.close();
        output="Escrita concluída com sucesso.";
        return output;
    }

    /**
     * *************************************************
     *  Copy From Remote
     * *************************************************
     */
    public String copyFromRemoteToLocal(String remoteFileName, String localFilePath) throws IOException {
        return copyFromRemoteToLocal(getsIP(),getsUsername(),getsPassword(),getsSharedFolder(),remoteFileName,localFilePath);
    }

    public String copyFromRemoteToLocal(String IP, String user, String pass, String sharedFolder, String remoteFileName, String localFilePath) throws IOException {
        String output=validateParameters(IP,user,pass,remoteFileName);
        if(!"".equalsIgnoreCase(output)) return output;
        String path="smb://"+IP+"/"+sharedFolder+"/"+remoteFileName;

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",user, pass);
        SmbFile smbFile = new SmbFile(path,auth);
        SmbFileInputStream smbfin = new SmbFileInputStream(smbFile);

        FileOutputStream out = new FileOutputStream( localFilePath );

        long t0 = System.currentTimeMillis();

        byte[] b = new byte[8192];
        int n, tot = 0;
        long t1 = t0;
        while(( n = smbfin.read( b )) > 0 ) {
            out.write( b, 0, n );
            tot += n;
            System.out.print( '#' );
        }
        smbfin.close();
        out.close();
        long t = System.currentTimeMillis() - t0;
        output=tot + " bytes transfered in " + ( t / 1000 ) + " seconds at " + (( tot / 1000 ) / Math.max( 1, ( t / 1000 ))) + "Kbytes/sec";
        System.out.println();
        System.out.println(output);
        return output;
    }

    /**
     * *************************************************
     *  validate Parameters
     * *************************************************
     */
    private String validateParameters(String IP, String User, String Pass, String FileName) {
        String error="";
        if("".equalsIgnoreCase(IP)) {
            error+= "IP não informado;\n";
        }
        if("".equalsIgnoreCase(User)) {
            error+= "Username não informado;\n";
        }
        if("".equalsIgnoreCase(Pass)) {
            error+= "Senha não informada;\n";
        }
        if("".equalsIgnoreCase(FileName)) {
            error+= "Nome do Arquivo não informado;\n";
        }
        return error;
    }

    /**
     * *************************************************
     *  Get's and Set's
     * *************************************************
     */
    public String getsIP() {
        if (sIP == null) {
            sIP="";
        }
        return sIP;
    }

    public void setsIP(String sIP) {
        this.sIP = sIP;
    }

    public String getsUsername() {
        if (sUsername == null) {
            sUsername="";
        }
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsPassword() {
        if (sPassword == null) {
            sPassword="";
        }
        return sPassword;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }

    public String getsSharedFolder() {
        if (sSharedFolder == null) {
            sSharedFolder="";
        }
        return sSharedFolder;
    }

    public void setsSharedFolder(String sSharedFolder) {
        this.sSharedFolder = sSharedFolder;
    }

}
