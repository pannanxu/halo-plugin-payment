package io.mvvm.halo.plugins.payment.endpoint;

import io.mvvm.halo.plugins.payment.PaymentProvider;
import io.mvvm.halo.plugins.payment.endpoint.vo.PaymentExtensionVo;
import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptorGetter;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentExtension;
import io.mvvm.halo.plugins.payment.sdk.PaymentQuery;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMethod;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;

import java.util.Set;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * InitPaymentEndpoint.
 *
 * @author: pan
 **/
@Setter
@Component
public class CorePaymentEndpoint implements PaymentEndpoint {

    private final ReactiveExtensionClient client;
    private PaymentDispatcher dispatcher;
    private PaymentProvider provider;

    public CorePaymentEndpoint(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return route(GET("/init/{name}"), this::init)
                .and(route(GET("/enable/{name}"), this::enable))
                .and(route(GET("/disable/{name}"), this::disable))
                .and(route(GET("/list/enabled"), this::list))
                .and(route(GET("/list/all"), this::listAll))
                ;
    }

    @Override
    public GroupVersion groupVersion() {
        return new GroupVersion("/apis/payment.mvvm.io", "v1");
    }

    Mono<ServerResponse> init(ServerRequest request) {
        try {
            Mono<ApiResponse> resp = provider.getOperator(request.pathVariable("name"))
                    .flatMap(IPaymentOperator::initConfig)
                    .map(res -> new ApiResponse(res, null, res))
                    .onErrorResume(ex -> Mono.just(new ApiResponse(false, ex.getMessage(), false)));
            return ServerResponse.ok().body(resp, Boolean.class);
        } catch (Exception ex) {
            return ServerResponse.ok().body(Mono.just(new ApiResponse(false, ex.getMessage(), false)), Boolean.class);
        }
    }

    /**
     * TODO 目前是直接写死在这里，未来需要在页面中进行选配
     */
    Mono<ServerResponse> enable(ServerRequest request) {
        String name = request.pathVariable("name");
        Mono<ApiResponse> extensionMono = provider.getOperator(request.pathVariable("name"))
                .flatMap(operator -> {
                    operator.getPluginWrapper().getPluginManager().startPlugin(operator.getPluginWrapper().getPluginId());
                    return client.fetch(PaymentExtension.class, name)
                            .switchIfEmpty(Mono.defer(() -> {
                                PaymentExtension ext = new PaymentExtension();
                                ext.setSpec(new PaymentExtension.Spec());
                                ext.getSpec().setEnabled(Boolean.TRUE);
                                ext.getSpec().setDisplayName(operator.getDescriptor().getTitle());
                                ext.getSpec().setEnableMethods(Set.of(PaymentMethod.fetch.name(),
                                        PaymentMethod.create.name(),
                                        PaymentMethod.cancel.name(),
                                        PaymentMethod.refund.name()));
                                ext.setMetadata(new Metadata());
                                ext.getMetadata().setName(operator.getDescriptor().getName());
                                ext.setKind(PaymentExtension.kind);
                                ext.setApiVersion(PaymentExtension.version);
                                ext.getMetadata().setVersion(1L);
                                return client.create(ext);
                            }))
                            .map(ext -> {
                                ext.getSpec().setEnabled(Boolean.TRUE);
                                ext.getSpec().setEnableMethods(Set.of(PaymentMethod.fetch.name(),
                                        PaymentMethod.create.name(),
                                        PaymentMethod.cancel.name(),
                                        PaymentMethod.refund.name()));
                                return ext;
                            })
                            .flatMap(client::update);
                })
                .map(ext -> new ApiResponse(true, null, ext))
                .onErrorResume(ex -> Mono.just(new ApiResponse(false, ex.getMessage(), false)));
        return ServerResponse.ok().body(extensionMono, ApiResponse.class);
    }

    Mono<ServerResponse> list(ServerRequest request) {
        String device = request.queryParam("device").orElse(null);
        PaymentQuery query = new PaymentQuery();
        query.setEndpoint(device);
        Flux<PaymentDescriptorGetter> descriptorFlux = dispatcher.payments(query).map(IPayment::getDescriptor);
        return ServerResponse.ok().body(descriptorFlux, PaymentDescriptor.class);
    }

    Mono<ServerResponse> listAll(ServerRequest request) {
        Flux<PaymentExtensionVo> list = provider.getPayments()
                .flatMap(payment -> client.fetch(PaymentExtension.class, payment.getDescriptor().getName())
                        .map(ext -> new PaymentExtensionVo(ext, payment.getDescriptor()))
                        .switchIfEmpty(Mono.just(new PaymentExtensionVo(null, payment.getDescriptor()))));
        return ServerResponse.ok().body(list, PaymentExtensionVo.class);
    }

    Mono<ServerResponse> disable(ServerRequest request) {
        Mono<ApiResponse> resp = provider.getOperator(request.pathVariable("name"))
                .flatMap(operator -> client.fetch(PaymentExtension.class, operator.getDescriptor().getName())
                        .flatMap(ext -> {
                            ext.getSpec().setEnabled(Boolean.FALSE);
                            return client.update(ext);
                        }))
                .map(ext -> new ApiResponse(true, null, ext))
                .onErrorResume(ex -> Mono.just(new ApiResponse(false, ex.getMessage(), false)));
        return ServerResponse.ok().body(resp, Boolean.class);
    }

}
