// package net.nanxu.payment.acl;
//
// import java.util.List;
// import lombok.RequiredArgsConstructor;
// import net.nanxu.payment.business.BusinessRegistry;
// import net.nanxu.payment.business.IBusiness;
// import net.nanxu.payment.exception.NotificationException;
// import org.springframework.stereotype.Component;
// import run.halo.app.plugin.extensionpoint.ExtensionGetter;
//
// /**
//  * NotificationExtensionGetter.
//  *
//  * @author: P
//  **/
// @RequiredArgsConstructor
// @Component
// public class BusinessExtensionGetter implements BusinessRegistry {
//     private final ExtensionGetter extensionGetter;
//
//     public List<IBusiness> getNotificationExtensions() {
//         return extensionGetter.getExtensionList(IBusiness.class);
//     }
//
//     @Override
//     public IBusiness getBusiness(String name) {
//         return getNotificationExtensions().stream()
//                 .filter(e -> e.getName().equals(name))
//                 .findFirst()
//                 .orElseThrow(() -> new NotificationException("不支持此通知类型"));
//     }
// }
