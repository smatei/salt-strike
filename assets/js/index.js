import Vue from 'vue'
import MinionsTable from './components/MinionsTable.vue'

new Vue({
  el: '#minion-table',
  data: {
	loading: 'loading'
  },
  template: '<div id="minion-table" ' +
    ':class="[{\'vuetable-wrapper ui basic segment\': true}, loading]">' +
    '<minions-table ref="minionref" /></div>',
  components: { MinionsTable }
})
