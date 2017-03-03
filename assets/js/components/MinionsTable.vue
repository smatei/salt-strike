<template>
  <div class="m-4">
    <filter-bar></filter-bar>
    <div :class="[{'vuetable-wrapper ui basic segment': true}, loading]">
      <vuetable ref="vuetable"
        api-url="/minions"
        :fields="fields"
        class="table table-striped table-hover"
        :appendParams="moreParams"
        :sort-order="sortOrder"
        @vuetable:loading="showLoader"
        @vuetable:load-success="onLoadSuccess"
        @vuetable:loaded="hideLoader">
      </vuetable>
    </div>
  </div>
</template>

<script>
import Vuetable from 'vuetable-2/src/components/Vuetable.vue'
import FilterBar from './MinionsFilterBar.vue'

import Vue from 'vue'
import VueEvents from 'vue-events'
Vue.use(VueEvents)

Vue.component('filter-bar', FilterBar)

export default {
  components: {
    Vuetable
  },
  data () {
    return {
      loading: 'loading',
      fields: [
        {
          name: 'String/id',
          title: 'Minion ID',
          sortField: 'String/id',
        },
        {
          name: 'String/nodename',
          title: 'Nodename',
          sortField: 'String/nodename',
        },
        {
          name: 'String/kernel',
          title: 'Kernel',
          sortField: 'String/kernel',
        },        
        {
          name: 'String/os/osmajorrelease',
          title: 'OS',
          sortField: 'String/os/osmajorrelease',
          callback: 'osAndLogo',
        },
        {
          name: 'String/osarch',
          title: 'OS Arch',
          sortField: 'String/osarch',
        },
        {
          name: 'Double/mem_total',
          title: 'RAM',
          titleClass: 'right aligned',
          dataClass: 'right aligned',
          sortField: 'Double/mem_total',
          callback: 'toGigaBytes',
        }
      ],
      sortOrder: [
        {
          field: 'String/id',
          sortField: 'String/id',
          direction: 'asc'
        }
      ],
      moreParams: {}
    }
  },
  methods: {
    showLoader: function() {
      this.loading = 'loading';
    },
    hideLoader: function() {
      this.loading = ''
    },
    onLoadSuccess(response) {
    },
    toGigaBytes(value) {
      var n = value / 1024;
      return n.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "1,") + " GB";
    },
    osAndLogo(value) {
      var os = value.substring(0, value.indexOf("|"));

      var url = "img/" + os.toLowerCase() + ".svg";
      var text = value.replace("|", " ");

      return "<img src='img/" + os.toLowerCase()
        + ".svg' style='width:20px; height:20px;'"
        + " onerror='this.onerror=null;this.src=\"img/linux.svg\"'"
        + ">&nbsp;"
        + text;
    }
  },
  events: {
    'filter-set' (filterText) {
      this.moreParams = {
        'filter': filterText
      }
      Vue.nextTick( () => this.$refs.vuetable.refresh())
    },
    'filter-reset' () {
      this.moreParams = {}
      this.$refs.vuetable.refresh()
      Vue.nextTick( () => this.$refs.vuetable.refresh())
    }
  }
}
</script>
