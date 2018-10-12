package com.fuzamei.http;

public class RequestFactory {
		public static Request createRequest(String url){
			
			if(("https").equals(url.substring(0,5))){
				return  new HttpsRequest();
			}else{
				return new HttpRequest();
			}
			
			
		}
}
