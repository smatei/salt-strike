import Vue from 'vue'
import RunCommands from './components/RunCommands.vue'


new Vue({
  el: '#run-commands',
  data: {
  },
  template: '<div id="run-commands">' +
    '<run-commands ref="runcommands"/></div>',
  components: { RunCommands }
})
