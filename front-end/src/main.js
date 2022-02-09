import Vue from 'vue'
import App from './App.vue'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Vue3TouchEvents from "vue3-touch-events";

Vue.use(Vue3TouchEvents);
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')


export const URL_REST = process.env.VUE_APP_URL_REST || 'localhost:3000/';
export const URL_WS = process.env.VUE_APP_URL_WS || 'localhost:4000/';
