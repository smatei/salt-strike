import Vue from 'vue'
import MinionsTable from './components/MinionsTable.vue'

Vue.component('minions-table', MinionsTable);

new Vue({
  el: '#minion-table',
  template: '<minions-table/>'
})
