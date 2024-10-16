package net.nanxu.payment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import net.nanxu.payment.account.Account;
import net.nanxu.payment.account.IAccount;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Extension;
import run.halo.app.extension.GroupVersionKind;
import run.halo.app.extension.JsonExtension;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequest;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Unstructured;
import run.halo.app.extension.Watcher;
import run.halo.app.extension.index.IndexedQueryEngine;
import run.halo.app.infra.utils.JsonUtils;

/**
 * ReactiveExtensionClientTest.
 *
 * @author: pan
 **/
public class ReactiveExtensionClientTest implements ReactiveExtensionClient {
    
    public static final List<IAccount> store = new CopyOnWriteArrayList<>();

    public ReactiveExtensionClientTest() {
        Account account = new Account();
        account.setName("test");
        account.setChannel("WeChat");
        account.setConfig(
            JsonUtils.jsonToObject("{\"appId\":\"123\",\"secret\":\"123\"}", ObjectNode.class));
        store.add(account);
    }

    @Override
    public <E extends Extension> Flux<E> list(Class<E> aClass, Predicate<E> predicate,
        Comparator<E> comparator) {
        return null;
    }

    @Override
    public <E extends Extension> Mono<ListResult<E>> list(Class<E> aClass, Predicate<E> predicate,
        Comparator<E> comparator, int i, int i1) {
        return null;
    }

    @Override
    public <E extends Extension> Flux<E> listAll(Class<E> aClass, ListOptions listOptions,
        Sort sort) {
        return null;
    }

    @Override
    public <E extends Extension> Mono<ListResult<E>> listBy(Class<E> aClass,
        ListOptions listOptions, PageRequest pageRequest) {
        return null;
    }

    @Override
    public <E extends Extension> Mono<E> fetch(Class<E> aClass, String s) {
        return null;
    }

    @Override
    public Mono<Unstructured> fetch(GroupVersionKind groupVersionKind, String s) {
        return null;
    }

    @Override
    public <E extends Extension> Mono<E> get(Class<E> aClass, String s) {
        if (Account.class.isAssignableFrom(aClass)) {
            return Mono.justOrEmpty((E) store.stream().filter(e -> e.getName().equals(s)).findFirst().orElse(null));
        }
        return Mono.empty();
    }

    @Override
    public Mono<JsonExtension> getJsonExtension(GroupVersionKind groupVersionKind, String s) {
        return null;
    }

    @Override
    public <E extends Extension> Mono<E> create(E e) {
        return null;
    }

    @Override
    public <E extends Extension> Mono<E> update(E e) {
        return null;
    }

    @Override
    public <E extends Extension> Mono<E> delete(E e) {
        return null;
    }

    @Override
    public IndexedQueryEngine indexedQueryEngine() {
        return null;
    }

    @Override
    public void watch(Watcher watcher) {

    }
}
