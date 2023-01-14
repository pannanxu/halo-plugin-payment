import DefaultView from "./views/DefaultView.vue";
import "./styles/index.css";
import {definePlugin} from "@halo-dev/console-shared";

import {IconGrid} from "@halo-dev/components";
import {markRaw} from "vue";
export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/payments",
        name: "Payments",
        component: DefaultView,
        meta: {
          permissions: ["plugin:payments:view"],
          menu: {
            name: "Payment Core",
            group: "content",
            icon: markRaw(IconGrid),
          },
        },
      },
    },
  ]
});
