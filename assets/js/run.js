import Vue from 'vue'
import RunCommands from './components/RunCommands.vue'

Vue.component('run-commands', RunCommands);

new Vue({
  el: '#run-commands',
  data: {
	modules: JSON.parse(modules)
  },
  template: '<run-commands :modulesProp="modules"/>'
})
