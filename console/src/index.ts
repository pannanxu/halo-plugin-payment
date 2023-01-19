import DefaultView from "./views/DefaultView.vue";
import "@/styles/index.css";
import {definePlugin} from "@halo-dev/console-shared";

import {IconGrid} from "@halo-dev/components";
import {markRaw} from "vue";

export default definePlugin({
  name: "Payment",
  components: [],
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/payment",
        children: [
          {
            path: "",
            name: "Payment",
            component: DefaultView,
            meta: {
              title: "Payment Core",
              searchable: true,
              menu: {
                name: "Core",
                group: "支付插件",
                icon: markRaw(IconGrid),
                priority: 0,
              },
            },
          },
        ],
      },
    },
  ],
  extensionPoints: {},
});
