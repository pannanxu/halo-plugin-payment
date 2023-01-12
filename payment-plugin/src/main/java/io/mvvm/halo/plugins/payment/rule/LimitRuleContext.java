package io.mvvm.halo.plugins.payment.rule;

import io.mvvm.halo.plugins.payment.sdk.ExpandConst;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentSetting;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * LimitRuleContext.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class LimitRuleContext {
    private final Map<String, RecentCounter> IP_LIMIT_MAP = new ConcurrentHashMap<>(20);
    private final PayEnvironmentFetcher fetcher;

    public LimitRuleContext(PayEnvironmentFetcher fetcher) {
        this.fetcher = fetcher;
        Timer timer = new Timer("LimitRule_Timer", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Set<String> keys = IP_LIMIT_MAP.keySet();
                    for (String key : keys) {
                        RecentCounter counter = IP_LIMIT_MAP.get(key);
                        if (null != counter) {
                            if (counter.isEmpty()) {
                                IP_LIMIT_MAP.remove(key);
                            } else {
                                counter.peek();
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }, 10000, 10000);
    }

    public Mono<Boolean> limit(PaymentRequest request) {
        String limitKey = request.getExpandAsString(ExpandConst.limitRuleKey);
        if (StringUtils.hasLength(limitKey)) {
            RecentCounter counter = IP_LIMIT_MAP.computeIfAbsent(limitKey, s -> new RecentCounter());
            return fetcher.fetchPaymentConfig(PaymentSetting.basic)
                    .flatMap(setting -> {
                        PaymentSetting.Limit limit = setting.getLimit();
                        if (!counter.ping(limit.getSecond(), limit.getCount())) {
                            return Mono.error(new BaseException("访问频繁，请稍后重试"));
                        }
                        return Mono.just(Boolean.TRUE);
                    });
        }
        return Mono.just(Boolean.TRUE);
    }

    private static class RecentCounter {
        private final Queue<Long> queue = new ConcurrentLinkedQueue<>();

        /**
         * second 秒内可以访问 count 次
         */
        public boolean ping(int second, int count) {
            peek();
            if (queue.size() < count) {
                queue.add((second * 1000L) + System.currentTimeMillis());
                return true;
            }
            return false;
        }

        public void peek() {
            Long peek = queue.peek();
            if (null != peek && peek <= System.currentTimeMillis()) {
                queue.poll();
                peek();
            }
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

    }

}
