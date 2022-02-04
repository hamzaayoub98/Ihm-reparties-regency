import Vue from 'vue'
import App from './App.vue'

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')

export const URL_REST = process.env.VUE_APP_URL_REST || 'localhost:3000/';
export const URL_WS = process.env.VUE_APP_URL_WS || 'localhost:4000/';
