import Vue from 'vue'
import App from './App.vue'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Vue2TouchEvents from "vue2-touch-events";

Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

Vue.use(Vue2TouchEvents, {
  disableClick: false,
  touchClass: '',
  tapTolerance: 10,
  touchHoldTolerance: 400,
  swipeTolerance: 30,
  longTapTimeInterval: 400,
  namespace: 'touch'
})


Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')



export const URL_REST = process.env.VUE_APP_URL_REST || 'localhost:3000';
export const URL_WS = process.env.VUE_APP_URL_WS || 'localhost:4000';
