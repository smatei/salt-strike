import Vue from 'vue'
import RunCommands from './components/RunCommands.vue'

Vue.component('run-commands', RunCommands);

new Vue({
  el: '#run-commands',
  template: '<run-commands/>'
})
