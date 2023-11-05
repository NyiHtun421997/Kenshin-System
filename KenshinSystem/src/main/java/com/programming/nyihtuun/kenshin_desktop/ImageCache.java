package main.java.com.programming.nyihtuun.kenshin_desktop;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

public class ImageCache {
	
	 private CacheManager cacheManager;
	 private Cache<String, byte[]> imageCache;
	
	
	public ImageCache() {
		
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
		cacheManager.init();
		
		imageCache = cacheManager.createCache("ImageCache", 
				CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class,byte[].class
				,ResourcePoolsBuilder.heap(30)));
	}


	public Cache<String, byte[]> getImageCache() {
		return imageCache;
	}


	public CacheManager getCacheManager() {
		return cacheManager;
	}

}