import Vue from 'vue'
import App from './App.vue'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Vue3TouchEvents from "vue3-touch-events";
import 'vue2-toast/lib/toast.css';
import Toast from 'vue2-toast';

Vue.use(Vue3TouchEvents);
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)
Vue.use(Toast, {
  type: 'center',
  duration: 3000,
  wordWrap: true,
  width: '150px'
});
Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')


export const URL_REST = 'localhost:3000';
export const URL_WS = 'localhost:4000';
