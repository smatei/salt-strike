import Vue from 'vue'
import ExampleTemplate from './components/ExampleTemplate.vue'

Vue.component('example-template', ExampleTemplate);

new Vue({
  el: '#example',
  data () {
    return {
      param: javaParameter
    }
  },
  // pass data from javascript to template
  template: '<example-template :prop="param"/>'
})