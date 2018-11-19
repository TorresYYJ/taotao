package com.taotao.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import com.taotao.utils.FastDFSClient;

public class TestFastDFS {

	@Test
	public void uploadFile() throws Exception {
		//1.向工程中添加jar包
		//2.创建一个配置文件。配置tracker的服务器地址
		//3.加载配置文件
		ClientGlobal.init("C:/Users/yyj/taotao/taotao-manager-web/src/main/resources/resource/client.conf");
		//4.创建一个TrackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		//5。使用TrackerClient对象获得trackerserver对象
		TrackerServer trackerServer  = trackerClient.getConnection();
		//6.创建StorageServer的引用null就可以
		StorageServer storageServer = null;
		//7.创建一个StorageClient对象。trackerserver、storageServer两个参数
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		//8、使用StorageClient对象上传文件
		String[] strings = storageClient.upload_file("C:/Users/yyj/Pictures/2017-08/IMG_1496.JPG", "JPG", null);
		for (String string : strings) {
			System.out.println(string);
		}
	}
	
	@Test
	public void testFastDfsClient() throws Exception {
		FastDFSClient fastDFSClient = new FastDFSClient("C:/Users/yyj/taotao/taotao-manager-web/src/main/resources/resource/client.conf");
		String string = fastDFSClient.uploadFile("C:/Users/yyj/Pictures/2018-05/IMG_1022.JPG");
		System.out.println(string);
	}
}
