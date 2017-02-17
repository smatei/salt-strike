<template>
  <vuetable ref="vuetable"
    api-url="/minions"
    :fields="fields"
    class="table table-striped table-hover"
    @vuetable:loading="showLoader"
    @vuetable:load-success="onLoadSuccess"
    @vuetable:loaded="hideLoader"
  ></vuetable>
</template>

<script>
import Vuetable from 'vuetable-2/src/components/Vuetable.vue'

export default {
  components: {
    Vuetable
  },
  data () {
    return {
      fields: [
        {
          name: 'String/id',
          title: 'Minion',
          sortField: 'String/id',
        },
        {
          name: 'String/nodename',
          title: 'Nodename',
          sortField: 'String/nodename',
        },
        {
          name: 'String/os/osmajorrelease',
          title: 'OS',
          sortField: 'String/os/osmajorrelease',
        },
        {
          name: 'String/osarch',
          title: 'OS Arch',
          sortField: 'String/osarch',
        },
        {
          name: 'String/cpuarch',
          title: 'CPU Arch',
          sortField: 'String/cpuarch',
        },
        {
          name: 'Double/mem_total',
          title: 'RAM',
          titleClass: 'right aligned',
          dataClass: 'right aligned',
          sortField: 'Double/mem_total',
        }
      ]
    }
  },
  methods: {
    showLoader: function() {
      this.$parent._data.loading = 'loading';
    },
    hideLoader: function() {
      this.$parent._data.loading = ''
    },
    onLoadSuccess (response) {
    }
  }
}
</script>
