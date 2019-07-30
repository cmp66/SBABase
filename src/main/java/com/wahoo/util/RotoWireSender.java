package com.wahoo.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class RotoWireSender
{
	private String _playerdata = null;
	private String _url = null;
	public RotoWireSender()
	{}
	

	public void setPlayerdata(String playerdata)
	{
		this._playerdata = playerdata;
	}
	
	public void setURL(String url)
	{
		this._url = url;
		
	}
	
	public void sendPlayerData()
	{
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(_url);
		
		NameValuePair[] data = { new NameValuePair("rotowire", _playerdata) };
		
		method.setRequestBody(data);
		
		try
		{
			client.executeMethod(method);
		}
		catch(HttpException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String fileName = args[0];
		String url = args[1];
		byte[] fileBytes = new byte[100000];
		
		File file = new File(fileName);
		
		try
		{
			FileInputStream is = new FileInputStream(file);
		
			int count = is.read(fileBytes);
		
			if (count == fileBytes.length)
			{
				System.out.println("Too Many bytes in file");
			}
		
	    
			RotoWireSender sender = new RotoWireSender();
			sender.setURL(url);
			sender.setPlayerdata(new String(fileBytes));
		
			sender.sendPlayerData();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		

	}



}
