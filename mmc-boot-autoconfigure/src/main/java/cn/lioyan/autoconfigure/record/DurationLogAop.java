package cn.lioyan.autoconfigure.record;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * {@link DurationLogAop}
 * 设置每个方法的执行时间
 *
 * @author cn.lioyan
 * @since 2022/4/14 14:23
 */
@Aspect
public class DurationLogAop {

    @Value("${sec.duration.log.active:true}")
    private boolean active;

    private static final Logger LOG = LoggerFactory.getLogger(DurationLogAop.class);

    @Around("@annotation(cn.lioyan.autoconfigure.record.Timing)")
    public Object timeRecord(ProceedingJoinPoint p) throws Throwable {
        if(active){
            long s = System.currentTimeMillis();
            Object o = p.proceed();
            long duration = System.currentTimeMillis() - s;
            LOG.info("execute use {} ms",duration);
            return o;
        }else {
            return p.proceed();
        }
    }

    @After("@annotation(cn.lioyan.autoconfigure.record.Timing)")
    public void after(JoinPoint joinPoint){
        if(active){
            LOG.info("execute on {} ",joinPoint.getTarget().getClass().getName()+"."+joinPoint.getSignature().getName() );
        }
    }

}
