package com.db.common.aspect;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import com.db.common.cache.CacheKey;
import com.db.common.cache.ReadWriteLockLruCache;
import com.db.common.cache.SynchronizedLruCache;
@Aspect
@Service
public class SysCacheAspect {
	 //private Map<CacheKey,Object> cacheMap=new ConcurrentHashMap<>();
	 //private SynchronizedLruCache<CacheKey,Object> cacheMap=new SynchronizedLruCache<>(3);
	 //基于读写锁实现的LRUCache
	 private ReadWriteLockLruCache<CacheKey,Object> cacheMap
	 =new ReadWriteLockLruCache<>(3);
	 
	 @Around("@annotation(com.db.common.annotation.RequiresCache)")
	 public Object aroundCache(ProceedingJoinPoint jp)
	 throws Throwable{
		 
		 CacheKey key=new CacheKey();
		 Object target=jp.getTarget();
		 key.setTargetClass(target.getClass());
		 MethodSignature ms=(MethodSignature)jp.getSignature();
		 key.setTargetMethod(ms.getMethod());
		 key.setArgs(jp.getArgs());
		 
		 Object result=cacheMap.get(key);
		 if(result!=null){
			 System.out.println("data from cache");
			 return result;
		 }
		 //调用目标方法(这个步骤会通过service访问数据库)
		 result=jp.proceed();
		 //将数据放入缓存
		 cacheMap.put(key,result);
		 return result;
	 }
}
